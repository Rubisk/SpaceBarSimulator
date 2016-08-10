package rienks.wouter.spacebarsimulator;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

public class SSClientActivity extends Activity {
	private final static String TAG = "SSClientActivity";
	private String currentSocket;
	private Map<String, Socket> devices;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.find_devices);
		findDevices();
	}

    private void closeDevices() {
        for (Socket s : devices.values()) {
            try {
                s.close();
            } catch (IOException e) {
                Log.e(TAG, e.toString());
            }
        }
    }

    private void reloadDevices() {
        closeDevices();
        findDevices();
    }

	private void loseConnection() {
		try {
			devices.get(currentSocket).close();
		} catch (IOException e) {
			Log.w(TAG, "Failed to close socket after losing connection");
		}
		devices.remove(currentSocket);
        reloadDevices();
	}

    public void sendSpace(View v) {
		Socket socket = devices.get(currentSocket);
		DataOutputStream dOut;
		try {
			dOut = new DataOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			loseConnection();
			Toast.makeText(this, "Lost the connection.", Toast.LENGTH_SHORT).show();
			Log.e(TAG, "Unable to send message", e);
			return;
		}
		try {
			dOut.writeUTF("Please press the spacebar, will you?");
		} catch (IOException e) {
			loseConnection();
			Toast.makeText(this, "Lost the connection.", Toast.LENGTH_SHORT).show();
			Log.e(TAG, "Unable to send message", e);
		}
	}

	private void findDevices() {
        (new FindDevicesTask(this)).execute();
	}

    public void foundDevices(Map<String, Socket> devices) {
        this.devices = devices;
        if (devices.size() == 0) {
            Toast.makeText(this, "No devices found, retrying.", Toast.LENGTH_SHORT).show();
            findDevices();
        }
        setContentView(R.layout.select_device);
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        ArrayList<String> names = new ArrayList<>();
        names.addAll(devices.keySet());
        currentSocket = names.get(0);
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                names);
        spinner.setAdapter(spinnerArrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                currentSocket = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                currentSocket = null;
            }
        });
    }

    public void reloadDevices(View view) {
        reloadDevices();
    }
}
