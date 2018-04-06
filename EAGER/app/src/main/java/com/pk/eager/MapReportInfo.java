package com.pk.eager;

import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MapReportInfo extends DialogFragment {

    private static final String ARG_TITLE = "title";
    private static final String ARG_INFO = "info";
    private static final String ARG_LOCATION = "location";
    private final String SPLIT = "~";

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

        return inflater.inflate(R.layout.fragment_map_report_info, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mapReportTitle = (TextView) view.findViewById(R.id.mapReportTitle);
        mapReportInformation = (TextView) view.findViewById(R.id.mapReportInformation);
        mapReportLocation = (TextView) view.findViewById(R.id.mapReportLocation);

        LinearLayout additionalReportLayout = (LinearLayout) view.findViewById(R.id.additionalReports);

        String[] titles = title.split(",");
        String information[] = info.split(SPLIT);

        if(titles.length > 1){
            mapReportTitle.setText(titles[0]);
            mapReportInformation.setText(information[0]);

           Typeface boldTypeface = Typeface.defaultFromStyle(Typeface.BOLD);

            for(int i=1; i<titles.length; i++) {
                TextView titleTextView = new TextView(getContext());
                titleTextView.setTextAppearance(getContext(), R.style.reportList);
                titleTextView.setTypeface(boldTypeface);
                titleTextView.setText(titles[i]);

                additionalReportLayout.addView(titleTextView);

                TextView informationTextView = new TextView(getContext());
                informationTextView.setTextAppearance(getContext(),R.style.reportList);
                informationTextView.setText(information[i]);

                additionalReportLayout.addView(informationTextView);

            }
        }else{
            mapReportTitle.setText(title);
            mapReportInformation.setText(information[0]);
        }

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

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
