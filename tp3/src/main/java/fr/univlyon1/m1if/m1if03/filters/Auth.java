package fr.univlyon1.m1if.m1if03.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Arrays;

/**
 * Filtre d'authentification.
 * N'autorise l'accès qu'aux clients ayant déjà une session existante ou ayant rempli le formulaire de la page <code>index.html</code>.
 * Dans ce dernier cas, le filtre crée la session de l'utilisateur, crée un objet User et l'ajoute en attribut de la session.
 * Laisse toutefois passer les URLs "/" et "/index.html".
 */
@WebFilter(filterName = "Auth", urlPatterns = {"*"})
public class Auth extends HttpFilter {
    private final String[] whiteList = {"/", "/index.html", "/css/style.css"};

    @Override
    public void init(FilterConfig config) throws ServletException {
        super.init(config);
    }

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        // Permet de retrouver la fin de l'URL (après l'URL du contexte) ; indépendant de l'URL de déploiement
        String url = request.getRequestURI().replace(request.getContextPath(), "");

        // Laisse passer les URLs ne nécessitant pas d'authentification et les requêtes par des utilisateurs authentifiés
        // Note :
        //   le paramètre false dans request.getSession(false) permet de récupérer null si la session n'est pas déjà créée.
        //   Sinon, l'appel de la méthode getSession() la crée automatiquement.
        if (Arrays.asList(whiteList).contains(url) || request.getSession(false) != null) {
            chain.doFilter(request, response);
            return;
        }

        // Traite les formulaires d'authentification
        String login = request.getParameter("login");
        if (url.equals("/connect") &&
                request.getMethod().equals("POST") &&
                login != null && !login.isEmpty()) {
            // Gestion de la session utilisateur
            HttpSession session = request.getSession(true);
            session.setAttribute("login", login);
            chain.doFilter(request, response);
            return;
        }

        // Bloque les autres requêtes
        response.sendError(HttpServletResponse.SC_FORBIDDEN, "Vous devez vous connecter pour accéder au site.");
    }
}
