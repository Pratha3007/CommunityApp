package src.model;

public class User {
    int uid;
    String name;
    String email;
    String password;
    String bio;

    public User(int uid, String name, String email, String password, String bio) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.password = password;
        this.bio = bio;
    }

    public User(String name, String email, String password, String bio) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.bio = bio;
    }

    public int getUid() {
        return uid;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getBio() {
        return bio;
    }

    public boolean equals(User user) {
        return uid == user.getUid();
    }
}
