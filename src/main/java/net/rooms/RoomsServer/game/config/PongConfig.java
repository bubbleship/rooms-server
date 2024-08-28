package net.rooms.RoomsServer.game.config;

import net.rooms.RoomsServer.game.GameRepository;

public record PongConfig(
		int maxPlayers,
		int winScore
) implements GameConfig {
	public static final int MAX_PLAYERS = 4;
	public static final int MIN_PLAYERS = 2;

	public boolean verify() {
		return maxPlayers <= MAX_PLAYERS && maxPlayers >= MIN_PLAYERS;
	}

	@Override
	public boolean verify(GameRepository.GameEntry entry) {
		return entry.participants().size() <= maxPlayers;
	}
}
