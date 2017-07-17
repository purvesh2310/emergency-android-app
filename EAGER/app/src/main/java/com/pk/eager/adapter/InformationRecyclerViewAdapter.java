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

        CompactReport report = reportList.get(i);

        Map<String, ArrayList<String>> compactReports = report.compactReports;
        Iterator it = compactReports.entrySet().iterator();

        String title = "";
        ArrayList<String> reportInfo = new ArrayList<String>();
        String indi = "";

        while (it.hasNext()){
            Map.Entry me = (Map.Entry)it.next();
            title = me.getKey().toString();
            reportInfo = (ArrayList<String>) me.getValue();
        }

        for(int j=0;j<reportInfo.size();j++){
            indi = reportInfo.get(j);
        }

        informationViewHolder.reportTitle.setText(title);
        informationViewHolder.reportInformation.setText(indi);
        informationViewHolder.reportLocation.setText("111.423434343434, 34.33534343");
    }

}
