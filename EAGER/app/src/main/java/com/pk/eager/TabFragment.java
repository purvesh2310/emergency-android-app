package com.pk.eager;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class TabFragment extends Fragment {

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
        tabHost.addTab(
                tabHost.newTabSpec("List View").setIndicator("List View", null),
                InformationListView.class, null);
        tabHost.addTab(
                tabHost.newTabSpec("Map View").setIndicator("Map View", null),
                Information.class, null);
    }
}
