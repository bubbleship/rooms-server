package net.rooms.RoomsServer.room;

public interface RoomRepository {
	boolean create(Room room);

	void delete(long roomID);

	boolean joinUser(Long roomID, String username);

	long lastID();
}
