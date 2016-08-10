package com.spacebarsim;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;
import java.net.Socket;

public class PushSpaceThread extends Thread{
    protected Socket socket;

    Robot robot;

    public PushSpaceThread(Socket clientSocket) {
        this.socket = clientSocket;
        try {
			robot = new Robot();
		} catch (AWTException e) {
			e.printStackTrace();
		}
    }

    public void run() {
    	DataOutputStream dOut;
    	try {
    		dOut = new DataOutputStream(socket.getOutputStream());
    		String name;
		    Map<String, String> env = System.getenv();
		    if (env.containsKey("COMPUTERNAME"))
		        name = env.get("COMPUTERNAME");
		    else if (env.containsKey("HOSTNAME"))
		        name = env.get("HOSTNAME");
		    else
		    	name = "Unkwown Computer";
    		dOut.writeUTF(name);
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    	while(true){
        	DataInputStream dIn;
    		try {
    			dIn = new DataInputStream(socket.getInputStream());
    		} catch (IOException e) {
    			return;
    		}
    		String text;
    		try {
    			text = dIn.readUTF();
    		} catch (IOException e) {
    			return;
    		}
    		if(text.equals("Please press the spacebar, will you?")){
    			robot.keyPress(KeyEvent.VK_SPACE);
    		}
    		try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	}

	}
}
