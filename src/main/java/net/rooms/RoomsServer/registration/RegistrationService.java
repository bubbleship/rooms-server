package net.rooms.RoomsServer.registration;

import lombok.AllArgsConstructor;
import net.rooms.RoomsServer.user.UserService;
import org.springframework.stereotype.Service;

/**
 * The backend of the signup process.
 */
@Service
@AllArgsConstructor
public class RegistrationService {

	private final UserService userService;

	/**
	 * Verify the signup details.
	 *
	 * @param request
	 * @return A string holding an error message in case the signup fails, otherwise success.
	 */
	public String register(RegistrationRequest request) {
		if (request.username().contains("\"")) return "Invalid username";
		if (request.nickname().contains("\"")) return "Nickname cannot contain \" symbol";
		if (request.password().length() < 8) return "Password is too short";
		if (request.password().contains("\"")) return "Invalid character: \"";

		return userService.signupUser(request.nickname(), request.username(), request.password(), request.role());
	}
}
