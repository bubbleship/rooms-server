package net.rooms.RoomsServer.room;

import java.util.List;

public interface RoomRepository {
	boolean create(Room room);

	void delete(long roomID);

	boolean joinUser(Long roomID, String username);

	long lastID();

	List<Room> listByUser(String username);
}
