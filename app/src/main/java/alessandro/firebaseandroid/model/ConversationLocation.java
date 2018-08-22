package alessandro.firebaseandroid.model;

public class ConversationLocation {
    public ConversationLocation() {
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public ConversationLocation(String location) {
        this.location = location;
    }

    private String location;
}
