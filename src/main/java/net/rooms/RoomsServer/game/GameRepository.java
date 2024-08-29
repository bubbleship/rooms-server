package net.rooms.RoomsServer.game;

import lombok.Synchronized;
import net.rooms.RoomsServer.JSON;
import net.rooms.RoomsServer.game.config.GameConfig;
import net.rooms.RoomsServer.game.config.GameType;
import net.rooms.RoomsServer.game.notifications.GameUpdate;
import net.rooms.RoomsServer.message.Message;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class GameRepository {
	private final HashMap<Long, GameEntry> games;
	private final Set<String> usernames;

	public GameRepository() {
		games = new HashMap<>();
		usernames = new HashSet<>();
	}

	@Synchronized
	public <T extends GameConfig> boolean open(Message message, GameType gameType, Class<T> type) {
		if (usernames.contains(message.sender())) return false;

		GameConfig config = JSON.fromJson(message.content(), type);
		if (!config.verify()) return false;

		games.put(message.id(), new GameEntry(message.sender(), gameType, config));
		usernames.add(message.sender());
		return true;
	}

	@Synchronized
	public GameUpdate join(long id, String username) {
		if (usernames.contains(username)) return null;
		if (!games.containsKey(id)) return null;

		GameEntry entry = games.get(id);
		if (!entry.state.isPending) return null;
		entry.participants.add(username);
		if (!entry.config.verify(entry)) {
			entry.participants.remove(username);
			return null;
		}

		usernames.add(username);
		return buildGameUpdate(entry, username);
	}

	@Synchronized
	public GameUpdate leave(long id, String username) {
		if (!games.containsKey(id)) return null;

		GameEntry entry = games.get(id);
		if (!entry.participants.contains(username)) return null;

		if (entry.host.equals(username)) {
			entry.participants.forEach(usernames::remove);
			games.remove(id);
		} else {
			entry.participants.remove(username);
			usernames.remove(username);
		}
		return buildGameUpdate(entry, username);
	}

	@Synchronized
	public GameUpdate startGame(long id, String username) {
		if (!games.containsKey(id)) return null;

		GameEntry entry = games.get(id);
		if (!entry.state.isPending) return null; // Game has already started
		if (!entry.host.equals(username)) return null; // Only the game host may start the game

		entry.state.isPending = false; // The game is no longer pending
		return buildGameUpdate(entry, username);
	}

	private GameUpdate buildGameUpdate(GameEntry entry, String username) {
		return new GameUpdate(entry.config(), username, entry.participants().stream().toList());
	}

	public Set<String> getGameParticipants(long id) {
		return games.get(id).participants;
	}

	public @NonNull String getHost(long id) {
		if (!games.containsKey(id)) return "";
		return games.get(id).host;
	}

	public record GameEntry(
		String host,
		Set<String> participants,
		GameType type,
		GameConfig config,
		GameState state
	) {
		public GameEntry(String sender, GameType type, GameConfig config) {
			this(sender, new HashSet<>(List.of(sender)), type, config, new GameState());
		}
	}

	private static class GameState {
		boolean isPending = true;
	}
}
