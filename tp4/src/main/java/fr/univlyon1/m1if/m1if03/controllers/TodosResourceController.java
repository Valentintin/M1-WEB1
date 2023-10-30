package fr.univlyon1.m1if.m1if03.controllers;

import fr.univlyon1.m1if.m1if03.dao.TodoDao;
import fr.univlyon1.m1if.m1if03.dto.todo.TodoDtoMapper;
import fr.univlyon1.m1if.m1if03.dto.todo.TodoResponseDto;
import fr.univlyon1.m1if.m1if03.exceptions.ForbiddenLoginException;
import fr.univlyon1.m1if.m1if03.model.Todo;
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

@WebServlet(name = "TodosResourceController", urlPatterns = {"/todo", "/todo/*"})
public class TodosResourceController extends HttpServlet {

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
                todoResource.create(title,creator);
                response.setHeader("Location", "todo/" + title.hashCode());
                response.setStatus(HttpServletResponse.SC_CREATED);
            } catch (IllegalArgumentException | ForbiddenLoginException ex) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
            } catch (NameAlreadyBoundException e) {
                response.sendError(HttpServletResponse.SC_CONFLICT, "Le todo " + title + " n'est plus disponible.");
            }
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setHeader("X-test", "doGet");
        String[] url = UrlUtils.getUrlParts(request);
        if (url.length == 1) { // Renvoie la liste de tous les todos
            request.setAttribute("todos", todoResource.readAll());
            // Transfère la gestion de l'interface à une JSP
            request.getRequestDispatcher("/WEB-INF/components/todo.jsp").include(request, response);
            return;
        }
        try {
            Todo todo = todoResource.readOne(url[1]);
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
                        response.sendRedirect("todos" + urlEnd);
                    } else {
                        response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                    }
                }
            }
        } catch (IllegalArgumentException ex) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
        } catch (NameNotFoundException e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "L'utilisateur " + url[1] + " n'existe pas.");
        } catch (InvalidNameException ignored) {
            // Ne devrait pas arriver car les paramètres sont déjà des Strings
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
        /**
         * Renvoie les titres de tous les utilisateurs présents dans le DAO.
         *
         * @return la collection de todo
         */
        public Collection<Todo> readAll() {
            return todoDao.findAll();
        }


        public Todo readOne(@NotNull String title) throws IllegalArgumentException, NameNotFoundException, InvalidNameException {
            if (title == null || title.isEmpty()) {
                throw new IllegalArgumentException("Le title ne doit pas être null ou vide.");
            }
            return todoDao.findOne(title.hashCode());
        }
    };

}