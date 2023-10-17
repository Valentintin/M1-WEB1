package fr.univlyon1.m1if.m1if03.dto.todo;

/**
 * DTO contenant la totalité des données d'un todo_ renvoyé par une vue du serveur.
 * @author Lionel Médini
 */
public class TodoResponseDto {
    private final String title;
    private final int hash;
    private final String assignee;

    public TodoResponseDto(String title, int hash, String assignee) {
        this.title = title;
        this.hash = hash;
        this.assignee = assignee;
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
}
