package net.rooms.RoomsServer.user;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Optional;

@Repository
public class JdbcUserRepository implements UserRepository{

	private final JdbcClient jdbcClient;

	public JdbcUserRepository(JdbcClient jdbcClient) {
		this.jdbcClient = jdbcClient;
	}

	@Override
	public List<User> findAll() {
		return jdbcClient.sql("SELECT * FROM users")
				.query(User.class)
				.list();
	}

	@Override
	public Optional<User> findById(Long uid) {
		return jdbcClient.sql("SELECT * FROM users WHERE uid = :uid" )
				.param("uid", uid)
				.query(User.class)
				.optional();
	}

	@Override
	public Optional<User> findByUsername(String username) {
		return jdbcClient.sql("SELECT * FROM users WHERE username = :username" )
				.param("username", username)
				.query(User.class)
				.optional();
	}

	@Override
	public void create(User user) {
		var updated = jdbcClient.sql("INSERT INTO users(uid,nickname,username,password,user_role,signup_date) VALUES(?,?,?,?,?,?)")
				.params(List.of(user.uid(), user.nickname(), user.username(), user.password(), user.role(), user.signupDate()))
				.update();
		Assert.state(updated == 1, "Failed to create user " + user.username());
	}

	@Override
	public void update(User user, Long uid) {
		var updated = jdbcClient.sql("UPDATE users SET nickname = ?, username = ?, password = ?, user_role = ? WHERE uid = ?")
				.params(List.of(user.nickname(),user.username(),user.password(),user.role(), uid))
				.update();

		Assert.state(updated == 1, "Failed to update user " + user.username() + ":" + user.uid());
	}

	@Override
	public void delete(Long uid) {
		var updated = jdbcClient.sql("DELETE FROM users WHERE uid = :uid")
				.param("uid", uid)
				.update();

		Assert.state(updated == 1, "Failed to delete user " + uid);
	}

	@Override
	public int count() {
		return jdbcClient.sql("SELECT * FROM users").query().listOfRows().size();
	}

	@Override
	public Long lastID() {
		return (Long) jdbcClient.sql("SELECT MAX(uid) FROM users").query().singleValue();
	}

	@Override
	public void saveAll(List<User> users) {
		users.forEach(this::create);
	}
}
