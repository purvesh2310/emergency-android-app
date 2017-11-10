
package com.pk.eager.util;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.pk.eager.ReportObject.CompactReport;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Purvesh on 7/18/2017.
 */

public class CompactReportUtil {

    private final String SPLIT = "~";

    public Map<String,String> parseReportData(CompactReport report, String source){
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy-hh-mm-ss");
        SimpleDateFormat displayDate = new SimpleDateFormat("E, dd MMM yyyy");
        SimpleDateFormat missingKidDate = new SimpleDateFormat("E, dd MMM yyyy hh:mm:ss 'EDT'");

        if(report.type.equals("Report")) {
            String reportTitle = "";
            ArrayList<ArrayList<String>> reportInfoList = new ArrayList<ArrayList<String>>();
            String individualInformation = "";
            String fullInformation = "";
            String location = "";
            Date date = null;

            try{
                date = dateFormat.parse(report.timestamp);
            }catch (Exception e){

            }

            Map<String, String> reportData = new HashMap<String, String>();

            Map<String, ArrayList<String>> compactReports = report.compactReports;

            for(String key : compactReports.keySet()){
                reportTitle +=key+"~";
                reportInfoList.add((ArrayList<String>) compactReports.get(key));
            }

            for (int j = 0; j < reportInfoList.size(); j++) {
                ArrayList<String> individualReportInfo = reportInfoList.get(j);
                String toString = "";
                for(int z = 0; z < individualReportInfo.size(); z++){
                    toString += individualReportInfo.get(z)+"/";
                }
                toString = toString.replace("/", "\n");
                individualInformation = toString+SPLIT;
                fullInformation += individualInformation;
                if (j != reportInfoList.size() - 1)
                    fullInformation = fullInformation + "\n";
            }

            location = String.valueOf(report.latitude) + "," + String.valueOf(report.longitude);

            reportData.put("title", reportTitle);
            reportData.put("information", fullInformation);
            reportData.put("location", location);
            reportData.put("date", displayDate.format(date));
            return reportData;
        }else if(report.type.equals("feed-crime")){
            String reportTitle = "Crime";
            String info = "";
            String location = "";
            String date="";
            String author="";

            Map<String, ArrayList<String>> compactReports = report.compactReports;
            Map<String, String> reportData = new HashMap<String, String>();

            info = compactReports.get("description").get(0);
            location = String.valueOf(report.latitude) + "," + String.valueOf(report.longitude);
            date = compactReports.get("date").get(0);
            author = compactReports.get("author").get(0);

            reportData.put("title", reportTitle);
            reportData.put("information", info);
            reportData.put("location", location);
            reportData.put("date", date);
            reportData.put("author", author);

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
            reportData.put("author", compactReports.get("author").get(0));
            return reportData;
        }else{
            String reportTitle = "Missing";
            String info = "";
            String location = "";
            String date = "";

            Map<String, ArrayList<String>> compactReports = report.compactReports;
            Map<String, String> reportData = new HashMap<String, String>();

            try{
                Date reportDate = missingKidDate.parse(compactReports.get("date").get(0));
                date = displayDate.format(reportDate);
            }catch (Exception ex){

            }

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
            reportData.put("author", "missingkids.com");
            reportData.put("date", date);
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

    public double distanceBetweenPoints(Location startPoint, Location endPoint){

        float[] results = new float[1];

        Location.distanceBetween(startPoint.getLatitude(),startPoint.getLongitude(),
                endPoint.getLatitude(),endPoint.getLongitude(),results);

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