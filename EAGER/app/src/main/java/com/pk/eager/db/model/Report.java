package com.pk.eager.db.model;

/**
 * Created by Purvesh on 7/26/2017.
 */

public class Report {

    int id;
    String title;
    String information;
    double latitude;
    double longitude;
    long timestamp;

    public Report(){

    }

    public Report(int id, String title, String information, double latitude, double longitude, long timestamp){
        this.id = id;
        this.title = title;
        this.information = information;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = timestamp;
    }

    public Report(String title, String information, double latitude, double longitude, long timestamp){
        this.title = title;
        this.information = information;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getInformation() {
        return information;
    }

    public void setInformation(String information) {
        this.information = information;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
