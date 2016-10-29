package com.waataja.anochat;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
	
	private File logFile;
	private PrintWriter writer;
	private boolean hasGoodWriter;
	private TextUser textUser;
	private boolean writeTime;
	private DateFormat dateFormat;
	
	public Logger(File logFile) {
		setLogFile(logFile);
		writeTime = true;
		dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
	}
	
	public Logger(File logFile, TextUser user) {
		this(logFile);
		this.textUser = user;
	}
	
	public Logger(String logFilePath) {
		this(new File(logFilePath));
	}
	
	public Logger(String logFilePath, TextUser user) {
		this(logFilePath);
		this.textUser = user;
	}
	
	public void setLogFile(File logFile) {
		hasGoodWriter = false;
		this.logFile = logFile;
		try {
			writer = new PrintWriter(new FileOutputStream(this.logFile));
			hasGoodWriter = true;
		} catch (FileNotFoundException e) {
			printMessage("Could not open file file " + logFile.getAbsolutePath() + " for logging.");
			hasGoodWriter = false;
		}
	}
	
	public void log(String logMessage) {
		String finalMessage = logMessage;
		if (hasGoodWriter) {
			if (this.writeTime) {
				Date currentDate = new Date();
				String dateString = dateFormat.format(currentDate);		
				finalMessage = dateString + ": " + finalMessage;
			}
			writer.println(finalMessage);
			writer.flush();
		} else {
			printMessage("No valid stream for logging, didn't log.");
		}
	}
	
	private void printMessage(String message) {
		if (textUser != null) {
			textUser.getTextHandler().printMessage(message);
		}
	}
	
	public void close() {
		hasGoodWriter = false;
		writer.close();
	}
	
	public boolean getHasGoodWriter() {
		return hasGoodWriter;
	}
	
	public boolean getWriteTime() {
		return this.writeTime;
	}
	
	public void setWriteTime(boolean writeTime) {
		this.writeTime = writeTime;
	}
}
