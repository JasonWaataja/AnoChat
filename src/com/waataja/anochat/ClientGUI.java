package com.waataja.anochat;

import javax.swing.*;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;

public class ClientGUI extends AnoChatGUI {
	
	protected JButton exitButton;
	protected JButton connectToServerButton;
	protected JButton disconnectButton;
	
	private AnoChatClient client;

	public ClientGUI(AnoChatClient client) {
		super(client);
		this.client = client;
		
		exitButton = new JButton("exit");
		exitButton.addActionListener(this);
		connectToServerButton = new JButton("connect to server");
		connectToServerButton.addActionListener(this);
		disconnectButton = new JButton("disconnect");
		disconnectButton.addActionListener(this);
		
		
		rightPanel = new JPanel();
		//rightPanel.setLayout(new BorderLayout());
		rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.PAGE_AXIS));
		rightPanel.add(exitButton);
		rightPanel.add(connectToServerButton);
		rightPanel.add(disconnectButton);
		
		this.frame.add(BorderLayout.EAST, rightPanel);
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		super.actionPerformed(arg0);
		
		if (arg0.getSource() == exitButton) {
			client.executeCommand("exit");
		} else if (arg0.getSource() == connectToServerButton) {
			if (client.getIsConnectedToServer()) {
				printMessage("Already connected to server.");
			} else {
				client.executeCommand("connect");
				if (client.getIsConnectedToServer()) {
					connectToServerButton.setText("connected");
				} else {
					connectToServerButton.setText("connect to server");
				}
			}
		} else if (arg0.getSource() == this.disconnectButton) {
			this.client.closeConnection();
			connectToServerButton.setText("connect to server");
		}
	}
}
