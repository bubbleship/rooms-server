package net.rooms.RoomsServer.registration;

import lombok.AllArgsConstructor;
import net.rooms.RoomsServer.user.UserService;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RegistrationService {

	private final UserService userService;

	public String register(RegistrationRequest request) {
		if (request.username().contains("\"")) throw new IllegalStateException("Invalid username");
		if (request.nickname().contains("\"")) throw new IllegalStateException("Nickname cannot contain \" symbol");
		if (request.password().length() < 8) throw new IllegalStateException("Password is too short");
		if (request.password().contains("\"")) throw new IllegalStateException("Invalid character: \"");

		userService.signupUser(request.nickname(), request.username(), request.password(), request.role());
		return "success";
	}
}
