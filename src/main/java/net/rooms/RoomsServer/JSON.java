package net.rooms.RoomsServer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.rooms.RoomsServer.adapters.LocalDateTimeAdapter;

import java.time.LocalDateTime;

public class JSON {
	private static final Gson gson = new GsonBuilder()
			.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
			.create();

	public static String toJson(Object src) {
		return gson.toJson(src);
	}

	public static <T> T fromJson(String json, Class<T> type) {
		return gson.fromJson(json, type);
	}
}
