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
		int updated = jdbcClient.sql("INSERT INTO message(rid,index,type,sender,content,send_date) VALUES(?,?,?,?,?,?)")
				.params(message.roomID(),message.index(), message.type().ordinal(), message.sender(), message.content(), message.sendDate())
				.update();
		return updated == 1;
	}

	@Override
	public long lastIndex(long roomID) {
		Object result = jdbcClient.sql("SELECT MAX(index) FROM message WHERE rid = ?")
				.params(roomID)
				.query()
				.singleValue();
		//noinspection ConstantValue
		if (result == null) return 0L;
		return Long.valueOf((Integer) result);
	}

	@Override
	public List<Message> listByRoom(long roomID) {
		return jdbcClient.sql("SELECT rid AS room_i_d, index, type, sender, content, send_date " +
							  "FROM message " +
							  "WHERE rid = ?")
				.params(roomID)
				.query(Message.class)
				.list();
	}
}
