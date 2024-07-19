package net.rooms.RoomsServer.room;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.AllArgsConstructor;
import net.rooms.RoomsServer.adapters.LocalDateTimeAdapter;
import net.rooms.RoomsServer.user.User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class RoomService {

	private final RoomRepository roomRepository;

	/**
	 * Handles the creation of a new room.
	 * Ensures none of its fields contains problematic characters.
	 * Prepares a unique identifier for the room.
	 * Sets the time of the room creation.
	 * Writes the room to the database and set the creator as a participant in the room.
	 *
	 * @param request Configurations set by the user about the room to create.
	 * @param user    The currently logged-in user (the creator of the room).
	 * @return A string with an error message in case the creation failed. Otherwise, "success".
	 */
	public String create(CreateRequest request, User user) {
		if (request.title().contains("\"")) return "Invalid character: \"";
		if (request.password().contains("\"")) return "Invalid character: \"";

		long roomID = roomRepository.lastID() + 1;
		Room room = new Room(roomID, request.title(), request.isPrivate(), request.password(), user.username(), LocalDateTime.now());
		if (!roomRepository.create(room)) return "Room creation failed on create";
		if (!roomRepository.joinUser(roomID, user.username())) {
			roomRepository.delete(roomID); // Cleans the room in case the join table could not be updated.
			return "Room creation failed on join";
		}
		return "success";
	}

	public String list(User user) {
		Gson gson = new GsonBuilder()
				.setPrettyPrinting()
				.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
				.create();
		return gson.toJson(roomRepository.listByUser(user.username()));
	}
}