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

import java.util.ArrayList;
import java.util.List;

import alessandro.firebaseandroid.R;
import alessandro.firebaseandroid.model.User;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.UserListViewHolder> implements Filterable{
    private final UserFilter mFilter;
    List<User> mUsersList;
    UserClickListener listener;

    public UserListAdapter(List<User> users, UserClickListener listener) {
        this.mUsersList = users;
        this.listener = listener;
        mFilter = new UserFilter(this, mUsersList);
    }



    @NonNull
    @Override
    public UserListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_list, null);

        return new UserListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserListAdapter.UserListViewHolder holder, int position) {

        User model = mUsersList.get(position);
        holder.setIvUser(model.getCredentials().getProfilePicLink());
        holder.userName.setText(model.getCredentials().getName());

    }

    @Override
    public int getItemCount() {
        return mUsersList.size();
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
            User model = mUsersList.get(position);
            listener.onClick(model);
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


    public class UserFilter extends Filter {

        private UserListAdapter mUsersListAdapter;

        private ArrayList<User> mUsers;

        private ArrayList<User> mFilteredUsers;

        public UserFilter(UserListAdapter usersListAdapter, List<User> users) {
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
                for (final User user : mUsers) {
                    if (user.getCredentials().getName().toLowerCase().contains(filterPattern)) {
                        mFilteredUsers.add(user);
                    }
                }
            }
            results.values = mFilteredUsers;
            results.count = mFilteredUsers.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            this.mUsersListAdapter.updateList( (List<User>)results.values);
        }
    }

    public void updateList(List<User> filteredUsers) {
        mUsersList.clear();
        mUsersList.addAll(filteredUsers);
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }
}
