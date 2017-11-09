package com.pk.eager.ReportFragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.pk.eager.BaseClass.BaseXBeeFragment;
import com.pk.eager.Dashboard;
import com.pk.eager.R;
import com.pk.eager.ReportObject.Choice;
import com.pk.eager.ReportObject.IncidentReport;
import com.pk.eager.ReportObject.Report;

import java.util.ArrayList;

public class TrafficEmergency extends BaseXBeeFragment {

    private static final String REPORT = "report";
    private IncidentReport incidentReport;
    private static final String TAG = "TrafficEmergency";
    private Report traffic;

    private int[] radioId = new int[]{R.id.radio_trafficq1_a1, R.id.radio_trafficq1_a2, R.id.radio_trafficq1_a3};
    private int[] checkId = new int[]{R.id.checkbox_trafficq1_a, R.id.checkbox_trafficq1_b, R.id.checkbox_trafficq1_c};

    public Button nextButton;

    public TrafficEmergency() {
        // Required empty public constructor
    }

    public static TrafficEmergency newInstance(IncidentReport report) {
        TrafficEmergency fragment = new TrafficEmergency();
        Bundle args = new Bundle();
        args.putParcelable(REPORT, report);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            incidentReport = getArguments().getParcelable(REPORT);
        }
        incidentReport = Dashboard.incidentReport;
        traffic = incidentReport.getReport(Constant.TRAFFIC);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_traffic_emergency, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().setTitle("Traffic Emergency");
        setButtonListener();
    }

    public void setButtonListener(){
        RadioButton[] r = new RadioButton[radioId.length];
        CheckBox[] c = new CheckBox[checkId.length];

        for(int i = 0; i < r.length; i++){
            r[i] = getRadioButton(radioId[i]);
            final int index = r[i].getId();
            r[i].setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    onButtonClick(index);
                }
            });
        }

        for(int i = 0; i < c.length; i++){
            c[i] = getCheckBoxButton(checkId[i]);
            final int index = c[i].getId();
            c[i].setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    onButtonClick(index);
                }
            });
        }

        nextButton = (Button) this.getView().findViewById(R.id.button_next_trafficEmergency);
        nextButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                onButtonClick(nextButton.getId());
            }
        });
    }

    public void onButtonClick(int buttonid){
        RadioButton radio;
        CheckBox checkBox;
        switch (buttonid){
            case R.id.checkbox_trafficq1_a:
                checkBox = getCheckBoxButton(buttonid);
                if(checkBox.isChecked()) {
                    traffic.setMultiChoice(0, new Choice(checkBox.getText().toString(), new ArrayList<String>()));
                    RadioGroup g = (RadioGroup) this.getView().findViewById(R.id.radioGroup_traffic);
                    ViewUtils.setRadioGroupClickable(g, true);
                }else{
                    RadioGroup g = (RadioGroup) this.getView().findViewById(R.id.radioGroup_traffic);
                    ViewUtils.setRadioGroupClickable(g, false);
                    traffic.removeMultiChoiceQuestion(0,new Choice(checkBox.getText().toString(), null));
                }
                Log.d(TAG, "Next 1" + traffic.toString());
                break;
            case R.id.radio_trafficq1_a1:
            case R.id.radio_trafficq1_a2:
            case R.id.radio_trafficq1_a3:
                radio = getRadioButton(buttonid);
                CheckBox box = (CheckBox)this.getView().findViewById(R.id.checkbox_trafficq1_a);
                if(box.isChecked() && radio.isChecked()){
                    traffic.replaceSubChoice(0,box.getText().toString(),radio.getText().toString());
                }
                Log.d(TAG, "Next 2" + traffic.toString());
                break;
            case R.id.checkbox_trafficq1_b:
            case R.id.checkbox_trafficq1_c:
                checkBox = getCheckBoxButton(buttonid);
                if(checkBox.isChecked())
                    traffic.setMultiChoice(0, new Choice(checkBox.getText().toString(), null));
                else traffic.removeMultiChoiceQuestion(0, new Choice(checkBox.getText().toString(), null));
                Log.d(TAG, "Next 3" + traffic.toString());
                break;
            case R.id.button_next_trafficEmergency:
                Log.d(TAG, "Next" + traffic.toString());
                Fragment fragment = Review.newInstance(incidentReport);
                FragmentTransaction ft = getActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.mainFrame, fragment)
                        .addToBackStack("trafficEmergency");
                ft.commit();
                break;

        }

    }


    public RadioButton getRadioButton(int id){
        return (RadioButton) this.getView().findViewById(id);
    }
    public CheckBox getCheckBoxButton(int id){
        return (CheckBox) this.getView().findViewById(id);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.clear();
    }

}
