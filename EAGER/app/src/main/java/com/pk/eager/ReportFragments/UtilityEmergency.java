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
import android.widget.RadioGroup;

import com.pk.eager.Dashboard;
import com.pk.eager.R;
import com.pk.eager.ReportObject.Choice;
import com.pk.eager.ReportObject.IncidentReport;
import com.pk.eager.ReportObject.Report;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UtilityEmergency.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UtilityEmergency#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UtilityEmergency extends Fragment {
    private static final String REPORT = "report";
    private IncidentReport incidentReport;
    private static final String TAG = "UtilityEmergency";
    private Report utility;

    private OnFragmentInteractionListener mListener;
    private int[] checkId = new int[]{R.id.checkbox_utilityq1, R.id.checkbox_utilityq1_b, R.id.checkbox_utilityq2_a, R.id.checkbox_utilityq3_a,
                                    R.id.checkbox_utilityq3_b, R.id.checkbox_utilityq4_a, R.id.checkbox_utilityq4_b};
    private int[] radioId = new int[]{R.id.radio_utilityq1_a, R.id.radio_utilityq1_b, R.id.radio_utilityq1_c,
                                    R.id.radio_utilityq2_a, R.id.radio_utilityq2_b, R.id.radio_utilityq2_c,
                                    R.id.radio_utilityq3_a, R.id.radio_utilityq3_b, R.id.radio_utilityq3_c,
                                    R.id.radio_utilityq4_a, R.id.radio_utilityq4_b, R.id.radio_utilityq4_c,
                                    R.id.radio_utilityq4_d, R.id.radio_utilityq4_e, R.id.radio_utilityq4_f};

    public Button nextButton;

    public UtilityEmergency() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment UtilityEmergency.
     */
    // TODO: Rename and change types and number of parameters
    public static UtilityEmergency newInstance(IncidentReport report) {
        UtilityEmergency fragment = new UtilityEmergency();
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
        utility = incidentReport.getReport(Constant.UTILITY);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_utility_emergency, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().setTitle("Utility Emergency");
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

        nextButton = (Button) this.getView().findViewById(R.id.button_next_utilityEmergency);
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
            case R.id.checkbox_utilityq1_b:
                checkBox = getCheckBoxButton(buttonid);
                if(checkBox.isChecked()){
                    utility.setMultiChoice(0, new Choice(checkBox.getText().toString(), null));
                }else utility.removeMultiChoiceQuestion(0, new Choice(checkBox.getText().toString(), null));
                Log.d(TAG, "utility" + utility.toString());
                break;
            case R.id.checkbox_utilityq3_b:
                checkBox = getCheckBoxButton(buttonid);
                if(checkBox.isChecked()){
                    utility.setMultiChoice(2, new Choice(checkBox.getText().toString(), null));
                }else utility.removeMultiChoiceQuestion(2, new Choice(checkBox.getText().toString(), null));
                Log.d(TAG, "utility" + utility.toString());
                break;
            case R.id.checkbox_utilityq1:
                checkBox = getCheckBoxButton(buttonid);
                if(checkBox.isChecked()){
                    ((RadioGroup) this.getView().findViewById(R.id.radioGroup_utility1)).setClickable(true);
                    utility.setMultiChoice(0, new Choice(checkBox.getText().toString(), null));
                }else{
                    ((RadioGroup) this.getView().findViewById(R.id.radioGroup_utility1)).setClickable(false);
                    utility.removeMultiChoiceQuestion(0, new Choice(checkBox.getText().toString(), null));
                }
                Log.d(TAG, "utility" + utility.toString());
                break;
            case R.id.checkbox_utilityq2_a:
                checkBox = getCheckBoxButton(buttonid);
                if(checkBox.isChecked()){
                    ((RadioGroup) this.getView().findViewById(R.id.radioGroup_utility2)).setClickable(true);
                    utility.setMultiChoice(1, new Choice(checkBox.getText().toString(), null));
                }else{
                    ((RadioGroup) this.getView().findViewById(R.id.radioGroup_utility2)).setClickable(false);
                    utility.removeMultiChoiceQuestion(1, new Choice(checkBox.getText().toString(), null));
                }
                Log.d(TAG, "utility" + utility.toString());
                break;
            case R.id.checkbox_utilityq3_a:
                checkBox = getCheckBoxButton(buttonid);
                if(checkBox.isChecked()){
                    ((RadioGroup) this.getView().findViewById(R.id.radioGroup_utility3)).setClickable(true);
                    utility.setMultiChoice(2, new Choice(checkBox.getText().toString(), null));
                }else{
                    ((RadioGroup) this.getView().findViewById(R.id.radioGroup_utility3)).setClickable(false);
                    utility.removeMultiChoiceQuestion(2, new Choice(checkBox.getText().toString(), null));
                }
                Log.d(TAG, "utility" + utility.toString());
                break;
            case R.id.checkbox_utilityq4_a:
                checkBox = getCheckBoxButton(buttonid);
                if(checkBox.isChecked()){
                    ((RadioGroup) this.getView().findViewById(R.id.radioGroup_utility4)).setClickable(true);
                    utility.setMultiChoice(3, new Choice(checkBox.getText().toString(), null));
                }else{
                    ((RadioGroup) this.getView().findViewById(R.id.radioGroup_utility4)).setClickable(false);
                    utility.removeMultiChoiceQuestion(3, new Choice(checkBox.getText().toString(), null));
                }
                Log.d(TAG, "utility" + utility.toString());
                break;
            case R.id.checkbox_utilityq4_b:
                checkBox = getCheckBoxButton(buttonid);
                if(checkBox.isChecked()){
                    ((RadioGroup) this.getView().findViewById(R.id.radioGroup_utility5)).setClickable(true);
                    utility.setMultiChoice(3, new Choice(checkBox.getText().toString(), null));
                }else{
                    ((RadioGroup) this.getView().findViewById(R.id.radioGroup_utility5)).setClickable(false);
                    utility.removeMultiChoiceQuestion(3, new Choice(checkBox.getText().toString(), null));
                }
                Log.d(TAG, "utility" + utility.toString());
                break;
            case R.id.radio_utilityq1_a:
            case R.id.radio_utilityq1_b:
            case R.id.radio_utilityq1_c:
                radio = getRadioButton(buttonid);
                CheckBox box = getCheckBoxButton(R.id.checkbox_utilityq1);
                if(box.isChecked() && radio.isChecked()){
                    utility.replaceSubChoice(0, box.getText().toString(), radio.getText().toString());
                }
                break;
            case R.id.radio_utilityq2_a:
            case R.id.radio_utilityq2_b:
            case R.id.radio_utilityq2_c:
                radio = getRadioButton(buttonid);
                box = getCheckBoxButton(R.id.checkbox_utilityq2_a);
                if(box.isChecked() && radio.isChecked())
                    utility.replaceSubChoice(1, box.getText().toString(), radio.getText().toString());
                Log.d(TAG, "utility" + utility.toString());
                break;
            case R.id.radio_utilityq3_a:
            case R.id.radio_utilityq3_b:
            case R.id.radio_utilityq3_c:
                radio = getRadioButton(buttonid);
                box = getCheckBoxButton(R.id.checkbox_utilityq3_a);
                if(box.isChecked() && radio.isChecked())
                    utility.replaceSubChoice(2, box.getText().toString(),radio.getText().toString());
                Log.d(TAG, "utility" + utility.toString());
                break;
            case R.id.radio_utilityq4_a:
            case R.id.radio_utilityq4_b:
            case R.id.radio_utilityq4_c:
                radio = getRadioButton(buttonid);
                box = getCheckBoxButton(R.id.checkbox_utilityq4_a);
                if(box.isChecked() && radio.isChecked())
                    utility.replaceSubChoice(3, box.getText().toString(), radio.getText().toString());
                Log.d(TAG, "utility" + utility.toString());
                break;
            case R.id.radio_utilityq4_d:
            case R.id.radio_utilityq4_e:
            case R.id.radio_utilityq4_f:
                radio = getRadioButton(buttonid);
                box = getCheckBoxButton(R.id.checkbox_utilityq4_b);
                if(box.isChecked() && radio.isChecked())
                    utility.replaceSubChoice(3, box.getText().toString(), radio.getText().toString());
                Log.d(TAG, "utility" + utility.toString());
                break;
            case R.id.button_next_utilityEmergency:
                Fragment fragment = Review.newInstance(incidentReport);
                FragmentTransaction ft = getActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.mainFrame, fragment)
                        .addToBackStack("utilityEmergency");
                ft.commit();
                Log.d(TAG, utility.toString());
                break;
        }
    }

    public RadioButton getRadioButton(int id){
        return (RadioButton) this.getView().findViewById(id);
    }
    public CheckBox getCheckBoxButton(int id){
        return (CheckBox) this.getView().findViewById(id);
    }
}
