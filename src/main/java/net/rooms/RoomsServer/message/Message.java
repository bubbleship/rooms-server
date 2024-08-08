package net.rooms.RoomsServer.message;

import java.time.LocalDateTime;

public record Message(
		long roomID, // The room to which the message is for
		long index,
		MessageType type,
		String sender, // Username of the sender of this message
		String content,
		LocalDateTime sendDate
) {
	public static final Message EMPTY = new Message(-1, 0, MessageType.MESSAGE, "", "", null);
}
