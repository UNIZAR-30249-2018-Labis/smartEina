package src.domain;

import java.sql.Date;

public class User {

    private String name;
    private String pass;
    private String email;
    private String type;
    private Date created;

    public User(String name, String pass, String email, String type, Date date) {
        this.name = name;
        this.pass = pass;
        this.email = email;
        this.type = type;
        this.created = date;
    }

    public User(){}

    public String getName() {
        return name;
    }

    public String getPass() {
        return pass;
    }

    public String getEmail() {
        return email;
    }

    public String getType() {
        return type;
    }

    public Date getCreated() {
        return created;
    }
}
