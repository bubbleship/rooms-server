package net.rooms.RoomsServer.room;

import lombok.AllArgsConstructor;
import net.rooms.RoomsServer.room.requests.CreateRequest;
import net.rooms.RoomsServer.room.requests.UpdateDescriptionRequest;
import net.rooms.RoomsServer.room.requests.UpdateTitleRequest;
import net.rooms.RoomsServer.user.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class RoomController {

	private final RoomService roomService;

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
}
