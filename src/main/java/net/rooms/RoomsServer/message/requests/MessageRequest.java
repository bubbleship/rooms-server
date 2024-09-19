package net.rooms.RoomsServer.message.requests;

import net.rooms.RoomsServer.message.MessageType;
import net.rooms.RoomsServer.websocket.util.WSRequest;

/**
 * Represents a message creation request. Requests for posting a new message in a room are
 * converted to this record for easy access. Contains information set by the user.
 * This is a WS request, meaning it should be sent to the server via a web socket connection.
 *
 * @param roomID     A unique identifier for the room on which to post this message.
 * @param type       Indicates how the message properties should be interpreted.
 * @param content    The content of the message.
 * @param jSessionID The session id of the currently logged-in user.
 */
public record MessageRequest(
		long roomID,
		MessageType type,
		String content,
		String jSessionID
) implements WSRequest {
}
