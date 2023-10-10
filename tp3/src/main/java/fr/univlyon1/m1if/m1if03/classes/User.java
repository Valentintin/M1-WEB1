package fr.univlyon1.m1if.m1if03.classes;

/**
 * Utilisateur de TODO_.
 */
public class User {
    private final String login;
    private String name;

    public User(String login, String name) {
        this.login = login;
        this.name = name;
    }

    public String getLogin() {
        return login;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
