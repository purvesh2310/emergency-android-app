package com.pk.eager.ReportObject;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by kimpham on 7/12/17.
 */

public class CompactReport implements Parcelable{

    public Map<String, ArrayList<String>> compactReports = new HashMap<>();

    public Double longitude;
    public Double latitude;
    public String phoneNumber;
    public String type;
    public String timestamp;


    public CompactReport(){}

    public Map<String, ArrayList<String>> getCompactReports() {
        return compactReports;
    }

    public void setCompactReports(Map<String, ArrayList<String>> compactReports) {
        this.compactReports = compactReports;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
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

    public CompactReport(IncidentReport incidentReport, Double longitude, Double latitude, String phone, String type, String timestamp){

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
        this.type = type;
        this.timestamp = timestamp;
    }

    //parceble implementation
    //Parceble implementation
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(phoneNumber);
        dest.writeDouble(longitude);
        dest.writeDouble(latitude);
        dest.writeString(type);
        dest.writeString(timestamp);
        dest.writeInt(compactReports.size());
        for(Map.Entry<String, ArrayList<String>> entry : compactReports.entrySet()){
            dest.writeString(entry.getKey());
            dest.writeList(entry.getValue());
        }
    }

    public static final Parcelable.Creator<CompactReport> CREATOR
            = new Parcelable.Creator<CompactReport>() {
        public CompactReport createFromParcel(Parcel in) {
            return new CompactReport(in);
        }

        public CompactReport[] newArray(int size) {
            return new CompactReport[size];
        }
    };

    private CompactReport(Parcel in) {
        phoneNumber = in.readString();
        longitude = in.readDouble();
        latitude = in.readDouble();
        type = in.readString();
        timestamp = in.readString();
        int size = in.readInt();
        for(int i = 0; i < size; i++){
            String key = in.readString();
            ArrayList<String> val = in.readArrayList(String.class.getClassLoader());
            compactReports.put(key, val);
        }
    }
}
