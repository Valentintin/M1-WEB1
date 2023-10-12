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

@WebFilter(filterName = "ChangeName", urlPatterns = {"/user.jsp"})
public class ChangeName extends HttpFilter {
    private final String[] whiteList = {"/", "/index.html", "/css/style.css"};

    @Override
    public void init(FilterConfig config) throws ServletException {
        super.init(config);
    }
    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
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
