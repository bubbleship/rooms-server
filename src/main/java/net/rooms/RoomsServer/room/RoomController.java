package net.rooms.RoomsServer.room;

import lombok.AllArgsConstructor;
import net.rooms.RoomsServer.game.GameController;
import net.rooms.RoomsServer.game.GameService;
import net.rooms.RoomsServer.room.requests.CreateRequest;
import net.rooms.RoomsServer.room.requests.InviteRequest;
import net.rooms.RoomsServer.room.requests.JoinRequest;
import net.rooms.RoomsServer.room.requests.LeaveRequest;
import net.rooms.RoomsServer.room.requests.UpdateDescriptionRequest;
import net.rooms.RoomsServer.room.requests.UpdateTitleRequest;
import net.rooms.RoomsServer.user.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class RoomController {

	private final RoomService roomService;
	private final GameService gameService;
	private final GameController gameController;

	/**
	 * Accepts API requests for room creation.
	 * Only logged-in users may create a room.
	 * JSON content request example:
	 * <code>
	 * {
	 *   "title" : "title",
	 *   "isPrivate" : false,
	 *   "password" : ""
	 * }
	 * </code>
	 *
	 * @param request Configurations set by the user about the room to create.
	 * @param user    The currently logged-in user.
	 * @return A string with an error message in case the creation failed. Otherwise, json of the
	 * newly created room.
	 */
	@PostMapping(path = "api/v1/room/create")
	public String create(@RequestBody CreateRequest request, @AuthenticationPrincipal User user) {
		return roomService.create(request, user);
	}

	/**
	 * Accepts REST API GET requests for list of rooms where the logged-in user is participating.
	 * Only logged-in users may receive a list of such rooms.
	 *
	 * @param user The currently logged-in user.
	 * @return A json string containing a list of rooms where the user is a participant.
	 */
	@GetMapping(path = "api/v1/room/list")
	public String list(@AuthenticationPrincipal User user) {
		return roomService.list(user);
	}

	/**
	 * Accepts REST API POST requests for adding the currently logged-in user to a room.
	 * Only logged-in users who provide the password and not already a participant in the room, may
	 * join it.
	 * <code>
	 * {
	 *   "roomID" : 1,
	 *   "password" : ""
	 * }
	 * </code>
	 * Sends a notification with the new participant details to all participants if the join was
	 * successful at "/queue/join".
	 *
	 * @param request Specifies to which room to join the user and the password.
	 * @param user    The currently logged-in user that attempts to join the room.
	 * @return A string with an error message in case the operation failed. Otherwise, "success".
	 */
	@PostMapping(path = "api/v1/room/join")
	public String join(@RequestBody JoinRequest request, @AuthenticationPrincipal User user) {
		return roomService.join(request, user);
	}

	/**
	 * Accepts REST API POST requests for inviting the specified user to a room.
	 * Only logged-in users who are already participants in the room, may invite other users.
	 * JSON content request example:
	 * <code>
	 * {
	 *   "roomID" : 1,
	 *   "username" : "username"
	 * }
	 * </code>
	 * Sends a notification with the new participant details to all participants if the invite was
	 * successful at "/queue/join".
	 *
	 * @param request Specifies to which room to invite the specified user.
	 * @param user    The currently logged-in user that attempts to invite the user.
	 * @return A string with an error message in case the operation failed. Otherwise, "success".
	 */
	@PostMapping(path = "api/v1/room/invite")
	public String invite(@RequestBody InviteRequest request, @AuthenticationPrincipal User user) {
		return roomService.invite(request, user);
	}

	/**
	 * Accepts REST API POST requests for removing the currently logged-in user from a room.
	 * Only logged-in users who are already a participant in the room may leave it.
	 * Also removes the user from any game within the room, if participating in one.
	 * <code>
	 * {
	 *   "roomID" : 1
	 * }
	 * </code>
	 * Sends a notification with the participant details to all participants if the removal was
	 * successful at "/queue/leave" and at "/queue/game/leave" if also participated in a game.
	 *
	 *
	 * @param request Specifies which room the user is leaving.
	 * @param user    The currently logged-in user that attempts to leave the room.
	 * @return A string with an error message in case the operation failed. Otherwise, "success".
	 */
	@PostMapping(path = "api/v1/room/leave")
	public String leave(@RequestBody LeaveRequest request, @AuthenticationPrincipal User user) {
		if (gameService.getRoomID(gameService.getGameID(user.username())) == request.roomID())
			gameController.leaveGameUsername(user.username()); // The user should also leave any game within the room, if they participated in one
		return roomService.leave(request, user);
	}

	/**
	 * Accepts REST API POST requests for updating the title of a specific room.
	 * Only logged-in users who are participants in the room may update its title.
	 * JSON content request example:
	 * <code>
	 * {
	 *   "roomID" : 1,
	 *   "title" : "A Title"
	 * }
	 * </code>
	 * Sends a notification with the new room details to all participants if the update was
	 * successful at "/queue/title".
	 *
	 * @param request Configurations set by the user about the new title and to which room.
	 * @param user    The currently logged-in user.
	 * @return A string with an error message in case the operation failed. Otherwise, "success".
	 */
	@PostMapping(path = "api/v1/room/update/title")
	public String updateTitle(@RequestBody UpdateTitleRequest request, @AuthenticationPrincipal User user) {
		return roomService.updateTitle(request, user);
	}

	/**
	 * Accepts REST API POST requests for updating the description of a specific room.
	 * Only logged-in users who are participants in the room may update its description.
	 * JSON content request example:
	 * <code>
	 * {
	 *   "roomID" : 1,
	 *   "description" : "A description"
	 * }
	 * </code>
	 * Sends a notification with the new room details to all participants if the update was
	 * successful at "/queue/description".
	 *
	 * @param request Configurations set by the user about the new description and to which room.
	 * @param user    The currently logged-in user.
	 * @return A string with an error message in case the operation failed. Otherwise, "success".
	 */
	@PostMapping(path = "api/v1/room/update/description")
	public String updateDescription(@RequestBody UpdateDescriptionRequest request, @AuthenticationPrincipal User user) {
		return roomService.updateDescription(request, user);
	}

	/**
	 * Accepts REST API GET requests for the list of participants of a given room.
	 * Only logged-in users who are participants in the room may receive such a list. As such, the
	 * list would always include the user that made the request.
	 *
	 * @param roomID The ID of the room from which the list is requested.
	 * @param user   The currently logged-in user.
	 * @return A json string with the list of participants for the specified room if the logged-in
	 * user is a participant in that room. Otherwise, a json string of an empty list.
	 */
	@GetMapping(path = "api/v1/room/{roomID}/participants")
	public String listParticipants(@PathVariable("roomID") long roomID, @AuthenticationPrincipal User user) {
		return roomService.listParticipants(roomID, user);
	}

	/**
	 * Accepts REST API GET requests for the list of all public rooms where their title starts with
	 * the specified prefix.
	 * Any logged-in user may request such a list.
	 *
	 * @param prefix A string used to search the rooms.
	 * @return A json string containing a list of {@link PublicRoom} objects where their title
	 * starts with the given prefix.
	 */
	@GetMapping(path = "api/v1/room/search/{prefix}")
	public String searchPublicRooms(@PathVariable("prefix") String prefix) {
		return roomService.searchPublicRooms(prefix);
	}
}
