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
	 * Query the 'room' table for the identifier of the most recent room. If the table is empty 0
	 * is returned.
	 *
	 * @return A long representing the identifier of most recent room or zero if no room was
	 * previously added.
	 */
	@Override
	public long lastID() {
		Object result = jdbcClient.sql("SELECT MAX(rid) FROM room").query().singleValue();
		//noinspection ConstantValue
		if (result == null) return 0L;
		return Long.valueOf((Integer) result);
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
		return jdbcClient.sql("SELECT users.nickname, users.username, users.signup_date " +
							  "FROM join_user_room AS jur " +
							  "JOIN users ON users.username = jur.username " +
							  "WHERE jur.rid = ?")
				.params(roomID)
				.query(Participant.class)
				.list();
	}
}
