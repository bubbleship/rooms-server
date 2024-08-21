package net.rooms.RoomsServer.room;

import java.time.LocalDateTime;

/**
 * Represent a single room the same way it is stored in the 'room' table in the database.
 *
 * @param roomID       A unique identifier for the room.
 * @param title        The title/name of the room.
 * @param isPrivate    Indicates whether the room would appear on public search queries.
 * @param password     A key phrase required to enter the room.
 * @param owner        The username of the user that created the room.
 * @param creationDate The date and time when the room was created.
 * @param description  A string of text that describes the room.
 */
public record Room(
		long roomID,
		String title,
		boolean isPrivate,
		String password,
		String owner,
		LocalDateTime creationDate,
		String description
) {
}
