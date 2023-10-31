package fr.univlyon1.m1if.m1if03.controllers;

import fr.univlyon1.m1if.m1if03.dao.TodoDao;
import fr.univlyon1.m1if.m1if03.dto.todo.TodoDtoMapper;
import fr.univlyon1.m1if.m1if03.dto.todo.TodoResponseDto;
import fr.univlyon1.m1if.m1if03.exceptions.ForbiddenLoginException;
import fr.univlyon1.m1if.m1if03.model.Todo;
import fr.univlyon1.m1if.m1if03.model.User;
import fr.univlyon1.m1if.m1if03.utils.UrlUtils;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;

import javax.naming.InvalidNameException;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NameNotFoundException;
import java.io.IOException;
import java.util.Collection;

@WebServlet(name = "TodoResourceController", urlPatterns = {"/todos", "/todos/*"})
public class TodoResourceController extends HttpServlet {

    private TodoDtoMapper todoMapper;
    private TodoResource todoResource;
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        TodoDao todoDao = (TodoDao) config.getServletContext().getAttribute("todoDao");
        todoMapper = new TodoDtoMapper(config.getServletContext());
        todoResource = new TodoResource(todoDao);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setHeader("X-test", "doPost");
        String[] url = UrlUtils.getUrlParts(request);

        if (url.length == 1) {// Création d'un todo
            // TODO Parsing des paramètres "old school" ; sera amélioré dans la partie négociation de contenus...
            //jpeut être mettre le hash code du todo à la place du titre pour le setHeader
            String title = request.getParameter("title");
            String creator = request.getParameter("creator");
            try {
                todoResource.create(title, creator);
                response.setHeader("Location", "todos/" + title.hashCode());
                response.setStatus(HttpServletResponse.SC_CREATED);
            } catch (IllegalArgumentException | ForbiddenLoginException ex) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
            } catch (NameAlreadyBoundException e) {
                response.sendError(HttpServletResponse.SC_CONFLICT, "Le todo " + title + " n'est plus disponible.");
            }
        } else if (url.length == 2) { // TOGGLE STATUS
            if (url[1].equals("toggleStatus")) {
                Integer id = Integer.parseInt(request.getParameter("hash"));
                try {
                    todoResource.modifStatut(id);
                    response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                } catch (IllegalArgumentException | ForbiddenLoginException ex) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
                } catch (NameNotFoundException e) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Le todo " + id + " n'existe pas.");
                } catch (NameAlreadyBoundException e) {
                    response.sendError(HttpServletResponse.SC_CONFLICT, "Le todo " + id + " n'est plus disponible.");
                } catch (InvalidNameException e) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Le todo " + id + " n'existe pas.");
                }
            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setHeader("X-test", "doGet");
        String[] url = UrlUtils.getUrlParts(request);
        if (url.length == 1) { // Renvoie la liste de tous les todos
            // TEST RECUP ID MARCHE PAS
            Collection<Todo> todos = todoResource.readAll();
            for (Todo todo : todos) {
                TodoResponseDto todoDto = todoMapper.toDto(todo);
                System.out.println(todoDto.getHash());
            }
            //
            request.setAttribute("todos", todoResource.readAll());
            // Transfère la gestion de l'interface à une JSP
            request.getRequestDispatcher("/WEB-INF/components/todolist.jsp").include(request, response);
            return;
        }
        try {
            Todo todo = todoResource.readOne(Integer.parseInt(url[1]));
            TodoResponseDto todoDto = todoMapper.toDto(todo);
            switch (url.length) {
                case 2 -> { // Renvoie un DTO de Todo (avec toutes les infos le concernant pour pouvoir le templater dans la vue)
                    request.setAttribute("todoDto", todoDto);
                    request.getRequestDispatcher("/WEB-INF/components/todo.jsp").include(request, response);
                }
                case 3 -> { // Renvoie le nom d'un todo
                    switch (url[2]) {
                        case "title" -> {
                            request.setAttribute("todoDto", new TodoResponseDto(todoDto.getTitle(), todoDto.getHash(), null));
                            request.getRequestDispatcher("/WEB-INF/components/todoProperty.jsp").include(request, response);
                        }
                        case "assignee" -> {
                            request.setAttribute("todoDto", new TodoResponseDto(null, todoDto.getHash(), todoDto.getAssignee()));
                            request.getRequestDispatcher("/WEB-INF/components/todoProperty.jsp").include(request, response);
                        }
                        default -> response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                    }
                }
                default -> { // Redirige vers l'URL qui devrait correspondre à la sous-propriété demandée (qu'elle existe ou pas ne concerne pas ce contrôleur)
                    if (url[2].equals("assignee")) {
                        // Construction de la fin de l'URL vers laquelle rediriger
                        String urlEnd = UrlUtils.getUrlEnd(request, 3);
                        response.sendRedirect("users" + urlEnd);
                    } else {
                        response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                    }
                }
            }
        } catch (IllegalArgumentException ex) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
        } catch (NameNotFoundException e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Le todo " + url[1] + " n'existe pas.");
        } catch (InvalidNameException ignored) {
            // Ne devrait pas arriver car les paramètres sont déjà des Strings
        }
    }

    /**
     * Réalise la modification d'un todo.
     * En fonction du title passé dans l'URL :
     * <ul>

     *     <li>Mise à jour d'un todo</li>
     * </ul>
     * Renvoie un code de statut 204 (No Content) en cas de succès ou une erreur HTTP appropriée sinon.
     *
     * @param request  Une requête dont l'URL est de la forme <code>/todos/{title}</code>
     * @param response Une réponse vide (si succès)
     * @throws IOException Voir doc...
     */
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String[] url = UrlUtils.getUrlParts(request);
        Integer id = Integer.parseInt(url[1]);
        // TODO Parsing des paramètres "old school" ; sera amélioré dans la partie négociation de contenus...
        String newtitle = request.getParameter("title");
        String assignee = request.getParameter("assignee");

        if (url.length == 2) {
            try {
                todoResource.update(id, newtitle, assignee);
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } catch (IllegalArgumentException ex) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
            } catch (NameNotFoundException e) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Le todo " + id + " n'existe pas.");
            } catch (InvalidNameException ignored) {
                // Ne devrait pas arriver car les paramètres sont déjà des Strings
            }
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    /**
     * Réalise l'aiguillage des requêtes DELETE.<br>
     * En clair : appelle simplement l'opération de suppression de todo.<br>
     * Renvoie un code 204 (No Content) si succès ou une erreur HTTP appropriée sinon.
     *
     * @param request  Une requête dont l'URL est de la forme <code>/todos/{title}</code>
     * @param response Une réponse vide (si succès)
     * @throws IOException Voir doc...
     */
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String[] url = UrlUtils.getUrlParts(request);
        Integer id = Integer.parseInt(url[1]);
        if (url.length == 2) {
            try {
                todoResource.delete(id);
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } catch (IllegalArgumentException ex) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
            } catch (NameNotFoundException e) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Le todo " + id + " n'existe pas.");
            } catch (InvalidNameException ignored) {
                // Ne devrait pas arriver car les paramètres sont déjà des Strings
            }
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    public class TodoResource {
        private final TodoDao todoDao;

        public TodoResource(TodoDao todoDao) {
            this.todoDao = todoDao;
        }

        public void create(@NotNull String title, @NotNull String creator)
                throws IllegalArgumentException, NameAlreadyBoundException, ForbiddenLoginException {
            if (title == null || title.isEmpty()) {
                throw new IllegalArgumentException("Le title ne doit pas être null ou vide.");
            }
            if (creator == null) {
                throw new IllegalArgumentException("Le creator ne doit pas être null.");
            }
            // Protection contre les valeurs de login qui poseraient problème au niveau des URLs
//            if (login.equals("login") || login.equals("logout")) {
//                throw new ForbiddenLoginException();
//            }
            todoDao.add(new Todo(title,creator));
        }

        public void modifStatut(@NotNull Integer hash)
                throws IllegalArgumentException, NameAlreadyBoundException, ForbiddenLoginException, InvalidNameException, NameNotFoundException {
            Todo todo = todoDao.findByHash(hash);
            todo.setCompleted(!todo.isCompleted());
        }
        /**
         * Renvoie les titres de tous les utilisateurs présents dans le DAO.
         *
         * @return la collection de todo
         */
        public Collection<Todo> readAll() {
            return todoDao.findAll();
        }


        public Todo readOne(@NotNull Integer id) throws IllegalArgumentException, NameNotFoundException, InvalidNameException {
            if (id == null) {
                throw new IllegalArgumentException("Le title ne doit pas être null ou vide.");
            }
            return todoDao.findByHash(id);
        }

        /**
         * Met à jour un todo en fonction des param.<br>
         * Si l'un des paramètres est nul ou vide, le champ correspondant n'est pas mis à jour.
         *
         * @param id     Le title de la todo
         * @param newtitle Le tile à modifier. Ou pas.
         * @param assignee      Le assignee à modifier. Ou pas.
         * @throws IllegalArgumentException Si le login est null ou vide
         * @throws InvalidNameException Ne doit pas arriver car les clés du DAO user sont des strings
         * @throws NameNotFoundException Si le login ne correspond pas à un utilisateur existant
         */
        public void update(@NotNull Integer id, String newtitle, String assignee) throws IllegalArgumentException, InvalidNameException, NameNotFoundException {
            Todo todo = readOne(id);
            if (newtitle != null && !newtitle.isEmpty()) {
                todo.setTitle(newtitle);
            }
            if (assignee != null && !assignee.isEmpty()) {
                todo.setAssignee(assignee);
            }
        }

        /**
         * Supprime un todo dans le DAO.
         *
         * @param hash L'id du todo à supprimer
         * @throws IllegalArgumentException Si le login est null ou vide
         * @throws NameNotFoundException Si le login ne correspond à aucune entrée dans le DAO
         * @throws InvalidNameException Ne doit pas arriver car les clés du DAO user sont des strings
         */
        public void delete(@NotNull Integer hash) throws IllegalArgumentException, NameNotFoundException, InvalidNameException {
            int id  = todoDao.getId(todoDao.findByHash(hash));
            todoDao.deleteById(id);
        }
    };

}