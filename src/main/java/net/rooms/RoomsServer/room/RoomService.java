package net.rooms.RoomsServer.room;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.AllArgsConstructor;
import net.rooms.RoomsServer.JSON;
import net.rooms.RoomsServer.adapters.LocalDateTimeAdapter;
import net.rooms.RoomsServer.room.requests.CreateRequest;
import net.rooms.RoomsServer.room.requests.JoinRequest;
import net.rooms.RoomsServer.room.requests.LeaveRequest;
import net.rooms.RoomsServer.room.requests.UpdateDescriptionRequest;
import net.rooms.RoomsServer.room.requests.UpdateTitleRequest;
import net.rooms.RoomsServer.user.Participant;
import net.rooms.RoomsServer.user.User;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class RoomService {

	private final SimpMessagingTemplate template;
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
	 * @return A string with an error message in case the creation failed. Otherwise, json of the
	 * newly created room.
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

		Gson gson = new GsonBuilder()
				.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
				.create();
		return gson.toJson(room);
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
	 * Adds the given user to the specified room. Allows the addition only if the specified
	 * password is correct and the user is not already a participant in the room.
	 * Sends a notification with the new participant details to all participants if the join was
	 * successful at "/queue/join".
	 *
	 * @param request Specifies to which room to join the user and the password.
	 * @param user    The currently logged-in user that attempts to join the room.
	 * @return A string with an error message in case the operation failed. Otherwise, "success".
	 */
	public String join(JoinRequest request, User user) {
		if (roomRepository.isParticipant(request.roomID(), user.username()))
			return "User " + user.username() + " is already a participant at room " + request.roomID();
		if (!roomRepository.getByID(request.roomID()).password().equals(request.password()))
			return "Join failed, invalid password";

		if (!roomRepository.joinUser(request.roomID(), user.username())) return "Join failed";

		Participant participant = new Participant(request.roomID(), user.nickname(), user.username(), user.signupDate());
		notifyParticipants(request.roomID(), "/queue/join", JSON.toJson(participant));

		return "success";
	}

	/**
	 * Removes the given user to the specified room. Allows the removal only if the user is a
	 * participant in the room.
	 * Sends a notification with the participant details to all participants if the removal was
	 * successful at "/queue/leave".
	 *
	 * @param request Specifies which room the user is leaving.
	 * @param user    The currently logged-in user that attempts to leave the room.
	 * @return A string with an error message in case the operation failed. Otherwise, "success".
	 */
	public String leave(LeaveRequest request, User user) {
		if (!roomRepository.isParticipant(request.roomID(), user.username()))
			return "User " + user.username() + " is not a participant at room " + request.roomID();

		if (!roomRepository.leaveUser(request.roomID(), user.username())) return "Leave failed";

		Participant participant = new Participant(request.roomID(), user.nickname(), user.username(), user.signupDate());
		notifyParticipants(request.roomID(), "/queue/leave", JSON.toJson(participant));

		return "success";
	}

	/**
	 * Updates the description of a specific room. Would allow the update only if the specified
	 * user is a participant in said room.
	 * Sends a notification with the new room details to all participants if the update was
	 * successful at "/queue/title".
	 *
	 * @param request Configurations set by the user about the new description and to which room.
	 * @param user    The currently logged-in user.
	 * @return A string with an error message in case the operation failed. Otherwise, "success".
	 */
	public String updateTitle(UpdateTitleRequest request, User user) {
		if (!roomRepository.isParticipant(request.roomID(), user.username()))
			return "Access denied. User " + user.username() + " is not a participant in room " + request.roomID();
		if (!roomRepository.updateTitle(request.roomID(), request.title()))
			return "Title update failed for room " + request.roomID();

		Room room = roomRepository.getByID(request.roomID());
		String parsedRoom = JSON.toJson(room);
		notifyParticipants(request.roomID(), "/queue/title", parsedRoom);

		return "success";
	}

	/**
	 * Updates the description of a specific room. Would allow the update only if the specified
	 * user is a participant in said room.
	 * Sends a notification with the new room details to all participants if the update was
	 * successful at "/queue/description".
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

		Room room = roomRepository.getByID(request.roomID());
		String parsedRoom = JSON.toJson(room);
		notifyParticipants(request.roomID(), "/queue/description", parsedRoom);

		return "success";
	}

	private void notifyParticipants(long roomID, String destination, String payload) {
		for (Participant participant : roomRepository.listParticipants(roomID))
			template.convertAndSendToUser(participant.username(), destination, payload);
	}
}
