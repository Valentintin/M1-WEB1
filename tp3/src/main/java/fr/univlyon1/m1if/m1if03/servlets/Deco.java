package fr.univlyon1.m1if.m1if03.servlets;

import fr.univlyon1.m1if.m1if03.classes.User;
import fr.univlyon1.m1if.m1if03.daos.Dao;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import javax.naming.InvalidNameException;
import javax.naming.NameNotFoundException;
import java.io.IOException;

/**
 * Cette servlet invalide la session de l'utilisateur et le supprime du contexte pour les méthodes GET et POST.
 *
 * @author Lionel Médini
 */
@WebServlet(name = "Deco", value = "/deco")
public class Deco extends HttpServlet {
    public void init(ServletConfig config) throws ServletException {
        // Cette instruction doit toujours être au début de la méthode init() pour pouvoir accéder à l'objet config.
        super.init(config);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        doGet(request, response);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        String login = null;
        try {
            login = (String) session.getAttribute("login");
            session.invalidate();
            ((Dao<User>) this.getServletContext().getAttribute("users")).deleteById(login);
            response.sendRedirect("index.html");
        } catch (NameNotFoundException | InvalidNameException e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Le login de l'utilisateur courant est erroné : " + login + ".");
        }
    }
}
