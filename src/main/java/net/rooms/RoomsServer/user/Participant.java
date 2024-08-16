package net.rooms.RoomsServer.user;

import java.time.LocalDateTime;

public record Participant(
		long roomID,
		String nickname,
		String username,
		LocalDateTime signupDate
) {
}
