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

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TrafficEmergency.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TrafficEmergency#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TrafficEmergency extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String REPORT = "report";
    private IncidentReport incidentReport;
    private static final String TAG = "TrafficEmergency";
    private Report traffic;

    private OnFragmentInteractionListener mListener;
    private int[] radioId = new int[]{R.id.radio_trafficq1_a1, R.id.radio_trafficq1_a2, R.id.radio_trafficq1_a3};
    private int[] checkId = new int[]{R.id.checkbox_trafficq1_a, R.id.checkbox_trafficq1_b, R.id.checkbox_trafficq1_c};

    public Button nextButton;

    public TrafficEmergency() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment TrafficEmergency.
     */
    // TODO: Rename and change types and number of parameters
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
                    g.setClickable(true);
                }else{
                    ((RadioGroup) this.getView().findViewById(R.id.radioGroup_traffic)).setClickable(false);
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

}
