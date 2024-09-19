package net.rooms.RoomsServer.game.requests;

import net.rooms.RoomsServer.websocket.util.WSRequest;

public record ParticipationRequest(
		long id, // Game id
		String jSessionID
) implements WSRequest {
}
