package com.waataja.anochat;

import java.util.HashMap;
public class Kappa {
	public Kappa(){
		kappalist.put("Kappa", "kappa.png");
		kappalist.put("FailFish", "failfish.png");
		kappalist.put("BibleThump", "biblethump.png");
	}
	public HashMap<String, String> kappalist = new HashMap<String, String>();
	public String parse(String s){
		String[] spl = s.split(" ");
		for(int i = 0; i < spl.length; ++i){
			if(kappalist.keySet().contains(spl[i])){
				spl[i] = String.valueOf((char) 27) + kappalist.get(spl[i]);
			}
		}
		return join(spl);
	}
	public static String join(String... str){
		StringBuilder sb = new StringBuilder();
		for(String s : str){
			sb.append(s);
			sb.append(" ");
		}
		return sb.toString();
	}
}
