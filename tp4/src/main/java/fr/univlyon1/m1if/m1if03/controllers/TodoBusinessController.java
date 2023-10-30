package fr.univlyon1.m1if.m1if03.controllers;

import fr.univlyon1.m1if.m1if03.dao.TodoDao;
import fr.univlyon1.m1if.m1if03.dao.TodoDao;
import fr.univlyon1.m1if.m1if03.model.User;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.NotNull;

import javax.naming.InvalidNameException;
import javax.naming.NameNotFoundException;
import java.io.IOException;

@WebServlet(name = "TodoBusinessController", urlPatterns = {"/todo/toggleStatus"})
public class TodoBusinessController extends HttpServlet {

    private TodoBusiness todoBusiness;
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        TodoDao todoDao = (TodoDao) config.getServletContext().getAttribute("todoDao");
        todoBusiness = new TodoBusiness(todoDao);
    }

    /**
     * Réalise l'opération demandée en fonction de la fin de l'URL de la requête
     * @param request  Voir doc...
     * @param response Voir doc...
     * @throws IOException Voir doc...
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (request.getRequestURI().endsWith("toggleStatus")) {
            // TODO Parsing des paramètres "old school". Sera amélioré par la suite.
            String title = request.getParameter("title");
            int id = title.hashCode();
            if (title != null && !title.isEmpty()) {
                try {
                    if (userBusiness.userLogin(login, password, request)) {
                        response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                    } else {
                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Les login et mot de passe ne correspondent pas.");
                    }
                } catch (IllegalArgumentException ex) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
                } catch (NameNotFoundException e) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "L'utilisateur " + login + " n'existe pas.");
                } catch (InvalidNameException ignored) {
                    // Ne doit pas arriver car les logins des utilisateurs sont des Strings
                }
            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            }
        } else if (request.getRequestURI().endsWith("logout")) {
            userBusiness.userLogout(request);
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } else {
            // Ne devrait pas arriver mais sait-on jamais...
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    private static class TodoBusiness {
        private final TodoDao todoDao;

        /**
         * Constructeur avec une injection du DAO nécessaire aux opérations.
         * @param todoDao le DAO d'utilisateurs provenant du contexte applicatif
         */
        TodoBusiness(TodoDao todoDao) {
            this.todoDao = todoDao;
        }
    }
}