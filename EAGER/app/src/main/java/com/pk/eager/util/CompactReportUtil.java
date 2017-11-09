
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

    public Map<String,String> parseReportData(CompactReport report, String source){

     if(report.type.equals("Report")) {
            String reportTitle = "";
            ArrayList<ArrayList<String>> reportInfoList = new ArrayList<ArrayList<String>>();
            String individualInformation = "";
            String fullInformation = "";
            String location = "";

            Map<String, String> reportData = new HashMap<String, String>();

            Map<String, ArrayList<String>> compactReports = report.compactReports;
            Iterator iterator = compactReports.entrySet().iterator();

            while (iterator.hasNext()) {
                Map.Entry reportEntry = (Map.Entry) iterator.next();
                reportTitle+=reportEntry.getKey().toString()+"-";
                reportInfoList.add((ArrayList<String>) reportEntry.getValue());
            }

            for (int j = 0; j < reportInfoList.size(); j++) {

                individualInformation = reportInfoList.get(j).toString();
                individualInformation = individualInformation.replace("/", "\n");
                fullInformation = fullInformation + individualInformation;

                if (j != reportInfoList.size() - 1)
                    fullInformation = fullInformation + "\n";
            }

            location = String.valueOf(report.latitude) + "," + String.valueOf(report.longitude);

            reportData.put("title", reportTitle);
            reportData.put("information", fullInformation);
            reportData.put("location", location);

            Log.d("CompactReportUtil", reportData.toString());

            return reportData;
        }else if(report.type.equals("feed-crime")){
            String reportTitle = "Crime";
            String info = "";
            String location = "";

            Map<String, ArrayList<String>> compactReports = report.compactReports;
            Map<String, String> reportData = new HashMap<String, String>();

            info = compactReports.get("description").get(0)+"\n"+compactReports.get("date").get(0);
            location = String.valueOf(report.latitude) + "," + String.valueOf(report.longitude);

            reportData.put("title", reportTitle);
            reportData.put("information", info);
            reportData.put("location", location);

            return reportData;
        }else if(report.type.equals("feed-weather")){

         String reportTitle = "Weather";
            String info = "";
            String location = "";

            Map<String, ArrayList<String>> compactReports = report.compactReports;
            Map<String, String> reportData = new HashMap<String, String>();

            switch (source){
                case "list":
                    info = parseWeatherInformationForListView(compactReports);
                    break;
                case "info":
                    info = parseWeatherInformationForInformationView(compactReports);
                    break;
            }

            reportData.put("title", reportTitle);
            reportData.put("information", info);
            reportData.put("location", location);
            return reportData;
        }else{
            String reportTitle = "Missing";
            String info = "";
            String location = "";

            Map<String, ArrayList<String>> compactReports = report.compactReports;
            Map<String, String> reportData = new HashMap<String, String>();

            switch (source){
                case "list":
                    info = parseMissingPersonInfoForListView(compactReports);
                    break;
                case "info":
                    info = parseMissingPersonInfoForInforamtionView(compactReports);
                    break;
            }

            reportData.put("title", reportTitle);
            reportData.put("information", info);
            reportData.put("location", location);
            return reportData;
        }
    }

    public double distanceBetweenPoints(LatLng startPoint, LatLng endPoint){

        float[] results = new float[1];

        Location.distanceBetween(startPoint.latitude,startPoint.longitude,
                endPoint.latitude,endPoint.longitude,results);

        double distanceInMile = results[0] * 0.000621371192;

        return distanceInMile;

    }

    public String parseWeatherInformationForListView(Map<String, ArrayList<String>> compactReports){

        StringBuffer info = new StringBuffer();

        String title = compactReports.get("title").get(0);
        String summary = compactReports.get("summary").get(0);

        summary = summary.replace(title + " ","");

        info.append(title);
        info.append("\n");
        info.append(summary);
        return info.toString();

    }

    public String parseWeatherInformationForInformationView(Map<String, ArrayList<String>> compactReports){

        StringBuffer info = new StringBuffer();

        String title = compactReports.get("title").get(0);
        String summary = compactReports.get("summary").get(0);
        String severity = compactReports.get("severity").get(0);
        String area = compactReports.get("area").get(0);
        String effectiveDate = compactReports.get("effectiveDate").get(0);
        String expiryDate = compactReports.get("expireDate").get(0);

        summary = summary.replace(title + " ","");

        info.append(title);
        info.append("\n");
        info.append(summary);
        info.append("\n");
        info.append("Severity: " + severity);
        info.append("\n");
        info.append("Area Affected: " + area);
        info.append("\n");
        info.append("Effective Date: " + effectiveDate );
        info.append("\n");
        info.append("Expiry Date: "+ expiryDate);

        return info.toString();
    }

    public String parseMissingPersonInfoForListView(Map<String, ArrayList<String>> compactReports){

        StringBuffer info = new StringBuffer();

        String summary = compactReports.get("summary").get(0);
        String date = compactReports.get("date").get(0);

        info.append(summary);
        info.append("\n");
        info.append(date);

        return info.toString();

    }

    public String parseMissingPersonInfoForInforamtionView(Map<String, ArrayList<String>> compactReports){

        StringBuffer info = new StringBuffer();

        String title = compactReports.get("title").get(0);
        String summary = compactReports.get("summary").get(0);
        String date = compactReports.get("date").get(0);

        info.append(title);
        info.append("\n");
        info.append(summary);
        info.append("\n");
        info.append(date);

        return info.toString();

    }
}