
package com.pk.eager;

import java.util.HashMap;

import android.content.Context;

//import com.digi.android.sample.xbeemanager.XBeeConstants;
import com.digi.xbee.api.RemoteXBeeDevice;
import com.digi.xbee.api.XBeeDevice;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.listeners.IDataReceiveListener;
import com.digi.xbee.api.listeners.IDiscoveryListener;
import com.digi.xbee.api.listeners.IIOSampleReceiveListener;
import com.digi.xbee.api.listeners.IModemStatusReceiveListener;
import com.digi.xbee.api.models.XBee64BitAddress;
import com.digi.xbee.api.utils.ByteUtils;
import com.digi.xbee.api.utils.HexUtils;

public class XBeeManager {
	
	// Constants.
	private final static String USB_HOST_API = "USB Host API";
	
	// Variables.
	private String port;
	
	private int baudRate;
	
	private XBeeDevice localDevice;
	
	private Context context;
	
	/**
	 * Class constructor. Instances a new {@code XBeeManager} object with the
	 * given parameters.
	 * 
	 * @param context The Android context.
	 */
	public XBeeManager(Context context) {
		this.context = context;
	}
	
	/**
	 * Creates the local XBee Device using the Android USB Host API with the
	 * given baud rate.
	 * 
	 * @param baudRate Baud rate to use in the XBee Connection.
	 */
	public void createXBeeDevice(int baudRate) {
		this.baudRate = baudRate;
		this.port = null;
		localDevice = new XBeeDevice(context, baudRate);
	}
	

	/**
	 * Returns the local XBee device.
	 * 
	 * @return The local XBee device.
	 */
	public XBeeDevice getLocalXBeeDevice() {
		return localDevice;
	}
	

	/**
	 * Sends the given data to the given remote device.
	 * 
	 * @param data Data to send.
	 * @param remoteDevice Remote XBee device to send data to.
	 * 
	 * @throws XBeeException if there is a timeout or
	 *                       any other error executing the request.
	 */
	public int sendDataToRemote(byte[] data, RemoteXBeeDevice remoteDevice) throws XBeeException {
		localDevice.sendData(remoteDevice, data);
		byte[] rssi =  localDevice.getParameter("DB");
		int rs = ByteUtils.byteArrayToInt(rssi);
		return rs;
	}

	public void broadcastData(byte[] data) throws XBeeException {
		//RemoteXBeeDevice remote = new RemoteXBeeDevice(localDevice, new XBee64BitAddress("0013A2004125D261"));
		//localDevice.sendData(remote,data);
		localDevice.sendBroadcastData(data);

	}


	/**
	 * Retrieves the local XBee device 64-bit address.
	 * 
	 * @return The local XBee device 64-bit address.
	 */

	public XBee64BitAddress getLocalXBee64BitAddress() {
		return localDevice.get64BitAddress();
	}
	
	/**
	 * Adds the given listener to the list of listeners that will be notified
	 * on device discovery events.
	 * 
	 * @param listener Discovery listener to add.
	 */
	public void addDiscoveryListener(IDiscoveryListener listener) {
		localDevice.getNetwork().addDiscoveryListener(listener);
	}
	
	/**
	 * Starts the device discovery process.
	 */
	public void startDiscoveryProcess() {
		localDevice.getNetwork().startDiscoveryProcess();
	}
	
	/**
	 * Returns whether the device discovery process is running or not.
	 * 
	 * @return {@code true} if device discovery process is running, 
	 *         {@code false} otherwise.
	 */
	public boolean isDiscoveryRunning() {
		return localDevice.getNetwork().isDiscoveryRunning();
	}
	
	/**
	 * Saves changes to flash.
	 * 
	 * @throws XBeeException if there is a timeout or
	 *                          any other error during the operation.
	 */
	public void saveChanges() throws XBeeException {
		localDevice.writeChanges();
	}
	
	/**
	 * Subscribes the given listener to the list of listeners that will be
	 * notified when XBee data packets are received.
	 * 
	 * @param listener Listener to subscribe.
	 */
	public void subscribeDataPacketListener(IDataReceiveListener listener) {
		localDevice.addDataListener(listener);
	}
	
	/**
	 * Unsubscribes the given listener from the list of data packet listeners.
	 * 
	 * @param listener Listener to unsubscribe.
	 */
	public void unsubscribeDataPacketListener(IDataReceiveListener listener) {
		localDevice.removeDataListener(listener);
	}
	


	
	/**
	 * Attempts to open the local XBee Device connection.
	 * 
	 * @throws XBeeException if any error occurs during the process.
	 */
	public void openConnection() throws XBeeException {
		if (!localDevice.isOpen())
			localDevice.open();
	}
	
	/**
	 * Attempts to close the local XBee Device connection.
	 */
	public void closeConnection() {
		if (localDevice.isOpen())
			localDevice.close();
	}
}
