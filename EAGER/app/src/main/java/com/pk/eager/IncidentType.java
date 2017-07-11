package com.pk.eager;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;

import com.pk.eager.ReportObject.Choice;
import com.pk.eager.ReportObject.IncidentReport;
import com.pk.eager.ReportObject.Report;
import com.pk.eager.ReportObject.Utils;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link IncidentType.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link IncidentType#newInstance} factory method to
 * create an instance of this fragment.
 */
public class IncidentType extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    // TODO: Rename and change types of parameters
    private String mParam1;

    private OnFragmentInteractionListener mListener;
    public Button nextButton;

    private IncidentReport incidentReport;
    private static final String REPORT = "report";
    private static final String TAG = "IncidentType";
    private int[] id = new int[]{R.id.radio_incidentType_trapped, R.id.radio_incidentType_medical, R.id.radio_incidentType_fire,
                                R.id.radio_incidentType_police, R.id.radio_incidentType_traffic, R.id.radio_incidentType_utility,
                                R.id.radio_incidentType_other, R.id.button_next_incidentType};



    public IncidentType() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment IncidentType.
     */
    // TODO: Rename and change types and number of parameters
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
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
        if(incidentReport==null)
            incidentReport = Dashboard.incidentReport;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d(TAG, incidentReport.toString());
        return inflater.inflate(R.layout.fragment_incident_type, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().setTitle("Choose Incident Type");
        RadioButton radio;

        for(int i = 0; i < 6; i++) {
            if (Utils.hasReport(incidentReport, i)) {
                radio = getRadioButton(id[i]);
                radio.setVisibility(View.GONE);
            }
        }



        setButtonListener();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


    public void setButtonListener(){
        RadioButton[] b = new RadioButton[7];
        for(int i = 0; i < 6; i++){
            b[i] = getRadioButton(id[i]);
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

    public RadioButton getRadioButton(int id){
        return (RadioButton) this.getView().findViewById(id);
    }

    public void startFragment(Fragment fragment){
        FragmentTransaction ft = getActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.mainFrame, fragment)
                .addToBackStack("incidentType");
        ft.commit();
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
                        Fragment fragment = MedicalEmergency.newInstance(incidentReport);
                        startFragment(fragment);
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
        Fragment fragment = null;
        switch (buttonid){
            case R.id.radio_incidentType_trapped:
                showTrappedDialog();
                break;
            case R.id.radio_incidentType_medical:
                fragment = MedicalEmergency.newInstance(incidentReport);
                startFragment(fragment);
                break;
            case R.id.radio_incidentType_fire:
                fragment = FireEmergency.newInstance(incidentReport);
                startFragment(fragment);
                break;
            case R.id.radio_incidentType_police:
                fragment = PoliceEmergency.newInstance(incidentReport);
                startFragment(fragment);
                break;
            case R.id.radio_incidentType_traffic:
                fragment = TrafficEmergency.newInstance(incidentReport);
                startFragment(fragment);
                break;
            case R.id.radio_incidentType_utility:
                fragment = UtilityEmergency.newInstance(incidentReport);
                startFragment(fragment);
                break;
            case R.id.radio_incidentType_other:
                fragment = MedicalEmergency.newInstance(incidentReport);
                startFragment(fragment);
                break;
            case R.id.button_next_incidentType:
                fragment = MedicalEmergency.newInstance(incidentReport);
                startFragment(fragment);
                break;

        }

    }
}
