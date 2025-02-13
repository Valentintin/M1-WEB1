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

/**
 * Filtre pour changer de nom.
 * N'autorise l'accès qu'aux clients qui possède ce compte.
 */
@WebFilter(filterName = "ChangeName", urlPatterns = {"/connect"})
public class ChangeName extends HttpFilter {
    public void init(FilterConfig config) throws ServletException {
        super.init(config);
    }
    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        if(request.getParameter("operation") == null || !request.getParameter("operation").equals("user")){
            chain.doFilter(request, response);
            return;
        }
        HttpSession session = request.getSession(false);
        String login = (String) session.getAttribute("login");
        if (login.equals(request.getParameter("user"))) {
            chain.doFilter(request, response);
            return;
        }
        // Bloque les autres requêtes
        response.sendError(HttpServletResponse.SC_FORBIDDEN, "Vous devez avoir le même login pour modifier.");
    }

}
