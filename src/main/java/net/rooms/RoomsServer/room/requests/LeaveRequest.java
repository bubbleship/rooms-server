package net.rooms.RoomsServer.room.requests;

/**
 * Represents a request to leave a room. Requests for leaving a room are converted to this record
 * for easy access.
 *
 * @param roomID The room the user is attempting to leave.
 */
public record LeaveRequest(
		long roomID
) {
}
