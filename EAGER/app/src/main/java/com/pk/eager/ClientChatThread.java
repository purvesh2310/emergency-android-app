package com.pk.eager;

/**
 * Created by NB on 08/07/17.
 * This is the client chat thread, anyone/phones can use this, but only the admin phone with the token:
 * cAblwfQQAJ0:APA91bGfxsPBZCojnYb1gHp2HWBlvnwBshoe2wmUBEPcQC0-nmljo3hpG3pWOXFY-ltTC_sFuym0U21rfq3nMEkDDO1yGXWUOuu6rXAaJTTD8rp2a8P23CrJvYxbUEY1pCgkYnu8IT3M
 * can get the message. All methods and variables serve the same purpose in AdminChatThread class.
 */


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.pk.eager.ReportFragments.Constant;
import com.pk.eager.db.model.Report;
import com.pk.eager.util.ChatPOJO;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ClientChatThread extends AppCompatActivity implements View.OnClickListener{

    public static class ChatViewHolder extends RecyclerView.ViewHolder{

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

    private static String TAG = ClientChatThread.class.getSimpleName();

    private Report mReport;
    private DatabaseReference mFirebaseReference;

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

    private RecyclerView mMessageRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private FirebaseRecyclerAdapter<ChatPOJO, ChatViewHolder> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_chat_thread);
        setTitle("History");

        deviceToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, deviceToken);

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
                    viewHolder.tvMessageThis.setText(mMessage.getMessage());
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

    }

    private void sendMessage(){

        /*
        Check for device's token, if is not equal to the admin's token then it is the client. Change
        the token below if needed, also replace the this class's introduction in line 4 when do so.
        */
        if(!deviceToken.equals(Constant.ADMIN)) {
            messenger = "client";

            chatString = chatInput.getText().toString();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("h:mm a");
            timestamp = simpleDateFormat.format(new Date());
            ChatPOJO chatPOJO = new ChatPOJO("client", chatString, timestamp);
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

}
