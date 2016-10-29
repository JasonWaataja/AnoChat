package com.waataja.anochat;

public class AnoChatServerGUIVersion extends AnoChatServer {
	
	private ServerGUI gui;
	
	public AnoChatServerGUIVersion() {
		super();
		gui = new ServerGUI(this);
		this.setTextHandler(gui);
	}

	public static void main(String[] args) {
		AnoChatServerGUIVersion program = new AnoChatServerGUIVersion();
		program.start();
	}
	
	@Override
	public void onInput(String message) {
		super.onInput(message);
		this.printMessage(message);
	}

	@Override
	public void printMessage(String message) {
		super.printMessage(message);
		gui.updateDisplay(getChatRoomInfo());
	}
}
