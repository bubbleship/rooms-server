package net.rooms.RoomsServer.user;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class JdbcUserRepository implements UserRepository{

	private final JdbcClient jdbcClient;

	@Override
	public List<User> findAll() {
		return jdbcClient.sql("SELECT * FROM users")
				.query(User.class)
				.list();
	}

	@Override
	public Optional<User> findByUsername(String username) {
		return jdbcClient.sql("SELECT * FROM users WHERE username = ?" )
				.params(username)
				.query(User.class)
				.optional();
	}

	@Override
	public void create(User user) {
		var updated = jdbcClient.sql("INSERT INTO users(nickname,username,password,role,signup_date) VALUES(?,?,?,?,?)")
				.params(List.of(user.nickname(), user.username(), user.password(), user.role().ordinal(), user.signupDate()))
				.update();
		Assert.state(updated == 1, "Failed to create user " + user.username());
	}

	@Override
	public void update(User user, String username) {
		var updated = jdbcClient.sql("UPDATE users SET nickname = ?, username = ?, password = ?, role = ? WHERE username = ?")
				.params(List.of(user.nickname(),user.username(),user.password(),user.role(), username))
				.update();

		Assert.state(updated == 1, "Failed to update user " + user.username());
	}

	@Override
	public void delete(String username) {
		var updated = jdbcClient.sql("DELETE FROM users WHERE username = ?")
				.params(username)
				.update();

		Assert.state(updated == 1, "Failed to delete user " + username);
	}

	@Override
	public int count() {
		return jdbcClient.sql("SELECT * FROM users").query().listOfRows().size();
	}

	@Override
	public void saveAll(List<User> users) {
		users.forEach(this::create);
	}
}
