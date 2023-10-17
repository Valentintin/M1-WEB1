package fr.univlyon1.m1if.m1if03.dto.todo;

import fr.univlyon1.m1if.m1if03.dao.TodoDao;
import fr.univlyon1.m1if.m1if03.model.Todo;
import jakarta.servlet.ServletContext;

import java.util.NoSuchElementException;

/**
 * Application du pattern DTO.<br>
 * Réalise le mapping pour les différents types de DTO de <code>Todo</code>, en entrée et en sortie du serveur.
 *
 * @author Lionel Médini
 */
public class TodoDtoMapper {

    private final TodoDao todoDao;

    /**
     * Initialise le mapper avec une référence au contexte pour qu'il puisse aller y chercher les DAOs.
     * @param context Le contexte applicatif qui contient les DAOs
     */
    public TodoDtoMapper(ServletContext context) {
        this.todoDao = (TodoDao) context.getAttribute("todoDao");
    }

    /**
     * Génère une instance de <code>TodoResponseDto</code> à partir d'un objet métier <code>Todo</code>.
     * @param todo L'instance de <code>Todo</code> dont on veut renvoyer une représentation
     * @return Un <code>TodoResponseDto</code> avec tous les champs positionnés
     */
    public TodoResponseDto toDto(Todo todo) {
        return new TodoResponseDto(todo.getTitle(), todo.hashCode(), todo.getAssignee());
    }

    /**
     * Renvoie une instance de <code>Todo</code> à partir d'un objet métier <code>TodoRequestDto</code>.
     * Si un objet d'id identique est trouvé dans le DAO, renvoie cet objet, en recopiant dedans les propriétés spécifiées par la requête.
     * Sinon, renvoie une nouvelle instance de l'objet.
     * @param todoRequestDto Une instance de <code>TodoRequestDto</code> construite à partir d'une requête
     * @return Une instance de <code>Todo</code> correspondante
     */
    public Todo toTodo(TodoRequestDto todoRequestDto) {
        Todo todo;
        try {
            todo = todoDao.findByHash(todoRequestDto.getHash());
            if(todoRequestDto.getTitle() != null) {
                todo.setTitle(todoRequestDto.getTitle());
            }
            if(todoRequestDto.getAssignee() != null) {
                todo.setAssignee(todoRequestDto.getAssignee());
            }
        } catch (NoSuchElementException e) {
            todo = new Todo(todoRequestDto.getTitle());
        }
        return todo;
    }
}
