package net.rooms.RoomsServer.room.requests;

/**
 * Represents a request to join a room. Requests for joining a room are converted to this record
 * for easy access.
 *
 * @param roomID   The identifier of the room the user is attempting to join.
 * @param password The password given by the user to join the room.
 */
public record JoinRequest(
		long roomID,
		String password
) {
}
