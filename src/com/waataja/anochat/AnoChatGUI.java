package com.waataja.anochat;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

public abstract class AnoChatGUI implements ActionListener, TextHandler {
	
	public static final int TEXT_WIDTH = 20;
	public static final int TEXT_HEIGHT = 10;
	public static final int FRAME_WIDTH_PIXELS = 400;
	public static final int FRAME_HEIGHT_PIXELS = 300;
	public static final String WINDOW_NAME = "AnoChat Program";
	
	protected JFrame frame;
	protected JTextPane outputArea;
	protected JTextField inputArea;
	protected JScrollPane scrollArea;
	protected JPanel topPanel;
	protected JPanel rightPanel;
	
	protected TextUser textUser;
	
	public void clearOutputArea() {
		outputArea.setText("");
	}
		
	public AnoChatGUI(TextUser textUser) {
		this.textUser = textUser;
		
		inputArea = new JTextField(TEXT_WIDTH);
		inputArea.setEditable(true);
		inputArea.addActionListener(this);
		
		outputArea = new JTextPane();
		outputArea.setEditable(false);
		
		scrollArea = new JScrollPane(outputArea);
		scrollArea.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollArea.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		DefaultCaret caret = (DefaultCaret) outputArea.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		
		topPanel = new JPanel();
		rightPanel = new JPanel();
				
		frame = new JFrame();
		frame.setPreferredSize(new Dimension(FRAME_WIDTH_PIXELS, FRAME_HEIGHT_PIXELS));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(BorderLayout.SOUTH, inputArea);
		frame.getContentPane().add(BorderLayout.CENTER, scrollArea);
		//frame.getContentPane().add(BorderLayout.NORTH, topPanel);
		//frame.getContentPane().add(BorderLayout.EAST, rightPanel);
		frame.pack();
		frame.setTitle(WINDOW_NAME);
		frame.setVisible(true);
	}
	public HashMap<String, ImageIcon> kappas = new HashMap<String, ImageIcon>();
	public static final String iconAuthority = "http://noop.us:2351/";
	public void printMessage(String message) {
		String[] spl = message.split(" ");
		for(int i = 0; i < spl.length; ++i){
			if(spl[i].startsWith(String.valueOf((char) 27))){
				if(!kappas.keySet().contains(spl[i])){
					try {
						kappas.put(spl[i], new ImageIcon(new URL(iconAuthority + spl[i].substring(1))));
					} catch (MalformedURLException e) {
						e.printStackTrace();
					}
				}
				outputArea.insertIcon(kappas.get(spl[i]));
				
			}else{
				try {
					outputArea.getStyledDocument().insertString(outputArea.getStyledDocument().getLength(), spl[i], null);
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
			}
			try {
				outputArea.getStyledDocument().insertString(outputArea.getStyledDocument().getLength(), " ", null);
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		}
		try {
			outputArea.getStyledDocument().insertString(outputArea.getStyledDocument().getLength(), "\n", null);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}
	
	public void actionPerformed(ActionEvent arg0) {
		if (arg0.getSource() == inputArea) {
			String input = inputArea.getText();
			inputArea.setText("");
			this.getTextUser().onInput(input);
		}
	}
	
	@Override
	public TextUser getTextUser() {
		return this.textUser;
	}
	
	public void setTextUser(TextUser user) {
		this.textUser = user;
	}
}
