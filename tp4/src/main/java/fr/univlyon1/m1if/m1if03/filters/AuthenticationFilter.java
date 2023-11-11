package fr.univlyon1.m1if.m1if03.filters;

import fr.univlyon1.m1if.m1if03.dao.AbstractListDao;
import fr.univlyon1.m1if.m1if03.dao.UserDao;
import fr.univlyon1.m1if.m1if03.model.User;
import fr.univlyon1.m1if.m1if03.utils.TodosM1if03JwtHelper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.naming.InvalidNameException;
import javax.naming.NameNotFoundException;
import java.io.IOException;

import static fr.univlyon1.m1if.m1if03.utils.TodosM1if03JwtHelper.verifyToken;

/**
 * Filtre d'authentification.
 * Vérifie qu'une requête est authentifiée, sauf requêtes autorisées.<br>
 * Autorise les requêtes suivantes :
 * <ol>
 *     <li>URLs ne nécessitant pas d'authentification (whitelist), y compris les requêtes d'authentification</li>
 *     <li>Requêtes d'utilisateurs déjà authentifiés</li>
 * </ol>
 * Dans les cas contraires, renvoie un code d'erreur HTTP 401 (Unauthorized).
 *
 * @author Lionel Médini
 */
@WebFilter
public class AuthenticationFilter extends HttpFilter {
    private static final String[] WHITELIST = {"/", "/index.html", "/login.html", "/css/style.css", "/users", "/users/", "/users/login"};

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        // Permet de retrouver la fin de l'URL (après l'URL du contexte) -> indépendant de l'URL de déploiement
        String url = request.getRequestURI().replace(request.getContextPath(), "");

        // 1) Laisse passer les URLs ne nécessitant pas d'authentification
        for(String tempUrl: WHITELIST) {
            if(url.equals(tempUrl)) {
                chain.doFilter(request, response);
                return;
            }
        }

        // 2) Traite les requêtes qui doivent être authentifiées

        String token = request.getHeader("Authorization");
        if(token != null) {
                try {
                    token = token.replace("Bearer ", "");
                    String login = verifyToken(token, request);
                    request.setAttribute("token", token);
                    UserDao userDao = (UserDao) request.getServletContext().getAttribute("userDao");
                    User user = userDao.findOne(login);
                    request.setAttribute("user", user);
                    chain.doFilter(request, response);
                    return;
                }catch (NullPointerException e) {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token non valide");
                }catch (NameNotFoundException e) {
                    chain.doFilter(request, response);
//                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token non valide");
                }  catch (InvalidNameException e) {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token non valide");
                }
            return;
        }
        // Note : c'est fait au dessus
        //   le paramètre false dans request.getSession(false) permet de récupérer null si la session n'est pas déjà créée.
        //   Sinon, l'appel de la méthode getSession() la crée automatiquement.
//        if(request.getSession(false) != null && request.getSession(false).getAttribute("user") != null) {
//            chain.doFilter(request, response);
//            return;
//        }

        // 3) Bloque les autres requêtes
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Vous devez vous connecter pour accéder au site.");
    }
}
