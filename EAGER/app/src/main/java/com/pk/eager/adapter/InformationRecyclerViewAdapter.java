package com.pk.eager.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pk.eager.R;
import com.pk.eager.ReportObject.CompactReport;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Purvesh on 7/15/2017.
 */

public class InformationRecyclerViewAdapter extends RecyclerView.Adapter<InformationRecyclerViewAdapter.InformationViewHolder> {

    Context context;
    List<CompactReport> reportList;

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

    public InformationRecyclerViewAdapter(Context context, List<CompactReport> reportList){
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

        String reportTitle = "";
        ArrayList<String> reportInfoList = new ArrayList<String>();
        String individualInformation = "";
        String fullInfo = "";
        String location = "";

        CompactReport report = reportList.get(i);

        Map<String, ArrayList<String>> compactReports = report.compactReports;
        Iterator iterator = compactReports.entrySet().iterator();

        while (iterator.hasNext()){
            Map.Entry reportEntry = (Map.Entry) iterator.next();
            reportTitle = reportEntry.getKey().toString();
            reportInfoList = (ArrayList<String>) reportEntry.getValue();
        }

        for(int j=0; j<reportInfoList.size(); j++){

            individualInformation = reportInfoList.get(j);
            individualInformation = individualInformation.replace("/","\n");
            fullInfo = fullInfo + individualInformation;
            if(j!= reportInfoList.size()-1)
                fullInfo = fullInfo + "\n" ;
        }

        location = String.valueOf(report.latitude) + "," + String.valueOf(report.longitude);

        informationViewHolder.reportTitle.setText(reportTitle);
        informationViewHolder.reportInformation.setText(fullInfo);
        informationViewHolder.reportLocation.setText(location);
    }
}
