package net.rooms.RoomsServer.room;

import java.util.List;

public interface RoomRepository {
	/**
	 * Inserts the given {@link Room} objects into the database as a new row in the 'room' table.
	 * It is the responsibility of the caller to ensure that {@link Room#roomID()} returns a unique
	 * value not present in the database.
	 *
	 * @param room The new room to insert into the database.
	 * @return True if the operation was successful. Otherwise, false.
	 */
	boolean create(Room room);

	/**
	 * Deletes the row with the given identifier from the 'room' table in the database.
	 *
	 * @param roomID The identifier (primary key) of the room to delete from the 'room' table.
	 */
	void delete(long roomID);

	/**
	 * Registers a user as a participant in a room by inserting the given parameters to the
	 * 'join_user_room' table.
	 *
	 * @param roomID   The identifier of the room where the user will become a participant.
	 * @param username The username of the new participant.
	 * @return True if the operation was successful. Otherwise, false.
	 */
	boolean joinUser(Long roomID, String username);

	/**
	 * Query the 'room' table for the identifier of the most recent room. If the table is empty 0
	 * is returned.
	 *
	 * @return A long representing the identifier of most recent room or zero if no room was
	 * previously added.
	 */
	long lastID();

	/**
	 * Query the 'room' and 'join_user_room' tables for all rooms where the given username is
	 * registered as a participant.
	 *
	 * @param username The username to search in the 'join_user_room' table.
	 * @return A list of {@link Room} objects representing the rooms where the user is a
	 * participant.
	 */
	List<Room> listByUser(String username);

	/**
	 * Updates the description of the specified room to the given string.
	 *
	 * @param roomID      The identifier of the room to update.
	 * @param description The new description text to replace the old one.
	 * @return True if the operation was successful. Otherwise, false.
	 */
	boolean updateDescription(long roomID, String description);
}
