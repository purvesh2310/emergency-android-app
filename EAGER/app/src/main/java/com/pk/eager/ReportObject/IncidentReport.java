package com.pk.eager.ReportObject;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by kimpham on 7/9/17.
 */


public class IncidentReport implements Parcelable {

    ArrayList<Report> reports = new ArrayList<>();

    public IncidentReport(){}

    public IncidentReport(String bla){
        reports = new ArrayList<Report>(5);
        reports.add(Utils.buildTrapReport());
        reports.add(Utils.buildMedicalReport());
        reports.add(Utils.buildFireReport());
        reports.add(Utils.buildPoliceReport());
        reports.add(Utils.buildTrafficReport());
        reports.add(Utils.buildUtilityReport());
    }

    public Report getReport(int type){
        return reports.get(type);
    }

    public void setReport(Report report, int type){
        reports.set(type, report);
    }

    public String toString(){
        String s = "";
        for(Report r : reports){
            s += r.toString();
        }
        return s;
    }

    public String getFirstType(){
        for(Report r : reports){
            if(!r.isEmpty()) return r.getType();
        }
        return "";
    }


    //Parceble implementation

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(reports);
    }

    public static final Parcelable.Creator<IncidentReport> CREATOR
            = new Parcelable.Creator<IncidentReport>() {
        public IncidentReport createFromParcel(Parcel in) {
            return new IncidentReport(in);
        }

        public IncidentReport[] newArray(int size) {
            return new IncidentReport[size];
        }
    };

    private IncidentReport(Parcel in) {
        reports = in.readArrayList(Report.class.getClassLoader());
    }
}
