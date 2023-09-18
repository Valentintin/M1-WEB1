package fr.univlyon1.m1if.m1if03.servlets;

import fr.univlyon1.m1if.m1if03.classes.Todo;

import fr.univlyon1.m1if.m1if03.exceptions.MissingParameterException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import jakarta.servlet.RequestDispatcher;

/**
 * Cette servlet gère la liste des TODOs.
 * Elle permet actuellement d'afficher la liste et de créer de nouveaux TODOs.
 * Elle devra aussi permettre de modifier l'état d'un TODO_
 */
@WebServlet(name = "TodoList", value = "/todolist")
public class TodoList extends HttpServlet {

    //TODO Placer la map dans le contexte
    private final List<Todo> todos = new ArrayList<>();

    @Override
    public void init(ServletConfig config) throws ServletException {
        // Cette instruction doit toujours être au début de la méthode init() pour pouvoir accéder à l'objet config.
        super.init(config);
        //Récupère le contexte applicatif et y place les variables globales
        ServletContext context = config.getServletContext();
        context.setAttribute("todos", todos);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        response.setContentType("text/html;charset=UTF-8");

        request.setAttribute("todos", todos);

        RequestDispatcher dispatcher = request.getRequestDispatcher("/todolist.jsp");

        dispatcher.include(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        try {
            switch (request.getParameter("operation")) {
                case "add" -> {
                    if (request.getParameter("title") == null || request.getParameter("login") == null)
                        throw new MissingParameterException("Paramètres du Todo insuffisamment spécifiés.");

                    // crée un nouveau TODO_ et l'ajoute à la liste
                    todos.add(new Todo(request.getParameter("title"), request.getParameter("login")));
                }
                case "update" -> {
                    // Récupération de l'index
                    int index = Integer.parseInt(request.getParameter("index"));
                    if (index < 0 || index >= todos.size())
                        throw new StringIndexOutOfBoundsException("Pas de todo avec l'index " + index + ".");
                    if (request.getParameter("toggle") == null)
                        throw new MissingParameterException("Modification à réaliser non spécifiée.");
                    todos.get(index).setCompleted(Objects.equals(request.getParameter("toggle"), "Done!"));
                }
                default -> throw new UnsupportedOperationException("Opération à réaliser non prise en charge.");
            }
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Format de l'index du Todo incorrect.");
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }

        // Reprend le comportement des requêtes GET
        doGet(request, response);
    }
}
