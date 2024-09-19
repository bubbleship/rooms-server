package net.rooms.RoomsServer.user;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

/**
 * Represents a user.
 * Instances of this class holds the details of a user the same way they are stored in a database.
 *
 * @param nickname   A display name for the user, may not be unique.
 * @param username   The identifier of the user, must be unique.
 * @param password   The password set by the user to log in, stored encrypted.
 * @param role       Indicates whether the user is a regular user or an admin.
 * @param signupDate The date when the user was created.
 */
public record User(
		String nickname,
		String username,
		String password,
		UserRole role,
		LocalDateTime signupDate
) implements UserDetails {

	public static final User EMPTY = new User("", "", "", UserRole.USER, LocalDateTime.MIN);

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		SimpleGrantedAuthority authority =
				new SimpleGrantedAuthority(role.name());
		return Collections.singletonList(authority);
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return username;
	}
}
