package fr.univlyon1.m1if.m1if03.controllers;

import fr.univlyon1.m1if.m1if03.dao.TodoDao;
import fr.univlyon1.m1if.m1if03.dto.todo.TodoDtoMapper;
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

import javax.naming.NameAlreadyBoundException;
import java.io.IOException;

@WebServlet(name = "TodosResourceController", urlPatterns = {"/todos", "/todos/*"})
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
                response.setHeader("Location", "todos/" + title );
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
    };

}