package com.spacebarsim;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	ServerSocket server_socket;

	public Server() {

		try {
			server_socket = new ServerSocket(5004);
			System.out.println("server is now running");

			Thread t = new Thread(new Runnable() {

				@Override
				public synchronized void run() {

					while(true){
						Socket socket;
						try {
							socket = server_socket.accept();
						} catch (IOException e) {
							continue;
						}
						new PushSpaceThread(socket).start();
					}
				}

			});


			t.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public synchronized void close() {
		try {
			server_socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}