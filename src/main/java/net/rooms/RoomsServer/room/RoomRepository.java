package net.rooms.RoomsServer.room;

import net.rooms.RoomsServer.user.Participant;

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
	 * Retrieves the {@link Room} with the given ID from the database.
	 *
	 * @param roomID The ID of the room to find.
	 * @return The {@link Room} object with the given ID.
	 */
	Room getByID(long roomID);

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
	 * Removes a user as a participant in a room by removing the given parameters from the
	 * 'join_user_room' table.
	 *
	 * @param roomID   The identifier of the room where the user will no longer be a participant.
	 * @param username The username of the participant.
	 * @return True if the operation was successful. Otherwise, false.
	 */
	boolean leaveUser(Long roomID, String username);

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
	 * Updates the title of the specified room to the given string.
	 *
	 * @param roomID The identifier of the room to update.
	 * @param title  The new title text to replace the old one.
	 * @return True if the operation was successful. Otherwise, false.
	 */
	boolean updateTitle(long roomID, String title);

	/**
	 * Updates the description of the specified room to the given string.
	 *
	 * @param roomID      The identifier of the room to update.
	 * @param description The new description text to replace the old one.
	 * @return True if the operation was successful. Otherwise, false.
	 */
	boolean updateDescription(long roomID, String description);

	/**
	 * Verify whether the user with the given username is a participant in the specified room.
	 *
	 * @param roomID   The identifier of the specified room.
	 * @param username The username of the user to verify.
	 * @return True if the given user is a participant in the specified room. Otherwise, false.
	 */
	boolean isParticipant(long roomID, String username);

	List<Participant> listParticipants(long roomID);

	/**
	 * Searches the entire database and provides a list of rooms where their titles starts with the
	 * given prefix.
	 * Only returns public rooms.
	 *
	 * @param titlePrefix The prefix used to search the database.
	 * @return A list of {@link PublicRoom} objects where their titles starts with the given prefix.
	 */
	List<PublicRoom> searchPublicRooms(String titlePrefix);
}
