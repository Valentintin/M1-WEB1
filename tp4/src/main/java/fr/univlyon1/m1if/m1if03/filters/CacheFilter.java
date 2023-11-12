package fr.univlyon1.m1if.m1if03.filters;

import fr.univlyon1.m1if.m1if03.dto.todo.TodoRequestDto;
import fr.univlyon1.m1if.m1if03.utils.BufferlessHttpServletResponseWrapper;
import fr.univlyon1.m1if.m1if03.utils.UrlUtils;
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
@WebFilter
public class CacheFilter extends HttpFilter {
    private Map<Integer, Date> dateMap;
    private Integer lastTodoHash;

    @Override
    public void init(FilterConfig config) throws ServletException {
        super.init(config);
        dateMap = new HashMap<>();
    }

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {

        Date now = new Date();

        //1 requete POST ajout todo ou Get sur liste
        boolean isGetList = (request.getMethod().equals("GET"));
        String[] url = UrlUtils.getUrlParts(request);
        boolean isPost = ((request.getMethod().equals("POST")) || request.getMethod().equals("DELETE") || request.getMethod().equals("PUT"));
        boolean todo = (url.length >= 2);
        if(isPost){
            //On veut que le filtre s'excute à la fin pour qu'il y ait un hash dans le dto
            HttpServletResponse wrapper = new BufferlessHttpServletResponseWrapper(response);
            super.doFilter(request, wrapper, chain);

            //passez la main à l'élément suivant de la chaîne
            //chain.doFilter(request, response);

            //stockez la date courante dans la map ci-dessus.
            TodoRequestDto requestDto = (TodoRequestDto) request.getAttribute("dto");
            Integer hashTmp = null;
            if (requestDto != null) {
                hashTmp = requestDto.getHash();
            }
            if (request.getMethod().equals("DELETE")) {// Dans le DELETE il n'y a pas de dto qui est passé
                hashTmp = Integer.parseInt(url[1]);
            }
            if(hashTmp != null) {
                lastTodoHash = hashTmp;
                System.out.println(lastTodoHash);
                this.dateMap.put(lastTodoHash, now);
            }
            this.dateMap.put(0, now);

            //fermer la socket sinon ça fonctionne pas
            response.getWriter().close();
        }
        if(isGetList) {
            //générez un en-tête de réponse Last-Modified, à l'aide de la méthode response.setDateHeader(...).
            long ifModifiedSince = request.getDateHeader("If-Modified-Since");
            Date lastModified  = dateMap.get(0);
            if(todo){
                lastModified = dateMap.get(Integer.parseInt(url[1]));
            }
            System.out.println(lastModified.getTime());
            System.out.println(ifModifiedSince);
            if (lastModified != null && (int)(ifModifiedSince/1000.)*1000 > 0 && (int)(ifModifiedSince/1000.)*1000 >=  (int)(lastModified.getTime()/1000.)*1000) {
                // If the resource hasn't been modified since the client's Modified-Since date,
                // respond with a 304 (Not Modified) status.
                response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
            } else {
                // If the resource has been modified or If-Modified-Since header is not provided,
                // generate a new Last-Modified header and process the request as usual.
                response.setDateHeader("Last-Modified", now.getTime());
                chain.doFilter(request, response);
            }
            //génération de la page + en-tête Last-Modified ou
            //code de statut 304 (Not Modified)
        }
    }
}
