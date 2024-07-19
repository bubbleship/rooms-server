package net.rooms.RoomsServer.room;

public record CreateRequest(
		String title,
		boolean isPrivate,
		String password
) {
}
