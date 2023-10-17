package fr.univlyon1.m1if.m1if03.model;

import jakarta.validation.constraints.NotNull;

/**
 * Utilisateur du chat.
 *
 * @author Lionel Médini
 */
public class User {
    private final String login;
    private String password;
    private String name;

    /**
     * Crée un utilisateur avec le login, le mot de passe et le nom spécifié.
     * @param login Le login de l'utilisateur - non modifiable
     * @param password Le password de l'utilisateur - non modifiable
     * @param name Le nom de l'utilisateur
     */
    public User(@NotNull String login, @NotNull String password, String name) {
        this.login = login;
        this.password = password;
        this.name = name;
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
     * Met à jour le nom de l'utilisateur.
     * @param name Le nouveau nom de l'utilisateur
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Met à jour le password de l'utilisateur.
     * @param password Le nouveau password de l'utilisateur
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Vérifie un password par rapport à celui stocké dans l'instance.
     * @param password Le password à vérifier
     * @return un booléen indiquant le succès de la comparaison des 2 passwords
     */
    public boolean verifyPassword(String password) {
        return this.password.equals(password);
    }
}
