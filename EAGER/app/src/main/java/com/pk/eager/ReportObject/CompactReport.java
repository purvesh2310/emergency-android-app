package com.pk.eager.ReportObject;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by kimpham on 7/12/17.
 */

public class CompactReport {

    public Map<String, ArrayList<String>> compactReports = new HashMap<>();

    public Double longitude;
    public Double latitude;
    public String phoneNumber;

    public CompactReport(){}

    public Map<String, ArrayList<String>> getCompactReports() {
        return compactReports;
    }

    public void setCompactReports(Map<String, ArrayList<String>> compactReports) {
        this.compactReports = compactReports;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public CompactReport(IncidentReport incidentReport, Double longitude, Double latitude, String phone){

        for(Report r : incidentReport.reports){
            Log.d("CompactReport", r.toString());
            ArrayList<String> questions = new ArrayList<>();
            for(Question q : r.questions){
                Log.d("CompactReport" + r.type, q.getQuestion()+", "+q.getChoices());
                questions.add(q.getQuestion()+":"+q.getChoices());
            }
            compactReports.put(r.getType(), questions);
        }
        if(incidentReport.reports.size() == 0) Log.d("CompactReport", "Empty");
        this.longitude = longitude;
        this.latitude = latitude;
        this.phoneNumber = phone;
    }
}
