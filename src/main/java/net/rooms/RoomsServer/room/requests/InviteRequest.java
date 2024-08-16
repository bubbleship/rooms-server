package net.rooms.RoomsServer.room.requests;

public record InviteRequest(
		long roomID,
		String username
) {
}
