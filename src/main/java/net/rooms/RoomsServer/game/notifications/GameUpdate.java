package net.rooms.RoomsServer.game.notifications;

import net.rooms.RoomsServer.game.config.GameConfig;

import java.util.List;

public record GameUpdate(
		GameConfig config,
		String username,
		List<String> participants
) {
}
