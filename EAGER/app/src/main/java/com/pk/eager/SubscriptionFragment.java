package com.pk.eager;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.messaging.FirebaseMessaging;


/**
 * A simple {@link Fragment} subclass.
 */
public class SubscriptionFragment extends Fragment {

    private Button submitButton;

    public SubscriptionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getActivity().setTitle("Subscribe");
        return inflater.inflate(R.layout.fragment_subscription, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setButtonListener();
    }

    public void setButtonListener(){

        submitButton = (Button) getActivity().findViewById(R.id.subscriptionSubmitButton);
        submitButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                String topic;

                EditText editText = (EditText) getActivity().findViewById(R.id.subscriptionZipCode);
                topic = editText.getText().toString();
                FirebaseMessaging.getInstance().subscribeToTopic(topic);

                TextView textView = (TextView) getActivity().findViewById(R.id.subscriptionTextView);
                textView.setText(textView.getText().toString()+"\n"+topic);
            }
        });
    }
}
