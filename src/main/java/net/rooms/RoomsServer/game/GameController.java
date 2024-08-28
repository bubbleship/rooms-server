package net.rooms.RoomsServer.game;

import lombok.AllArgsConstructor;
import net.rooms.RoomsServer.JSON;
import net.rooms.RoomsServer.game.requests.ParticipationRequest;
import net.rooms.RoomsServer.message.Message;
import net.rooms.RoomsServer.message.MessageService;
import net.rooms.RoomsServer.user.Participant;
import net.rooms.RoomsServer.user.User;
import net.rooms.RoomsServer.websocket.util.WSAuth;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@AllArgsConstructor
public class GameController {
	private final SimpMessagingTemplate template;
	private final GameService gameService;
	private final MessageService messageService;

	@MessageMapping("/game/join")
	public void joinGame(@Payload String payload) {
		ParticipationRequest request = JSON.fromJson(payload, ParticipationRequest.class);
		User user = WSAuth.getUser(request);

		Message message = gameService.join(request, user);
		notifyParticipants(message.roomID(), user, "/queue/game/join", JSON.toJson(message));
	}

	@MessageMapping("/game/leave")
	public void leaveGame(@Payload String payload) {
		ParticipationRequest request = JSON.fromJson(payload, ParticipationRequest.class);
		User user = WSAuth.getUser(request);

		Message message = gameService.leave(request, user);
		notifyParticipants(message.roomID(), user, "/queue/game/leave", JSON.toJson(message));
	}

	private void notifyParticipants(long roomID, User user, String destination, String payload) {
		for (Participant participant : messageService.participants(roomID, user))
			template.convertAndSendToUser(participant.username(), destination, payload);
	}
}
