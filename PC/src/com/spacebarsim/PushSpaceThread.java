package com.spacebarsim;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class PushSpaceThread extends Thread{
    protected Socket socket;

    Robot robot;

    public PushSpaceThread(Socket clientSocket) {
        this.socket = clientSocket;
        try {
			robot = new Robot();
		} catch (AWTException e) {
			e.printStackTrace()
		}
    }

    public void run() {
    	while(true){
        	DataInputStream dIn;
    		try {
    			dIn = new DataInputStream(socket.getInputStream());
    		} catch (IOException e) {
    			continue;
    		}
    		String text;
    		try {
    			text = dIn.readUTF();
    		} catch (IOException e) {
    			continue;
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
