package net.rooms.RoomsServer.room.requests;

/**
 * Represents a room creation request. Requests for creating a new room are converted to this
 * record for easy access. Contains information set by the user.
 *
 * @param title     The display title of the room.
 * @param isPrivate Indicates whether the room would appear on public search queries.
 * @param password  The password for the room, may be empty if there is no password for the room.
 */
public record CreateRequest(
		String title,
		boolean isPrivate,
		String password,
		String description
) {
}
