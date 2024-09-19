package net.rooms.RoomsServer.room;

import java.time.LocalDateTime;

/**
 * Represents a single room the same way it would appear on search queries.
 * The fields below are publicly visible to all users of the platform.
 *
 * @param roomID       A unique identifier for the room.
 * @param title        The title/name of the room.
 * @param hasPassword  Indicates whether the room has a password.
 * @param owner        The username of the user that created the room.
 * @param creationDate The date and time when the room was created.
 * @param description  A string of text that describes the room.
 */
public record PublicRoom(
		long roomID,
		String title,
		boolean hasPassword,
		String owner,
		LocalDateTime creationDate,
		String description
) {
}
