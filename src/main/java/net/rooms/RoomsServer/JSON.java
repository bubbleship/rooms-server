package net.rooms.RoomsServer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.rooms.RoomsServer.adapters.LocalDateTimeAdapter;

import java.time.LocalDateTime;

/**
 * A utility class that acts like a shell for the gson library.
 * Holds a single static instance of a {@link Gson} object that can be used to convert objects to
 * and from JSON strings.
 */
public class JSON {
	private static final Gson gson = new GsonBuilder()
			.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
			.create();

	/**
	 * Serializes the given object into a JSON string.
	 *
	 * @param src The object to serialize.
	 * @return A JSON string representation of the given object.
	 */
	public static String toJson(Object src) {
		return gson.toJson(src);
	}

	/**
	 * Deserializes the given JSON into an object with the specified type.
	 *
	 * @param json The JSON string to deserialize.
	 * @param type The type of the object to deserialize the given JSON to.
	 * @return An object of the specified type from the JSON.
	 */
	public static <T> T fromJson(String json, Class<T> type) {
		return gson.fromJson(json, type);
	}
}
