package net.rooms.RoomsServer.message.requests;

import net.rooms.RoomsServer.message.MessageType;

public record MessageRequest(
		long roomID,
		MessageType type,
		String content
) {
}
