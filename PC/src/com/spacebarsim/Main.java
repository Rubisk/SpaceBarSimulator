package com.spacebarsim;

import java.awt.AWTException;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

public class Main {
	static Server server;

	public static void main(String[] args) {
		if (!SystemTray.isSupported()) {
            System.out.println("SystemTray is not supported");
            return;
        }
        final PopupMenu popup = new PopupMenu();
        URL url = Main.class.getResource("/assets/icon.png");
        final TrayIcon trayIcon =
                new TrayIcon(Toolkit.getDefaultToolkit().getImage(url));
        final SystemTray tray = SystemTray.getSystemTray();
        MenuItem close = new MenuItem("Stop");
        close.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try{
					server.close();
				}
				finally{
					System.exit(0);
				}
			}
		});

        popup.add(close);
        trayIcon.setPopupMenu(popup);

		try {
			tray.add(trayIcon);
		} catch (AWTException e1) {
			e1.printStackTrace();
		}


		server = new Server();
	}

}
