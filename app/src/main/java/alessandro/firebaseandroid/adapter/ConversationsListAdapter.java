package alessandro.firebaseandroid.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import alessandro.firebaseandroid.R;
import alessandro.firebaseandroid.model.ChatData;
import alessandro.firebaseandroid.model.ConversationModel;
import alessandro.firebaseandroid.model.UserCredential;
import alessandro.firebaseandroid.util.Util;

import static alessandro.firebaseandroid.view.UserListActivity.ENV;
import static alessandro.firebaseandroid.view.UserListActivity.USER_REFERENCE;

public class ConversationsListAdapter extends RecyclerView.Adapter<ConversationsListAdapter.ConversationsListViewHolder> implements Filterable {
    private final ConversationFilter mFilter;
    List<ConversationModel> mConversations;
    ConversationClickListener mClickListener;

    public ConversationsListAdapter(List<ConversationModel> users, ConversationClickListener mClickListener) {
        this.mConversations = users;
        this.mClickListener = mClickListener;
        mFilter = new ConversationFilter(this, mConversations);
    }

    @NonNull
    @Override
    public ConversationsListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_conversations_list, null);

        return new ConversationsListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ConversationsListViewHolder holder, int position) {
        ConversationModel model = mConversations.get(position);
        loadUserDetails(holder, position, model.getUid());
        loadLastMessage(holder, position, model.getConversationId());
    }

    private void loadLastMessage(final ConversationsListViewHolder holder, final int position, String conversationId) {
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(ENV).child("conversations").child(conversationId);
        Query lastQuery = databaseReference.orderByKey().limitToLast(1);
        lastQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ChatData chatData = snapshot.getValue(ChatData.class);
                    holder.txtConversation.setText(chatData.getContent());
                    holder.txtTime.setText(Util.getTimeDifference(chatData.getTimestamp()));
                    mConversations.get(position).setLastMessage(chatData.getContent());
//                    notifyItemChanged(position);
                }
                notifyItemChanged(position);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void loadUserDetails(final ConversationsListViewHolder holder, final int position, String uid) {

        FirebaseDatabase.getInstance().getReference().child(ENV).child(USER_REFERENCE).child(uid).child("credentials").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserCredential userCredential = dataSnapshot.getValue(UserCredential.class);
                holder.userName.setText(userCredential.getName());
                holder.setIvUser(userCredential.getProfilePicLink());
                mConversations.get(position).setCredential(userCredential);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return mConversations.size();
    }

    public class ConversationsListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView userName, txtConversation, txtTime;
        ImageView ivUser;

        public ConversationsListViewHolder(View itemView) {
            super(itemView);
            userName = (TextView) itemView.findViewById(R.id.txtName);
            txtConversation = itemView.findViewById(R.id.txtConversation);
            txtTime = itemView.findViewById(R.id.txtTime);
            ivUser = (ImageView) itemView.findViewById(R.id.ivUserChat);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            ConversationModel model = mConversations.get(position);

            if (model.getCredential() != null) {
                mClickListener.onClick(model);
            }
        }


        public void setIvUser(String urlPhotoUser) {
            if (ivUser == null) return;
            Glide.with(ivUser.getContext()).load(urlPhotoUser).centerCrop().transform(new CircleTransform(ivUser.getContext())).override(40, 40).into(ivUser);
        }

    }


    public class ConversationFilter extends Filter {

        private ConversationsListAdapter mUsersListAdapter;

        private ArrayList<ConversationModel> mUsers;

        private ArrayList<ConversationModel> mFilteredUsers;

        public ConversationFilter(ConversationsListAdapter usersListAdapter, List<ConversationModel> users) {
            super();
            mUsersListAdapter = usersListAdapter;
            mUsers = new ArrayList<>();
            mUsers.addAll(users);
            mFilteredUsers = new ArrayList<>();
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            mFilteredUsers.clear();
            final FilterResults results = new FilterResults();
            if (constraint.length() == 0) {
                mFilteredUsers.addAll(mUsers);
            } else {
                final String filterPattern = constraint.toString().toLowerCase().trim();
                for (final ConversationModel conversationModel : mUsers) {
                    if (conversationModel.getCredential().getName().toLowerCase().contains(filterPattern) || conversationModel.getLastMessage().toLowerCase().contains(filterPattern)) {
                        mFilteredUsers.add(conversationModel);
                    }
                }
            }
            results.values = mFilteredUsers;
            results.count = mFilteredUsers.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            this.mUsersListAdapter.updateList((List<ConversationModel>) results.values);
        }
    }

    public void updateList(List<ConversationModel> filteredUsers) {
        mConversations.clear();
        mConversations.addAll(filteredUsers);
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }
}
