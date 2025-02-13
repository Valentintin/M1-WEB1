package fr.univlyon1.m1if.m1if03.dto.todo;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

/**
 * DTO contenant la totalité des données d'un todo_ renvoyé par une vue du serveur.
 * @author Lionel Médini
 */
@JacksonXmlRootElement(localName = "todo")
public class TodoResponseDto {
    private final String title;
    private final int hash;
    private final String assignee;
    private final Boolean completed;
    private  final String image;

    public TodoResponseDto(String title, int hash, String assignee, Boolean completed, String image) {
        this.title = title;
        this.hash = hash;
        this.assignee = assignee;
        this.completed = completed;
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public int getHash() {
        return hash;
    }

    public String getAssignee() {
        return assignee;
    }

    public String getImage() {
        return image;
    }

    /**
     * Récupère la valeur du champ <code>completed</code>.
     * On utilise un nom de getter "classique" plutôt qu'un getter "booléen" (isCompleted) pour que le moteur EL de Jasper (JSP -> servlet)
     * soit capable de déduire le nom de cette méthode à partir de la propriété <code>completed</code> utilisée dans la JSP.
     * @return la valeur du champ <code>completed</code>
     */
    public Boolean getCompleted() {
        return completed;
    }
}
