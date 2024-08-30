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
	private final HashMap<String, Long> usernames; // Username and game ID hash map

	public GameRepository() {
		games = new HashMap<>();
		usernames = new HashMap<>();
	}

	@Synchronized
	public <T extends GameConfig> boolean open(Message message, GameType gameType, Class<T> type) {
		if (usernames.containsKey(message.sender())) return false;

		GameConfig config = JSON.fromJson(message.content(), type);
		if (!config.verify()) return false;

		games.put(message.id(), new GameEntry(message.roomID(), message.sender(), gameType, config));
		usernames.put(message.sender(), message.id());
		return true;
	}

	@Synchronized
	public GameUpdate join(long id, String username) {
		if (usernames.containsKey(username)) return null;
		if (!games.containsKey(id)) return null;

		GameEntry entry = games.get(id);
		if (!entry.state.isPending) return null;
		entry.participants.add(username);
		if (!entry.config.verify(entry)) {
			entry.participants.remove(username);
			return null;
		}

		usernames.put(username, id);
		return buildGameUpdate(entry, username);
	}

	@Synchronized
	public GameUpdate leave(String username) {
		long id = getGameID(username);
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

	@Synchronized
	public boolean closeGame(long id, String username) {
		if (!games.containsKey(id)) return false;

		GameEntry entry = games.get(id);
		if (!entry.host.equals(username)) return false; // Only the game host may close the game
		if (entry.state.isPending) return false; // Pending games haven't started yet and cannot be closed
		entry.participants.forEach(usernames::remove);
		games.remove(id);
		return true;
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

	public long getGameID(String username) {
		return usernames.getOrDefault(username, 0L);
	}

	public long getRoomID(long id) {
		if (!games.containsKey(id)) return 0L;
		return games.get(id).roomID;
	}

	public record GameEntry(
		long roomID,
		String host,
		Set<String> participants,
		GameType type,
		GameConfig config,
		GameState state
	) {
		public GameEntry(long roomID, String sender, GameType type, GameConfig config) {
			this(roomID, sender, new HashSet<>(List.of(sender)), type, config, new GameState());
		}
	}

	private static class GameState {
		boolean isPending = true;
	}
}
