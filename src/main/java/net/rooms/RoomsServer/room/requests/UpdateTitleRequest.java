package net.rooms.RoomsServer.room.requests;

/**
 * Represents a title update request for a specific room. Requests for updating the title of a room
 * are converted to this record for easy access. Contains information set by the user.
 *
 * @param roomID The identifier of the room to update.
 * @param title  The new title for the specified room.
 */
public record UpdateTitleRequest(
		long roomID,
		String title
) {
}
