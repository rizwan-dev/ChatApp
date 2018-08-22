package alessandro.firebaseandroid.model;

import android.os.Parcel;
import android.os.Parcelable;

public class UserCredential implements Parcelable{

    private String email = "";
    private boolean isOnline = false;
    private long lastSeen = 0l;
    private String name = "";
    private String profilePicLink = "";
    private String uid = "";


    public UserCredential(String email, boolean isOnline, long lastSeen, String name, String profilePicLink, String uid) {
        this.email = email;
        this.isOnline = isOnline;
        this.lastSeen = lastSeen;
        this.name = name;
        this.profilePicLink = profilePicLink;
        this.uid = uid;
    }

    public UserCredential() {
    }

    protected UserCredential(Parcel in) {
        email = in.readString();
        isOnline = in.readByte() != 0;
        lastSeen = in.readLong();
        name = in.readString();
        profilePicLink = in.readString();
        uid = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(email);
        dest.writeByte((byte) (isOnline ? 1 : 0));
        dest.writeLong(lastSeen);
        dest.writeString(name);
        dest.writeString(profilePicLink);
        dest.writeString(uid);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<UserCredential> CREATOR = new Creator<UserCredential>() {
        @Override
        public UserCredential createFromParcel(Parcel in) {
            return new UserCredential(in);
        }

        @Override
        public UserCredential[] newArray(int size) {
            return new UserCredential[size];
        }
    };

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public long getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(long lastSeen) {
        this.lastSeen = lastSeen;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfilePicLink() {
        return profilePicLink;
    }

    public void setProfilePicLink(String profilePicLink) {
        this.profilePicLink = profilePicLink;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
