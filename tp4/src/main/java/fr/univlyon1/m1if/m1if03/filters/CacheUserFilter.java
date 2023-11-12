package fr.univlyon1.m1if.m1if03.filters;

import fr.univlyon1.m1if.m1if03.dao.TodoDao;
import fr.univlyon1.m1if.m1if03.dao.UserDao;
import fr.univlyon1.m1if.m1if03.model.Todo;
import fr.univlyon1.m1if.m1if03.model.User;
import fr.univlyon1.m1if.m1if03.utils.UrlUtils;
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
import java.util.List;

/**
 * filtre de cache user.
 */
@WebFilter
public class CacheUserFilter extends HttpFilter {
    private UserDao userDao;

    public CacheUserFilter() {
        userDao = null;
    }

    @Override
    public void init(FilterConfig config) throws ServletException {
        super.init(config);
        this.userDao = (UserDao) config.getServletContext().getAttribute("userDao");
    }

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String[] url = UrlUtils.getUrlParts(request);
        if (request.getMethod().equals("GET") && url.length >= 2) {
            if (userDao == null) {
                userDao = (UserDao) getServletContext().getAttribute("userDao");
            }
            String ifNoneMatch = request.getHeader("If-None-Match");
            long tagHeader = 0;
            if (ifNoneMatch != null) {
                tagHeader = Long.parseLong(ifNoneMatch);
            }
            //UserRequestDto requestDto = (UserRequestDto) request.getAttribute("dto");
            String login = url[1];
            User user = null;
            try {
                user = userDao.findOne(login);
            } catch (NameNotFoundException e) {
                throw new RuntimeException(e);
            } catch (InvalidNameException e) {
                throw new RuntimeException(e);
            }
            long tagUser = getTag(user);

            //Comparaison pour l'erreur 304
            if (tagHeader > 0 || ifNoneMatch != null) {
                if (tagHeader == tagUser) {
                    response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
                }
            } else {
                response.setHeader("ETag", Long.valueOf(tagUser).toString());
            }
        }
        chain.doFilter(request, response);
    }

    private Integer getTag(User user) {
        TodoDao todo = (TodoDao) getServletContext().getAttribute("todoDao");
        Integer userHashLogin = user.getLogin().hashCode();
        Integer userHashName = user.getName().hashCode();
        List<Integer> assignedTodoHashList = todo.findByAssignee(user.getLogin()).stream().map(Todo::hashCode).toList();
        Integer assignedTodoHash = 0;
        for (Integer assigned : assignedTodoHashList) {
            assignedTodoHash += assigned;
        }
        return userHashLogin + userHashName + assignedTodoHash;
    }
}
