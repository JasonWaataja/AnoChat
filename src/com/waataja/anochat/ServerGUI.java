package com.waataja.anochat;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

import javax.swing.*;

public class ServerGUI extends AnoChatGUI {
	
	private AnoChatServer server;
	
	private JButton refreshButton;
	private JButton trollButton;
	private JTextArea infoArea;

	public ServerGUI(AnoChatServer server) {
		super(server);
		this.server = server;
		trollButton = new JButton("troll");
		trollButton.addActionListener(this);
		refreshButton = new JButton("refresh");
		refreshButton.addActionListener(this);
		infoArea = new JTextArea();
		//rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.PAGE_AXIS));
		rightPanel.setLayout(new BorderLayout());
		rightPanel.add(BorderLayout.NORTH, refreshButton);
		rightPanel.add(BorderLayout.CENTER, infoArea);
		rightPanel.add(BorderLayout.SOUTH, trollButton);
		frame.add(BorderLayout.EAST, rightPanel);
	}
	
	public void updateDisplay(String info) {
		String serverInfo = "";
		String clientTotalString = "Total clients: " + Integer.toString(server.getAmountOfClients());
		serverInfo += clientTotalString + "\n";
		String[] lines = info.split("\n");
		for (String line : lines) {
			String[] words = line.split(" ");
			if (words.length >= 2) {
				try {
					int chatRoomNumber = Integer.parseInt(words[0]);
					int amountOfClients = Integer.parseInt(words[1]);
					String labelString = "Chat room " + chatRoomNumber + ": " + amountOfClients;

					serverInfo += labelString + "\n";
				} catch (NumberFormatException e) {
					
				}
			}
		}
		infoArea.setText(serverInfo);
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		super.actionPerformed(arg0);
		if (arg0.getSource() == refreshButton) {
			updateDisplay(server.getChatRoomInfo());
		} else if (arg0.getSource() == trollButton) {
			//server.kickRandomClient();
			server.executeCommand("troll");
		}
	}
}
