package rienks.wouter.spacebarsimulator;

import android.util.Log;
import android.widget.ProgressBar;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class ConnectThread extends Thread {
    private static final String TAG = "CONNECTTHREAD";
    private Socket socket;
    private int[] ip;
    String name;

    ConnectThread(int[] ip) {
        this.ip = ip;
    }

    private void connect() {
        try {
            String ipAsString = ip[0] + "." + ip[1] + "." + ip[2] + "." + ip[3];
            InetAddress address = InetAddress.getByName(ipAsString);
            socket = new Socket();
            socket.connect(new InetSocketAddress(address, 5004), 1000);
            Log.i(TAG, "Connected to " + address);
        } catch (UnknownHostException e) {
            Log.e(TAG, e.toString());
        } catch (IOException e) {
            socket = null;
        }
    }

    private void retrieveName() {
        DataInputStream dIn;
        try {
            dIn = new DataInputStream(socket.getInputStream());
            name = dIn.readUTF();
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }
    }

    public void run() {
        connect();
        if (valid()) {
            retrieveName();
        }
    }

    public boolean valid() {
        return socket != null;
    }

    public Socket getSocket() {
        return socket;
    }

    public String getDeviceName() {
        return name;
    }
}
