package net.rooms.RoomsServer.game;

import lombok.AllArgsConstructor;
import net.rooms.RoomsServer.JSON;
import net.rooms.RoomsServer.game.notifications.BroadcastNotification;
import net.rooms.RoomsServer.game.requests.BroadcastRequest;
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

	/**
	 * Accepts WS requests for starting a pending game.
	 * Only requests with a valid session id that is associated with a user who is a participant in
	 * the specified room and the host of the game, would be honored.
	 * Sends a notification with an updated message to all participants of the room, if the request
	 * was successful, at "/queue/game/start".
	 *
	 * @param payload The payload containing the game id.
	 */
	@MessageMapping("/game/start")
	public void startGame(@Payload String payload) {
		ParticipationRequest request = JSON.fromJson(payload, ParticipationRequest.class);
		User user = WSAuth.getUser(request);

		Message message = gameService.start(request, user);
		notifyParticipants(message.roomID(), user, "/queue/game/start", JSON.toJson(message));
	}

	/**
	 * Accepts WS requests for broadcasting a payload to game participants.
	 * Only requests with a valid session id that is associated with a user who is the host of the
	 * game, would be honored.
	 * Sends a notification with the payload only to participants of the game, if the request was
	 * successful, at "/queue/game/guest-channel".
	 *
	 * @param payload The payload containing the game id.
	 */
	@MessageMapping("/game/broadcast")
	public void broadcast(@Payload String payload) {
		BroadcastRequest request = JSON.fromJson(payload, BroadcastRequest.class);
		User user = WSAuth.getUser(request);

		String notification = JSON.toJson(new BroadcastNotification(request.payload()));
		for (String gameParticipant : gameService.processBroadcastRequest(request, user))
			if (!gameParticipant.equals(user.username()))
				template.convertAndSendToUser(gameParticipant, "/queue/game/guest-channel", notification);
	}

	/**
	 * Accepts WS requests for transmitting a payload from a game participant to the game host.
	 * Only requests with a valid session id that is associated with a user who is a participant in
	 * the game, would be honored.
	 * Sends a notification with the payload only to the host of the game, if the request was
	 * successful, at "/queue/game/host-channel".
	 *
	 * @param payload The payload containing the game id.
	 */
	@MessageMapping("/game/unicast")
	public void unicast(@Payload String payload) {
		BroadcastRequest request = JSON.fromJson(payload, BroadcastRequest.class);
		User user = WSAuth.getUser(request);

		String notification = JSON.toJson(new BroadcastNotification(request.payload()));
		String host = gameService.processUnicastRequest(request, user);
		template.convertAndSendToUser(host, "/queue/game/host-channel", notification);
	}

	private void notifyParticipants(long roomID, User user, String destination, String payload) {
		for (Participant participant : messageService.participants(roomID, user))
			template.convertAndSendToUser(participant.username(), destination, payload);
	}
}
