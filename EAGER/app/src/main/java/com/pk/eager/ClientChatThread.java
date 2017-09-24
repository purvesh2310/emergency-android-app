package com.pk.eager;

/**
 * Created by NB on 08/07/17.
 * This is the client chat thread, anyone/phones can use this, but only the admin phone with the token:
 * cAblwfQQAJ0:APA91bGfxsPBZCojnYb1gHp2HWBlvnwBshoe2wmUBEPcQC0-nmljo3hpG3pWOXFY-ltTC_sFuym0U21rfq3nMEkDDO1yGXWUOuu6rXAaJTTD8rp2a8P23CrJvYxbUEY1pCgkYnu8IT3M
 * can get the message. All methods and variables serve the same purpose in AdminChatThread class.
 */


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.pk.eager.ReportFragments.Constant;
import com.pk.eager.db.model.Report;
import com.pk.eager.util.ChatPOJO;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ClientChatThread extends AppCompatActivity implements View.OnClickListener{

    public static class ChatViewHolder extends RecyclerView.ViewHolder{

        ImageView imgThis;
        ImageView imageMessageThis;
        VideoView videoViewThis;
        TextView tvMessageThis;
        TextView tvMessengerThis;
        TextView tvTimestampThis;

        ImageView imgThat;
        TextView tvMessageThat;
        TextView tvMessengerThat;
        TextView tvTimestampThat;

        public ChatViewHolder(View v) {
            super(v);

            imgThis = (ImageView) itemView.findViewById(R.id.image_message_profile_this);
            imageMessageThis = (ImageView) itemView.findViewById(R.id.imageView_this);
            videoViewThis = (VideoView) itemView.findViewById(R.id.videoView_this);
            tvMessageThis = (TextView) itemView.findViewById(R.id.messageTextView_this);
            tvMessengerThis = (TextView) itemView.findViewById(R.id.messengerTextView_this);
            tvTimestampThis = (TextView) itemView.findViewById(R.id.text_message_time_this);

            imgThat = (ImageView) itemView.findViewById(R.id.image_message_profile_that);
            tvMessageThat = (TextView) itemView.findViewById(R.id.messageTextView_that);
            tvMessengerThat = (TextView) itemView.findViewById(R.id.messengerTextView_that);
            tvTimestampThat = (TextView) itemView.findViewById(R.id.text_message_time_that);
        }
    }

    private static String TAG = ClientChatThread.class.getSimpleName();

    private Report mReport;
    private DatabaseReference mFirebaseReference;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageRef;

    private String uid;
    private String title;
    private String information;
    private String roundDistance;
    private String chatString;
    private String messenger;
    private String deviceToken;
    private String timestamp;

    private TextView reportTitle;
    private TextView reportInformation;
    private TextView reportLocation;
    private ImageView reportLogo;
    private EditText chatInput;
    private ImageButton sendButton;
    private ImageButton uploadImageButton;

    private RecyclerView mMessageRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private FirebaseRecyclerAdapter<ChatPOJO, ChatViewHolder> mAdapter;
    private static final int REQUEST_IMAGE_CAPTURE = 111;
    private static final int SELECT_IMAGE = 1234;
    private static final int SELECT_VIDEO = 789;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_chat_thread);
        setTitle("Chat");

        deviceToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, deviceToken);

        firebaseStorage = FirebaseStorage.getInstance();
        storageRef = firebaseStorage.getReference();

        getAllInfo();
        getRecyclerView();
        getUi();
    }

    private void getAllInfo() {
        mReport = getIntent().getParcelableExtra("reportObj");
        uid = mReport.getUid();
        title = mReport.getTitle();
        information = mReport.getInformation();
        roundDistance = getIntent().getStringExtra("roundDistance");
    }

    private void getRecyclerView(){
        mMessageRecyclerView = (RecyclerView) findViewById(R.id.messageRecyclerView);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setStackFromEnd(true);

        // New child entries
        mFirebaseReference = FirebaseDatabase.getInstance().getReference().child("ChatRoom");
        mAdapter = new FirebaseRecyclerAdapter<ChatPOJO, ChatViewHolder>(
                ChatPOJO.class,
                R.layout.chat_layout,
                ChatViewHolder.class,
                mFirebaseReference.child(uid)) {

            @Override
            protected void populateViewHolder(final ChatViewHolder viewHolder,
                                              ChatPOJO mMessage, int position) {

                messenger = mMessage.getMessenger();

                if (mMessage.getMessage() != null && messenger.equals("client")) {
                    viewHolder.imgThat.setImageResource(R.drawable.man);

                    if(mMessage.getMessageType().equals("TEXT")){
                        viewHolder.tvMessageThis.setText(mMessage.getMessage());
                    } else if (mMessage.getMessageType().equals("IMAGE")){
                        GlideApp.with(ClientChatThread.this).load(mMessage.getMessage()).into(viewHolder.imageMessageThis);
                        viewHolder.imageMessageThis.setVisibility(View.VISIBLE);
                        viewHolder.tvMessageThis.setVisibility(View.GONE);
                    } else if(mMessage.getMessageType().equals("IMAGE-CAMERA")){
                        Bitmap imageBitmap = decodeFromFirebaseBase64(mMessage.getMessage());
                        viewHolder.imageMessageThis.setImageBitmap(imageBitmap);
                        viewHolder.imageMessageThis.setVisibility(View.VISIBLE);
                        viewHolder.tvMessageThis.setVisibility(View.GONE);
                    } else if(mMessage.getMessageType().equals("VIDEO")){
                        viewHolder.videoViewThis.setVideoPath(mMessage.getMessage());
                        viewHolder.videoViewThis.setVisibility(View.VISIBLE);
                        viewHolder.imageMessageThis.setVisibility(View.GONE);
                        viewHolder.tvMessageThis.setVisibility(View.GONE);
                    }
                    viewHolder.tvMessengerThis.setText(mMessage.getMessenger());
                    viewHolder.tvTimestampThis.setText(mMessage.getTimestamp());
                    hideOther(viewHolder.imgThat,
                            viewHolder.tvMessageThat,
                            viewHolder.tvMessengerThat,
                            viewHolder.tvTimestampThat);
                } else if (mMessage.getMessage() != null && messenger.equals("dispatcher")){
                    viewHolder.imgThis.setImageResource(R.drawable.admin_man);
                    viewHolder.tvMessageThat.setText(mMessage.getMessage());
                    viewHolder.tvMessengerThat.setText(mMessage.getMessenger());
                    viewHolder.tvTimestampThat.setText(mMessage.getTimestamp());
                    hideOther(viewHolder.imgThis,
                            viewHolder.tvMessageThis,
                            viewHolder.tvMessengerThis,
                            viewHolder.tvTimestampThis);
                }

            }
        };

        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyMessageCount = mAdapter.getItemCount();
                int lastVisiblePosition =
                        mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                // If the recycler view is initially being loaded or the
                // user is at the bottom of the list, scroll to the bottom
                // of the list to show the newly added message.
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (friendlyMessageCount - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    mMessageRecyclerView.scrollToPosition(positionStart);
                }
            }
        });

        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);
        mMessageRecyclerView.setAdapter(mAdapter);
    }

    private void getUi() {

        reportTitle = (TextView) findViewById(R.id.reportTitleTextView);
        reportInformation = (TextView) findViewById(R.id.reportInformationTextView);
        reportLocation = (TextView) findViewById(R.id.reportLocationTextView);
        reportLogo = (ImageView) findViewById(R.id.incidentTypeLogo);

        chatInput = (EditText) findViewById(R.id.etChatInput);
        sendButton = (ImageButton) findViewById(R.id.bSend);
        uploadImageButton = (ImageButton) findViewById(R.id.bImageUpload);

        reportTitle.setText(title);
        reportInformation.setText(information);
        reportLocation.setText(roundDistance);
        switch(title){
            case "Medical":
                reportLogo.setImageResource(R.drawable.hospital);
                break;
            case "Fire":
                reportLogo.setImageResource(R.drawable.flame);
                break;
            case "Police":
                reportLogo.setImageResource(R.drawable.siren);
                break;
            case "Traffic":
                reportLogo.setImageResource(R.drawable.cone);
                break;
            case "Utility":
                reportLogo.setImageResource(R.drawable.repairing);
                break;
        }

        sendButton.setOnClickListener(this);
        uploadImageButton.setOnClickListener(this);

    }

    private void sendMessage(String message, String type){

        /*
        Check for device's token, if is not equal to the admin's token then it is the client. Change
        the token below if needed, also replace the this class's introduction in line 4 when do so.
        */
        if(!deviceToken.equals(Constant.ADMIN)) {

            messenger = "client";

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("h:mm a");
            timestamp = simpleDateFormat.format(new Date());
            ChatPOJO chatPOJO = new ChatPOJO("client", message, timestamp, type);
            mFirebaseReference = FirebaseDatabase.getInstance().getReference().child("ChatRoom")
                    .child(uid).push();
            mFirebaseReference.setValue(chatPOJO);
            chatInput.setText("");

            StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://wwwsjsu-closer.000webhostapp.com/SinglePushNotification.php",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    }) {

                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("messenger", messenger);
                    params.put("message", chatString);
                    params.put("token", Constant.ADMIN);
                    params.put("notificationType", "ChatNotification");
                    return params;
                }
            };
            MyVolley.getInstance(this).addToRequestQueue(stringRequest);
        }
    }

    private void uploadImage(){

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_DENIED){
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, REQUEST_IMAGE_CAPTURE);
            } else {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private void hideOther(ImageView img, TextView msg, TextView msger, TextView timestamp){
        img.setVisibility(View.GONE);
        msg.setVisibility(View.GONE);
        msger.setVisibility(View.GONE);
        timestamp.setVisibility(View.GONE);
    }


    @Override
    public void onClick(View v){
        int i = v.getId();
        if(i == R.id.bSend)
            sendTextMessage();
        if(i == R.id.bImageUpload)
            uploadImage();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        boolean cameraAccepted =false;
        switch (requestCode){
            case 111:
                cameraAccepted = grantResults[0]==PackageManager.PERMISSION_GRANTED;
        }

        if(cameraAccepted){
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == this.RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            encodeBitmapAndSaveToFirebase(imageBitmap);
        }

        if(requestCode == SELECT_IMAGE && resultCode == this.RESULT_OK){

            Uri uri = data.getData();

            Long tsLong = System.currentTimeMillis();
            String imageName = "IMG_" + tsLong;

            StorageReference imagesRef = storageRef.child("images/"+imageName);

            try{
                InputStream iStream =   getContentResolver().openInputStream(uri);
                byte[] inputData = getBytes(iStream);

                UploadTask uploadTask = imagesRef.putBytes(inputData);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();

                        sendMessage(downloadUrl.toString(),"IMAGE");
                    }
                });

            } catch (IOException e){

            }
        }

        if(requestCode == SELECT_VIDEO && resultCode == this.RESULT_OK){

            Uri uri = data.getData();

            Long tsLong = System.currentTimeMillis();
            String imageName = "VID_" + tsLong;

            StorageReference imagesRef = storageRef.child("videos/"+imageName);

            try{
                InputStream iStream =   getContentResolver().openInputStream(uri);
                byte[] inputData = getBytes(iStream);

                UploadTask uploadTask = imagesRef.putBytes(inputData);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();

                        sendMessage(downloadUrl.toString(),"VIDEO");
                    }
                });

            } catch (IOException e){

            }
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
            case R.id.action_attach_image:
                openGalleryToSelectImage();
                return true;
            case R.id.action_attach_video:
                openGalleryToSelectVideo();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

        public void encodeBitmapAndSaveToFirebase(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        String imageEncoded = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);

        String messageType = "IMAGE-CAMERA";

        sendMessage(imageEncoded, messageType);
    }

    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    public void openGalleryToSelectImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"),SELECT_IMAGE);
    }

    public void openGalleryToSelectVideo(){
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Video"),SELECT_VIDEO);
    }

    public void sendTextMessage(){

        chatString = chatInput.getText().toString();
        String messageType = "TEXT";

        sendMessage(chatString, messageType);
    }

    public static Bitmap decodeFromFirebaseBase64(String image){
        byte[] decodedByteArray = android.util.Base64.decode(image, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.length);
    }
}
