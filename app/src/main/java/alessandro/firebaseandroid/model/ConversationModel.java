package alessandro.firebaseandroid.model;

import android.os.Parcel;
import android.os.Parcelable;

public class ConversationModel implements Parcelable{

    protected ConversationModel(Parcel in) {
        uid = in.readString();
        conversationId = in.readString();
        credential = in.readParcelable(UserCredential.class.getClassLoader());
        lastMessage = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uid);
        dest.writeString(conversationId);
        dest.writeParcelable(credential, flags);
        dest.writeString(lastMessage);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ConversationModel> CREATOR = new Creator<ConversationModel>() {
        @Override
        public ConversationModel createFromParcel(Parcel in) {
            return new ConversationModel(in);
        }

        @Override
        public ConversationModel[] newArray(int size) {
            return new ConversationModel[size];
        }
    };

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public ConversationModel() {

    }

    public ConversationModel(String uid, String conversationId) {

        this.uid = uid;
        this.conversationId = conversationId;
    }

    private String uid;
    private String conversationId;

    public UserCredential getCredential() {
        return credential;
    }

    public void setCredential(UserCredential credential) {
        this.credential = credential;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    private UserCredential credential = new UserCredential();

    private String lastMessage = "";
}
