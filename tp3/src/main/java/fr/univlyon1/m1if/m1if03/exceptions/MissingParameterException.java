package fr.univlyon1.m1if.m1if03.exceptions;

/**
 * Exception levée quand il manque une donnée qui aurait dû être spécifiée dans un paramètre de requête.
 *
 * @author Lionel Médini
 */
public class MissingParameterException extends Exception {
    public MissingParameterException(String message) {
        super(message);
    }
}
