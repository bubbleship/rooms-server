package net.rooms.RoomsServer.websocket;

import lombok.RequiredArgsConstructor;
import net.rooms.RoomsServer.game.GameController;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;

@Component
@RequiredArgsConstructor
public class WebSocketEventListener {

	private final GameController gameController;

	@EventListener
	public void disconnect(SessionDisconnectEvent event) {
		Principal principal = event.getUser();
		if (principal == null) return;
		gameController.leaveGameUsername(principal.getName());
	}
}
