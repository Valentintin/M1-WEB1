package fr.univlyon1.m1if.m1if03.filters;

import fr.univlyon1.m1if.m1if03.classes.Todo;
import fr.univlyon1.m1if.m1if03.daos.Dao;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.naming.InvalidNameException;
import javax.naming.NameNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;

@WebFilter(filterName = "Cache", urlPatterns = {"/todolist"})
public class Cache extends HttpFilter {
    public Map<String, Date> dateMap;

    @Override
    public void init(FilterConfig config) throws ServletException {
        super.init(config);
    }

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        //vérifier utilisateur connecté
        if (request.getSession(false) != null) {
            chain.doFilter(request, response);
            return;
        }
        //1 requete POST ajout todo ou Get sur liste
        boolean isGetList = (request.getMethod().equals("GET") && request.getAttribute("todos") != null);
        if(request.getParameter("operation").equals("add") || isGetList){
            //passez la main à l'élément suivant de la chaîne
            chain.doFilter(request, response);
            //stockez la date courante dans la map ci-dessus.
            Date date = new Date();
            String todo = request.getParameter("title");
            dateMap.put(todo, date);
            //2 Get sur liste
            if(isGetList) {
                //générez un en-tête de réponse Last-Modified, à l'aide de la méthode response.setDateHeader(...).
                Dao<Todo> todos = (Dao<Todo>) request.getAttribute("todos");
                int v = todos.findAll().size() - 1;
                try {
                    String title = todos.findOne(v).getTitle();
                    response.setDateHeader("Last-Modified", this.dateMap.get((title)).getTime());
                } catch (NameNotFoundException e) {
                    throw new RuntimeException(e);
                } catch (InvalidNameException e) {
                    throw new RuntimeException(e);
                }
                //testez pour vérifier que votre client vous renvoie bien cette valeur dans un en-tête If-Modified-Since lors de la requête d'actualisation de la page
                //3 Finalisez le traitement à la réception d'une requête GET : si la requête contient un en-tête If-Modified-Since, comparez la valeur de cet en-tête avec celle stockée dans la map et réagissez en conséquence
                //génération de la page + en-tête Last-Modified ou
                //code de statut 304 (Not Modified)
            }


        }




    }
}
