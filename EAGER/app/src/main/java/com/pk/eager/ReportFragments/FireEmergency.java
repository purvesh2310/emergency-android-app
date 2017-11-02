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
import android.widget.EditText;
import android.widget.RadioButton;

import com.pk.eager.Dashboard;
import com.pk.eager.R;
import com.pk.eager.ReportObject.Choice;
import com.pk.eager.ReportObject.IncidentReport;
import com.pk.eager.ReportObject.Report;

import java.util.ArrayList;

public class FireEmergency extends Fragment {
    private static final String REPORT = "report";
    private IncidentReport incidentReport;
    private static final String TAG = "FireEmergency";
    private Report fire;

    private int[] radioId = new int[]{R.id.radio_fireq1_a, R.id.radio_fireq1_b, R.id.radio_fireq1_c};
    private int[] checkId = new int[]{R.id.checkbox_fireq2_a, R.id.checkbox_fireq2_b, R.id.checkbox_fireq2_c};

    public Button nextButton;

    public FireEmergency() {
        // Required empty public constructor
    }

    public static FireEmergency newInstance(IncidentReport report) {
        FireEmergency fragment = new FireEmergency();
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
        else incidentReport = new IncidentReport();
        incidentReport = Dashboard.incidentReport;
        fire = incidentReport.getReport(Constant.FIRE);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_fire_emergency, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().setTitle("Fire Emergency");
        setButtonListener();
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(savedInstanceState!=null) {
            for (int i = 0; i < radioId.length; i++) {
                RadioButton radio = getRadioButton(radioId[i]);
                Log.d(TAG, " id is " + radioId[i] + "");
                int id = savedInstanceState.getInt(radioId[i] + "");
                if (id != 0)
                    radio.setChecked(true);
            }
        }
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
            c[i] = getCheckBox(checkId[i]);
            final int index = c[i].getId();
            c[i].setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    onButtonClick(index);
                }
            });
        }

        nextButton = (Button) this.getView().findViewById(R.id.button_next_fireEmergency);
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
            case R.id.radio_fireq1_a:
            case R.id.radio_fireq1_b:
            case R.id.radio_fireq1_c:
                radio = getRadioButton(buttonid);
                if(radio.isChecked())
                    fire.setSingleChoice(0, new Choice(radio.getText().toString(), null));
                else
                    fire.removeOneChoiceQuestion(0);
                break;
            case R.id.checkbox_fireq2_a:
            case R.id.checkbox_fireq2_b:
                checkBox = getCheckBox(buttonid);
                if(checkBox.isChecked())
                    fire.setMultiChoice(1, new Choice(checkBox.getText().toString(), null));
                else
                    fire.removeMultiChoiceQuestion(1, new Choice(checkBox.getText().toString()));
                break;
            case R.id.checkbox_fireq2_c:
                checkBox = getCheckBox(buttonid);
                if(checkBox.isChecked()) {
                    EditText edittext = (EditText) this.getView().findViewById(R.id.info_fire_emergency);
                    edittext.setEnabled(true);
                    fire.setMultiChoice(1, new Choice(checkBox.getText().toString(), new ArrayList<String>()));
                }
                else {
                    fire.removeMultiChoiceQuestion(1, new Choice(checkBox.getText().toString()));
                    ((EditText) this.getView().findViewById(R.id.info_fire_emergency)).setEnabled(false);
                }
                break;
            case R.id.button_next_fireEmergency:
                checkBox = getCheckBox(R.id.checkbox_fireq2_c);
                EditText edittext = (EditText) this.getView().findViewById(R.id.info_fire_emergency);
                if(checkBox.isChecked() && !edittext.getText().toString().isEmpty())
                    fire.addSubChoice(1, checkBox.getText().toString(), edittext.getText().toString());
                Fragment fragment = Review.newInstance(incidentReport);
                FragmentTransaction ft = getActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.mainFrame, fragment)
                        .addToBackStack("fireEmergency");
                ft.commit();
                break;
        }
    }

    public RadioButton getRadioButton(int id){
        return (RadioButton) this.getView().findViewById(id);
    }

    public CheckBox getCheckBox(int id){
        return (CheckBox) this.getView().findViewById(id);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.clear();
    }
}
