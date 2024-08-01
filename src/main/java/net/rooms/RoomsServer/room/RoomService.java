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
		if (request.password().contains("\"")) return "Invalid character: \"";

		long roomID = roomRepository.lastID() + 1;
		Room room = new Room(roomID, request.title(), request.isPrivate(), request.password(), user.username(), LocalDateTime.now(), request.description());
		if (!roomRepository.create(room)) return "Room creation failed on create";
		if (!roomRepository.joinUser(roomID, user.username())) {
			roomRepository.delete(roomID); // Cleans the room in case the join table could not be updated.
			return "Room creation failed on join";
		}
		return "success";
	}

	/**
	 * Prepares the json string with the list of rooms for the specified user.
	 *
	 * @param user The currently logged-in user.
	 * @return A json string containing a list of rooms where the user is a participant.
	 */
	public String list(User user) {
		Gson gson = new GsonBuilder()
				.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
				.create();
		return gson.toJson(roomRepository.listByUser(user.username()));
	}

	/**
	 * Updates the description of a specific room. Would allow the update only if the specified
	 * user is a participant in said room.
	 *
	 * @param request Configurations set by the user about the new description and to which room.
	 * @param user    The currently logged-in user.
	 * @return A string with an error message in case the operation failed. Otherwise, "success".
	 */
	public String updateDescription(UpdateDescriptionRequest request, User user) {
		if (!roomRepository.isParticipant(request.roomID(), user.username()))
			return "Access denied. User " + user.username() + " is not a participant in room " + request.roomID();
		if (!roomRepository.updateDescription(request.roomID(), request.description()))
			return "Description update failed for room " + request.roomID();

		return "success";
	}
}
