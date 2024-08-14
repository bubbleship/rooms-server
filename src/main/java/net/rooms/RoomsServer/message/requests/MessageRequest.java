package net.rooms.RoomsServer.message.requests;

import net.rooms.RoomsServer.message.MessageType;
import net.rooms.RoomsServer.websocket.util.WSRequest;

public record MessageRequest(
		long roomID,
		MessageType type,
		String content,
		String jSessionID
) implements WSRequest {
}
