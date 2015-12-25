package com.vipkid.util;

import java.util.Random;

public class TextUtils {
	public static final String SEPERATOR = " - ";
	public static final String SPACE = " ";
	public static final String TAB = "\t";
	public static final String LINE = "\n";
	public static final String NONE = "";
	public static final String COLON = ":";
	public static final String SEMICOLON = ";";
	public static final String COMMA = ",";
	public static final String DOT = ".";
	public static final String LFET_SLASH = "/";
	public static final String RIGHT_SLASH = "\\";

	public static boolean isEmpty(String string) {
		if (string == null || string.equals(NONE)) {
			return true;
		} else {
			return false;
		}
	}
	
	public static String generateRandomNumber(int n){
		Random rd = new Random();
		boolean[] isExists = new boolean[10];
		StringBuilder sb = new StringBuilder();
		int randomNumber = 0;
		for (int i=0; i<n; i++){
			do{
				randomNumber = rd.nextInt(10);
			}while(isExists[randomNumber]);
			isExists[randomNumber] = true;
			sb.append(randomNumber);
		}
		return sb.toString();
	}
	
	public static String removeEnter(String string){
		if(string != null) {
			return string.replace(TextUtils.LINE, TextUtils.NONE);
		}else {
			return null;
		}
	}
}
