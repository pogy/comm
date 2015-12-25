package com.vipkid.model.json.gson;

import java.util.Date;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonManager {
	private static GsonManager instance;
	private Gson gson;
	
	private GsonManager(){
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(Date.class, new DateTypeAdapter());
		gsonBuilder.registerTypeAdapter(CharSequence.class, new CharSequenceTypeAdapter());
		gson = gsonBuilder.create();
	}
	
	public static GsonManager getInstance(){
		if(instance == null){
			instance = new GsonManager();
		}
		return instance;
	}
	
	public Gson getGson(){
		return gson;
	}
}
