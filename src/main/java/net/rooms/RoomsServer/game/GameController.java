package net.rooms.RoomsServer.game;

import lombok.AllArgsConstructor;
import net.rooms.RoomsServer.JSON;
import net.rooms.RoomsServer.game.notifications.BroadcastNotification;
import net.rooms.RoomsServer.game.notifications.GameUpdate;
import net.rooms.RoomsServer.game.requests.BroadcastRequest;
import net.rooms.RoomsServer.game.requests.ParticipationRequest;
import net.rooms.RoomsServer.message.Message;
import net.rooms.RoomsServer.message.MessageService;
import net.rooms.RoomsServer.message.MessageType;
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

	/**
	 * Accepts WS requests for joining a game.
	 * The request is expected to contain json of {@link ParticipationRequest}.
	 * Only requests with a valid session id that is associated with a user who is a participant in
	 * the specified room, would be honored.
	 * Sends a notification with an updated message to all participants of the room, if the request
	 * was successful, at "/queue/game/start".
	 * <br>
	 * The notification is a {@link Message} where {@link Message#content()} is json of a
	 * {@link GameUpdate} object and {@link Message#type()} may be any type in {@link MessageType}
	 * that ends with "OPEN" indicating the state of the game.
	 * <br>
	 * Join requests may get rejected if the game lobby is full, in which case no notification is
	 * sent.
	 *
	 * @param payload Json of {@link ParticipationRequest} containing the game id.
	 */
	@MessageMapping("/game/join")
	public void joinGame(@Payload String payload) {
		ParticipationRequest request = JSON.fromJson(payload, ParticipationRequest.class);
		User user = WSAuth.getUser(request);

		Message message = gameService.join(request, user);
		notifyParticipants(message.roomID(), user.username(), "/queue/game/join", JSON.toJson(message));
	}

	/**
	 * Accepts WS requests for leaving a game.
	 * The request is expected to contain json of {@link ParticipationRequest}.
	 * Only requests with a valid session id that is associated with a user who is a participant in
	 * the specified game, would be honored.
	 * Sends a notification with an updated message to all participants of the room, if the request
	 * was successful, at "/queue/game/start".
	 * <br>
	 * The notification is a {@link Message} where {@link Message#content()} is json of a
	 * {@link GameUpdate} object and {@link Message#type()} may be any type in {@link MessageType}
	 * that ends with "OPEN", "ONGOING" or "ABORT" indicating the state of the game.
	 * <br>
	 * In case the leaving participant happens to be the host, all participants gets removed and
	 * the game closes but only a message of host leaving would be sent. The type associated with
	 * this case is any type that ends with "ABORT".
	 *
	 * @param payload Json of {@link ParticipationRequest} containing the game id.
	 */
	@MessageMapping("/game/leave")
	public void leaveGame(@Payload String payload) {
		ParticipationRequest request = JSON.fromJson(payload, ParticipationRequest.class);
		User user = WSAuth.getUser(request);

		leaveGameUsername(user.username());
	}

	public void leaveGameUsername(String username) {
		Message message = gameService.leave(username);
		notifyParticipants(message.roomID(), username, "/queue/game/leave", JSON.toJson(message));
	}

	/**
	 * Accepts WS requests for starting a pending game.
	 * The request is expected to contain json of {@link ParticipationRequest}.
	 * Only requests with a valid session id that is associated with a user who is a participant in
	 * the specified room and the host of the game, would be honored.
	 * Sends a notification with an updated message to all participants of the room, if the request
	 * was successful, at "/queue/game/start".
	 * <br>
	 * The notification is a {@link Message} where {@link Message#content()} is json of a
	 * {@link GameUpdate} object and {@link Message#type()} may be any type in {@link MessageType}
	 * that ends with "ONGOING" indicating that the game has started.
	 *
	 * @param payload Json of {@link ParticipationRequest} containing the game id.
	 */
	@MessageMapping("/game/start")
	public void startGame(@Payload String payload) {
		ParticipationRequest request = JSON.fromJson(payload, ParticipationRequest.class);
		User user = WSAuth.getUser(request);

		Message message = gameService.start(request, user);
		notifyParticipants(message.roomID(), user.username(), "/queue/game/start", JSON.toJson(message));
	}

	/**
	 * Accepts WS requests for submitting a game results.
	 * The request is expected to contain json of {@link BroadcastRequest}.
	 * Only requests with a valid session id that is associated with a user who is the host of the
	 * game, would be honored.
	 * Sends a notification with an updated message to all participants of the room, if the request
	 * was successful, at "/queue/game/results".
	 * <br>
	 * The notification is a {@link Message} where {@link Message#content()} is json of some object
	 * made by the host and {@link Message#type()} may be any type in {@link MessageType} that ends
	 * with "RESULT" indicating that the game has concluded.
	 *
	 * @param payload Json of {@link BroadcastRequest} containing the game id.
	 */
	@MessageMapping("/game/submit")
	public void submitGame(@Payload String payload) {
		BroadcastRequest request = JSON.fromJson(payload, BroadcastRequest.class);
		User user = WSAuth.getUser(request);

		Message message = gameService.submit(request, user);
		notifyParticipants(message.roomID(), user.username(), "/queue/game/results", JSON.toJson(message));
	}

	/**
	 * Accepts WS requests for broadcasting a payload to game participants.
	 * The request is expected to contain json of {@link BroadcastRequest}.
	 * Only requests with a valid session id that is associated with a user who is the host of the
	 * game, would be honored.
	 * Sends a notification with the payload only to participants of the game, if the request was
	 * successful, at "/queue/game/guest-channel".
	 *
	 * @param payload Json of {@link BroadcastRequest} containing the game id.
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
	 * The request is expected to contain json of {@link BroadcastRequest}.
	 * Only requests with a valid session id that is associated with a user who is a participant in
	 * the game, would be honored.
	 * Sends a notification with the payload only to the host of the game, if the request was
	 * successful, at "/queue/game/host-channel".
	 *
	 * @param payload Json of {@link BroadcastRequest} containing the game id.
	 */
	@MessageMapping("/game/unicast")
	public void unicast(@Payload String payload) {
		BroadcastRequest request = JSON.fromJson(payload, BroadcastRequest.class);
		User user = WSAuth.getUser(request);

		String notification = JSON.toJson(new BroadcastNotification(request.payload()));
		String host = gameService.processUnicastRequest(request, user);
		template.convertAndSendToUser(host, "/queue/game/host-channel", notification);
	}

	private void notifyParticipants(long roomID, String username, String destination, String payload) {
		for (Participant participant : messageService.participants(roomID, username))
			template.convertAndSendToUser(participant.username(), destination, payload);
	}
}
