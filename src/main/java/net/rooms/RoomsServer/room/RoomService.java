package net.rooms.RoomsServer.room;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.AllArgsConstructor;
import net.rooms.RoomsServer.adapters.LocalDateTimeAdapter;
import net.rooms.RoomsServer.user.User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class RoomService {

	private final RoomRepository roomRepository;

	public String create(CreateRequest request, User user) {
		if (request.title().contains("\"")) return "Invalid character: \"";
		if (request.password().contains("\"")) return "Invalid character: \"";

		long roomID = roomRepository.lastID() + 1;
		Room room = new Room(roomID, request.title(), request.isPrivate(), request.password(), user.username(), LocalDateTime.now());
		if (!roomRepository.create(room)) return "Room creation failed on create";
		if (!roomRepository.joinUser(roomID, user.username())) {
			roomRepository.delete(roomID);
			return "Room creation failed on join";
		}
		return "success";
	}

	public String list(User user) {
		Gson gson = new GsonBuilder()
				.setPrettyPrinting()
				.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
				.create();
		return gson.toJson(roomRepository.listByUser(user.username()));
	}
}
