package net.rooms.RoomsServer.room;

import lombok.AllArgsConstructor;
import net.rooms.RoomsServer.user.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class RoomController {

	private final RoomService roomService;

	@PostMapping(path = "api/v1/room/create")
	public String create(@RequestBody CreateRequest request, @AuthenticationPrincipal User user) {
		return roomService.create(request, user);
	}
}
