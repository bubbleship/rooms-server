package net.rooms.RoomsServer.message;

import java.util.List;

public interface MessageRepository {
	boolean create(Message message);
	long lastIndex(long roomID);
	List<Message> listByRoom(long roomID);
}
