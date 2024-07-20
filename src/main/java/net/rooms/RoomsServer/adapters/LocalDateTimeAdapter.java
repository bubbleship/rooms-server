package net.rooms.RoomsServer.adapters;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Allows serializing {@link LocalDateTime} objects into json.
 */
public class LocalDateTimeAdapter implements JsonSerializer<LocalDateTime> {
	@Override
	public JsonElement serialize(LocalDateTime src, Type typeOfSrc, JsonSerializationContext context) {
		return new JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
	}
}
