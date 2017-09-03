package com.pk.eager.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.pk.eager.ClientChatThread;
import com.pk.eager.R;
import com.pk.eager.db.model.Report;
import com.pk.eager.util.CompactReportUtil;

import java.util.List;

/**
 * Created by Purvesh on 7/27/2017.
 */

public class HistoryRecyclerViewAdapter extends RecyclerView.Adapter<HistoryRecyclerViewAdapter.InformationViewHolder> {

    Context context;
    List<Report> reportList;
    LatLng currentLocation;
    String roundDistance;

    private Intent clientChatThread;

    public static class InformationViewHolder extends RecyclerView.ViewHolder {

        TextView reportTitle;
        TextView reportInformation;
        TextView reportLocation;
        ImageView incidentTypeLogo;

        private ClickListener mClickListener;

        InformationViewHolder(View itemView) {
            super(itemView);

            reportTitle = (TextView) itemView.findViewById(R.id.reportTitleTextView);
            reportInformation = (TextView) itemView.findViewById(R.id.reportInformationTextView);
            reportLocation = (TextView) itemView.findViewById(R.id.reportLocationTextView);
            incidentTypeLogo = (ImageView) itemView.findViewById(R.id.incidentTypeLogo);

            // Set ClickListener for the entire row, can set on individual components within a row
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mClickListener.onItemClick(v, getAdapterPosition());
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mClickListener.onItemLongClick(v, getAdapterPosition());
                    return true;
                }
            });
        }

        public void setOnClickListener(ClickListener clickListener) {
            mClickListener = clickListener;
        }

        //Interface to send callbacks...
        public interface ClickListener {
            void onItemClick(View view, int position);

            void onItemLongClick(View view, int position);
        }
    }

    public HistoryRecyclerViewAdapter(Context context, List<Report> reportList, LatLng location){
        this.context = context;
        this.reportList = reportList;
        this.currentLocation = location;
    }

    @Override
    public InformationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_information_list, parent, false);
        HistoryRecyclerViewAdapter.InformationViewHolder ivh = new HistoryRecyclerViewAdapter.InformationViewHolder(v);
        ivh.setOnClickListener(new InformationViewHolder.ClickListener() {
            @Override
            public void onItemClick(View v, int pos) {
                switchActivity(v, pos);
            }

            @Override
            public void onItemLongClick(View v, int pos) {
                switchActivity(v, pos);
            }
        });
        return ivh;
    }

    @Override
    public int getItemCount() {
        return reportList.size();
    }

    @Override
    public void onBindViewHolder(InformationViewHolder holder, int position) {

        Report report = reportList.get(position);

        String title = report.getTitle();
        String information = report.getInformation();
        LatLng incidentLocation = new LatLng(report.getLatitude(),report.getLongitude());

        CompactReportUtil cmpUtil = new CompactReportUtil();
        double distanceInMile = cmpUtil.distanceBetweenPoints(currentLocation,incidentLocation);
        roundDistance = String.format("%.2f", distanceInMile);
        roundDistance = roundDistance + " miles far";

        holder.reportTitle.setText(title);
        holder.reportInformation.setText(information);
        holder.reportLocation.setText(roundDistance);

        switch(title){
            case "Medical":
                holder.incidentTypeLogo.setImageResource(R.drawable.hospital);
                break;
            case "Fire":
                holder.incidentTypeLogo.setImageResource(R.drawable.flame);
                break;
            case "Police":
                holder.incidentTypeLogo.setImageResource(R.drawable.siren);
                break;
            case "Traffic":
                holder.incidentTypeLogo.setImageResource(R.drawable.cone);
                break;
            case "Utility":
                holder.incidentTypeLogo.setImageResource(R.drawable.repairing);
                break;
        }

    }

    private void switchActivity(View v, int pos){
        Report report = reportList.get(pos);
        Context context = v.getContext();
        clientChatThread = new Intent(context, ClientChatThread.class);
        clientChatThread.putExtra("reportObj", report);
        clientChatThread.putExtra("roundDistance", roundDistance);
        context.startActivity(clientChatThread);
    }
}