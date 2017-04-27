package rienks.wouter.spacebarsimulator;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
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
	private String currentSocketName;
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

	public void loseConnection() {
        Toast.makeText(this, "Connection lost, reconnecting...", Toast.LENGTH_SHORT).show();
        InetAddress address = devices.get(currentSocketName).getInetAddress();
		try {
			devices.get(currentSocketName).close();
		} catch (IOException e) {
			Log.w(TAG, "Failed to close socket after losing connection");
		}
        (new ReconnectTask(this)).execute(address.toString(), currentSocketName);
	}

    public void reconnect(Socket s, String name) {
        if (s == null) {
            Toast.makeText(this, "Reconnecting failed.", Toast.LENGTH_SHORT).show();
            devices.remove(currentSocketName);
            foundDevices(devices);
        }
        else {
            Toast.makeText(this, "Reconnected.", Toast.LENGTH_SHORT).show();
            devices.put(name, s);
        }
    }

    public void sendSpace(View v) {
        Socket socket = devices.get(currentSocketName);
        new SendSpaceTask(this).execute(socket);
	}

	private void findDevices() {
        (new FindDevicesTask(this)).execute();
	}

    public void foundDevices(Map<String, Socket> devices) {
        this.devices = devices;
        setContentView(R.layout.select_device);
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        ArrayList<String> names = new ArrayList<>();
        names.addAll(devices.keySet());
        if (names.size() == 0) {
            Toast.makeText(this, "No devices found, retrying...", Toast.LENGTH_SHORT).show();
            findDevices();
        } else {
            currentSocketName = names.get(0);
            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_item,
                    names);
            spinner.setAdapter(spinnerArrayAdapter);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    currentSocketName = adapterView.getItemAtPosition(i).toString();
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                    currentSocketName = null;
                }
            });
        }
    }

    public void reloadDevices(View view) {
        reloadDevices();
    }
}
