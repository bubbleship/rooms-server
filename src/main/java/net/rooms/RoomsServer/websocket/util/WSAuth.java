package net.rooms.RoomsServer.websocket.util;

import jakarta.servlet.http.HttpSession;
import lombok.NonNull;
import net.rooms.RoomsServer.CustomSessionListener;
import net.rooms.RoomsServer.user.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;

public final class WSAuth {
	/**
	 * Provides a convenient interface for interacting with {@link CustomSessionListener} to get
	 * the currently logged-in user given a {@link WSRequest} object.
	 *
	 * @param request The request sent over the websocket connection with the session ID.
	 * @return The {@link User} associated with that session ID. {@link User#EMPTY} if the session
	 * ID is not valid.
	 */
	public static @NonNull User getUser(WSRequest request) {
		return getUser(request.jSessionID());
	}

	/**
	 * Provides a convenient interface for interacting with {@link CustomSessionListener} to get
	 * the currently logged-in user given its session ID.
	 *
	 * @param sessionID A string representing a  session ID.
	 * @return The {@link User} associated with that session ID. {@link User#EMPTY} if the session
	 * ID is not valid.
	 */
	public static @NonNull User getUser(String sessionID) {
		HttpSession session = CustomSessionListener.getSession(sessionID);
		if (session == null) return User.EMPTY;

		SecurityContext securityContext = (SecurityContext) session.getAttribute("SPRING_SECURITY_CONTEXT");
		if (securityContext == null) return User.EMPTY;

		Authentication authentication = securityContext.getAuthentication();
		if (authentication == null) return User.EMPTY;

		return (User) authentication.getPrincipal();
	}
}
