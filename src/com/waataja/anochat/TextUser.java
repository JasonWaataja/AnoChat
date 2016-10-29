package com.waataja.anochat;

public interface TextUser {
	public TextHandler getTextHandler();
	public void setTextHandler(TextHandler handler);
	public void onInput(String message);
}
