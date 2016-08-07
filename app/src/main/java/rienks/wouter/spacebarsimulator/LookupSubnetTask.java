package rienks.wouter.spacebarsimulator;

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import android.os.AsyncTask;
import android.util.Log;

public class LookupSubnetTask extends AsyncTask<SSClientActivity, Void, String> {
	private static final String TAG = "LookupHostsTask";
	SSClientActivity activity;

	private String GetSubnetMask() throws SocketException {
	    Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
	    while (interfaces.hasMoreElements()) {
	        NetworkInterface networkInterface = interfaces.nextElement();

	        if (networkInterface.isLoopback())
	            continue; // Don't want to broadcast to the loopback interface

	        for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
	                InetAddress broadcast = interfaceAddress.getBroadcast();

	                if (broadcast == null)
	                    continue;

	                return broadcast.getHostAddress();
	        }
	    }
	    throw new SocketException();
	}

	@Override
	protected String doInBackground(SSClientActivity... params) {
		activity = params[0];
		String subnet;
		Log.i(TAG, "Retrieving subnet address...");
		try {
			subnet = GetSubnetMask();
			String[] strings = subnet.split("\\.");
			subnet = strings[0] + "." + strings[1] + "." + strings[2];
			Log.i(TAG, "Found subnet address at " + subnet);
		} catch (SocketException e) {
			Log.e(TAG, "Unable to retrieve subnet address", e);
			return "";
		}
		return subnet;
	}

	protected void onPostExecute(String subnet) {
		activity.afterLookup(subnet);
	}
}
