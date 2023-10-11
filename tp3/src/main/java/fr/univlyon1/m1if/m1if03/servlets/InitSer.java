package fr.univlyon1.m1if.m1if03.servlets;

import fr.univlyon1.m1if.m1if03.classes.User;
import fr.univlyon1.m1if.m1if03.daos.Dao;
import fr.univlyon1.m1if.m1if03.daos.TodoDao;
import fr.univlyon1.m1if.m1if03.daos.UserDao;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;

/**
 * Cette servlet initialise les objets communs à toute l'application.
 */
@WebServlet(name = "Init", urlPatterns = {"/init"}, loadOnStartup = 1)
public class InitSer extends HttpServlet {

    // DAO d'objets User
    private final Dao<User> users = new UserDao();
    @Override
    public void init(ServletConfig config) throws ServletException {
        // Cette instruction doit toujours être au début de la méthode init() pour pouvoir accéder à l'objet config.
        super.init(config);
        //Récupère le contexte applicatif et y place les variables globales
        ServletContext context = config.getServletContext();

        // Variables communes pour toute l'application (remplacent la BD).
        // Elles seront stockées dans le contexte applicatif pour pouvoir être accédées par tous les objets de l'application :
        context.setAttribute("users", users);
        // A modifier (DAO)
        context.setAttribute("todos", new TodoDao());
    }

}
