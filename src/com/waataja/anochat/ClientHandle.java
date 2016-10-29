package com.waataja.anochat;

import java.net.*;
import java.util.Random;
import java.io.*;

public class ClientHandle {
	private int handle;
	private TextConnection connection;
	private ChatRoom room;
	private Thread receiverThread;
	
	public ClientHandle(TextConnection connection, ChatRoom room, int handle) {
		this.connection = connection;
		this.room = room;
		this.handle = handle;
		
		receiverThread = new Thread(new Receiver());
		receiverThread.start();
	}
	
	public ClientHandle(TextConnection connection, ChatRoom room) {
		this(connection, room, (new Random()).nextInt());
	}
	
	public class Receiver implements Runnable {
		public void run() {
			String message;
			try {
				while ((message = connection.waitForTextMessage()) != null) {
					processMessage(message);
				}
				onClientDisconnect();
			} catch (SocketException e) {
				onClientDisconnect();
			}
		}
		
		public void onClientDisconnect() {
			getServer().printMessage("Client " + handle + " dropped.");
			room.remove(ClientHandle.this);
			if (room.isEmpty()) {
				getServer().removeClient(ClientHandle.this);
			}
		}
	}
	
	private void processMessage(String message) {
		getServer().processMessageFromClientInRoom(message, getChatRoom(), this);
	}
	
	public void sendMessageToClient(String message) {
		connection.sendMessage(message);
	}
	
	public int getHandle() {
		return this.handle;
	}
	
	public void setHandle(int handle) {
		this.handle = handle;
	}
	
	public ChatRoom getChatRoom() {
		return this.room;
	}
	
	public AnoChatServer getServer() {
		return room.getServer();
	}
	
	public TextConnection getConnection() {
		return this.connection;
	}
	
	public void close() {
		this.connection.close();
	}
}
