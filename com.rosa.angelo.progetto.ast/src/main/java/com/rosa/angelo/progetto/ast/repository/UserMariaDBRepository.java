package com.rosa.angelo.progetto.ast.repository;

import java.sql.*;

import com.rosa.angelo.progetto.ast.model.User;

public class UserMariaDBRepository implements UserRepository {

	public static final String IMAGE = System.getProperty("mariadb.image", "mariadb");
	public static final String VERSION = System.getProperty("mariadb.version", "10.9");
	public static final int PORT = Integer.parseInt(System.getProperty("mariadb.port", "3306"));

	public static final String AST_DB_NAME = System.getProperty("mariadb.dbname", "testdb");
	public static final String USER_TABLE_NAME = "users";
	public static final String DB_USERNAME = System.getProperty("mariadb.user", "testuser");
	public static final String DB_PASSWORD = System.getProperty("mariadb.password", "password");

	public static final String ID_KEY = "id";
	public static final String USERNAME_KEY = "username";
	public static final String PASSWORD_KEY = "password";

	private Connection connection;

	public UserMariaDBRepository(Connection connection) {
		this.connection = connection;
	}

	@Override
	public void save(User user) throws SQLException {
		if (user == null || user.getUsername() == null || user.getPassword() == null) {
			return;
		}
		String query = "INSERT INTO %s (username,password,id) VALUES (?,?,?)";
		String statement = String.format(query, USER_TABLE_NAME);
		try (PreparedStatement stmt = connection.prepareStatement(statement)) {
			stmt.setString(1, user.getUsername());
			stmt.setString(2, user.getPassword());
			stmt.setInt(3, user.getId());
			stmt.executeUpdate();
		}
	}

	@Override
	public String getRegistrationToken() {
		/*
		 * This is just a simplification for the purpose of the project Registration
		 * tokens should be single disposable after each registration
		 */
		return REGISTRATION_TOKEN;
	}

	@Override
	public User findUserById(int id) throws SQLException {
		String query = "SELECT * from %s WHERE id = ?";
		String statement = String.format(query, USER_TABLE_NAME);
		try (PreparedStatement stmt = connection.prepareStatement(statement)) {
			stmt.setInt(1, id);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				return databaseToUser(rs);
			}
		}
		return null;
	}

	private User databaseToUser(ResultSet rs) throws SQLException {
		return new User(rs.getString("username"), rs.getString("password"), rs.getInt("id"));
	}
	
	@Override
	public User findUserByUsername(String username) throws SQLException {
		String query = "SELECT * from %s WHERE username = ?";
		String statement = String.format(query, USER_TABLE_NAME);
		try (PreparedStatement stmt = connection.prepareStatement(statement)) {
			stmt.setString(1, username);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				return databaseToUser(rs);
			}
		}
		return null;
	}

	@Override
	public User findUserByUsernameAndPassword(String username, String password) {
		// TODO Auto-generated method stub
		return null;
	}
}
