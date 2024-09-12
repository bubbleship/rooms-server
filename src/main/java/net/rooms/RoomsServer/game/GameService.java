package net.rooms.RoomsServer.game;

import lombok.AllArgsConstructor;
import net.rooms.RoomsServer.JSON;
import net.rooms.RoomsServer.game.config.GameType;
import net.rooms.RoomsServer.game.config.PongConfig;
import net.rooms.RoomsServer.game.config.SnakesConfig;
import net.rooms.RoomsServer.game.notifications.GameUpdate;
import net.rooms.RoomsServer.game.requests.BroadcastRequest;
import net.rooms.RoomsServer.game.requests.ParticipationRequest;
import net.rooms.RoomsServer.message.Message;
import net.rooms.RoomsServer.message.MessageRepository;
import net.rooms.RoomsServer.message.MessageType;
import net.rooms.RoomsServer.room.RoomRepository;
import net.rooms.RoomsServer.user.User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Set;

@Service
@AllArgsConstructor
public class GameService {

	private final GameRepository gameRepository;
	private final RoomRepository roomRepository;
	private final MessageRepository messageRepository;

	public boolean handle(Message message) {
		switch (message.type()) {
			case MESSAGE -> {
				return true;
			}
			case PONG_GAME_OPEN -> {
				return gameRepository.open(message, GameType.PONG, PongConfig.class);
			}
			case SNAKES_GAME_OPEN -> {
				return gameRepository.open(message, GameType.SNAKES, SnakesConfig.class);
			}
		}
		return false;
	}

	public Message join(ParticipationRequest request, User user) {
		Message message = messageRepository.get(request.id());
		if (!roomRepository.isParticipant(message.roomID(), user.username())) return Message.EMPTY;
		GameUpdate update = gameRepository.join(message.id(), user.username());
		if (update == null) return Message.EMPTY;

		Message updatedMessage = new Message(message.id(), message.roomID(), message.type(), message.sender(), JSON.toJson(update), message.sendDate());
		if (!messageRepository.update(updatedMessage)) {
			gameRepository.leave(user.username()); // Undo the operation in the unlikely case of failure
			return Message.EMPTY;
		}
		return updatedMessage;
	}

	public Message leave(String username) {
		long id = getGameID(username);
		GameUpdate update = gameRepository.leave(username);
		if (update == null) return Message.EMPTY;

		Message message = messageRepository.get(id);
		MessageType messageType = message.type();
		if (update.participants().isEmpty())
			switch (messageType) {
				case PONG_GAME_OPEN, PONG_GAME_ONGOING -> messageType = MessageType.PONG_GAME_ABORT;
				case SNAKES_GAME_OPEN, SNAKES_GAME_ONGOING -> messageType = MessageType.SNAKES_GAME_ABORT;
			}
		Message updatedMessage = new Message(message.id(), message.roomID(), messageType, message.sender(), JSON.toJson(update), message.sendDate());
		if (!messageRepository.update(updatedMessage)) {
			gameRepository.join(message.id(), username); // Undo the operation in the unlikely case of failure
			return Message.EMPTY;
		}
		return updatedMessage;
	}

	public Message start(ParticipationRequest request, User user) {
		if (!gameRepository.getHost(request.id()).equals(user.username())) return Message.EMPTY;
		GameUpdate update = gameRepository.startGame(request.id(), user.username());
		if (update == null) return null;

		Message message = messageRepository.get(request.id());
		MessageType messageType = message.type();
		switch (messageType) {
			case PONG_GAME_OPEN -> messageType = MessageType.PONG_GAME_ONGOING;
			case SNAKES_GAME_OPEN -> messageType = MessageType.SNAKES_GAME_ONGOING;
		}
		Message updatedMessage = new Message(message.id(), message.roomID(), messageType, message.sender(), JSON.toJson(update), message.sendDate());
		messageRepository.update(updatedMessage);
		return updatedMessage;
	}

	public Message submit(BroadcastRequest request, User user) {
		if (!gameRepository.closeGame(request.id(), user.username())) return Message.EMPTY;

		Message message = messageRepository.get(request.id());
		MessageType messageType = message.type();
		switch (messageType) {
			case PONG_GAME_ONGOING -> messageType = MessageType.PONG_GAME_RESULT;
			case SNAKES_GAME_ONGOING -> messageType = MessageType.SNAKES_GAME_RESULT;
		}
		Message updatedMessage = new Message(message.id(), message.roomID(), messageType, message.sender(), request.payload(), message.sendDate());
		messageRepository.update(updatedMessage);
		return updatedMessage;
	}

	public long getGameID(String username) {
		return gameRepository.getGameID(username);
	}

	public long getRoomID(long id) {
		return gameRepository.getRoomID(id);
	}

	public Set<String> processBroadcastRequest(BroadcastRequest request, User user) {
		if (!gameRepository.getHost(request.id()).equals(user.username())) return Collections.emptySet();
		return gameRepository.getGameParticipants(request.id());
	}

	public String processUnicastRequest(BroadcastRequest request, User user) {
		if (!gameRepository.getGameParticipants(request.id()).contains(user.username()))
			return ""; // Not a game participant
		return gameRepository.getHost(request.id());
	}
}
