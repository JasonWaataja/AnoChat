package com.waataja.anochat;

import java.util.*;
import java.net.*;

public class ChatRoom {
	/**
	 * the maximum number of clients per chat room.
	 */
	public static final int MAX_CLIENTS = 2;
	
	/**
	 * the list of clients in the room
	 */
	private ArrayList<ClientHandle> clientList;
	
	private AnoChatServer server;
	
	public ChatRoom(AnoChatServer server) {
		this.server = server;
		clientList = new ArrayList<ClientHandle>();
	}
	
	public void printToAll(String message) {
		for (ClientHandle client : clientList) {
			client.sendMessageToClient(message);
		}
	}

	public void remove(ClientHandle c){
		clientList.remove(c);
	}

	public boolean isFull() {
		return (getClientListSize() >= MAX_CLIENTS);
	}
	
	public int getClientListSize() {
		return clientList.size();
	}
	
	public void addClient(ClientHandle client) {
		client.setHandle(generateNewHandle());
		clientList.add(client);
	}
	
	public boolean isEmpty() {
		return (clientList.isEmpty());
	}
	
	public ClientHandle getClientByHandle(int handle) {
		for (ClientHandle client : clientList) {
			if (client.getHandle() == handle) {
				return client;
			}
		}
		return null;
	}
	
	/**
	 * randomly generates handles until an unused one is found
	 * @return a new handle
	 */
	private int generateNewHandle() {
		Random rand = new Random();
		int handle;
		boolean alreadyExists;
		do {
			handle = rand.nextInt();
			alreadyExists = false;
			for (ClientHandle client : clientList) {
				if (client.getHandle() == handle) {
					alreadyExists = true;
				}
				//break //would make the program go faster, but bad practice.
			}
		} while (alreadyExists);
		return handle;
	}
	
	public ArrayList<ClientHandle> getClientList() {
		return clientList;
	}
	
	public void processMessageFromClient(String message, ClientHandle client) {
		server.processMessageFromClientInRoom(message,  this, client);
	}
	
	public AnoChatServer getServer() {
		return this.server;
	}
}
