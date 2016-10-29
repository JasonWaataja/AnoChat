package com.waataja.anochat;

import java.io.IOException;
import java.net.*;
import java.util.*;

public class AnoChatServer implements TextUser {
	
	public static final int SERVER_PORT = 2000;
	public static final int MAX_CLIENTS = 30;
	public static final String SERVER_LOG_PATH = "logs/serverlog.txt";
	
	protected ServerSocket serverSocket;
	protected ArrayList<ChatRoom> chatRooms;
	
	protected TextHandler textHandler;
	protected Logger logger;
	protected boolean shouldLog;
	
	protected boolean shouldEndServer;
	
	protected HashMap<String, ServerCommand> commands;
	
	//null if not if in a room
	protected ChatRoom currentRoom;
	
	interface ServerCommand {
		void performCommand(AnoChatServer server, String[] args);
	}
	
	protected void addCommand(String commandString, ServerCommand commandAction) {
		commands.put(commandString, commandAction);
		log("Added command: " + commandString);
	}
	
	public void addDefaultCommands() {
		addCommand("exit", (AnoChatServer server, String[] args) -> {
			for (ClientHandle client : getAllClients()) {
				client.sendMessageToClient("Server shutting down.");
				client.close();
				log("Closed client " + client.getHandle());
			}
			log("Closing server.");
			System.exit(0);});
		
		addCommand("log", (AnoChatServer server, String[] args) -> {
			String logMessage = "";
			for (String word : args) {
				logMessage += word;
			}
			log(logMessage);
		});
		
		addCommand("kick", (AnoChatServer server, String[] args) -> {
			if (args.length < 1) {
				printMessage("Can't kick because not enough arguments.");
				log("Failed to kick because not enough arguments.");
			} else {
				try {
					int asInt = Integer.parseInt(args[0]);
					ClientHandle client = getClientByHandle(asInt);
					if (client != null) {
						ChatRoom room = client.getChatRoom();
						room.remove(client);
						client.close();
						printMessage("Kicked " + asInt);
						log("Kicked client with handle " + asInt);
						if (room.isEmpty()) {
							this.removeClient(client);
						}
					} else {
						printMessage("Can't kick because client can't be found.");
						log("Attempted to kick but that handle couldn't be found.");
					}
				} catch (NumberFormatException e) {
					printMessage("Can't kick because int not entered.");
					log("Tried to kick but argument wasn't an int.");
				}
			}
		});
		
		addCommand("cr", (AnoChatServer server, String[] args) -> {
			if (args.length > 0) {
				if (args[0].equals("null") || args[0].equals("none")) {
					setCurrentRoom(null);
				} else {
					try {
						int asInt = Integer.parseInt(args[0]);
						if (asInt >= 0 && asInt < chatRooms.size()) {
							setCurrentRoom(chatRooms.get(asInt));
							printMessage("Changed room to " + asInt);
							log("Changed room to " + asInt);
						} else {
							printMessage("Can't change room, room not in range.");
						}
					} catch (NumberFormatException e) {
						printMessage("Can't change room, not and integer.");
					}
				}
				
			} else {
				setCurrentRoom(null);
				printMessage("No longer in a chat room.");
				log("No longer in a chat room.");
			}
		});
		
		addCommand("clear", (AnoChatServer server, String[] args) -> {
			if (textHandler instanceof AnoChatGUI) {
				AnoChatGUI asGUI = (AnoChatGUI) textHandler;
				if (asGUI != null) {
					asGUI.clearOutputArea();
					log("Server display cleared.");
				}
			} else {
				printMessage("Not a gui, can't clear.");
			}
		});
		
		addCommand("info", (AnoChatServer server, String[] args) -> {
			if (args.length > 0) {
				String item = args[0];
				if (item.equals("room")) {
					if (args.length > 1) {
						try {
							int asInt = Integer.parseInt(args[1]);
							if (asInt >= 0 && asInt < chatRooms.size()) {
								printMessage(getInfoForChatRoom(chatRooms.get(asInt)));
							} else {
								printMessage("Can't print info, room not in range.");
							}
						} catch (NumberFormatException e) {
							printMessage("Can't print info, arg not int.");
						}
					} else {
						printMessage("Can't print info for room, supply and argument");
					}
				} else if (item.equals("client")) {
					if (args.length > 1) {
						try {
							int asInt = Integer.parseInt(args[1]);
							ClientHandle client = this.getClientByHandle(asInt);
							if (client != null) {
								printMessage(getInfoForClient(client));
							} else {
								printMessage("Can't print info, client can't be found");
							}
						} catch (NumberFormatException e) {
							printMessage("Can't print info, not an int.");
						}
					} else {
						printMessage("Can't print info, supply an argument.");
					}
				}
			} else {
				for (ChatRoom room : chatRooms) {
					printMessage(getInfoForChatRoom(room));
				}
			}
		});
		
		addCommand("troll", (AnoChatServer server, String[] args) -> {
			kickRandomClient();
		});
		
		log("Added default commands.");
	}
	
	public String getInfoForClient(ClientHandle client) {
		String info = "";
		boolean hasGoodConnection = client.getConnection().getHasGoodConnection();
		if (hasGoodConnection) {
			info += "\thas good connection." + "\n";
			String ip = client.getConnection().getIP();
			int port = client.getConnection().getPort();
			info += "\tip: " + ip + "\n";
			info += "\tport: " + port + "\n";
			int roomNumber = getChatRoomNumber(client.getChatRoom());
			info += "\tchat room: " + roomNumber + "\n";
		} else {
			info += "\tdoesn't have good connection." + "\n";
		}
		return info;
	}
	
	public AnoChatServer(TextHandler handler) {
		this.textHandler = handler;
		logger = new Logger(SERVER_LOG_PATH, this);
		shouldLog = true;
		log("Created logger, starting to log.");
		chatRooms = new ArrayList<ChatRoom>();
		chatRooms.add(new ChatRoom(this));
		initNetworking();
		shouldEndServer = false;
		commands = new HashMap<String, ServerCommand>();
		addDefaultCommands();
		log("Finished server constructor.");
	}
	
	public String getInfoForChatRoom(ChatRoom room) {
		String info = "clients: " + room.getClientListSize() + "\n";
		info += "room number: " + getChatRoomNumber(room) + "\n";
		int index = 0;
		for (ClientHandle client : room.getClientList()) {
			info += "\tclient: " + index + " " + client.getHandle() + "\n";
			index++;
		}
		return info;
	}
	
	public void kickRandomClient() {
		if (this.getAmountOfClients() > 0) {
			int randomIndex = (int) (Math.random() * this.getAmountOfClients());
			ClientHandle client = this.getAllClients().get(randomIndex);
			ChatRoom room = client.getChatRoom();
			if (room != null) {
				client.sendMessageToClient("Trollollollolloll");
				client.close();
				room.remove(client);
			}
			removeClient(client);
		} else {
			printMessage("Can't kick, not enough clients.");
		}
	}
	
	public AnoChatServer() {
		this.textHandler = new ConsoleTextHandler(this);
		logger = new Logger(SERVER_LOG_PATH, this);
		shouldLog = true;
		log("Created logger, starting to log.");
		chatRooms = new ArrayList<ChatRoom>();
		chatRooms.add(new ChatRoom(this));
		initNetworking();
		shouldEndServer = false;
		commands = new HashMap<String, ServerCommand>();
		addDefaultCommands();
		log("Finished server constructor.");
	}
	
	protected void initNetworking() {
		setPort(SERVER_PORT);
	}
	
	protected void setPort(int port) {
		try {
			serverSocket = new ServerSocket(SERVER_PORT);
			log("Port set to " + port);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public TextHandler getTextHandler() {
		return this.textHandler;
	}

	@Override
	public void setTextHandler(TextHandler handler) {
		this.textHandler = handler;
	}
	
	private void printToAllClientsInRoom(String message, ChatRoom room) {
		if (room != null) {
			for (ClientHandle client : room.getClientList()) {
				client.sendMessageToClient(message);
			}
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
				for (Map.Entry<String, ServerCommand> commandEntry : commands.entrySet()) {
					if (commandEntry.getKey().equals(command)) {
						commandEntry.getValue().performCommand(this, args);
						log("performed command: " + message);
					}
				}
			}
		}
	}

	@Override
	public  synchronized void onInput(String message) {
		if (message != null) {
			boolean isCommand = false;
			String[] words = message.split(" ");
			if (words.length > 0) {
				String command = words[0];
				String[] args = new String[words.length - 1];
				for (int i = 0; i < args.length; i++) {
					args[i] = words[i + 1];
				}
				for (Map.Entry<String, ServerCommand> commandEntry : commands.entrySet()) {
					if (commandEntry.getKey().equals(command)) {
						commandEntry.getValue().performCommand(this, args);
						isCommand = true;
						log("performed command: " + message);
					}
				}
			}
			if (!isCommand) {
				if (currentRoom != null) {
					printToAllClientsInRoom("From server: " + message, currentRoom);
				} else {
					printToAllClients("From server: " + message);
				}
			}
		}
	}
	
	private void setCurrentRoom(ChatRoom currentRoom) {
		this.currentRoom = currentRoom;
	}
	
	public void printMessage(String message) {
		this.getTextHandler().printMessage(message);
	}

	public void start() {
		log("Starting server.");
		while (!this.shouldEndServer) {
			try {
				log("Waiting for a connection.");
				printMessage("Waiting for a connection.");
				Socket clientSocket = serverSocket.accept();
				log("Connected to " + clientSocket.getInetAddress().getHostAddress());
				printMessage("Made a connection.");
				TextConnection clientConnection = new TextConnection(clientSocket);
				clientConnection.setTextUser(this);
				addClientWithConnection(clientConnection);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public synchronized int getAmountOfClients() {
		int clientAmount = 0;
		for (ChatRoom room : chatRooms) {
			clientAmount += room.getClientListSize();
		}
		return clientAmount;
	}
	
	public synchronized boolean isFull() {
		return (getAmountOfClients() >= MAX_CLIENTS);
	}
	
	protected synchronized void addClientWithConnection(TextConnection clientConnection) {
		
		/*if (chatRooms.size() > 1) {
			for (int i = 0; i < chatRooms.size();) {
				if (chatRooms.get(i).isEmpty() && chatRooms.size() > 1) {
					chatRooms.remove(i);
				} else {
					i++;
				}
			}
		}*/
		
		if (!isFull()) {
			boolean newChatRoomRequired = true;
			for (ChatRoom room : chatRooms) {
				if (!room.isFull()) {
					room.addClient(new ClientHandle(clientConnection, room));
					log("Added client. Put in room: " + getChatRoomNumber(room));
					newChatRoomRequired = false;
					printToAllClientsInRoom("Client joined.", room);
					break;
				}
			}
			if (newChatRoomRequired) {
				ChatRoom newRoom = new ChatRoom(this);
				log("Created new chat room.");
				chatRooms.add(newRoom);
				newRoom.addClient(new ClientHandle(clientConnection, newRoom));
				printToAllClientsInRoom("Client joined.", newRoom);
				log("Added client to new room.");
			}
		} else {
			clientConnection.sendMessage("The server is full, connection refused.");
		}
	}
	
	protected synchronized ArrayList<ClientHandle> getAllClients() {
		ArrayList<ClientHandle> allClients = new ArrayList<ClientHandle>();
		for (ChatRoom room : chatRooms) {
			allClients.addAll(room.getClientList());
		}
		return allClients;
	}
	
	/*protected synchronized void printToAllChatRooms(String message) {
		for (ChatRoom room : chatRooms) {
			room.printToAll(message);
		}
	}*/
	
	protected synchronized void printToAllClients(String message) {
		ArrayList<ClientHandle> allClients = getAllClients();
		for (ClientHandle client : allClients) {
			client.sendMessageToClient(message);
		}
		log("Message sent to all clients: " + message);
	}
	
	public synchronized int getChatRoomNumber(ChatRoom room) {
		return chatRooms.indexOf(room);
	}
	
	protected Kappa kappa = new Kappa();
	public synchronized void processMessageFromClientInRoom(String message, ChatRoom room, ClientHandle client) {
		String serverDisplay = "From chat room " + getChatRoomNumber(room) + " from client " + client.getHandle() + ": " + message;
		if (room == currentRoom) {
			printMessage(kappa.parse(serverDisplay));
		}
		for (ClientHandle otherClient : room.getClientList()) {
			//if (otherClient != client) {
				otherClient.sendMessageToClient(kappa.parse(message));
			//}
		}
		log("Received message " + message + " from " + client.getHandle() + " in room " + getChatRoomNumber(room));
	}
	
	//!!!!CURRENTLY, THIS IS NOT THE METHOD THAT ACTUALLY REMOVES ANYTHING FROM THE ARRAY, THAT'S DONE IN CLIENTHANDLE
	public synchronized void removeClient(ClientHandle client) {
		/*
		for (ChatRoom room : chatRooms) {
			ArrayList<ClientHandle> clientsInRoom = room.getClientList();
			for (ClientHandle handle : clientsInRoom) {
				if (client == handle) {
					clientsInRoom.remove(client);
				}
			}
		}*/
		log("Removed client: " + client.getHandle());
		for (int i = 0; i < chatRooms.size();) {
			if (chatRooms.get(i).isEmpty() && chatRooms.size() > 1) {
				chatRooms.remove(i);
				log("Removed chat room at index " + i);
			} else {
				i++;
			}
		}
	}
	
	protected void log(String message) {
		if (logger != null && this.shouldLog) {
			logger.log(message);
		}
	}
	
	public boolean getShouldLog() {
		return this.shouldLog;
	}
	
	public void setShouldLog(boolean shouldLog) {
		this.shouldLog = shouldLog;
	}
	
	protected ClientHandle getClientByHandle(int handle) {
		for (ChatRoom room : chatRooms) {
			ClientHandle inRoom = room.getClientByHandle(handle);
			if (inRoom != null) {
				return inRoom;
			}
		}
		return null;
	}
	
	//returns string in the form of "[chat room number] [amount of clients]" one line for each chat room
	public String getChatRoomInfo() {
		String displayInfo = "";
		for (ChatRoom room : chatRooms) {
			displayInfo += (getChatRoomNumber(room) + " " + room.getClientListSize() + "\n");
		}
		return displayInfo;
	}
}
