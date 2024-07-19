package net.rooms.RoomsServer.room;

import lombok.AllArgsConstructor;
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
		int updated = jdbcClient.sql("INSERT INTO room(rid,title,is_private,password,owner,creation_date) VALUES(?,?,?,?,?,?)")
				.params(room.roomID(), room.title(), room.isPrivate(), room.password(), room.owner(), room.creationDate())
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

	@Override
	public List<Room> listByUser(String username) {
		return jdbcClient.sql("SELECT room.rid AS room_i_d, room.title, room.is_private, room.password, room.owner, room.creation_date " +
							  "FROM join_user_room AS jur " +
							  "JOIN room ON room.rid = jur.rid " +
							  "WHERE jur.username = ?")
				.params(username)
				.query(Room.class)
				.list();
	}
}
