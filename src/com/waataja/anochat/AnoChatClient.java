package com.waataja.anochat;

import java.net.*;
import java.util.*;
import java.io.*;

import com.waataja.anochat.AnoChatServer.ServerCommand;

public class AnoChatClient implements TextUser {
	
	private TextHandler textHandler;
	
	public static final String SERVER_IP = "127.0.0.1";
	public static final int SERVER_PORT = 2000;
	
	private TextConnection connection;
	
	private HashMap<String, ClientCommand> commands;
	
	interface ClientCommand {
		void performCommand(AnoChatClient client, String[] args);
	}
	
	private void addCommand(String commandString, ClientCommand commandAction) {
		commands.put(commandString, commandAction);
	}
	
	private void addDefaultCommands() {
		addCommand("exit", (AnoChatClient client, String[] args) -> {
			if (connection != null) {
				connection.close();
			}
			System.exit(0);
			});
		addCommand("connect", (AnoChatClient client, String[] args) -> {
			try {
				connection = new TextConnection(SERVER_IP, SERVER_PORT);
			} catch (ConnectException e) {
				printMessage("Connection to server failed.");
			} catch (UnknownHostException e) {
				printMessage("Server ip could not be resolved.");
			}
		});
		addCommand("disconnect", (AnoChatClient client, String[] args) -> {
			this.closeConnection();
		});
	}
	
	public boolean getIsConnectedToServer() {
		return (this.connection != null && this.connection.getHasGoodConnection());
	}
	
	public void closeConnection() {
		if (this.getIsConnectedToServer()) {
			this.connection.close();
			printMessage("Closed connection to server.");
		} else {
			printMessage("Already disconnected, can't disconnect.");
		}
	}
	
	public void executeCommand(String message) {
		if (message != null) {
			String[] words = message.split(" ");
			if (words.length > 0) {
				String command = words[0];
				String[] args = new String[words.length - 1];
				for (int i = 0; i < args.length; i++) {
					args[i] = words[i + 1];
				}
				for (Map.Entry<String, ClientCommand> commandEntry : commands.entrySet()) {
					if (commandEntry.getKey().equals(command)) {
						commandEntry.getValue().performCommand(this, args);
					}
				}
			}
		}
	}
	
	public AnoChatClient(TextHandler handler) {
		this.textHandler = handler;
		try {
			connection = new TextConnection(SERVER_IP, SERVER_PORT);
		} catch (ConnectException e) {
			printMessage("Connection to server failed.");
		} catch (UnknownHostException e) {
			printMessage("Server ip could not be resolved.");
		}
		connection.setTextUser(this);
		commands = new HashMap<String, ClientCommand>();
		addDefaultCommands();
	}
	
	public AnoChatClient() {
		this.textHandler = new ConsoleTextHandler(this);
		try {
			connection = new TextConnection(SERVER_IP, SERVER_PORT);
		} catch (ConnectException e) {
			printMessage("Connection to server failed.");
		} catch (UnknownHostException e) {
			printMessage("Server ip could not be resolved.");
		}
		commands = new HashMap<String, ClientCommand>();
		addDefaultCommands();
	}

	@Override
	public TextHandler getTextHandler() {
		return this.textHandler;
	}

	@Override
	public synchronized void setTextHandler(TextHandler handler) {
		this.textHandler = handler;
	}

	/**
	 * method to be called when an input is gotten fromt the gui
	 */
	@Override
	public synchronized void onInput(String message) {
		if (message != null) {
			boolean isCommand = false;
			String[] words = message.split(" ");
			if (words.length > 0) {
				String command = words[0];
				String[] args = new String[words.length - 1];
				for (int i = 0; i < args.length; i++) {
					args[i] = words[i + 1];
				}
				for (Map.Entry<String, ClientCommand> commandEntry : commands.entrySet()) {
					if (commandEntry.getKey().equals(command)) {
						commandEntry.getValue().performCommand(this, args);
						isCommand = true;
					}
				}
			}
			if (!isCommand) {
				if (this.hasGoodConnection()) {
					sendMessageToServer(message);
				} else {
					printMessage("No connection to server, can't send message.");
				}
			}
		}
	}
	
	/**
	 * prints a message to the textHandler
	 * @param message the message to send
	 */
	public void printMessage(String message) {
		this.getTextHandler().printMessage(message);
	}
	
	public synchronized void processServerInput(String message) {
		printMessage(message);
	}
	
	private synchronized void sendMessageToServer(String message) {
		if (this.hasGoodConnection()) {
			connection.sendMessage(message);
		} else {
			printMessage("No connection to server, can't send message.");
		}
	}
	
	public void start() {
		String message;
		try {
			if (this.hasGoodConnection()) {
				while ((message = connection.waitForTextMessage()) != null) {
					processServerInput(message);
				}
				printMessage("Server disconnected, will not be able to send or receive messages.");
				connection.close();
			} else {
				printMessage("No connection to the server, not starting program.");
			}
		} catch (SocketException e) {
			printMessage("Server forcedly closed, cannot send or receive messages.");
			connection.close();
		}
	}
	
	public boolean hasGoodConnection() {
		return (connection != null && connection.getHasGoodConnection());
	}
	
	public void setConnection(TextConnection connection) {
		this.connection = connection;
	}
}
