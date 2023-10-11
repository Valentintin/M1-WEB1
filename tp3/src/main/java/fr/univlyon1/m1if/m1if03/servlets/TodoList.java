package fr.univlyon1.m1if.m1if03.servlets;

import fr.univlyon1.m1if.m1if03.classes.Todo;

import fr.univlyon1.m1if.m1if03.classes.User;
import fr.univlyon1.m1if.m1if03.daos.Dao;
import fr.univlyon1.m1if.m1if03.exceptions.MissingParameterException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Objects;

/**
 * Cette servlet gère la liste des TODOs.
 * Elle permet actuellement d'afficher la liste et de créer de nouveaux TODOs.
 * Elle devra aussi permettre de modifier l'état d'un TODO_.
 *
 * @author Lionel Médini
 */
@WebServlet(name = "TodoList", value = "/todolist")
public class TodoList extends HttpServlet {
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        request.getRequestDispatcher("todolist.jsp").include(request, response);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        try {
            Dao<Todo> todos = (Dao<Todo>) this.getServletContext().getAttribute("todos");
            switch (request.getParameter("operation")) {
                case "add" -> {
                    if (request.getParameter("title") == null || request.getParameter("login") == null) {
                        throw new MissingParameterException("Paramètres du Todo insuffisamment spécifiés.");
                    }

                    // crée un nouveau TODO_ et l'ajoute à la liste
                    todos.add(new Todo(request.getParameter("title"), request.getParameter("login")));
                }
                case "update" -> {
                    // Récupération de l'index
                    int index = Integer.parseInt(request.getParameter("index"));
                    if (index < 0 || index >= todos.findAll().size()) {
                        throw new StringIndexOutOfBoundsException("Pas de todo avec l'index " + index + ".");
                    }
                    Todo todo = todos.findOne(index);
                    if (request.getParameter("toggle") != null && !request.getParameter("toggle").isEmpty()) {
                        todo.setCompleted(Objects.equals(request.getParameter("toggle"), "Done!"));
                    } else {
                        if (request.getParameter("assign") != null && !request.getParameter("assign").isEmpty()) {
                            String login = (String) request.getSession().getAttribute("login");
                            User user = ((Dao<User>) this.getServletContext().getAttribute("users")).findOne(login);
                            todo.setAssignee(user);
                        } else {
                            throw new MissingParameterException("Modification à réaliser non spécifiée.");
                        }
                    }
                }
                default -> throw new UnsupportedOperationException("Opération à réaliser non prise en charge.");
            }
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Format de l'index du Todo incorrect.");
            return;
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            return;
        }

        doGet(request, response);
    }
}
