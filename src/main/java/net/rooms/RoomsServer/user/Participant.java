package net.rooms.RoomsServer.user;

import java.time.LocalDateTime;

public record Participant(
		String nickname,
		String username,
		LocalDateTime signupDate
) {
}
