package com.pk.eager.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
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

    public static class InformationViewHolder extends RecyclerView.ViewHolder {

        TextView reportTitle;
        TextView reportInformation;
        TextView reportLocation;

        InformationViewHolder(View itemView) {
            super(itemView);

            reportTitle = (TextView) itemView.findViewById(R.id.reportTitleTextView);
            reportInformation = (TextView) itemView.findViewById(R.id.reportInformationTextView);
            reportLocation = (TextView) itemView.findViewById(R.id.reportLocationTextView);
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
        String roundDistance = String.format("%.2f", distanceInMile);
        roundDistance = roundDistance + " miles far";

        holder.reportTitle.setText(title);
        holder.reportInformation.setText(information);
        holder.reportLocation.setText(roundDistance);

    }
}
