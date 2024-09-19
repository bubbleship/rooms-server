package net.rooms.RoomsServer.message;

import java.util.List;

public interface MessageRepository {
	boolean create(Message message);
	boolean update(Message message);
	Message get(long id);
	long lastID();
	List<Message> listByRoom(long roomID);
}
