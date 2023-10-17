package fr.univlyon1.m1if.m1if03.dto.todo;

/**
 * DTO contenant les données que peut recevoir le serveur pour créer ou mettre à jour un todo_.
 *
 * @author Lionel Médini
 */
public class TodoRequestDto {
    private String title;
    private Integer hash;
    private String assignee;

    public TodoRequestDto() {
    }

    public TodoRequestDto(String title, Integer hash, String assignee) {
        this.title = title;
        this.hash = hash;
        this.assignee = assignee;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getHash() {
        return hash;
    }

    public void setHash(Integer hash) {
        this.hash = hash;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }
}
