package fr.univlyon1.m1if.m1if03.dto.user;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.List;

/**
 * DTO contenant la totalité des données d'un utilisateur renvoyé par une vue du serveur.
 * @author Lionel Médini
 */
@JacksonXmlRootElement(localName = "user")
public class UserResponseDto {
    private final String login;
    private final String name;
    private final List<Integer> assignedTodos;

    /**
     * Crée un <code>UserResponseDto</code> à templater dans la réponse.
     * @param login Le login de l'utilisateur
     * @param name Le nom du salon
     * @param assignedTodos La liste des salons dont l'utilisateur est propriétaire
     */
    public UserResponseDto(String login, String name, List<Integer> assignedTodos) {
        this.login = login;
        this.name = name;
        this.assignedTodos = assignedTodos;
    }

    /**
     * Renvoie le login de l'utilisateur.
     * @return Le login de l'utilisateur
     */
    public String getLogin() {
        return login;
    }

    /**
     * Renvoie le nom de l'utilisateur.
     * @return Le nom de l'utilisateur
     */
    public String getName() {
        return name;
    }

    /**
     * Renvoie la liste des todos assignés à cet utilisateur.
     * @return La liste des todos assignés à cet utilisateur
     */
    public List<Integer> getAssignedTodos() {
        return assignedTodos;
    }
}
