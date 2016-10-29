package com.waataja.anochat;

public class AnoChatClientGUIVersion extends AnoChatClient{

	public AnoChatClientGUIVersion() {
		super();
		this.setTextHandler(new ClientGUI(this));
	}
	
	@Override
	public void onInput(String message) {
		super.onInput(message);
	}

	public static void main(String[] args) {
		AnoChatClientGUIVersion program = new AnoChatClientGUIVersion();
		program.start();
	}
}