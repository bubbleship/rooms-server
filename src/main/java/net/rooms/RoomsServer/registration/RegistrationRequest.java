package net.rooms.RoomsServer.registration;

import net.rooms.RoomsServer.user.UserRole;

/**
 * Represents a signup request.
 */
public record RegistrationRequest(
		String nickname,
		String username,
		String password,
		UserRole role
) {
}
