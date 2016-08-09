package rienks.wouter.spacebarsimulator;

import java.io.IOException;
import java.net.Socket;

import android.os.AsyncTask;
import android.util.Log;

public class ConnectTask extends AsyncTask<Object, Void, Socket> {
	private SSClientActivity act;
	private static final String TAG = "ConnectTask";

	@Override
	protected Socket doInBackground(Object... params) {
		act = (SSClientActivity) params[0];
		Log.i(TAG, "Connecting to " + params[1]);
		Socket socket;
		try {
			socket = new Socket((String) params[1], 5004);
		} catch (IOException e) {
			Log.e(TAG, "Can't connect!", e);
			return null;
		}
		Log.i(TAG, "Connected");
		return socket;
	}

	protected void onPostExecute(Socket s) {
		act.setSocket(s);
	}
}
