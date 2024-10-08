package net.rooms.RoomsServer.room;

import lombok.AllArgsConstructor;
import net.rooms.RoomsServer.user.Participant;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@AllArgsConstructor
public class JdbcRoomRepository implements RoomRepository {

	private final JdbcClient jdbcClient;

	/**
	 * Inserts the given {@link Room} objects into the database as a new row in the 'room' table.
	 * It is the responsibility of the caller to ensure that {@link Room#roomID()} returns a unique
	 * value not present in the database.
	 *
	 * @param room The new room to insert into the database.
	 * @return True if the operation was successful. Otherwise, false.
	 */
	@Override
	public boolean create(Room room) {
		int updated = jdbcClient.sql("INSERT INTO room(rid,title,is_private,password,owner,creation_date,description) VALUES(?,?,?,?,?,?,?)")
				.params(room.roomID(), room.title(), room.isPrivate(), room.password(), room.owner(), room.creationDate(), room.description())
				.update();
		return updated == 1;
	}

	/**
	 * Retrieves the {@link Room} with the given ID from the database.
	 *
	 * @param roomID The ID of the room to find.
	 * @return The {@link Room} object with the given ID.
	 */
	@Override
	public Room getByID(long roomID) {
		return jdbcClient.sql("SELECT rid AS room_i_d, title, is_private, password, owner, creation_date, description " +
							  "FROM room " +
							  "WHERE rid = ?")
				.params(roomID)
				.query(Room.class)
				.single();
	}

	/**
	 * Deletes the row with the given identifier from the 'room' table in the database.
	 *
	 * @param roomID The identifier (primary key) of the room to delete from the 'room' table.
	 */
	@Override
	public void delete(long roomID) {
		jdbcClient.sql("DELETE FROM room WHERE rid = ?")
				.params(roomID)
				.update();
	}

	/**
	 * Registers a user as a participant in a room by inserting the given parameters to the
	 * 'join_user_room' table.
	 *
	 * @param roomID   The identifier of the room where the user will become a participant.
	 * @param username The username of the new participant.
	 * @return True if the operation was successful. Otherwise, false.
	 */
	@Override
	public boolean joinUser(Long roomID, String username) {
		int updated = jdbcClient.sql("INSERT INTO join_user_room(username,rid) VALUES(?,?)")
				.params(username, roomID)
				.update();
		return updated == 1;
	}

	/**
	 * Removes a user as a participant in a room by removing the given parameters from the
	 * 'join_user_room' table.
	 *
	 * @param roomID   The identifier of the room where the user will no longer be a participant.
	 * @param username The username of the participant.
	 * @return True if the operation was successful. Otherwise, false.
	 */
	@Override
	public boolean leaveUser(Long roomID, String username) {
		int updated = jdbcClient.sql("DELETE FROM join_user_room WHERE username = ? AND rid = ?")
				.params(username, roomID)
				.update();
		return updated == 1;
	}

	/**
	 * Query the 'room' table for the identifier of the most recent room. If the table is empty 0
	 * is returned.
	 *
	 * @return A long representing the identifier of most recent room or zero if no room was
	 * previously added.
	 */
	@Override
	public long lastID() {
		Object result = jdbcClient.sql("SELECT NEXT VALUE FOR room_id").query().singleValue();
		//noinspection ConstantValue
		if (result == null) return 0L;
		return (Long) result;
	}

	/**
	 * Query the 'room' and 'join_user_room' tables for all rooms where the given username is
	 * registered as a participant.
	 *
	 * @param username The username to search in the 'join_user_room' table.
	 * @return A list of {@link Room} objects representing the rooms where the user is a
	 * participant.
	 */
	@Override
	public List<Room> listByUser(String username) {
		return jdbcClient.sql("SELECT room.rid AS room_i_d, room.title, room.is_private, room.password, room.owner, room.creation_date, room.description " +
							  "FROM join_user_room AS jur " +
							  "JOIN room ON room.rid = jur.rid " +
							  "WHERE jur.username = ?")
				.params(username)
				.query(Room.class)
				.list();
	}

	/**
	 * Updates the title of the specified room to the given string.
	 *
	 * @param roomID The identifier of the room to update.
	 * @param title  The new title text to replace the old one.
	 * @return True if the operation was successful. Otherwise, false.
	 */
	@Override
	public boolean updateTitle(long roomID, String title) {
		int updated = jdbcClient.sql("UPDATE room SET title = ? WHERE rid = ?")
				.params(title, roomID)
				.update();
		return updated == 1;
	}

	/**
	 * Updates the description of the specified room to the given string.
	 *
	 * @param roomID      The identifier of the room to update.
	 * @param description The new description text to replace the old one.
	 * @return True if the operation was successful. Otherwise, false.
	 */
	@Override
	public boolean updateDescription(long roomID, String description) {
		int updated = jdbcClient.sql("UPDATE room SET description = ? WHERE rid = ?")
				.params(description, roomID)
				.update();
		return updated == 1;
	}

	/**
	 * Verify whether the user with the given username is a participant in the specified room.
	 *
	 * @param roomID   The identifier of the specified room.
	 * @param username The username of the user to verify.
	 * @return True if the given user is a participant in the specified room. Otherwise, false.
	 */
	@Override
	public boolean isParticipant(long roomID, String username) {
		int status = jdbcClient.sql("SELECT 1 FROM join_user_room WHERE rid = ? AND username = ? LIMIT 1")
				.params(roomID, username)
				.query()
				.listOfRows()
				.size();
		return status == 1;
	}

	@Override
	public List<Participant> listParticipants(long roomID) {
		return jdbcClient.sql("SELECT jur.rid AS room_i_d, users.nickname, users.username, users.signup_date " +
							  "FROM join_user_room AS jur " +
							  "JOIN users ON users.username = jur.username " +
							  "WHERE jur.rid = ?")
				.params(roomID)
				.query(Participant.class)
				.list();
	}

	/**
	 * Searches the entire database and provides a list of rooms where their titles starts with the
	 * given prefix.
	 * Only returns public rooms.
	 *
	 * @param titlePrefix The prefix used to search the database.
	 * @return A list of {@link PublicRoom} objects where their titles starts with the given prefix.
	 */
	@Override
	public List<PublicRoom> searchPublicRooms(String titlePrefix) {
		return jdbcClient.sql("""
						SELECT rid AS room_i_d, title, CASE\s
						        WHEN password = '' THEN FALSE
						        ELSE TRUE
						    END AS has_password, owner, creation_date, description \
						FROM room \
						WHERE is_private = FALSE AND title LIKE ?""")
				.params(titlePrefix + "%")
				.query(PublicRoom.class)
				.list();
	}
}
