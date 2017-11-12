package com.pk.eager;

/**
 * Created by NB on 08/07/17.
 * This is the admin chat mode, right now there is only one phone that has the token of:
 * cAblwfQQAJ0:APA91bGfxsPBZCojnYb1gHp2HWBlvnwBshoe2wmUBEPcQC0-nmljo3hpG3pWOXFY-ltTC_sFuym0U21rfq3nMEkDDO1yGXWUOuu6rXAaJTTD8rp2a8P23CrJvYxbUEY1pCgkYnu8IT3M
 * will be able to send any message. Anyone can access to this activity, but if the device's token
 * does not match then the person can't sent any message.
 */

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.pk.eager.ReportFragments.Constant;
import com.pk.eager.ReportObject.CompactReport;
import com.pk.eager.util.ChatPOJO;
import com.pk.eager.util.CompactReportUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AdminChatThread extends AppCompatActivity implements View.OnClickListener{

    /*
    ChatViewHolder that hold the View inside RecyclerView
     */
    public static class ChatViewHolder extends RecyclerView.ViewHolder{

        /*
        "This" mean the person who is using the app, "That" is the person on the other side
         */

        ImageView imgThis;
        TextView tvMessageThis;
        TextView tvMessengerThis;
        TextView tvTimestampThis;

        ImageView imgThat;
        TextView tvMessageThat;
        TextView tvMessengerThat;
        TextView tvTimestampThat;

        ChatViewHolder(View v) {
            super(v);
            imgThis = (ImageView) itemView.findViewById(R.id.image_message_profile_this);
            tvMessageThis = (TextView) itemView.findViewById(R.id.messageTextView_this);
            tvMessengerThis = (TextView) itemView.findViewById(R.id.messengerTextView_this);
            tvTimestampThis = (TextView) itemView.findViewById(R.id.text_message_time_this);

            imgThat = (ImageView) itemView.findViewById(R.id.image_message_profile_that);
            tvMessageThat = (TextView) itemView.findViewById(R.id.messageTextView_that);
            tvMessengerThat = (TextView) itemView.findViewById(R.id.messengerTextView_that);
            tvTimestampThat = (TextView) itemView.findViewById(R.id.text_message_time_that);
        }
    }

    private static String TAG = AdminChatThread.class.getSimpleName();
    private static String REPORT = "report";

    private CompactReport mReport;
    private DatabaseReference mFirebaseReference;
    private StringRequest stringRequest;

    private String uid;
    private String title;
    private String information;
    private String incidentLocation;
    private String chatString;
    private String messenger;
    private String deviceToken;
    private String timestamp;
    private String sendTo;

    private TextView reportTitle;
    private TextView reportInformation;
    private TextView reportLocation;
    private ImageView reportLogo;
    private EditText chatInput;
    private ImageButton sendButton;

    private RecyclerView mMessageRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private FirebaseRecyclerAdapter<ChatPOJO, ChatViewHolder> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_chat_thread);

        setTitle("Admin Chat");

        deviceToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, deviceToken);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getAllInfo();
        getRecyclerView();
        getUi();
    }

    // Get all the info needed for the chat header
    private void getAllInfo() {
        mReport = getIntent().getParcelableExtra(REPORT);
        CompactReportUtil cmpUtils = new CompactReportUtil();
        Map<String, String> map = cmpUtils.parseReportData(mReport, "info");

        uid = getIntent().getExtras().getString("key");
        title = map.get("title");
        information = map.get("information");

        String[] reportTitles = title.split("~");
        title = reportTitles[0];

        String[] fullInfoArray = information.split("~");
        information = fullInfoArray[0].trim();

        incidentLocation = map.get("location");
    }

    // Get the recyclerView, with the help of Firebase UI
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

                // Since this is admin mode, dispatcher will be on the left, cliend on the right
                if (mMessage.getMessage() != null && messenger.equals("dispatcher")) {
                    viewHolder.imgThis.setImageResource(R.drawable.admin_man);
                    viewHolder.tvMessageThis.setText(mMessage.getMessage());
                    viewHolder.tvMessengerThis.setText("Dispatcher");
                    viewHolder.tvTimestampThis.setText(mMessage.getTimestamp());
                    hideOther(viewHolder.imgThat,
                            viewHolder.tvMessageThat,
                            viewHolder.tvMessengerThat,
                            viewHolder.tvTimestampThat);
                } else if (mMessage.getMessage() != null && messenger.equals("client")){
                    viewHolder.imgThat.setImageResource(R.drawable.man);
                    viewHolder.tvMessageThat.setText(mMessage.getMessage());
                    viewHolder.tvMessengerThat.setText("Sender");
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

    // Get the ui and set onClick listener to the button
    private void getUi() {

        reportTitle = (TextView) findViewById(R.id.reportTitleTextView);
        reportInformation = (TextView) findViewById(R.id.reportInformationTextView);
        reportLocation = (TextView) findViewById(R.id.reportLocationTextView);
        reportLogo = (ImageView) findViewById(R.id.incidentTypeLogo);

        chatInput = (EditText) findViewById(R.id.etChatInput);
        sendButton = (ImageButton) findViewById(R.id.bSend);



        reportTitle.setText(title);
        reportInformation.setText(information);
        reportLocation.setText(incidentLocation);
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
    }

    private void sendMessage(){

        /*
        Like said above, only the device with the token will be able to send the msg, if you want to
        change the device replace the token below and change the header's token too.
         */

        if(deviceToken.equals(Constant.ADMIN)) {

            messenger = "dispatcher";
            chatString = chatInput.getText().toString();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("h:mm a");
            timestamp = simpleDateFormat.format(new Date());

            // Create the chat Object with the messenger, message input and the timestamp
            ChatPOJO chatPOJO = new ChatPOJO("dispatcher", chatString, timestamp);
            mFirebaseReference = FirebaseDatabase.getInstance().getReference().child("ChatRoom")
                    .child(uid).push();
            mFirebaseReference.setValue(chatPOJO);
            chatInput.setText("");

            /*
            In admin mode, we will have to recover which device that created the alert, this is why
            we need the ReportOwner child with the device's token. Changes may be made in future.
            */
            mFirebaseReference = FirebaseDatabase.getInstance().getReference()
                    .child("ReportOwner").child(uid).child("owner");
            mFirebaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                // Get the device's token that needed to send the notification to
                public void onDataChange(DataSnapshot dataSnapshot) {

                    Log.d(TAG, dataSnapshot.getValue().toString());

                    sendTo = dataSnapshot.getValue().toString();

                    // Get the php file in web server.
                    stringRequest = new StringRequest(Request.Method.POST, "https://wwwsjsu-closer.000webhostapp.com/SinglePushNotification.php",
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
                        // Put all necessary fields
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            final Map<String, String> params = new HashMap<>();
                            params.put("messenger", messenger);
                            params.put("message", chatString);
                            params.put("token", sendTo);
                            params.put("notificationType", "ChatNotification");

                            return params;
                        }

                    };
                    MyVolley.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    // This method is used to hide the ui so that both "This" and "That" doesn't show up the same time.
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
            sendMessage();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
