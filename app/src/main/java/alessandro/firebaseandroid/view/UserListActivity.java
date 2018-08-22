package alessandro.firebaseandroid.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import alessandro.firebaseandroid.R;
import alessandro.firebaseandroid.adapter.UserClickListener;
import alessandro.firebaseandroid.adapter.UserListAdapter;
import alessandro.firebaseandroid.model.User;

public class UserListActivity extends AppCompatActivity implements UserClickListener {

    private DatabaseReference mFirebaseDatabaseReference;

    public static final String ENV = "Development";
    public static final String USER_REFERENCE = "users";
    List<User> userList;
    RecyclerView rvUsers;
    EditText edtSearch;
    private UserListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        rvUsers = findViewById(R.id.rvUsers);
        edtSearch = findViewById(R.id.edtSearch);

        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

        final DatabaseReference userReference = mFirebaseDatabaseReference.child(ENV).child(USER_REFERENCE);
        userList = new ArrayList<>();

//        UserListFirebaseAdapter userListFirebaseAdapter = new UserListFirebaseAdapter(userReference,"",this);
        rvUsers.setLayoutManager(new LinearLayoutManager(this));
        rvUsers.addItemDecoration(new DividerItemDecoration(rvUsers.getContext(), DividerItemDecoration.VERTICAL));
//        rvUsers.setAdapter(userListFirebaseAdapter);


        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence search, int i, int i1, int i2) {
                adapter.getFilter().filter(search);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        userReference.addValueEventListener(new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    String key = dataSnapshot1.getKey();
                    final User user = dataSnapshot1.getValue(User.class);
                    userList.add(user);
                    /*final List<ConversationData> conversations = new ArrayList<>();

                    DatabaseReference userConversationReference = userReference.child(key).child("conversations");
                    userConversationReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                String key = ds.getKey();
                                String value = ds.getValue().toString();
                                ConversationData conversation = new ConversationData(value, key);
                                conversations.add(conversation);
                                user.setConversationDataList(conversations);
                            }

                            if (!user.getCredentials().getName().equals(Constant.NAME)) {
                                Log.e("USER_ID", "===  " + user.getCredentials().getUid());
                                if (user.getConversationDataList() != null && user.getConversationDataList().size() > 0) {
                                    Log.e("Conversation", "=== " + user.getConversationDataList().get(0).toString());
                                }
                                userList.add(user);
                                adapter.notifyDataSetChanged();
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });*/
                }
                adapter = new UserListAdapter(userList, UserListActivity.this);
                rvUsers.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    @Override
    public void onClick(User user) {
        Toast.makeText(getApplicationContext(), "User Name  " + user.getCredentials().getName(), Toast.LENGTH_SHORT).show();
    }
}
