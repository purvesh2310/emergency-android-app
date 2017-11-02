package com.pk.eager.ReportFragments;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;

import com.pk.eager.Dashboard;
import com.pk.eager.R;
import com.pk.eager.ReportObject.Choice;
import com.pk.eager.ReportObject.IncidentReport;
import com.pk.eager.ReportObject.Report;
import com.pk.eager.ReportObject.Utils;

import java.util.ArrayList;

public class IncidentType extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private String mParam1;

    private OnFragmentInteractionListener mListener;
    public Button nextButton;
    private boolean falseReportWarning = false;
    private IncidentReport incidentReport;
    private static final String REPORT = "report";
    private static final String TAG = "IncidentType";
    private int[] id = new int[]{R.id.radio_incidentType_trapped, R.id.radio_incidentType_medical, R.id.radio_incidentType_fire,
                                R.id.radio_incidentType_police, R.id.radio_incidentType_traffic, R.id.radio_incidentType_utility,
                                R.id.radio_incidentType_other, R.id.button_next_incidentType};
    private Fragment[] fragments = new Fragment[6];

    public IncidentType() {
        // Required empty public constructor
    }

    public static IncidentType newInstance(String param1, String param2) {
        IncidentType fragment = new IncidentType();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
        if(incidentReport==null)
            incidentReport = Dashboard.incidentReport;

        for(int i = 0; i < fragments.length; i++){
            switch (i){
                case Constant.MEDICAL:
                    fragments[Constant.MEDICAL] = new MedicalEmergency();
                    break;
                case Constant.FIRE:
                    fragments[Constant.FIRE] = new FireEmergency();
                    break;
                case Constant.POLICE:
                    fragments[Constant.POLICE] = new PoliceEmergency();
                    break;
                case Constant.TRAFFIC:
                    fragments[Constant.TRAFFIC] = new TrafficEmergency();
                    break;
                case Constant.UTILITY:
                    fragments[Constant.UTILITY] = new UtilityEmergency();
                    break;
            }

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_incident_type, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().setTitle("Choose Incident Type");
        setButtonListener();
        showWarningDialog();

    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.clear();
    }

    public void setButtonListener(){
        CheckBox[] b = new CheckBox[7];
        for(int i = 0; i < 6; i++){
            b[i] = getCheckBoxButton(id[i]);
            final int index = b[i].getId();
            b[i].setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {onButtonClick(index);
                }
            });
        }
        nextButton = (Button) this.getView().findViewById(R.id.button_next_incidentType);
        nextButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {onButtonClick(nextButton.getId());
            }
        });
    }

    public CheckBox getCheckBoxButton(int id){
        return (CheckBox) this.getView().findViewById(id);
    }

    public void startFragment(Fragment fragment, int i){
        if(fragment == null)
            fragment = makeFragment(i);
        FragmentTransaction ft = getActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.mainFrame, fragment)
                .addToBackStack("incidentType");
        ft.commit();
    }

    public Fragment makeFragment(int i){
        switch (i){
            case Constant.MEDICAL:
                return new MedicalEmergency();
            case Constant.FIRE:
                return new FireEmergency();
            case Constant.POLICE:
                return new PoliceEmergency();
            case Constant.TRAFFIC:
                return new TrafficEmergency();
            case Constant.UTILITY:
                return new UtilityEmergency();
        }
        return null;
    }

    public void showTrappedDialog(){
        AlertDialog alertDialog = new AlertDialog.Builder(this.getContext()).create();
        alertDialog.setTitle("Trap Emergency");
        alertDialog.setMessage("Are you trapped?");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Report trap = incidentReport.getReport(Constant.TRAP);
                        trap.setSingleChoice(0, new Choice("Yes", null));
                        startFragment(fragments[Constant.MEDICAL], Constant.MEDICAL);
                        dialog.dismiss();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    public void onButtonClick(int buttonid){
        switch (buttonid){
            case R.id.radio_incidentType_trapped:
                if(getCheckBoxButton(buttonid).isChecked())
                    showTrappedDialog();
                else incidentReport.getReport(Constant.TRAP).removeSingleChoice(0, new Choice(getCheckBoxButton(buttonid).getText().toString() ));
                break;
            case R.id.radio_incidentType_medical:
                if(!getCheckBoxButton(buttonid).isChecked()) {
                    incidentReport.setReport(Utils.buildMedicalReport(), Constant.MEDICAL);
                    fragments[Constant.MEDICAL] = null;
                }
                else
                    startFragment(fragments[Constant.MEDICAL], Constant.MEDICAL);
                break;
            case R.id.radio_incidentType_fire:
                if(!getCheckBoxButton(buttonid).isChecked()){
                    incidentReport.setReport(Utils.buildFireReport(), Constant.FIRE);
                    fragments[Constant.FIRE] = null;
                }
                else
                    startFragment(fragments[Constant.FIRE], Constant.FIRE);
                break;
            case R.id.radio_incidentType_police:
                if(!getCheckBoxButton(buttonid).isChecked()){
                    incidentReport.setReport(Utils.buildPoliceReport(), Constant.POLICE);
                    fragments[Constant.POLICE] = null;
                }
                else
                    startFragment(fragments[Constant.POLICE], Constant.POLICE);
                break;
            case R.id.radio_incidentType_traffic:
                if(!getCheckBoxButton(buttonid).isChecked()){
                    incidentReport.setReport(Utils.buildTrafficReport(), Constant.TRAFFIC);
                    fragments[Constant.TRAFFIC] = null;
                }
                else
                    startFragment(fragments[Constant.TRAFFIC], Constant.TRAFFIC);
                break;
            case R.id.radio_incidentType_utility:
                if(!getCheckBoxButton(buttonid).isChecked()){
                    incidentReport.setReport(Utils.buildUtilityReport(), Constant.UTILITY);
                    fragments[Constant.UTILITY] = null;
                }
                else
                    startFragment(fragments[Constant.UTILITY], Constant.UTILITY);
                break;
            case R.id.radio_incidentType_other:
                if(!getCheckBoxButton(buttonid).isChecked()){
                    incidentReport.setReport(Utils.buildMedicalReport(), Constant.MEDICAL);
                    fragments[Constant.MEDICAL] = null;
                }
                else
                    startFragment(fragments[Constant.MEDICAL], Constant.MEDICAL);
                break;
            case R.id.button_next_incidentType:
                CharSequence[] items = getCheckedChoice();
                if(items.length > 0)
                    showNavigationDialog(items);
                else
                    startFragment(fragments[Constant.MEDICAL], Constant.MEDICAL);
                break;
        }
    }

    public CharSequence[] getCheckedChoice(){
        final ArrayList<String> items = new ArrayList<>();
        for(int i = 1; i < id.length-1; i++){
            CheckBox box = (CheckBox)getView().findViewById(id[i]);
            if(box.isChecked()){
                items.add(box.getText().toString());
            }
        }
        final CharSequence[] itemsList = new CharSequence[items.size()+1];
        for(int i = 0; i < items.size(); i++){
            itemsList[i] = items.get(i);
        }
        itemsList[itemsList.length-1] = "Review";
        return itemsList;
    }

    public void showNavigationDialog(final CharSequence[] itemsList){
        final AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Go To...")
                .setSingleChoiceItems(itemsList, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String text = itemsList[which].toString();
                        switch(text){
                            case "Medical":
                                startFragment(fragments[Constant.MEDICAL], Constant.MEDICAL);
                                break;
                            case "Fire":
                                startFragment(fragments[Constant.FIRE], Constant.FIRE);
                                break;
                            case "Police":
                                startFragment(fragments[Constant.POLICE], Constant.POLICE);
                                break;
                            case "Traffic":
                                startFragment(fragments[Constant.TRAFFIC], Constant.TRAFFIC);
                                break;
                            case "Utility":
                                startFragment(fragments[Constant.UTILITY], Constant.UTILITY);
                                break;
                            case "Review":
                                Fragment fragment = Review.newInstance(incidentReport);
                                FragmentTransaction ft = getActivity()
                                        .getSupportFragmentManager()
                                        .beginTransaction()
                                        .replace(R.id.mainFrame, fragment)
                                        .addToBackStack("trafficEmergency");
                                ft.commit();
                                break;
                        }
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();

    }

    public void showWarningDialog(){
        if(!falseReportWarning) {
            AlertDialog.Builder builder;
            builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Warning")
                    .setMessage("False report will be prosecuted.")
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Do Nothing
                        }
                    })
                    .show();
        }
        falseReportWarning = true;

    }
}
