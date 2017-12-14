package com.pk.eager;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pk.eager.BaseClass.BaseXBeeFragment;

public class TabFragment extends BaseXBeeFragment {

    FragmentTabHost tabHost;


    public TabFragment() {
        // Required empty public constructor
    }

    public static TabFragment newInstance(String param1, String param2) {
        TabFragment fragment = new TabFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tab, container, false);
    }

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        tabHost = (FragmentTabHost)getView().findViewById(android.R.id.tabhost);
        tabHost.setup(getActivity().getApplicationContext(), getActivity().getSupportFragmentManager(), android.R.id.tabcontent);

        View listViewTabIndicator = LayoutInflater.from(getActivity()).inflate(R.layout.tab_listview_indicator,
                tabHost.getTabWidget(), false);

        tabHost.addTab(
                tabHost.newTabSpec("List View").setIndicator(listViewTabIndicator),
                InformationListView.class, null);

        View mapViewTabIndicator = LayoutInflater.from(getActivity()).inflate(R.layout.tab_mapview_indicator,
                tabHost.getTabWidget(), false);

        tabHost.addTab(
                tabHost.newTabSpec("Map View").setIndicator(mapViewTabIndicator),
                Information.class, null);


    }
}