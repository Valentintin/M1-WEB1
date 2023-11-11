package fr.univlyon1.m1if.m1if03.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
/**
 * filtre de cache.
 */
@WebFilter(filterName = "Cache", urlPatterns = {"/todolist"})
public class CacheFilter extends HttpFilter {
    private Map<String, Date> dateMap;
    private String lastTodoTitle;

    @Override
    public void init(FilterConfig config) throws ServletException {
        super.init(config);
        dateMap = new HashMap<>();
    }

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        //1 requete POST ajout todo ou Get sur liste
        boolean isGetList = (request.getMethod().equals("GET"));
        boolean isPost = (request.getMethod().equals("POST") && request.getParameter("operation").equals("add"));
        if(isPost){
            //passez la main à l'élément suivant de la chaîne
            chain.doFilter(request, response);
            //stockez la date courante dans la map ci-dessus.
            String titleTmp = request.getParameter("title");
            if(titleTmp != null) {
                lastTodoTitle = titleTmp;
                System.out.println(lastTodoTitle);
            }
        }
        this.dateMap.put(lastTodoTitle, new Date());
        if(isGetList) {
            //générez un en-tête de réponse Last-Modified, à l'aide de la méthode response.setDateHeader(...).
            long ifModifiedSince = request.getDateHeader("If-Modified-Since");
            Date lastModified  = dateMap.get(this.lastTodoTitle);
            System.out.println(lastModified.getTime());
            System.out.println(ifModifiedSince);
            if (lastModified != null && ifModifiedSince > 0 && lastModified.getTime() >= ifModifiedSince) {
                // If the resource hasn't been modified since the client's If-Modified-Since date,
                // respond with a 304 (Not Modified) status.
                response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
            } else {
                // If the resource has been modified or If-Modified-Since header is not provided,
                // generate a new Last-Modified header and process the request as usual.
                if (lastModified != null) {
                    response.setDateHeader("Last-Modified", lastModified.getTime());
                }
                chain.doFilter(request, response);
            }
            //génération de la page + en-tête Last-Modified ou
            //code de statut 304 (Not Modified)
        }
    }
}
