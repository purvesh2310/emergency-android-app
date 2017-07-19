package com.pk.eager;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MapReportInfo extends DialogFragment {

    private static final String ARG_TITLE = "title";
    private static final String ARG_INFO = "info";
    private static final String ARG_LOCATION = "location";

    private OnFragmentInteractionListener mListener;

    TextView mapReportTitle;
    TextView mapReportInformation;
    TextView mapReportLocation;

    private String title;
    private String info;
    private String location;

    public MapReportInfo() {
        // Required empty public constructor
    }

    public static MapReportInfo newInstance(String title, String info, String location) {
        MapReportInfo fragment = new MapReportInfo();

        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putString(ARG_INFO, info);
        args.putString(ARG_LOCATION, location);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            title = getArguments().getString(ARG_TITLE);
            info = getArguments().getString(ARG_INFO);
            location = getArguments().getString(ARG_LOCATION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map_report_info, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mapReportTitle = (TextView) view.findViewById(R.id.mapReportTitle);
        mapReportInformation = (TextView) view.findViewById(R.id.mapReportInformation);
        mapReportLocation = (TextView) view.findViewById(R.id.mapReportLocation);


        mapReportTitle.setText(title);
        mapReportInformation.setText(info);
        mapReportLocation.setText(location);
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
}
