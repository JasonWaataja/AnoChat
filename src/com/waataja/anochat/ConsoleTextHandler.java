package com.waataja.anochat;

import java.util.Scanner;

public class ConsoleTextHandler implements TextHandler {
	
	private TextUser textUser;
	private Scanner scanner;
	private Thread receiverThread;
	
	public ConsoleTextHandler(TextUser user) {
		this.textUser = user;
		scanner = new Scanner(System.in);
		receiverThread = new Thread(new Receiver());
		receiverThread.start();
	}

	public TextUser getTextUser() {
		return this.textUser;
	}

	public void setTextUser(TextUser user) {
		this.textUser = user;
	}

	public void printMessage(String message) {
		System.out.println(message);
	}

	public class Receiver implements Runnable {

		@Override
		public void run() {
			String input = null;
			while ((scanner != null) && (input = scanner.nextLine()) != null) {
				getTextUser().onInput(input);
			}
		}
		
	}
}
