package net.rooms.RoomsServer.message;

import lombok.AllArgsConstructor;
import net.rooms.RoomsServer.JSON;
import net.rooms.RoomsServer.message.requests.MessageRequest;
import net.rooms.RoomsServer.user.Participant;
import net.rooms.RoomsServer.user.User;
import net.rooms.RoomsServer.websocket.util.WSAuth;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
@AllArgsConstructor
public class MessageController {
	private final SimpMessagingTemplate template;
	private final MessageService messageService;

	/**
	 * Accepts WS requests for posting a new message in a room.
	 * Only requests with a valid session id that is associated with a user who is a participant in
	 * the specified room would be honored.
	 * Sends a notification with the newly created message to all participants of the room, if the
	 * request was successful, at "/queue/messages".
	 *
	 * @param json The payload containing the message and various other details. See
	 *             {@link MessageRequest}.
	 */
	@MessageMapping("/message")
	public void processMessage(@Payload String json) {
		MessageRequest request = JSON.fromJson(json, MessageRequest.class);
		User user = WSAuth.getUser(request);

		Message message = messageService.create(request, user);
		if (message == Message.EMPTY) return;

		String parsedMessage = JSON.toJson(message);
		for (Participant participant : messageService.participants(request.roomID(), user.username()))
			template.convertAndSendToUser(participant.username(), "/queue/messages", parsedMessage);
	}

	/**
	 * Accepts REST API GET requests for the list of message of a given room.
	 * Only logged-in users who are participants in the room may receive such a list.
	 *
	 * @param roomID The ID of the room from which the list is requested.
	 * @param user   The currently logged-in user.
	 * @return A list of the messages from the specified room.
	 */
	@GetMapping(path = "messages/{roomID}")
	public ResponseEntity<List<Message>> list(@PathVariable("roomID") long roomID, @AuthenticationPrincipal User user) {
		return ResponseEntity.ok(messageService.list(roomID, user));
	}
}
