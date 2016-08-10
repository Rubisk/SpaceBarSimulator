package rienks.wouter.spacebarsimulator;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.ProgressBar;

import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class FindDevicesTask extends AsyncTask<Void, Integer, Map<String, Socket>> {
    private static final String TAG = "FINDDEVICESTASK";
    private SSClientActivity act;
    private ProgressBar progressBar;

    FindDevicesTask(SSClientActivity act) {
        this.act = act;
    }

    public void onPreExecute() {
        act.setContentView(R.layout.find_devices);
        progressBar = (ProgressBar) act.findViewById(R.id.progressBar);
        progressBar.setMax(100);
    }

    @Override
    protected Map<String, Socket> doInBackground(Void... _) {
        ConnectThread[] threads = new ConnectThread[254];
        int[] ip = {192, 168, 178, 0};
        for (int i = 1; i < 255; i++) {
            ip[3] = i;
            threads[i - 1] = new ConnectThread(ip);
            threads[i - 1].start();
        }

        // All connect tasks should take 10 seconds, so we make the loading bar just a
        // 10 second wait thread.
        publishProgress(0);
        Thread timeThread = new Thread(new Runnable() {
            @Override
            public void run() {
                for (Integer i = 1; i <= 100; i++) {
                    try {
                        Thread.sleep(10); // wait 0.1 second;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    publishProgress(i);
                }
            }
        });
        timeThread.run();

        Map<String, Socket> networkDevices = new HashMap<>();
        for (int i = 0; i < 254; i++) {
            try {
                threads[i].join();
                if (threads[i].valid()) {
                    Log.i(TAG, "Found device: " + threads[i].getDeviceName());
                    networkDevices.put(threads[i].getDeviceName(), threads[i].getSocket());
                }
            } catch (InterruptedException e) {
                Log.d(TAG, e.toString());
            }
        }
        return networkDevices;
    }

    public void onProgressUpdate(Integer... progress) {
        progressBar.setProgress(progress[0]);
    }

    public void onPostExecute(Map<String, Socket> result) {
        act.foundDevices(result);
    }
}
