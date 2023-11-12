package fr.univlyon1.m1if.m1if03.filters;

import fr.univlyon1.m1if.m1if03.dao.TodoDao;
import fr.univlyon1.m1if.m1if03.dao.UserDao;
import fr.univlyon1.m1if.m1if03.dto.user.UserRequestDto;
import fr.univlyon1.m1if.m1if03.model.Todo;
import fr.univlyon1.m1if.m1if03.model.User;
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
        if (request.getMethod().equals("GET")) {
            long tagHeader = Long.parseLong(request.getHeader("If-None-Match"));
            UserRequestDto requestDto = (UserRequestDto) request.getAttribute("dto");
            User user = null;
            try {
                user = userDao.findOne(requestDto.getLogin());
            } catch (NameNotFoundException e) {
                throw new RuntimeException(e);
            } catch (InvalidNameException e) {
                throw new RuntimeException(e);
            }
            long tagUser = getTag(user, request);
            if (tagHeader > 0){
                if (tagHeader == tagUser) {
                    response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
                }
            } else {
              response.setHeader("ETag", String.valueOf(tagUser));
            }
        }
    }

    private Integer getTag(User user, HttpServletRequest request){
        TodoDao todo = (TodoDao) request.getServletContext().getAttribute("todoDao");
        Integer userHash =  user.toString().hashCode();
        List<Integer> assignedTodoHashList =  todo.findByAssignee(user.getLogin()).stream().map(Todo::hashCode).toList();
        Integer assignedTodoHash = null;
        for(Integer assigned: assignedTodoHashList) {
            assignedTodoHash += assigned;
        }
        return userHash + assignedTodoHash;
    }
}
