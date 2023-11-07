package fr.univlyon1.m1if.m1if03.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import fr.univlyon1.m1if.m1if03.dto.todo.TodoRequestDto;
import fr.univlyon1.m1if.m1if03.dto.user.UserRequestDto;
import fr.univlyon1.m1if.m1if03.utils.BufferlessHttpServletResponseWrapper;
import fr.univlyon1.m1if.m1if03.utils.ContentNegotiationHelper;
import fr.univlyon1.m1if.m1if03.utils.UrlUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.annotation.WebFilter;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

@WebFilter(filterName = "ContentNegotiationFilter")
public class ContentNegotiationFilter extends HttpFilter {
    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String[] url = UrlUtils.getUrlParts(request);

        Class<?> dtoClass;
        switch (request.getMethod()) {
            case "POST", "PUT" -> {
                // Traitement des requêtes qui peuvent avoir des contenus
                if (request.getHeader("Content-Type") == null || request.getHeader("Content-Type").isEmpty()) {
                    // On commence par traiter les requêtes sans contenus
                    super.doFilter(request, response, chain);
                } else {
                    switch (url[0]) {
                        case "users":
                            dtoClass = UserRequestDto.class;
                            break;
                        case "todos":
                            dtoClass = TodoRequestDto.class;
                            break;
                        default:
                            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                            return;
                    }
                    try {
                        request.setAttribute("dto", ContentNegotiationHelper.getDtoFromRequest(request, dtoClass));
                        super.doFilter(request, response, chain);
                    } catch (UnsupportedEncodingException e) {
                        response.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE, "Type MIME de la requête non supporté.");
                    } catch (IOException e) {
                        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Contenu du corps de la requête syntaxiquement incorrect.");
                    }
                }
            }
            case "DELETE" -> {
                // Rien à faire : pas de contenus dans la requête ni dans la réponse
                super.doFilter(request, response, chain);
            }
            case "GET" -> {
                // Traitement des requêtes qui peuvent attendre un contenu
                // On remplace la réponse par un wrapper (pour ne pas fermer le flux de sortie) dans la suite de la chaîne
                HttpServletResponse wrapper = new BufferlessHttpServletResponseWrapper(response);
                super.doFilter(request, wrapper, chain);
                // On analyse les résultats du traitement et génère la réponse en conséquence
                if (request.getAttribute("model") != null) {
                    if (response.getStatus() == HttpServletResponse.SC_OK) {
                        String accept = request.getHeader("Accept");
                        // Type de retour par défaut :
                        accept = (accept == null || accept.equals("*/*")) ? "text/html" : accept;
                        switch (ContentNegotiationHelper.parseMimeHeader(accept)) {
                            case "text/html" -> {
                                response.setHeader("Content-Type", "text/html");
                                request.getRequestDispatcher("/WEB-INF/components/" + request.getAttribute("view") + ".jsp").include(request, response);
                            }
                            case "application/xml" -> {
                                response.setHeader("Content-Type", "application/xml");
                                XmlMapper xmlMapper = new XmlMapper();
                                xmlMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
                                xmlMapper.writeValue(response.getWriter(), request.getAttribute("model"));
                            }
                            case "application/json" -> {
                                response.setHeader("Content-Type", "application/json");
                                ObjectMapper objectMapper = new ObjectMapper();
                                objectMapper.writeValue(response.getWriter(), request.getAttribute("model"));
                            }
                            default -> response.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE);
                        }
                    }
                }
            }
            default -> response.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED);
        }
    }
}
