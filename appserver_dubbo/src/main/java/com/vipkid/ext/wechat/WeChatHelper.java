package com.vipkid.ext.wechat;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.vipkid.util.Configurations.WeChat;


public class WeChatHelper {
	
	public static final String SHA1 = "SHA-1";
	public static final String MD5 = "MD5";

	public static String getWeChatRequriedFormat(String signature, String timestamp, String nonce){
		List<String> list = new ArrayList<String>(){
			private static final long serialVersionUID = -4406142727462712424L;
			public String toString() {
				return get(0) + get(1) + get(2);
			}
		};
		list.add(WeChat.TOKEN);
		list.add(timestamp);
		list.add(nonce);
		Collections.sort(list);
		return encode(list.toString(), SHA1);
	}

	public static String encode(String strSrc, String encodeType) {
		MessageDigest md = null;
		String strDes = null;
		byte[] bt = strSrc.getBytes();
		try {
			if (encodeType == null || "".equals(encodeType)){
				encodeType = MD5;
			}
			md = MessageDigest.getInstance(encodeType);
			md.update(bt);
			strDes = bytes2Hex(md.digest());	
		} catch (NoSuchAlgorithmException e) {
				return strSrc;
		}
		return strDes;
	}


	public static String bytes2Hex(byte[] bts) {
		String des = "";
		String tmp = null;
		for (int i = 0; i < bts.length; i++) {
			tmp = (Integer.toHexString(bts[i] & 0xFF));
			if (tmp.length() == 1) {
				des += "0";
			}
			des += tmp;
		}
		return des;
	}
	

	public static String getWeChatFormattedDate(){
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
		return df.format(new Date());
	}

}
