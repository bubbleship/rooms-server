package net.rooms.RoomsServer.room;

import java.time.LocalDateTime;

public record PublicRoom(
		long roomID,
		String title,
		String owner,
		LocalDateTime creationDate,
		String description
) {
}
