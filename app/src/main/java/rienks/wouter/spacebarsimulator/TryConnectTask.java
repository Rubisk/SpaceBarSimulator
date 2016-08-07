package rienks.wouter.spacebarsimulator;

import android.os.AsyncTask;

import java.io.IOException;
import java.net.InetAddress;

public class TryConnectTask extends AsyncTask<Object, Void, Integer> {
    private SSClientActivity act;
    private static final String TAG = "ConnectTask";
    // We can only have 128 of these running, so test 2 addresses per instance.
    Integer firstHost;
    Integer secondHost;
    @Override
    protected Integer doInBackground(Object... params) {
        act = (SSClientActivity) params[0];
        String subnet = (String) params[1];
        int i = (int) params[2];
        firstHost = i;
        secondHost = i + 1;
        int result = 0;
        try {
            System.out.println("Pinging" + subnet + "." + firstHost);
            Process p1 = java.lang.Runtime.getRuntime().exec("ping -c 1 " + subnet + "." + firstHost);
            int returnVal = p1.waitFor();
            if (returnVal == 0){
                result += 1;
            }
        } catch (Exception e) {
        }
        try {
            System.out.println("Pinging" + subnet + "." + secondHost);
            Process p1 = java.lang.Runtime.getRuntime().exec("ping -c 1 " + subnet +"." + secondHost);
            int returnVal = p1.waitFor();
            if (returnVal == 0){
                result += 2;
            }
        } catch (Exception e) {

        }
        return result;
    }

    protected void onPostExecute(Integer result) {
        if (result % 2 == 1) act.networkDeviceFound(String.format("%03d", firstHost));
        if (result > 1) act.networkDeviceFound(String.format("%03d", secondHost));
    }
}
