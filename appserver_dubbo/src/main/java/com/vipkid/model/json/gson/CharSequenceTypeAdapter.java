package com.vipkid.model.json.gson;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class CharSequenceTypeAdapter implements JsonSerializer<CharSequence>, JsonDeserializer<CharSequence> {

	@Override
	public JsonElement serialize(CharSequence charSequence, Type type, JsonSerializationContext context) {
		return new JsonPrimitive(charSequence.toString());
	}
	
	@Override
	public CharSequence deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
		return jsonElement.getAsString();
	}

}
