package fr.univlyon1.m1if.m1if03.utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import fr.univlyon1.m1if.m1if03.dto.todo.TodoRequestDto;
import fr.univlyon1.m1if.m1if03.dto.user.UserRequestDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotNull;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Permet un parsing des requêtes dans un objet <code>RequestDto</code> en fonction du type de contenus de la requête.
 */
public final class ContentNegotiationHelper {
    /**
     * Pour valider la règle <code>HideUtilityClassConstructorCheck</code> de CheckStyle...
     * Une classe utilitaire qui n'a que des méthodes statiques ne doit pas avoir de constructeur public, parce qu'il ne servirait à rien de l'instancier.
     */
    private ContentNegotiationHelper() {
        throw new UnsupportedOperationException();
    }

    /**
     * Renvoie un DTO en fonction du contenu de la requête, si son type MIME est supporté.<br>
     * Supporte 3 types MIME : <code>application/json</code>, <code>application/xml</code> et <code>application/x-www-form-urlencoded</code>.
     * Méthode utilisée par les contrôleurs traitant des requêtes contenant un payload sous différents types MIME.
     * @param request la requête contenant le payload et un header <code>Content-Type</code>
     * @param dtoType le type du DTO dans lequel on veut utiliser le contenu de la requête
     * @return une instance de la classe <code>dtoType</code> ou null si le type MIME n'est pas reconnu
     * @throws IOException si le payload de la requête ne peut pas être lu ou s'il ne correspond pas à la classe du DTO
     * @throws UnsupportedOperationException si le type MIME n'est pas supporté
     */
    public static Object getDtoFromRequest(HttpServletRequest request, Class<?> dtoType) throws IOException, UnsupportedOperationException {

        String contentType = parseMimeHeader(request.getHeader("content-type"));
        switch (contentType) {
            case "application/x-www-form-urlencoded" -> {
                return applicationSpecificProcessing(dtoType.getSimpleName(), request);
            }
            case "application/xml" -> {
                XmlMapper xmlMapper = new XmlMapper();
                xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                return xmlMapper.readValue(request.getReader(), dtoType);
            }
            case "application/json" -> {
                ObjectMapper jsonMapper = new ObjectMapper();
                jsonMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                return jsonMapper.readValue(request.getReader(), dtoType);
            }
            default -> throw new UnsupportedEncodingException("Type MIME " + contentType + " non supporté.");
        }
    }

    /**
     * La poussière sous le tapis : gère l'instanciation de DTOs spécifiques à l'application à partir des paramètres (de formulaire) de la requête.
     * @param dtoTypeName Le nom du type de DTO dans lequel instancier le contenu de la requête
     * @param request La requête contenant le payload en <code>application/x-www-form-urlencoded</code>
     * @return une instance de <code>dtoType</code> initialisée avec les valeurs
     * @throws UnsupportedOperationException si le nom du type de DTO n'est pas reconnu
     */
    private static Object applicationSpecificProcessing(String dtoTypeName, HttpServletRequest request) throws UnsupportedOperationException {
        String[] url = UrlUtils.getUrlParts(request);
        return switch (dtoTypeName) {
            case "UserRequestDto" -> new UserRequestDto(
                    (url.length > 1 && url[1] != null && !url[1].equals("login")) ? url[1] : request.getParameter("login"),
                    request.getParameter("password"),
                    request.getParameter("name")
            );
            case "TodoRequestDto" -> new TodoRequestDto(
                    request.getParameter("title"),
                    (url.length > 1 && url[1] != null) ? Integer.valueOf(url[1]) : null,
                    request.getParameter("assignee")
            );
            default ->
                    throw new UnsupportedOperationException("La classe " + dtoTypeName + " n'est pas reconnue par cette application.");
        };
    }

    /**
     * Renvoie un type MIME depuis la valeur d'un header (Accept ou Content-Type) de négociation de contenus.<br>
     * La forme générale peut ressembler à :
     * Content-Type: multipart/form-data;boundary="boundary"<br>
     * Accept: text/html, application/xhtml+xml, application/xml;q=0.9, *\/*;q=0.8
     * @param headerValue La valeur du header à décomposer
     * @return le premier type MIME présent dans la valeur (si existant)
     * @throws UnsupportedOperationException Si le header passé en paramètre est vide ou null
     */
    public static String parseMimeHeader(@NotNull String headerValue) throws UnsupportedOperationException {
        if(headerValue == null || headerValue.isEmpty()) {
            throw new UnsupportedOperationException("Header de négociation de contenus null ou vide.");
        }
        String[] mimeTypes = headerValue.split(";")[0].split(",");
        return mimeTypes[0].strip();
    }
}
