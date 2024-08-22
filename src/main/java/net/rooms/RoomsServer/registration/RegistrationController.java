package net.rooms.RoomsServer.registration;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * The API endpoint that accepts signup requests.
 */
@RestController
@RequestMapping(path = "api/v1/registration")
@AllArgsConstructor
public class RegistrationController {

	private final RegistrationService registrationService;

	/**
	 * Accepts REST API POST requests for creating a new user account.
	 * JSON content request example:
	 * <code>
	 * {
	 *   "nickname" : "nickname",
	 *   "username" : "username",
	 *   "password" : "12345678",
	 *   "role" : 0
	 * }
	 * </code>
	 *
	 * @param request The details for the new account.
	 * @return A string with an error message in case the operation failed. Otherwise, "success".
	 */
	@PostMapping
	public String register(@RequestBody RegistrationRequest request) {
		return registrationService.register(request);
	}

}
