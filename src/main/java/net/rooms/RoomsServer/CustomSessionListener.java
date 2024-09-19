package net.rooms.RoomsServer;

import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A listener and repository of session IDs used exclusively to provide authentication for requests
 * received over the WS connection.
 */
@Component
public class CustomSessionListener implements HttpSessionListener {

	/**
	 * A {@link Map} used to associate session ID tokens with their respected sessions.
	 */
	private static final Map<String, HttpSession> sessions = new ConcurrentHashMap<>();

	@Override
	public void sessionCreated(HttpSessionEvent event) {
		HttpSession session = event.getSession();
		// Queries to this class are expected to begin with "JSESSIONID=".
		sessions.put("JSESSIONID=" + session.getId(), session);
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent event) {
		HttpSession session = event.getSession();
		// Queries to this class are expected to begin with "JSESSIONID=".
		sessions.remove("JSESSIONID=" + session.getId());
	}

	public static HttpSession getSession(String sessionId) {
		return sessions.get(sessionId);
	}
}

