package rienks.wouter.spacebarsimulator;

import android.util.Log;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class PingThread extends Thread {
    private static final String TAG = "PINGTHREAD";
    private int[] ip;
    private String addressName;
    private boolean exists;
    private InetAddress address;

    PingThread(int[] ip) {
        this.ip = ip;
    }

    public void run() {
        try {
            String ipAsString = ip[0] + "." + ip[1] + "." + ip[2] + "." + ip[3];
            address = InetAddress.getByName(ipAsString);
            exists = true;
            System.out.println("Looking for " + address);
            if (!address.isReachable(5000)) {
                System.out.println("Not reachable: " + address);
                exists = false;
                return;
            }
            addressName = address.getCanonicalHostName();
            System.out.println("Found " + addressName);
        } catch (UnknownHostException e) {
            exists = false;
            Log.e(TAG, e.toString());
        } catch (IOException e) {
            Log.e(TAG, e.toString());
            return;
        }
    }

    public boolean exists() {
        return exists;
    }

    public String getAddressName() {
        return addressName;
    }

    public InetAddress getAddress() {
        return address;
    }
}
