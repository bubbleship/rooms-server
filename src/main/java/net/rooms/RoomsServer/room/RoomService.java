package net.rooms.RoomsServer.room;

import lombok.AllArgsConstructor;
import net.rooms.RoomsServer.JSON;
import net.rooms.RoomsServer.room.requests.CreateRequest;
import net.rooms.RoomsServer.room.requests.InviteRequest;
import net.rooms.RoomsServer.room.requests.JoinRequest;
import net.rooms.RoomsServer.room.requests.LeaveRequest;
import net.rooms.RoomsServer.room.requests.UpdateDescriptionRequest;
import net.rooms.RoomsServer.room.requests.UpdateTitleRequest;
import net.rooms.RoomsServer.user.Participant;
import net.rooms.RoomsServer.user.User;
import net.rooms.RoomsServer.user.UserRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

@Service
@AllArgsConstructor
public class RoomService {

	private final SimpMessagingTemplate template;
	private final RoomRepository roomRepository;
	private final UserRepository userRepository;

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

		return JSON.toJson(room);
	}

	/**
	 * Prepares the json string with the list of rooms for the specified user.
	 *
	 * @param user The currently logged-in user.
	 * @return A json string containing a list of rooms where the user is a participant.
	 */
	public String list(User user) {
		return JSON.toJson(roomRepository.listByUser(user.username()));
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
	 * Adds the specified user to the specified room. Allows the addition only if the inviting
	 * user is a participant in the room and the specified user isn't.
	 * Sends a notification with the new participant details to all participants if the invitation
	 * was successful at "/queue/join".
	 *
	 * @param request Specifies to which room to invite the specified user.
	 * @param user    The currently logged-in user that attempts to invite the user.
	 * @return A string with an error message in case the operation failed. Otherwise, "success".
	 */
	public String invite(InviteRequest request, User user) {
		if (!roomRepository.isParticipant(request.roomID(), user.username()))
			return "User " + user.username() + " is not a participant at room " + request.roomID();
		if (userRepository.findByUsername(request.username()).isEmpty())
			return "User " + user.username() + " does not exist";
		if (roomRepository.isParticipant(request.roomID(), request.username()))
			return "User " + user.username() + " is already a participant at room " + request.roomID();

		if (!roomRepository.joinUser(request.roomID(), request.username())) return "Invite failed";
		Optional<User> optional = userRepository.findByUsername(request.username());
		if (optional.isEmpty()) return "Invite failed";

		Participant participant = new Participant(request.roomID(), optional.get().nickname(), optional.get().username(), optional.get().signupDate());
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

	/**
	 * Prepares a json string containing the list of participants for the specified room. The
	 * operation would work only if the given user is a participant in the specified room.
	 * Otherwise, an empty list is returned.
	 * The json string represents a list of {@link Participant} objects.
	 *
	 * @param roomID The ID of the specified room.
	 * @param user   The currently logged-in user.
	 * @return A json string with the list of participants for the specified room if the logged-in
	 * user is a participant in that room. Otherwise, a json string of an empty list.
	 */
	public String listParticipants(long roomID, User user) {
		if (!roomRepository.isParticipant(roomID, user.username()))
			return JSON.toJson(new ArrayList<>());

		return JSON.toJson(roomRepository.listParticipants(roomID));
	}

	/**
	 * Prepares a json string with a list of {@link PublicRoom} objects representing all the rooms
	 * where their {@link PublicRoom#title()} property begins with the given prefix.
	 * The list would never contain a reference to a private room.
	 * {@link PublicRoom} only contain some of the fields from {@link Room}.
	 *
	 * @param titlePrefix A string representing the title prefix.
	 * @return A json string containing a list of {@link PublicRoom} objects where their title
	 * starts with the given prefix.
	 */
	public String searchPublicRooms(String titlePrefix) {
		return JSON.toJson(roomRepository.searchPublicRooms(titlePrefix));
	}
}
