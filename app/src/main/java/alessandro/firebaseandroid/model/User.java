package alessandro.firebaseandroid.model;

import java.util.List;

public class User {
    private UserCredential credentials;


    public User(UserCredential credentials) {
        this.credentials = credentials;
    }

    public User() {
    }

    public UserCredential getCredentials() {
        return credentials;
    }

    public void setCredentials(UserCredential credentials) {
        this.credentials = credentials;
    }



}
