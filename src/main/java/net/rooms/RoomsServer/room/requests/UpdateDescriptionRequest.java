package net.rooms.RoomsServer.room.requests;

/**
 * Represents a description update request for a specific room. Requests for updating the
 * description of a room are converted to this record for easy access. Contains information set by the user.
 *
 * @param roomID      The identifier of the room to update.
 * @param description The new description for the specified room.
 */
public record UpdateDescriptionRequest(
		long roomID,
		String description
) {
}
