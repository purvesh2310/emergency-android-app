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

import com.pk.eager.BaseClass.BaseXBeeFragment;
import com.pk.eager.Dashboard;
import com.pk.eager.R;
import com.pk.eager.ReportObject.Choice;
import com.pk.eager.ReportObject.IncidentReport;
import com.pk.eager.ReportObject.Report;

import java.util.ArrayList;

public class MedicalEmergency extends BaseXBeeFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String REPORT = "report";
    private IncidentReport incidentReport;
    private static final String TAG = "MedicalEmergency";
    private Report medical;

    int[] radioId = new int[]{R.id.radio_medicalq1_a, R.id.radio_medicalq1_b, R.id.radio_medicalq1_c};
    int[] checkId = new int[]{R.id.checkbox_medicalq2_a, R.id.checkbox_medicalq2_b, R.id.checkbox_medicalq2_c,
            R.id.checkbox_medicalq2_d, R.id.checkbox_medicalq2_d1, R.id.checkbox_medicalq2_d2, R.id.checkbox_medicalq2_d3, R.id.checkbox_medicalq2_d4};

    public Button nextButton;



    public MedicalEmergency() {
        // Required empty public constructor
    }

    public static MedicalEmergency newInstance(IncidentReport param1) {
        MedicalEmergency fragment = new MedicalEmergency();
        Bundle args = new Bundle();
        args.putParcelable(REPORT, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            incidentReport = getArguments().getParcelable(REPORT);
            Log.d(TAG, incidentReport.toString());
        }
        else incidentReport = new IncidentReport();
        incidentReport = Dashboard.incidentReport;
        medical = incidentReport.getReport(Constant.MEDICAL);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_medical_emergency, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, incidentReport.toString());

        setEnableCheckBox(false);
        getActivity().setTitle("Medical Emergency");
        setButtonListener();
    }

    public void setButtonListener(){
        RadioButton[] r = new RadioButton[radioId.length];
        CheckBox[] c = new CheckBox[checkId.length];

        for(int i = 0; i < radioId.length; i++){
            r[i] = getRadioButton(radioId[i]);
            final int index = r[i].getId();
            r[i].setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {

                    onButtonClick(index);
                }
            });
        }

        for(int i = 0; i < checkId.length; i++){
            c[i] = getCheckBox(checkId[i]);
            final int index = c[i].getId();
            c[i].setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    onButtonClick(index);
                }
            });
        }

        nextButton = (Button) this.getView().findViewById(R.id.button_next_medicalEmergency);
        nextButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

               onButtonClick(nextButton.getId());
            }
        });
    }

    public RadioButton getRadioButton(int id){
        return (RadioButton) this.getView().findViewById(id);
    }

    public CheckBox getCheckBox(int id){
        return (CheckBox) this.getView().findViewById(id);
    }

    public void setEnableCheckBox(boolean enable){
        int[] id = {R.id.checkbox_medicalq2_d1,R.id.checkbox_medicalq2_d2,R.id.checkbox_medicalq2_d3,R.id.checkbox_medicalq2_d4};
        for(int i = 0; i < id.length; i++){
            CheckBox box = (CheckBox)getView().findViewById(id[i]);
            box.setEnabled(enable);
            if(!enable)
                box.setAlpha(0.4f);
            else
                box.setAlpha(1.0f);
        }

    }

    public void onButtonClick(int buttonid){
        RadioButton radio;
        CheckBox checkBox = null;
        switch (buttonid){
            case R.id.radio_medicalq1_a:
            case R.id.radio_medicalq1_b:
            case R.id.radio_medicalq1_c:
                radio = (RadioButton) this.getView().findViewById(buttonid);
                if(radio.isChecked()) {
                    medical.setSingleChoice(0, new Choice(radio.getText().toString(), null));
                    Log.d(TAG, "is checked " + radio.isChecked());
                }
                else {
                    radio.setChecked(false);
                    Log.d(TAG, "is not checked" + radio.isChecked());
                    medical.removeOneChoiceQuestion(0);
                }
                break;
            case R.id.checkbox_medicalq2_a:
            case R.id.checkbox_medicalq2_b:
            case R.id.checkbox_medicalq2_c:
                checkBox = (CheckBox) this.getView().findViewById(buttonid);
                if (checkBox.isChecked())
                    medical.setMultiChoice(1, new Choice(checkBox.getText().toString(), null));
                else
                    medical.removeMultiChoiceQuestion(1, new Choice(checkBox.getText().toString(), null));
                break;
            case R.id.checkbox_medicalq2_d:
                checkBox = (CheckBox) this.getView().findViewById(buttonid);
                if (checkBox.isChecked()) {
                    medical.setMultiChoice(1, new Choice(checkBox.getText().toString(), new ArrayList<String>()));
                    setEnableCheckBox(true);
                }
                else {
                    medical.removeMultiChoiceQuestion(1, new Choice(checkBox.getText().toString(), null));
                    setEnableCheckBox(false);
                }
                break;
            case R.id.checkbox_medicalq2_d1:
            case R.id.checkbox_medicalq2_d2:
            case R.id.checkbox_medicalq2_d3:
            case R.id.checkbox_medicalq2_d4:
                CheckBox subBox = (CheckBox) this.getView().findViewById(buttonid);
                CheckBox box = (CheckBox) this.getView().findViewById(R.id.checkbox_medicalq2_d);
                if (box.isChecked() && subBox.isChecked())
                    medical.addSubChoice(1, box.getText().toString(), subBox.getText().toString());
                else if(box.isChecked() && !subBox.isChecked())
                    medical.removeSubChoice(1, box.getText().toString(), subBox.getText().toString());
                break;
            case R.id.button_next_medicalEmergency:
                Fragment fragment = Review.newInstance(incidentReport);
                FragmentTransaction ft = getActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.mainFrame, fragment)
                        .addToBackStack("medicalEmergency");
                ft.commit();
                break;

        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.clear();
    }
}
