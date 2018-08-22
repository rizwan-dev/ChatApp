package alessandro.firebaseandroid.view;

import android.content.Intent;
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
import alessandro.firebaseandroid.adapter.ConversationClickListener;
import alessandro.firebaseandroid.adapter.ConversationsListAdapter;
import alessandro.firebaseandroid.adapter.UserClickListener;
import alessandro.firebaseandroid.model.ConversationLocation;
import alessandro.firebaseandroid.model.ConversationModel;
import alessandro.firebaseandroid.model.User;
import alessandro.firebaseandroid.util.Constant;

import static alessandro.firebaseandroid.util.Constant.CHAT_DATA;

public class ConversationActivity extends AppCompatActivity implements ConversationClickListener {

    private DatabaseReference mFirebaseDatabaseReference;

    private static final String ENV = "Development";
    private static final String USER_REFERENCE = "users";

    RecyclerView rvUsers;
    EditText edtSearch;
    private List<ConversationModel> mConversationDataList;
    private ConversationsListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        findViewById(R.id.linLayParent).requestFocus();

        rvUsers = findViewById(R.id.rvUsers);
        edtSearch = findViewById(R.id.edtSearch);

        rvUsers.setLayoutManager(new LinearLayoutManager(this));
        rvUsers.addItemDecoration(new DividerItemDecoration(rvUsers.getContext(), DividerItemDecoration.VERTICAL));

        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

        final DatabaseReference userReference = mFirebaseDatabaseReference.child(ENV).child(USER_REFERENCE).child(Constant.UID).child("conversations");

        mConversationDataList = new ArrayList<>();

        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String userId = snapshot.getKey();
                    ConversationLocation conversationId = snapshot.getValue(ConversationLocation.class);
                    mConversationDataList.add(new ConversationModel(userId, conversationId.getLocation()));
                }

                adapter = new ConversationsListAdapter(mConversationDataList, ConversationActivity.this);
                rvUsers.setAdapter(adapter);
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence search, int i, int i1, int i2) {
                if (adapter != null && adapter.getFilter() != null)
                    adapter.getFilter().filter(search);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }


    @Override
    public void onClick(ConversationModel conversationModel) {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra(CHAT_DATA,conversationModel);
        startActivity(intent);
//        Toast.makeText(this, conversationModel.getCredential().getName(), Toast.LENGTH_SHORT).show();
    }
}
