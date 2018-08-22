package alessandro.firebaseandroid.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;

import alessandro.firebaseandroid.R;
import alessandro.firebaseandroid.model.ChatModel;
import alessandro.firebaseandroid.model.User;
import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;


public class UserListFirebaseAdapter extends FirebaseRecyclerAdapter<User, UserListFirebaseAdapter.UserListViewHolder> {

    private static final int RIGHT_MSG = 0;
    private static final int LEFT_MSG = 1;
    private static final int RIGHT_MSG_IMG = 2;
    private static final int LEFT_MSG_IMG = 3;

    private UserClickListener mClickListenerChatFirebase;

    private String nameUser;


    public UserListFirebaseAdapter(DatabaseReference ref, String nameUser, UserClickListener mClickListenerChatFirebase) {
        super(User.class, R.layout.item_message_left, UserListFirebaseAdapter.UserListViewHolder.class, ref);
        this.nameUser = nameUser;
        this.mClickListenerChatFirebase = mClickListenerChatFirebase;
    }

    @Override
    public UserListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_list, parent, false);
            return new UserListViewHolder(view);

    }

    @Override
    public int getItemViewType(int position) {

            return LEFT_MSG;
    }

    @Override
    protected void populateViewHolder(UserListViewHolder viewHolder, User model, int position) {
        viewHolder.setIvUser(model.getCredentials().getProfilePicLink());
        viewHolder.userName.setText(model.getCredentials().getName());

    }

    public class UserListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView userName;
        ImageView ivUser, ivChatPhoto;

        public UserListViewHolder(View itemView) {
            super(itemView);
            userName = (TextView) itemView.findViewById(R.id.txtName);
            ivUser = (ImageView) itemView.findViewById(R.id.ivUserChat);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            User model = getItem(position);
            mClickListenerChatFirebase.onClick(model);
           /* if (model.getMapModel() != null){
                mClickListenerChatFirebase.clickImageMapChat(view,position,model.getMapModel().getLatitude(),model.getMapModel().getLongitude());
            }else{
                mClickListenerChatFirebase.clickImageChat(view,position,model.getUserModel().getName(),model.getUserModel().getPhoto_profile(),model.getFile().getUrl_file());
            }*/
        }


        public void setIvUser(String urlPhotoUser) {
            if (ivUser == null) return;
            Glide.with(ivUser.getContext()).load(urlPhotoUser).centerCrop().transform(new CircleTransform(ivUser.getContext())).override(40, 40).into(ivUser);
        }

    }

}
