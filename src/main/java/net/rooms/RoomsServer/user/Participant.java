package net.rooms.RoomsServer.user;

import java.time.LocalDateTime;

/**
 * Represents a participant in a room. Contains only the public properties of a user. See
 * {@link User}.
 *
 * @param roomID     The identifier of the room where the specified user is a participant.
 * @param nickname   The display name of the participant.
 * @param username   The username of the participant.
 * @param signupDate The date when the user was created.
 */
public record Participant(
		long roomID,
		String nickname,
		String username,
		LocalDateTime signupDate
) {
}
