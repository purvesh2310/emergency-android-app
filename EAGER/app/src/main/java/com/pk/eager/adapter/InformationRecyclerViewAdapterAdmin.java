package com.pk.eager.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.pk.eager.R;
import com.pk.eager.ReportObject.CompactReport;
import com.pk.eager.util.CompactReportUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by NB on 8/12/2017.
 *
 * This is a duplicate of InformationRecyclerViewAdapter but with 1 lesser field to input.
 * This one is being used for the AdminMode class. This one can be deleted if we can:
 *  1: Merge this with the InformationRecyclerViewAdapter.
 *  2: Change the AdminMode class so that the admin class will use the InformationRecyclerViewAdapter
 *  instead of this one.
 *
 */
public class InformationRecyclerViewAdapterAdmin extends RecyclerView.Adapter<InformationRecyclerViewAdapterAdmin.InformationViewHolder> {

    Context context;
    List<CompactReport> reportList;
    LatLng currentLocation;

    public static class InformationViewHolder extends RecyclerView.ViewHolder {

        TextView reportTitle;
        TextView reportInformation;
        TextView reportLocation;
        ImageView incidentTypeLogo;

        InformationViewHolder(View itemView) {
            super(itemView);

            reportTitle = (TextView) itemView.findViewById(R.id.reportTitleTextView);
            reportInformation = (TextView) itemView.findViewById(R.id.reportInformationTextView);
            reportLocation = (TextView) itemView.findViewById(R.id.reportLocationTextView);
            incidentTypeLogo = (ImageView) itemView.findViewById(R.id.incidentTypeLogo);

        }
    }

    public InformationRecyclerViewAdapterAdmin(){

    }

    public InformationRecyclerViewAdapterAdmin(Context context, List<CompactReport> reportList){
        this.context = context;
        this.reportList = reportList;
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
        String roundDistance = "";

        CompactReportUtil cmpUtil = new CompactReportUtil();
        Map<String, String> reportData = cmpUtil.parseReportData(report, "info");

        if(currentLocation != null) {
            if (!report.type.equals("feed-missing") && !report.type.equals("feed-weather")) {
                double distanceInMile = cmpUtil.distanceBetweenPoints(currentLocation, reportLocation);
                roundDistance = String.format("%.2f", distanceInMile);
                roundDistance = roundDistance + " miles far";
            }
        }

        String reportTitle = reportData.get("title");
        String fullInfo = reportData.get("information");

        informationViewHolder.reportTitle.setText(reportTitle);
        informationViewHolder.reportInformation.setText(fullInfo);
        if(currentLocation != null) {
            informationViewHolder.reportLocation.setText(roundDistance);
        }

        switch(reportTitle){
            case "Medical":
                informationViewHolder.incidentTypeLogo.setImageResource(R.drawable.hospital);
                break;
            case "Fire":
                informationViewHolder.incidentTypeLogo.setImageResource(R.drawable.flame);
                break;
            case "Police":
                informationViewHolder.incidentTypeLogo.setImageResource(R.drawable.siren);
                break;
            case "Traffic":
                informationViewHolder.incidentTypeLogo.setImageResource(R.drawable.cone);
                break;
            case "Utility":
                informationViewHolder.incidentTypeLogo.setImageResource(R.drawable.repairing);
                break;
        }
    }

    public void updateList(List<CompactReport> list){
        reportList.clear();
        reportList.addAll(list);
        notifyDataSetChanged();
    }

    public void filterByQuery(String query){
        List<CompactReport> temp = new ArrayList();

        CompactReportUtil cmpUtil = new CompactReportUtil();

        for(CompactReport cmpReport: reportList){

            Map<String, String> reportData = cmpUtil.parseReportData(cmpReport, "info");
            String reportTitle = reportData.get("title");

            if(reportTitle.toLowerCase().equals(query.toLowerCase())){
                temp.add(cmpReport);
            }
        }
        //update recyclerview
        updateList(temp);
    }

    public void filterByCategory(List<String> categoryList){

        List<CompactReport> temp = new ArrayList();
        CompactReportUtil cmpUtil = new CompactReportUtil();

        for(CompactReport cmpReport: reportList){

            Map<String, String> reportData = cmpUtil.parseReportData(cmpReport, "info");
            String reportTitle = reportData.get("title");

            for(String category: categoryList){
                if(reportTitle.toLowerCase().equals(category.toLowerCase())){
                    temp.add(cmpReport);
                }
            }

        }
        //update recyclerview
        updateList(temp);
    }

    public void filterByDistance(double queryDistance){

        List<CompactReport> temp = new ArrayList();
        CompactReportUtil cmpUtil = new CompactReportUtil();

        for(CompactReport cmpReport: reportList){
            LatLng reportLocation = new LatLng(cmpReport.latitude,cmpReport.longitude);
            double distanceInMile = cmpUtil.distanceBetweenPoints(currentLocation,reportLocation);

            if (distanceInMile <= queryDistance){
                temp.add(cmpReport);
            }
        }
        updateList(temp);
    }

    public void combineFilter(List<String> categoryList, double queryDistance) {

        List<CompactReport> temp = new ArrayList();
        CompactReportUtil cmpUtil = new CompactReportUtil();

        for (CompactReport cmpReport : reportList) {

            Map<String, String> reportData = cmpUtil.parseReportData(cmpReport, "info");
            String reportTitle = reportData.get("title");

            LatLng reportLocation = new LatLng(cmpReport.latitude,cmpReport.longitude);
            double distanceInMile = cmpUtil.distanceBetweenPoints(currentLocation,reportLocation);

            for (String category : categoryList) {
                if ((reportTitle.toLowerCase().equals(category.toLowerCase())) && distanceInMile <= queryDistance) {
                    temp.add(cmpReport);
                }
            }
        }

        updateList(temp);
    }
}
