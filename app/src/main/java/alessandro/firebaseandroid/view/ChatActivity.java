package alessandro.firebaseandroid.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.Date;

import alessandro.firebaseandroid.BuildConfig;
import alessandro.firebaseandroid.R;
import alessandro.firebaseandroid.adapter.ChatFirebaseAdapter;
import alessandro.firebaseandroid.adapter.ChattingFirebaseAdapter;
import alessandro.firebaseandroid.adapter.ClickListenerChatFirebase;
import alessandro.firebaseandroid.model.ChatData;
import alessandro.firebaseandroid.model.ConversationModel;
import alessandro.firebaseandroid.model.UserCredential;
import alessandro.firebaseandroid.util.Constant;
import alessandro.firebaseandroid.util.FileUtil;
import alessandro.firebaseandroid.util.Util;
import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;

import static alessandro.firebaseandroid.util.Constant.CHAT_DATA;
import static alessandro.firebaseandroid.view.UserListActivity.ENV;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener, ClickListenerChatFirebase {

    private static final int IMAGE_GALLERY_REQUEST = 1;
    private static final int IMAGE_CAMERA_REQUEST = 2;
    private static final int PLACE_PICKER_REQUEST = 3;

    static final String TAG = ChatActivity.class.getSimpleName();
    private static final String CONVERSATION = "conversations";
    public static final String TEXT = "text";
    public static final String MEDIA = "media";

    // Firebase database reference
    private DatabaseReference mFirebaseDatabaseReference;
    private String conversationId;
    private String toId;
    FirebaseStorage storage = FirebaseStorage.getInstance();


    //Views UI
    private RecyclerView rvListMessage;
    private LinearLayoutManager mLinearLayoutManager;
    private ImageView btSendMessage, btEmoji;
    private EmojiconEditText edMessage;
    private View contentRoot;
    private EmojIconActions emojIcon;
    private UserCredential toUser;

    //File
    private File filePathImageCamera;

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private ChattingFirebaseAdapter firebaseAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        ConversationModel conversationModel = getIntent().getParcelableExtra(CHAT_DATA);
        toUser = conversationModel.getCredential();
        toId = toUser.getUid();
        conversationId = conversationModel.getConversationId();

        initFirebaseDBReference();

        bindViews();

        loadMessageFromFirebase();
    }

    /**
     * Ler collections chatmodel Firebase
     */
    private void loadMessageFromFirebase() {
        DatabaseReference reference = mFirebaseDatabaseReference.child(ENV).child(CONVERSATION).child(conversationId);

         firebaseAdapter = new ChattingFirebaseAdapter(reference, Constant.UID, this, toUser);
        firebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyMessageCount = firebaseAdapter.getItemCount();
                int lastVisiblePosition = mLinearLayoutManager.findLastCompletelyVisibleItemPosition();

                firebaseAdapter.notifyDataSetChanged();
                rvListMessage.scrollToPosition(positionStart);
              /*  if (lastVisiblePosition == -1 ||
                        (positionStart >= (friendlyMessageCount - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    rvListMessage.scrollToPosition(positionStart);
                }*/
            }
        });
        rvListMessage.setLayoutManager(mLinearLayoutManager);
        rvListMessage.setAdapter(firebaseAdapter);
    }

    private void initFirebaseDBReference() {
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
    }

    private void bindViews() {
        contentRoot = findViewById(R.id.contentRoot);
        edMessage = (EmojiconEditText) findViewById(R.id.editTextMessage);
        btSendMessage = (ImageView) findViewById(R.id.buttonMessage);
        btSendMessage.setOnClickListener(this);
        btEmoji = (ImageView) findViewById(R.id.buttonEmoji);
        emojIcon = new EmojIconActions(this, contentRoot, edMessage, btEmoji);
        emojIcon.ShowEmojIcon();
        rvListMessage = (RecyclerView) findViewById(R.id.messageRecyclerView);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setStackFromEnd(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        StorageReference storageRef = storage.getReferenceFromUrl(Util.URL_STORAGE_REFERENCE).child(Util.FOLDER_STORAGE_IMG);

        if (requestCode == IMAGE_GALLERY_REQUEST) {
            if (resultCode == RESULT_OK) {
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null) {
                    sendFileFirebase(storageRef, selectedImageUri);
                } else {
                    //URI IS NULL
                }
            }
        } else if (requestCode == IMAGE_CAMERA_REQUEST) {
            if (resultCode == RESULT_OK) {
                if (filePathImageCamera != null && filePathImageCamera.exists()) {
                    StorageReference imageCameraRef = storageRef.child(filePathImageCamera.getName() + "_camera");
                    sendFileFirebase(imageCameraRef, filePathImageCamera);
                } else {
                    //IS NULL
                }
            }
        } else if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);
                if (place != null) {
/*                    LatLng latLng = place.getLatLng();
                    MapModel mapModel = new MapModel(latLng.latitude+"",latLng.longitude+"");
                    ChatModel chatModel = new ChatModel(userModel, Calendar.getInstance().getTime().getTime()+"",mapModel);*/
                    sendMessageUtil("Hi", TEXT);
                } else {
                    //PLACE IS NULL
                }
            }
        }

    }

    private void sendMessageUtil(String content, String type) {
        String key = mFirebaseDatabaseReference.child(ENV).child(CONVERSATION).child(conversationId).push().getKey();
        ChatData chatData = new ChatData();
        chatData.setTimestamp(System.currentTimeMillis());
        chatData.setConversationId(conversationId);
        chatData.setToID(toId);
        chatData.setFromID(Constant.UID);
        chatData.setMessageId(key);
        chatData.setOwner(1);
        if (type.equals(TEXT))
            chatData.setContent(content);
        else {
            chatData.setMediaUrl(content);
            chatData.setContent(MEDIA);
        }
        chatData.setType(type);
        mFirebaseDatabaseReference.child(ENV).child(CONVERSATION).child(conversationId).child(key).setValue(chatData);

    }

    private ChatData sendMessageMediaUtil(String content, String localURL) {
        String key = mFirebaseDatabaseReference.child(ENV).child(CONVERSATION).child(conversationId).push().getKey();
        ChatData chatData = new ChatData();
        chatData.setTimestamp(System.currentTimeMillis());
        chatData.setConversationId(conversationId);
        chatData.setToID(toId);
        chatData.setFromID(Constant.UID);
        chatData.setMessageId(key);
        chatData.setOwner(1);
        chatData.setType(MEDIA);
        chatData.setContent(MEDIA);
        chatData.setLocalMediaURL(localURL);
        mFirebaseDatabaseReference.child(ENV).child(CONVERSATION).child(conversationId).child(key).setValue(chatData);
        return chatData;

    }

    @SuppressLint("NewApi")
    private void sendFileFirebase(StorageReference storageReference, final Uri file) {
        if (storageReference != null) {
            final String name = DateFormat.format("yyyy-MM-dd_hhmmss", new Date()).toString();
            StorageReference imageGalleryRef = storageReference.child(name + "_gallery");
             final ChatData chatData = sendMessageMediaUtil(MEDIA, FileUtil.getFilePath(this, file));
            UploadTask uploadTask = imageGalleryRef.putFile(file);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, "onFailure sendFileFirebase " + e.getMessage());
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.i(TAG, "onSuccess sendFileFirebase");
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    chatData.setMediaUrl(downloadUrl.toString());
                    mFirebaseDatabaseReference.child(ENV).child(CONVERSATION).child(conversationId).child(chatData.getMessageId()).setValue(chatData);
                    firebaseAdapter.notifyDataSetChanged();
                }
            });
        } else {
            //IS NULL
        }

    }

    /**
     * Sending file to firebase
     */
    private void sendFileFirebase(StorageReference storageReference, final File file) {
        if (storageReference != null) {
            Uri photoURI = FileProvider.getUriForFile(ChatActivity.this,
                    BuildConfig.APPLICATION_ID + ".provider",
                    file);


            final ChatData chatData = sendMessageMediaUtil(MEDIA,file.getAbsolutePath());
            UploadTask uploadTask = storageReference.putFile(photoURI);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, "onFailure sendFileFirebase " + e.getMessage());
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.i(TAG, "onSuccess sendFileFirebase");
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    chatData.setMediaUrl(downloadUrl.toString());
                    mFirebaseDatabaseReference.child(ENV).child(CONVERSATION).child(conversationId).child(chatData.getMessageId()).setValue(chatData);
                }
            });
        } else {
            //IS NULL
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonMessage:
                sendMessageFirebase();
                break;
        }
    }

    private void sendMessageFirebase() {
        String message = edMessage.getText().toString().trim();
        if (TextUtils.isEmpty(message)) {
            Toast.makeText(this, "Please enter message", Toast.LENGTH_SHORT).show();
            return;
        }
        sendMessageUtil(message, TEXT);
        edMessage.setText(null);
    }

    /**
     * Obter local do usuario
     */
    private void locationPlacesIntent() {
        try {
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    /**
     * Enviar foto tirada pela camera
     */
    private void photoCameraIntent() {
        String nomeFoto = DateFormat.format("yyyy-MM-dd_hhmmss", new Date()).toString();
        filePathImageCamera = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), nomeFoto + "camera.jpg");
        Intent it = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri photoURI = FileProvider.getUriForFile(ChatActivity.this,
                BuildConfig.APPLICATION_ID + ".provider",
                filePathImageCamera);
        it.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
        startActivityForResult(it, IMAGE_CAMERA_REQUEST);
    }

    /**
     * Enviar foto pela galeria
     */
    private void photoGalleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_picture_title)), IMAGE_GALLERY_REQUEST);
    }

    /**
     * Checks if the app has permission to write to device storage
     * <p>
     * If the app does not has permission then the user will be prompted to grant permissions
     */
    public void verifyStoragePermissions() {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(ChatActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    ChatActivity.this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        } else {
            // we already have permission, lets go ahead and call camera intent
            photoCameraIntent();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.sendPhoto:
                verifyStoragePermissions();
//                photoCameraIntent();
                break;
            case R.id.sendPhotoGallery:
                photoGalleryIntent();
                break;
            case R.id.sendLocation:
                locationPlacesIntent();
                break;
            case R.id.sign_out:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void clickImageChat(View view, int position, String nameUser, String urlPhotoUser, String urlPhotoClick) {

    }

    @Override
    public void clickImageMapChat(View view, int position, String latitude, String longitude) {

    }
}
