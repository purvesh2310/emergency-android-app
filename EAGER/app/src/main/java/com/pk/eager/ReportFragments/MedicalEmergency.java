package com.pk.eager.ReportFragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
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

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MedicalEmergency.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MedicalEmergency#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MedicalEmergency extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String REPORT = "report";
    private IncidentReport incidentReport;
    private static final String TAG = "MedicalEmergency";
    private Report medical;

    int[] radioId = new int[]{R.id.radio_medicalq1_a, R.id.radio_medicalq1_b, R.id.radio_medicalq1_c};
    int[] checkId = new int[]{R.id.checkbox_medicalq2_a, R.id.checkbox_medicalq2_b, R.id.checkbox_medicalq2_c,
            R.id.checkbox_medicalq2_d, R.id.checkbox_medicalq2_d1, R.id.checkbox_medicalq2_d2, R.id.checkbox_medicalq2_d3, R.id.checkbox_medicalq2_d4};

    private OnFragmentInteractionListener mListener;

    public Button nextButton;



    public MedicalEmergency() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment MedicalEmergency.
     */
    // TODO: Rename and change types and number of parameters
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

        getActivity().setTitle("Medical Emergency");
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

    public void onButtonClick(int buttonid){
        RadioButton radio;
        CheckBox checkBox = null;
        switch (buttonid){
            case R.id.radio_medicalq1_a:
            case R.id.radio_medicalq1_b:
            case R.id.radio_medicalq1_c:
                radio = (RadioButton) this.getView().findViewById(buttonid);
                if(radio.isChecked())
                    medical.setSingleChoice(0, new Choice(radio.getText().toString(), null));
                else
                    medical.removeOneChoiceQuestion(0);
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
                if (checkBox.isChecked())
                    medical.setMultiChoice(1, new Choice(checkBox.getText().toString(), new ArrayList<String>()));
                else
                    medical.removeMultiChoiceQuestion(1, new Choice(checkBox.getText().toString(), null));
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
}
