package com.pk.eager.adapter;

import android.content.Context;
import android.location.Location;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.pk.eager.R;
import com.pk.eager.ReportObject.CompactReport;
import com.pk.eager.util.CompactReportUtil;

import java.util.List;
import java.util.Map;

/**
 * Created by Purvesh on 7/15/2017.
 */

public class InformationRecyclerViewAdapter extends RecyclerView.Adapter<InformationRecyclerViewAdapter.InformationViewHolder> {

    Context context;
    List<CompactReport> reportList;
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

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {



                }

            });
        }
    }

    public InformationRecyclerViewAdapter(Context context, List<CompactReport> reportList, LatLng location){
        this.context = context;
        this.reportList = reportList;
        this.currentLocation = location;
    }


    @Override
    public InformationViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_information_list, viewGroup, false);
        InformationViewHolder ivh = new InformationViewHolder(v);
        return ivh;
    }

    @Override
    public int getItemCount() {
        return reportList.size();
    }

    @Override
    public void onBindViewHolder(InformationViewHolder informationViewHolder, final int i) {

        CompactReport report = reportList.get(i);
        LatLng reportLocation = new LatLng(report.latitude,report.longitude);

        CompactReportUtil cmpUtil = new CompactReportUtil();
        Map<String, String> reportData = cmpUtil.parseReportData(report);

        double distanceInMile = cmpUtil.distanceBetweenPoints(currentLocation,reportLocation);
        String roundDistance = String.format("%.2f", distanceInMile);
        roundDistance = roundDistance + " miles far";

        String reportTitle = reportData.get("title");
        String fullInfo = reportData.get("information");

        informationViewHolder.reportTitle.setText(reportTitle);
        informationViewHolder.reportInformation.setText(fullInfo);
        informationViewHolder.reportLocation.setText(roundDistance);
    }
}
