package net.rooms.RoomsServer.message;

import lombok.AllArgsConstructor;
import net.rooms.RoomsServer.message.requests.MessageRequest;
import net.rooms.RoomsServer.room.RoomRepository;
import net.rooms.RoomsServer.user.Participant;
import net.rooms.RoomsServer.user.User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class MessageService {

	private final MessageRepository messageRepository;
	private final RoomRepository roomRepository;

	public Message create(MessageRequest request, User user) {
		if (!roomRepository.isParticipant(request.roomID(), user.username())) return Message.EMPTY;

		long id = messageRepository.lastID();
		Message message = new Message(id, request.roomID(), request.type(), user.username(), request.content(), LocalDateTime.now());
		if (!messageRepository.create(message)) return Message.EMPTY;
		return message;
	}

	public List<Participant> participants(long roomID, User user) {
		if (!roomRepository.isParticipant(roomID, user.username())) return new ArrayList<>();

		return roomRepository.listParticipants(roomID);
	}

	public List<Message> list(long roomID, User user) {
		if (!roomRepository.isParticipant(roomID, user.username())) return new ArrayList<>();

		return messageRepository.listByRoom(roomID);
	}
}
