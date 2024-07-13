package net.rooms.RoomsServer.user;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
	List<User> findAll();

	Optional<User> findById(Long uid);

	Optional<User> findByUsername(String username);

	void create(User user);

	void update(User user, Long uid);

	void delete(Long uid);

	int count();

	Long lastID();

	void saveAll(List<User> users);
}
