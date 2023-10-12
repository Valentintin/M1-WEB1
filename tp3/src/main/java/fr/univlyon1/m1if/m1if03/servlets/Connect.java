package fr.univlyon1.m1if.m1if03.servlets;

import fr.univlyon1.m1if.m1if03.classes.User;
import fr.univlyon1.m1if.m1if03.daos.Dao;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import javax.naming.InvalidNameException;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NameNotFoundException;
import java.io.IOException;

/**
 *
 * récupère les infos de l'utilisateur pour les placer dans sa session
 * et affiche l'interface du chat.
 *
 * @author Lionel Médini
 */
@WebServlet(name = "Connect", urlPatterns = {"/connect"})
public class Connect extends HttpServlet {
    // Elles seront stockées dans le contexte applicatif pour pouvoir être accédées par tous les objets de l'application :

    // DAO d'objets User
    private Dao<User> users = null;
    @Override
    public void init(ServletConfig config) throws ServletException {
        // Cette instruction doit toujours être au début de la méthode init() pour pouvoir accéder à l'objet config.
        super.init(config);
        //Récupère le contexte applicatif et y place les variables globales
        ServletContext context = config.getServletContext();
        users = (Dao<User>) context.getAttribute("users");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if(request.getParameter("operation").equals("deco")){
            doGet(request, response);
        } else if(request.getParameter("operation").equals("connect")){
            // Gestion de la session utilisateur
            HttpSession session = request.getSession(true);
            User user = new User(request.getParameter("login"), request.getParameter("name"));

            session.setAttribute("login", user.getLogin());
            try {
                users.add(user);
            } catch (NameAlreadyBoundException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Un utilisateur avec le login " + user.getLogin() + " existe déjà.");
                return;
            }
            // Ceci est une redirection HTTP : le client est informé de cette redirection.
            response.sendRedirect("interface.jsp");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        // Utilise un RequestDispatcher pour "transférer" la requête à un autre objet, en interne du serveur.
        // Ceci n'est pas une redirection HTTP ; le client n'est pas informé de cette redirection.
        // Note :
        //     il existe deux méthodes pour transférer une requête (et une réponse) à l'aide d'un RequestDispatcher : include et forward
        //     voir les différences ici : https://docs.oracle.com/javaee/6/tutorial/doc/bnagi.html
        if(request.getParameter("operation").equals("deco")) {

            //nouveau code
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
        } else if(request.getParameter("operation").equals("connect")){
            request.getRequestDispatcher("interface.jsp").forward(request, response); //ancienne ligne
        }
    }
}
