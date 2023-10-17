package fr.univlyon1.m1if.m1if03.exceptions;

/**
 * Exception levée quand on essaye de créer un utilisateur nommé "login" ou "logout".<br>
 * Ces logins sont interdits, car ils peuvent créer des conflits avec les URLs de login ou de logout.
 *
 * @author Lionel Médini
 */
public class ForbiddenLoginException extends Exception {
    public ForbiddenLoginException() {
        super("Les identifiants \"login\" et \"logout\" ne sont pas autorisés.");
    }
}
