package rienks.wouter.spacebarsimulator;

import android.os.AsyncTask;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ReconnectTask extends AsyncTask<String, Void, Socket> {
    private final SSClientActivity act;
    private String name;

    ReconnectTask(SSClientActivity act) {
        this.act = act;
    }

    @Override
    protected Socket doInBackground(String... params) {
        Socket socket = new Socket();
        name = params[1];
        try {
            socket.connect(new InetSocketAddress(params[0], 5004), 1000);
        } catch (IOException e) {
            socket = null;
        }
        return socket;
    }

    public void onPostExecute(Socket result) {
        act.reconnect(result, name);
    }
}
