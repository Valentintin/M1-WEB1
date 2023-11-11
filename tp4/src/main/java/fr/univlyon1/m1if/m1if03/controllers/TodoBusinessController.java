package fr.univlyon1.m1if.m1if03.controllers;

import fr.univlyon1.m1if.m1if03.dao.TodoDao;
import fr.univlyon1.m1if.m1if03.dto.todo.TodoDtoMapper;
import fr.univlyon1.m1if.m1if03.dto.todo.TodoRequestDto;
import fr.univlyon1.m1if.m1if03.exceptions.ForbiddenLoginException;
import fr.univlyon1.m1if.m1if03.model.Todo;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;

import javax.naming.InvalidNameException;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NameNotFoundException;
import java.io.IOException;


/**
 * Contrôleur d'opérations métier "todos".<br>
 * Concrètement : gère les opérations de togglestatus

 */
@WebServlet(name = "TodoBusinessController", urlPatterns = {"/todos/toggleStatus"})
public class TodoBusinessController extends HttpServlet {
    private TodoBusiness todoBusiness;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        todoBusiness = new TodoBusiness(config);
    }

    /**
     * Réalise l'opération demandée
     * Renvoie un code HTTP 204 (No Content) en cas de succès.
     * Sinon, renvoie une erreur HTTP appropriée.
     *
     * @param request  Voir doc...
     * @param response Voir doc...
     * @throws IOException Voir doc...
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        TodoRequestDto requestDto = (TodoRequestDto) request.getAttribute("dto");

        if (request.getRequestURI().endsWith("toggleStatus")) { // TOGGLE STATUS
            Integer id = requestDto.getHash();
            try {
                todoBusiness.modifStatut(id);
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } catch (IllegalArgumentException | ForbiddenLoginException ex) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
            } catch (NameNotFoundException e) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Le todo " + id + " n'existe pas.");
            } catch (NameAlreadyBoundException e) {
                response.sendError(HttpServletResponse.SC_CONFLICT, "Le todo " + id + " n'est plus disponible.");
            } catch (InvalidNameException e) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Le todo " + id + " n'existe pas.");
            }
        }
    }

    /**
     * Nested class qui réalise les opérations de changement de statut d'un todos<br>
     *
     */
    private static class TodoBusiness {
        private final TodoDao todoDao;
        private TodoDtoMapper todoDtoMapper;

        /**
         * Constructeur avec une injection du DAO nécessaire aux opérations.
         * @param config le contexte de la servlet
         */
        TodoBusiness(ServletConfig  config) {
            this.todoDao = (TodoDao) config.getServletContext().getAttribute("todoDao");

            this.todoDtoMapper = new TodoDtoMapper(config.getServletContext());
        }

        /**
         * Réalise la modification du statut de la todo
         *
         * @param hash le hash du todo a modifier
         */
        public void modifStatut(@NotNull Integer hash)
                throws IllegalArgumentException, NameAlreadyBoundException, ForbiddenLoginException, InvalidNameException, NameNotFoundException {
            Todo todo = todoDao.findByHash(hash);
            if(todo.isCompleted()){
                todo.setImage("&#x2610;");
            }
            else {
                todo.setImage("&#x2611;");
            }
            todo.setCompleted(!todo.isCompleted());
        }
    }
}
