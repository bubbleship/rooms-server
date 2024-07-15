package net.rooms.RoomsServer.user;

import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {

	private final static String USER_NOT_FOUND_MSG = "user with username %s not found";

	private final UserRepository userRepository;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return userRepository.findByUsername(username)
				.orElseThrow(() ->
						new UsernameNotFoundException(
								String.format(USER_NOT_FOUND_MSG, username)));
	}

	public void signupUser(String nickname, String username, String password, UserRole role) {
		boolean userExists = userRepository
				.findByUsername(username)
				.isPresent();

		if (userExists) throw new IllegalStateException("username already taken");

		password = bCryptPasswordEncoder.encode(password);

		userRepository.create(new User(nickname, username, password, role, LocalDateTime.now()));
	}
}
