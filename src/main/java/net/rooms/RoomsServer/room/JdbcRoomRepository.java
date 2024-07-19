package net.rooms.RoomsServer.room;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class JdbcRoomRepository implements RoomRepository {

	private final JdbcClient jdbcClient;

	@Override
	public boolean create(Room room) {
		int updated = jdbcClient.sql("INSERT INTO room(rid,title,is_private,password,owner,creation_date) VALUES(?,?,?,?,?,?)")
				.params(room.roomID(), room.title(), room.isPrivate(), room.password(), room.owner(), room.creationDate())
				.update();
		return updated == 1;
	}

	@Override
	public void delete(long roomID) {
		jdbcClient.sql("DELETE FROM room WHERE rid = ?")
				.params(roomID)
				.update();
	}

	@Override
	public boolean joinUser(Long roomID, String username) {
		int updated = jdbcClient.sql("INSERT INTO join_user_room(username,rid) VALUES(?,?)")
				.params(username, roomID)
				.update();
		return updated == 1;
	}

	@Override
	public long lastID() {
		Long result = (Long) jdbcClient.sql("SELECT MAX(rid) FROM room").query().singleValue();
		//noinspection ConstantValue
		if (result == null) return 0L;
		return result;
	}
}
