package net.rooms.RoomsServer.websocket.util;

import jakarta.servlet.http.HttpSession;
import lombok.NonNull;
import net.rooms.RoomsServer.CustomSessionListener;
import net.rooms.RoomsServer.user.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;

public final class WSAuth {
	public static @NonNull User getUser(WSRequest request) {
		HttpSession session = CustomSessionListener.getSession(request.jSessionID());
		if (session == null) return User.EMPTY;

		SecurityContext securityContext = (SecurityContext) session.getAttribute("SPRING_SECURITY_CONTEXT");
		if (securityContext == null) return User.EMPTY;

		Authentication authentication = securityContext.getAuthentication();
		if (authentication == null) return User.EMPTY;

		return (User) authentication.getPrincipal();
	}
}
