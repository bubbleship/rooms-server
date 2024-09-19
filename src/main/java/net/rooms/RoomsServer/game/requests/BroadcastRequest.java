package net.rooms.RoomsServer.game.requests;

import net.rooms.RoomsServer.websocket.util.WSRequest;

public record BroadcastRequest(
		long id, // Game ID
		String payload, // Json payload containing the game packet that needs transfer
		String jSessionID
) implements WSRequest {
}
