package com.pk.eager.ReportObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by kimpham on 7/12/17.
 */

public class CompactReport {
    Map<String, ArrayList<String>> compactReports = new HashMap<>();
    Double longitude;
    Double latitude;
    String phoneNumber;

    public CompactReport(IncidentReport incidentReport, Double longitude, Double latitude, String phone){
        for(Report r : incidentReport.reports){
            ArrayList<String> questions = new ArrayList<>();
            for(Question q : r.questions){
                questions.add(q.getQuestion()+":"+q.getChoices());
            }
            compactReports.put(r.getType(), questions);
        }
        this.longitude = longitude;
        this.latitude = latitude;
        this.phoneNumber = phone;
    }
}
