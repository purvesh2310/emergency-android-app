package com.pk.eager;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.digi.xbee.api.listeners.IDataReceiveListener;
import com.digi.xbee.api.models.XBeeMessage;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.pk.eager.ReportObject.CompactReport;
import com.pk.eager.ReportObject.Packet;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BaseXBeeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BaseXBeeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BaseXBeeFragment extends Fragment implements IDataReceiveListener{

    private XBeeManager xbeeManager;

    private OnFragmentInteractionListener mListener;

    public BaseXBeeFragment() {

    }

    public static BaseXBeeFragment newInstance(String param1, String param2) {
        BaseXBeeFragment fragment = new BaseXBeeFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        xbeeManager = XBeeManagerApplication.getInstance().getXBeeManager();

    }

    @Override
    public void onStart() {
        super.onStart();
        if (xbeeManager.getLocalXBeeDevice() != null && xbeeManager.getLocalXBeeDevice().isOpen()) {
            xbeeManager.subscribeDataPacketListener(this);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_base_xbee, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
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

    @Override
    public void dataReceived(XBeeMessage xbeeMessage){
        String data = new String(xbeeMessage.getData());
        receiveDataFromChannel(data);
    }

    // Receive data over XBE/BLE and upload to Firebase
    public void receiveDataFromChannel(String data){
        if(data.equals("a")){
            Toast.makeText(this.getContext(), "P2P received", Toast.LENGTH_SHORT);
        }else {

            // On Receiving the data, convert it to object and add the XBEE device id in path to server
            Gson gson = new Gson();
            CompactReport cmpReport = gson.fromJson(data, CompactReport.class);

            List<String> pathToServer = cmpReport.getPathToServer();
            String receiverDeviceAddress = xbeeManager.getLocalXBee64BitAddress().toString();
            pathToServer.add(receiverDeviceAddress);

            Packet newPacket = new Packet(pathToServer, FirebaseInstanceId.getInstance().getToken());

            // boolean isConnected = checkInternetConnection();

            if (true) {

                // Saving the path to Firebase without report ID. Need to add report id afterwards
                DatabaseReference path = FirebaseDatabase.getInstance().getReference("path").push();
                //path.setValue(pathToServer);
                path.setValue(newPacket);

                DatabaseReference newChild = FirebaseDatabase.getInstance().getReference("Reports").push();
                newChild.setValue(cmpReport);

            } else {
                data = gson.toJson(cmpReport);
                //sendDataOverChannel(data);
            }
        }
    }
}
