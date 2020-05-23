package net.DChore.DChoreApp;

public class User {

    private int id;
    private String username, email, gender, category, location;

    public User(int id, String username, String email, String gender, String category, String location) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.gender = gender;
        this.category = category;
        this.location = location;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }


    public String getLocation() {
        return location;
    }

    public String getEmail() {
        return email;
    }

    public String getGender() {
        return gender;
    }

    public String getCategory() {
        return category;
    }
}