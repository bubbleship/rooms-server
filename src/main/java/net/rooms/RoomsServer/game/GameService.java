package net.rooms.RoomsServer.game;

import lombok.AllArgsConstructor;
import net.rooms.RoomsServer.JSON;
import net.rooms.RoomsServer.game.config.GameType;
import net.rooms.RoomsServer.game.config.PongConfig;
import net.rooms.RoomsServer.game.notifications.GameUpdate;
import net.rooms.RoomsServer.game.requests.ParticipationRequest;
import net.rooms.RoomsServer.message.Message;
import net.rooms.RoomsServer.message.MessageRepository;
import net.rooms.RoomsServer.message.MessageType;
import net.rooms.RoomsServer.room.RoomRepository;
import net.rooms.RoomsServer.user.User;
import org.springframework.stereotype.Service;

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
			gameRepository.leave(message.id(), user.username()); // Undo the operation in the unlikely case of failure
			return Message.EMPTY;
		}
		return updatedMessage;
	}

	public Message leave(ParticipationRequest request, User user) {
		Message message = messageRepository.get(request.id());
		if (!roomRepository.isParticipant(message.roomID(), user.username())) return Message.EMPTY;
		GameUpdate update = gameRepository.leave(message.id(), user.username());
		if (update == null) return Message.EMPTY;

		MessageType messageType = message.type();
		if (update.participants().isEmpty())
			switch (messageType) {
				case PONG_GAME_OPEN -> messageType = MessageType.PONG_GAME_ABORT;
			}
		Message updatedMessage =  new Message(message.id(), message.roomID(), messageType, message.sender(), JSON.toJson(update), message.sendDate());
		if (!messageRepository.update(updatedMessage)) {
			gameRepository.join(message.id(), user.username()); // Undo the operation in the unlikely case of failure
			return Message.EMPTY;
		}
		return updatedMessage;
	}
}
