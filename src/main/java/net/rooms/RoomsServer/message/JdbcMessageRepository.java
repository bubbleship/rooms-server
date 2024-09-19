package net.rooms.RoomsServer.message;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@AllArgsConstructor
public class JdbcMessageRepository implements MessageRepository {

	private final JdbcClient jdbcClient;

	@Override
	public boolean create(Message message) {
		int updated = jdbcClient.sql("INSERT INTO message(id,rid,type,sender,content,send_date) VALUES(?,?,?,?,?,?)")
				.params(message.id(), message.roomID(), message.type().ordinal(), message.sender(), message.content(), message.sendDate())
				.update();
		return updated == 1;
	}

	@Override
	public boolean update(Message message) {
		int updated = jdbcClient.sql("UPDATE message " +
									 "SET rid = ?, type = ?, sender = ?, content = ?, send_date = ? " +
									 "WHERE id = ?")
				.params(message.roomID(), message.type().ordinal(), message.sender(), message.content(), message.sendDate(), message.id())
				.update();
		return updated == 1;
	}

	@Override
	public Message get(long id) {
		return jdbcClient.sql("SELECT id, rid AS roomID, type, sender, content, send_date " +
							  "FROM message " +
							  "WHERE id = ?")
				.params(id)
				.query(Message.class)
				.single();
	}

	@Override
	public long lastID() {
		Object result = jdbcClient.sql("SELECT NEXT VALUE FOR msg_id").query().singleValue();
		//noinspection ConstantValue
		if (result == null) return 0L;
		return (Long) result;
	}

	@Override
	public List<Message> listByRoom(long roomID) {
		return jdbcClient.sql("SELECT id, rid AS room_i_d, type, sender, content, send_date " +
							  "FROM message " +
							  "WHERE rid = ?")
				.params(roomID)
				.query(Message.class)
				.list();
	}
}
