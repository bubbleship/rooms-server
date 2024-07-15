package net.rooms.RoomsServer.user;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
	List<User> findAll();

	Optional<User> findByUsername(String username);

	void create(User user);

	void update(User user, String username);

	void delete(String username);

	int count();

	void saveAll(List<User> users);
}
