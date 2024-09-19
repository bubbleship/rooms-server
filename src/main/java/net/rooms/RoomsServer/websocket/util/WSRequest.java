package net.rooms.RoomsServer.websocket.util;

/**
 * Represents a request payload sent over a websocket connection. Such requests are expected to
 * contain a session ID that can be associated with a user.
 */
public interface WSRequest {
	String jSessionID();
}
