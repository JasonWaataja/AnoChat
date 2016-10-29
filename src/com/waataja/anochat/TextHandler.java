package com.waataja.anochat;

public interface TextHandler {
	public TextUser getTextUser();
	public void setTextUser(TextUser user);
	public void printMessage(String message);
}
