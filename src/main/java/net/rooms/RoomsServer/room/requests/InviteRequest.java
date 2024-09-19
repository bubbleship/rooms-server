package net.rooms.RoomsServer.room.requests;

/**
 * Represents a room invitation request used to invite a user to a room. Requests for inviting a
 * user to a room are converted to this record for easy access.
 *
 * @param roomID   The identifier of the room the user is invited to.
 * @param username The username of the invited user.
 */
public record InviteRequest(
		long roomID,
		String username
) {
}
