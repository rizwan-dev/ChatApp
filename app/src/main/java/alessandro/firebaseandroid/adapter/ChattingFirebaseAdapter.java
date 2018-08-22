package alessandro.firebaseandroid.adapter;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;

import java.io.File;

import alessandro.firebaseandroid.R;
import alessandro.firebaseandroid.model.ChatData;
import alessandro.firebaseandroid.model.UserCredential;
import alessandro.firebaseandroid.util.Constant;
import alessandro.firebaseandroid.util.Util;
import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;

import static alessandro.firebaseandroid.view.ChatActivity.MEDIA;

/**
 * Created by Alessandro Barreto on 23/06/2016.
 */
public class ChattingFirebaseAdapter extends FirebaseRecyclerAdapter<ChatData, ChattingFirebaseAdapter.MyChatViewHolder> {

    private static final int RIGHT_MSG = 0;
    private static final int LEFT_MSG = 1;
    private static final int RIGHT_MSG_IMG = 2;
    private static final int LEFT_MSG_IMG = 3;

    private ClickListenerChatFirebase mClickListenerChatFirebase;

    private String userID;

    private UserCredential mUser;


    public ChattingFirebaseAdapter(DatabaseReference ref, String userID, ClickListenerChatFirebase mClickListenerChatFirebase, UserCredential userCredential) {
        super(ChatData.class, R.layout.item_message_left, ChattingFirebaseAdapter.MyChatViewHolder.class, ref);
        this.userID = userID;
        this.mClickListenerChatFirebase = mClickListenerChatFirebase;
        this.mUser = userCredential;
    }

    @Override
    public MyChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == RIGHT_MSG) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_right, parent, false);
            return new MyChatViewHolder(view);
        } else if (viewType == LEFT_MSG) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_left, parent, false);
            return new MyChatViewHolder(view);
        } else if (viewType == RIGHT_MSG_IMG) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_right_img, parent, false);
            return new MyChatViewHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_left_img, parent, false);
            return new MyChatViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        ChatData model = getItem(position);
        if (model.getFromID().equals(userID) && model.getType().equals(MEDIA)) {
            return RIGHT_MSG_IMG;
        } else if (!model.getFromID().equals(userID) && model.getType().equals(MEDIA)) {
            return LEFT_MSG_IMG;
        } else if (model.getFromID().equals(userID)) {
            return RIGHT_MSG;
        } else {
            return LEFT_MSG;
        }
    }

    @Override
    protected void populateViewHolder(MyChatViewHolder viewHolder, ChatData model, int position) {
        viewHolder.setIvUser(Constant.PROFILE_PIC_LINK);
        viewHolder.setIvOtherUser(mUser.getProfilePicLink());
        viewHolder.setTxtMessage(model.getContent());
        viewHolder.setTvTimestamp(String.valueOf(model.getTimestamp()));
        viewHolder.tvIsLocation(View.GONE);
        if (model.getType().equals(MEDIA)) {
            viewHolder.tvIsLocation(View.GONE);
            if(TextUtils.isEmpty(model.getMediaUrl())){
            if (model.getLocalMediaURL() != null) {
                viewHolder.setIvLocalChatPhoto(model.getLocalMediaURL());
                viewHolder.progress.setVisibility(View.VISIBLE);
            }
            }
            else{
                viewHolder.setIvChatPhoto(model.getMediaUrl());
                viewHolder.progress.setVisibility(View.GONE);
            }

        }
       /*else if(model.getMapModel() != null){
            viewHolder.setIvChatPhoto(alessandro.firebaseandroid.util.Util.local(model.getMapModel().getLatitude(),model.getMapModel().getLongitude()));
            viewHolder.tvIsLocation(View.VISIBLE);
        }*/
    }

    public class MyChatViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvTimestamp, tvLocation;
        EmojiconTextView txtMessage;
        ImageView ivUser, ivChatPhoto, ivOtherUserChat;
        ProgressBar progress;

        public MyChatViewHolder(View itemView) {
            super(itemView);
            tvTimestamp = (TextView) itemView.findViewById(R.id.timestamp);
            txtMessage = (EmojiconTextView) itemView.findViewById(R.id.txtMessage);
            tvLocation = (TextView) itemView.findViewById(R.id.tvLocation);
            ivChatPhoto = (ImageView) itemView.findViewById(R.id.img_chat);
            ivUser = (ImageView) itemView.findViewById(R.id.ivUserChat);
            ivOtherUserChat = itemView.findViewById(R.id.ivOtherUserChat);
            progress = itemView.findViewById(R.id.progress);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            ChatData model = getItem(position);
           /* if (model != null){
                mClickListenerChatFirebase.clickImageMapChat(view,position,model.getMapModel().getLatitude(),model.getMapModel().getLongitude());
            }else{
                mClickListenerChatFirebase.clickImageChat(view,position,model.getUserModel().getName(),model.getUserModel().getPhoto_profile(),model.getFile().getUrl_file());
            }*/
        }

        public void setTxtMessage(String message) {
            if (txtMessage == null) return;
            txtMessage.setText(message);
        }

        public void setIvUser(String urlPhotoUser) {
            if (ivUser == null) return;
            Glide.with(ivUser.getContext()).load(urlPhotoUser)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).centerCrop().transform(new CircleTransform(ivUser.getContext())).override(40, 40).into(ivUser);
        }

        public void setIvOtherUser(String urlPhotoUser) {
            if (ivOtherUserChat == null) return;
            Glide.with(ivOtherUserChat.getContext()).load(urlPhotoUser).centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL).transform(new CircleTransform(ivOtherUserChat.getContext())).override(40, 40).into(ivOtherUserChat);
        }

        public void setTvTimestamp(String timestamp) {
            if (tvTimestamp == null) return;
            tvTimestamp.setText(converteTimestamp(timestamp));
        }

        public void setIvChatPhoto(String url) {
            if (ivChatPhoto == null) return;
            Glide.with(ivChatPhoto.getContext()).load(url)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(ivChatPhoto);
            ivChatPhoto.setOnClickListener(this);
        }

        public void setIvLocalChatPhoto(String url) {
            if (ivChatPhoto == null) return;
            Glide.with(ivChatPhoto.getContext())
                    .load(new File(url))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(ivChatPhoto);
            ivChatPhoto.setOnClickListener(this);
        }

        public void tvIsLocation(int visible) {
            if (tvLocation == null) return;
            tvLocation.setVisibility(visible);
        }
    }

    private CharSequence converteTimestamp(String mileSegundos) {
        return Util.getTimeDifference(Long.parseLong(mileSegundos));
    }
}
