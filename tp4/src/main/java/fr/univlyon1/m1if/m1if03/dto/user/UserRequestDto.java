package fr.univlyon1.m1if.m1if03.dto.user;

/**
 * DTO contenant les données que peut recevoir le serveur pour créer ou mettre à jour un utilisateur.
 *
 * @author Lionel Médini
 */
public class UserRequestDto {
    private String login;
    private String password;
    private String name;

    //<editor-fold desc="Constructeurs">
    /**
     * Crée un <code>UserRequestDto</code> vide.<br>
     * Appelé par Jackson dans <code>ContentNegotiationHelper</code>, cf. :
     * <code><a href="http://fasterxml.github.io/jackson-core/javadoc/2.13/com/fasterxml/jackson/core/ObjectCodec.html">ObjectCodec</a>.readValue()</code>.
     */
    public UserRequestDto() {
        super();
    }

    /**
     * Crée un <code>UserRequestDto</code> à l'aide des paramètres de la requête.<br>
     * Appelé "à la main" par <code>ContentNegotiationHelper.applicationSpecificProcessing()</code>.
     * @param login Le login de l'utilisateur (peut se trouver dans un paramètre ou dans l'URL)
     * @param password Le password (dans un paramètre)
     * @param name Le nom (dans un paramètre)
     */
    public UserRequestDto(String login, String password, String name) {
        this.login = login;
        this.password = password;
        this.name = name;
    }
    //</editor-fold>

    //<editor-fold desc="Setters">
    /**
     * Positionne le login de l'utilisateur (pour utilisation par un <code>CodecMapper</code> Jackson).
     * @param login Le login de l'utilisateur présent dans la requête
     */
    public void setLogin(String login) {
        this.login = login;
    }

    /**
     * Positionne le password de l'utilisateur (pour utilisation par un <code>CodecMapper</code> Jackson).
     * @param password Le password de l'utilisateur présent dans la requête
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Positionne le nom de l'utilisateur (pour utilisation par un <code>CodecMapper</code> Jackson).
     * @param name Le nom de l'utilisateur présent dans la requête
     */
    public void setName(String name) {
        this.name = name;
    }
    //</editor-fold>

    //<editor-fold desc="Getters">
    /**
     * Renvoie le login de l'utilisateur passé dans la requête.
     * @return Le login de l'utilisateur passé dans la requête
     */
    public String getLogin() {
        return login;
    } // TODO : corrigé (was protected)

    /**
     * Renvoie le password de l'utilisateur passé dans la requête.
     * @return Le password de l'utilisateur passé dans la requête
     */
    public String getPassword() {
        return password;
    } // TODO : corrigé (was protected)

    /**
     * Renvoie le nom de l'utilisateur passé dans la requête.
     * @return Le nom de l'utilisateur passé dans la requête
     */
    public String getName() {
        return name;
    } // TODO : corrigé (was protected)
    //</editor-fold>
}
