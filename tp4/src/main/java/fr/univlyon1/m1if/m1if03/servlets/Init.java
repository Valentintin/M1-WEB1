package fr.univlyon1.m1if.m1if03.servlets;

import fr.univlyon1.m1if.m1if03.dao.TodoDao;
import fr.univlyon1.m1if.m1if03.dao.UserDao;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Servlet d'initialisation des DAOs.<br>
 * Sa responsabilité est d'être démarrée dès le début de l'application pour pouvoir initialiser les DAOs.<br>
 * Comme il faut nécessairement qu'elle soit mappée à une URL et qu'elle implémente une méthode de service...
 */
@WebServlet(name = "Init", urlPatterns = "/teapot", loadOnStartup = 1)
public class Init extends HttpServlet {
    @Override
    public void init(ServletConfig config) throws ServletException {
        // Cette instruction doit toujours être au début de la méthode init() pour pouvoir accéder à l'objet config.
        super.init(config);

        //Récupère le contexte applicatif
        ServletContext context = config.getServletContext();

        // Ajout des DAOs dans le contexte
        context.setAttribute("userDao", new UserDao());
        context.setAttribute("todoDao", new TodoDao());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.sendError(418, "I'm a teapot."); // https://developer.mozilla.org/fr/docs/Web/HTTP/Status/418
    }
}
