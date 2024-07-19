package net.rooms.RoomsServer.room;

/**
 * Represents a room creation request. Requests for creating a new room are converted to this
 * record for easy access. Contains information set by the user.
 *
 * @param title     The display title of the room.
 * @param isPrivate Indicates whether the room requires password.
 * @param password  The password for the room, may be empty if the room is not private.
 */
public record CreateRequest(
		String title,
		boolean isPrivate,
		String password
) {
}
