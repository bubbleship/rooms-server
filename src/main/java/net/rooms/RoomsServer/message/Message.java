package net.rooms.RoomsServer.message;

import java.time.LocalDateTime;

/**
 * Represents a single message in a room the same way it is stored in the 'message' table in the
 * database.
 *
 * @param id       A unique identifier for the message.
 * @param roomID   A unique identifier for the room to which the message is for.
 * @param type     Indicates how the message properties should be interpreted.
 * @param sender   The username of the author of this message.
 * @param content  The content of the message.
 * @param sendDate The date and time the message was processed by the server.
 */
public record Message(
		long id,
		long roomID, // The room to which the message is for
		MessageType type,
		String sender, // Username of the sender of this message
		String content,
		LocalDateTime sendDate
) {
	public static final Message EMPTY = new Message(-1, -1, MessageType.MESSAGE, "", "", null);
}
