package net.rooms.RoomsServer.registration;

import net.rooms.RoomsServer.user.UserRole;

/**
 * Represents a signup request. Requests for creating a new account are converted to this record
 * for easy access.
 *
 * @param nickname The display name of the new user.
 * @param username The username of the new user (used for authentication).
 * @param password The password for the account (used for authentication).
 * @param role     Indicates whether to create a user or an admin account,
 */
public record RegistrationRequest(
		String nickname,
		String username,
		String password,
		UserRole role
) {
}
