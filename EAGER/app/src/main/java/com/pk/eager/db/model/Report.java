package com.pk.eager.db.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Purvesh on 7/26/2017.
 *
 * NB add-on:
 * Make some modification so that I can pass this object around as putExtra().
 */

public class Report implements Parcelable {

    // This is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<Report> CREATOR = new Parcelable.Creator<Report>() {
        public Report createFromParcel(Parcel in) {
            return new Report(in);
        }

        public Report[] newArray(int size) {
            return new Report[size];
        }
    };

    int id;
    String uid;
    String title;
    String information;
    double latitude;
    double longitude;
    long timestamp;

    public Report(){

    }

    public Report(int id, String uid, String title, String information, double latitude, double longitude, long timestamp){
        this.id = id;
        this.uid = uid;
        this.title = title;
        this.information = information;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = timestamp;
    }

    public Report(String uid, String title, String information, double latitude, double longitude, long timestamp){
        this.uid = uid;
        this.title = title;
        this.information = information;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = timestamp;
    }

    private Report(Parcel in) {
        id = in.readInt();
        uid = in.readString();
        title = in.readString();
        information = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        timestamp = in.readLong();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
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

    /* everything below here is for implementing Parcelable */

    @Override
    public int describeContents() {
        return 0;
    }

    // write your object's data to the passed-in Parcel
    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(id);
        out.writeString(uid);
        out.writeString(title);
        out.writeString(information);
        out.writeDouble(latitude);
        out.writeDouble(longitude);
        out.writeLong(timestamp);
    }

}
