package com.waataja.anochat;

import java.net.*;
import java.io.*;

public class TextConnection {
	
	private Socket connection;
	private boolean hasGoodConnection;
	private String currentIP;
	private int currentPort;
	private BufferedReader reader;
	private PrintWriter writer;
	private TextUser textUser;
	
	public TextConnection(String ip, int port) throws UnknownHostException, ConnectException {
		setConnection(ip, port);
	}
	
	public TextConnection(Socket connection) {
		setConnection(connection);
	}
	
	public void setTextUser(TextUser user) {
		this.textUser = user;
	}
	
	public void printMessage(String message) {
		if (textUser != null) {
			textUser.getTextHandler().printMessage(message);
		}
	}
	
	public void setConnection(Socket connection) {
		this.connection = connection;
		this.currentIP = connection.getInetAddress().getHostAddress();
		this.currentPort = connection.getPort();
		try {
			reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			writer = new PrintWriter(connection.getOutputStream());
			hasGoodConnection = true;
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void setConnection(String ip, int port) throws ConnectException, UnknownHostException {
		hasGoodConnection = false;
		this.currentIP = ip;
		this.currentPort = port;
		try {
			connection = new Socket(currentIP, currentPort);
			reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			writer = new PrintWriter(connection.getOutputStream());
			hasGoodConnection = true;
		} catch (UnknownHostException e) {
			throw e;
		} catch (ConnectException e) {
			throw e;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String waitForTextMessage() throws SocketException {
		String message = null;
		try {
			message = reader.readLine();
		} catch (SocketException e) {
			throw e;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return message;
	}
	
	public void sendMessage(String message) {
		writer.println(message);
		writer.flush();
	}
	
	public String getIP() {
		return this.currentIP;
	}
	
	public int getPort() {
		return this.currentPort;
	}
	
	public boolean getHasGoodConnection() {
		return this.hasGoodConnection;
	}
	
	public void close() {
		try {
			this.hasGoodConnection = false;
			this.connection.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			this.printMessage("No connection to close.");
		}
	}
}
