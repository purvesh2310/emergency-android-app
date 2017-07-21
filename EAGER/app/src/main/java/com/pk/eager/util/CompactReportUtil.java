package com.pk.eager.util;

import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.pk.eager.ReportObject.CompactReport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Purvesh on 7/18/2017.
 */

public class CompactReportUtil {

    public Map<String,String> parseReportData(CompactReport report){

        String reportTitle = "";
        ArrayList<String> reportInfoList = new ArrayList<String>();
        String individualInformation = "";
        String fullInformation = "";
        String location = "";

        Map<String, String> reportData = new HashMap<String,String>();

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
            fullInformation = fullInformation + individualInformation;

            if(j!= reportInfoList.size()-1)
                fullInformation = fullInformation + "\n" ;
        }

        location = String.valueOf(report.latitude) + "," + String.valueOf(report.longitude);

        reportData.put("title",reportTitle);
        reportData.put("information",fullInformation);
        reportData.put("location",location);

        return reportData;
    }

    public double distanceBetweenPoints(LatLng startPoint, LatLng endPoint){

        float[] results = new float[1];

        Location.distanceBetween(startPoint.latitude,startPoint.longitude,
                endPoint.latitude,endPoint.longitude,results);

        double distanceInMile = results[0] * 0.000621371192;

        return distanceInMile;

    }
}
