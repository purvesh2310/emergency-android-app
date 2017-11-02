package com.pk.eager.ReportFragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;

import com.pk.eager.Dashboard;
import com.pk.eager.R;
import com.pk.eager.ReportObject.Choice;
import com.pk.eager.ReportObject.IncidentReport;
import com.pk.eager.ReportObject.Report;

public class PoliceEmergency extends Fragment {
    private static final String REPORT = "report";
    private IncidentReport incidentReport;
    private static final String TAG = "PoliceEmergency";
    private Report police;
    private int[] checkId = new int[]{R.id.checkbox_policeq1_a, R.id.checkbox_policeq1_b, R.id.checkbox_policeq1_c,
                                    R.id.checkbox_policeq1_d, R.id.checkbox_policeq1_e};

    public Button nextButton;

    public PoliceEmergency() {
        // Required empty public constructor
    }

    public static PoliceEmergency newInstance(IncidentReport report) {
        PoliceEmergency fragment = new PoliceEmergency();
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
        }else incidentReport = new IncidentReport();
        incidentReport = Dashboard.incidentReport;
        police = incidentReport.getReport(Constant.POLICE);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_police_emergency, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().setTitle("Police Emergency");
        setButtonListener();
    }

    public void setButtonListener(){

        CheckBox[] c = new CheckBox[checkId.length];
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

        nextButton = (Button) this.getView().findViewById(R.id.button_next_policeEmergency);
        nextButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                onButtonClick(nextButton.getId());
            }
        });
    }

    public void onButtonClick(int buttonid){
        CheckBox checkBox;
        switch (buttonid) {
            case R.id.button_next_policeEmergency:
                Fragment fragment = Review.newInstance(incidentReport);
                FragmentTransaction ft = getActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.mainFrame, fragment)
                        .addToBackStack("policeEmergency");
                ft.commit();
                break;
            default:
                checkBox = getCheckBox(buttonid);
                if(checkBox!=null && checkBox.isChecked())
                    police.setMultiChoice(0, new Choice(checkBox.getText().toString(), null));
                else
                    police.removeMultiChoiceQuestion(0, new Choice(checkBox.getText().toString(), null));
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
