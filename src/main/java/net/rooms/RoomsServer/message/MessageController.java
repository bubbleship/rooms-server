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

	@MessageMapping("/message")
	public void processMessage(@Payload String json) {
		MessageRequest request = JSON.fromJson(json, MessageRequest.class);
		User user = WSAuth.getUser(request);

		Message message = messageService.create(request, user);
		if (message == Message.EMPTY) return;

		String parsedMessage = JSON.toJson(message);
		for (Participant participant : messageService.participants(request.roomID(), user))
			template.convertAndSendToUser(participant.username(), "/queue/messages", parsedMessage);
	}

	@GetMapping(path = "messages/{roomID}")
	public ResponseEntity<List<Message>> list(@PathVariable("roomID") long roomID, @AuthenticationPrincipal User user) {
		return ResponseEntity.ok(messageService.list(roomID, user));
	}
}
