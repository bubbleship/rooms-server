package net.rooms.RoomsServer.room.requests;

public record JoinRequest(
		long roomID,
		String password
) {
}
