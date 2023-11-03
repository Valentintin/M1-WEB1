package fr.univlyon1.m1if.m1if03.filters;

import fr.univlyon1.m1if.m1if03.dao.TodoDao;
import fr.univlyon1.m1if.m1if03.model.Todo;
import fr.univlyon1.m1if.m1if03.model.User;
import fr.univlyon1.m1if.m1if03.utils.UrlUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

/**
 * Filtre d'autorisation.
 * <ul>
 *     <li>Vérifie que l'utilisateur authentifié a le droit d'accéder à certaines ressources. Renvoie un code 403 (Forbidden) sinon.</li>
 *     <li>Vérifie si l'utilisateur authentifié a le droit d'afficher complètement certaines ressources. Positionne un attribut de requête en conséquence.</li>
 * </ul>
 *
 * @author Lionel Médini
 */
@WebFilter
public class AuthorizationFilter extends HttpFilter {

    // Liste des ressources pour lesquelles renvoyer un 403 si l'utilisateur n'est pas le bon
    private static final String[][] RESOURCES_WITH_AUTHORIZATION = {
            {"PUT", "users", "*"},
            {"POST", "users", "*", "password"},
            {"PUT", "users", "*", "password"},
            {"POST", "users", "*", "name"},
            {"PUT", "users", "*", "name"},
            {"*", "users", "*", "assignedTodos"},
            {"POST", "todos", "toggleStatus"},
            {"*", "todos", "*", "assignee"}
    };

    // Liste des ressources qui
    private static final String[][] RESOURCES_WITH_LIMITATIONS = {
            {"GET", "users", "*"},
            {"GET", "todos", "*"}
    };

    public void init(FilterConfig config) throws ServletException {
        super.init(config);
    }

    @Override
    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        // Si l'utilisateur n'est pas authentifié (mais que la requête a passé le filtre d'authentification), c'est que ce filtre est sans objet
        if(request.getSession(false) == null || request.getSession(false).getAttribute("user") == null) {
            chain.doFilter(request, response);
            return;
        }

        TodoDao todoDao = (TodoDao) this.getServletContext().getAttribute("todoDao");
        String[] url = UrlUtils.getUrlParts(request);

        // S'il faut un attribut pour décider plus tard de l'affichage, par exemple d'une partie de la ressource.
        if (Stream.of(RESOURCES_WITH_LIMITATIONS).anyMatch(pattern -> UrlUtils.matchRequest(request, pattern))) {
            if (url[0].equals("users")) {
                request.setAttribute("authorizedUser", url[1].equals(((User) request.getSession(false).getAttribute("user")).getLogin()));
            } else if (url[0].equals("todos")) {
                try {
                    Todo todo = todoDao.findByHash(Integer.parseInt(url[1]));
                    request.setAttribute("authorizedUser", todo.getAssignee() != null &&
                            todo.getAssignee().equals(request.getSession(false).getAttribute("user")));
                } catch(Exception ignored) {} // Les exceptions sont traitées dans le contrôleur.
            }
        }

        // Application du filtre
        if (Stream.of(RESOURCES_WITH_AUTHORIZATION).anyMatch(pattern -> UrlUtils.matchRequest(request, pattern))) {
            switch (url[0]) {
                case "users" -> {
                    if (url[1].equals(((User) request.getSession(false).getAttribute("user")).getLogin())) {
                        chain.doFilter(request, response);
                    } else {
                        response.sendError(HttpServletResponse.SC_FORBIDDEN, "Vous n'avez pas accès aux informations de cet utilisateur.");
                    }
                }
                case "todos" -> {
                    try {
                        // Dans le cas du POST -> toggleStatus, le hash est dans le corps de la requête.
                        // TODO Parsing des paramètres "old school". Sera amélioré par la suite.
                        String todoHash = (url[1] != null && url[1].equals("toggleStatus")) ? request.getParameter("hash") : url[1];
                        Todo todo = todoDao.findByHash(Integer.parseInt(todoHash));
                        if (todo.getAssignee() != null && todo.getAssignee().equals(((User) (request.getSession(false).getAttribute("user"))).getLogin())) {
                            chain.doFilter(request, response);
                        } else {
                            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Vous n'êtes pas assigné.e à ce todo.");
                        }
                    } catch (IllegalArgumentException e) {
                        response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
                    } catch (NoSuchElementException e) {
                        response.sendError(HttpServletResponse.SC_NOT_FOUND, "Le todo " + url[1] + " n'existe pas.");
                    }
                }
                default -> // On laisse Tomcat générer un 404.
                        chain.doFilter(request, response);
            }
        } else {
            chain.doFilter(request, response);
        }
    }
}
