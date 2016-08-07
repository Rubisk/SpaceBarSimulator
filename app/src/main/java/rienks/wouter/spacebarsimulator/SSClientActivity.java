package rienks.wouter.spacebarsimulator;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class SSClientActivity extends Activity {
	private final static String TAG = "SSClientActivity";
	private Socket socket;
	private String subnet;
	boolean connecting = false;
    List<String> availableIPs = new ArrayList<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ssclient);
		new LookupSubnetTask().execute(this);
	}

	public void afterLookup(String subnet) {
		this.subnet = subnet;
		if (subnet == "") {
			Toast.makeText(this, "Can't find subnet, trying again.", Toast.LENGTH_SHORT).show();
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			new LookupSubnetTask().execute(this);
			return;
		}
		startCheckingHosts(subnet);
        setContentView(R.layout.main_layout);
        ((TextView) findViewById(R.id.ip_view)).setText(subnet + ".");
        Button button = (Button) findViewById(R.id.connect_button);
        button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                connect();
            }

        });

    }

    public void networkDeviceFound(String ip) {
        availableIPs.add(ip);
        Collections.sort(availableIPs);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, availableIPs);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ((Spinner) findViewById(R.id.spinner)).setAdapter(dataAdapter);
    }

	private void connect() {
		if(connecting) return;
		Spinner spinner = (Spinner) findViewById(R.id.spinner);
		if(socket != null){
			sendSpace();
			return;
		}
		Button button = (Button) findViewById(R.id.connect_button);
		button.setText("Connecting...");

		new ConnectTask().execute(this, subnet + "." + Integer.parseInt(spinner.getSelectedItem().toString())).toString();
	}

	private void loseConnection() {
		try {
			socket.close();
		} catch (IOException e) {
			Log.w(TAG, "Failed to close socket after losing connection");
		}
		socket = null;
		Button button = (Button) findViewById(R.id.connect_button);
		button.setText("Connect!");
	}

    public void startCheckingHosts(String subnet){
        for (int i = 1; i < 128; i++){
            new TryConnectTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, this, subnet, 2*i);
        }
    }

    private void sendSpace() {
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

	public void setSocket(Socket s) {
		connecting = false;
		if(s == null){
			Toast.makeText(this, "Failed to connect.", Toast.LENGTH_SHORT).show();

			Button button = (Button) findViewById(R.id.connect_button);
			button.setText("Connect!");
			return;
		}
		Toast.makeText(this, "Connected!", Toast.LENGTH_SHORT).show();
		socket = s;
		Button button = (Button) findViewById(R.id.connect_button);
		button.setText("SPACE");
	}
}
