package net.rooms.RoomsServer.game.config;

import net.rooms.RoomsServer.game.GameRepository;

public interface GameConfig {
	boolean verify();
	boolean verify(GameRepository.GameEntry entry);
}
