package com.pk.eager.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pk.eager.R;
import com.pk.eager.ReportObject.Notification;

import java.util.ArrayList;

/**
 * Created by kimpham on 8/8/17.
 */

public class NotificationRecyclerViewAdapter extends RecyclerView.Adapter<NotificationRecyclerViewAdapter.NotificationViewHolder>{
    private static final String TAG = NotificationRecyclerViewAdapter.class.getSimpleName();
    private ArrayList<Notification> notificationList;

    public static class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView textview;

        NotificationViewHolder(View viewItem){
            super(viewItem);
            textview = (TextView) viewItem.findViewById(R.id.row_notification_textview);
        }
    }

    public NotificationRecyclerViewAdapter(){}

    public NotificationRecyclerViewAdapter(ArrayList<Notification> list){
        this.notificationList = list;
    }

    public NotificationViewHolder onCreateViewHolder(ViewGroup parent, int viewType){

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_notification_list, parent, false);
        NotificationViewHolder vh = new NotificationViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(NotificationViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        if(holder!=null && holder.textview!=null) {
            Log.d(TAG, notificationList.get(position).getBody());
            holder.textview.setText(notificationList.get(position).getBody());

        }else Log.d(TAG, "null view");

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return notificationList.size();
    }


}
