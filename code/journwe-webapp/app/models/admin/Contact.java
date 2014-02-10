package models.admin;

/**
 * Created with IntelliJ IDEA.
 * User: mugglmenzel
 * Date: 10.02.14
 * Time: 12:49
 * To change this template use File | Settings | File Templates.
 */
public class Contact {

    private String name;

    private String email;

    private String subject;

    private String text;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
