package com.rosa.angelo.progetto.ast.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.google.inject.Inject;
import com.rosa.angelo.progetto.ast.model.User;

public class UserMariaDBRepository implements UserRepository {

	public static final String ID_KEY = "id";
	public static final String USERNAME_KEY = "username";
	public static final String PWD_DB_KEY = "password";

	public static final String IMAGE = System.getProperty("mariadb.image", "mariadb");
	public static final String VERSION = System.getProperty("mariadb.version", "10.9");
	public static final int PORT = Integer.parseInt(System.getProperty("mariadb.port", "3306"));

	public static final String AST_DB_NAME = System.getProperty("mariadb.dbname", "testdb");
	public static final String USER_TABLE_NAME = "users";
	public static final String DB_USERNAME = System.getProperty("mariadb.user", "testuser");
	public static final String DB_PASSWORD = System.getProperty("mariadb.password", PWD_DB_KEY);

	private Connection connection;

	@Inject
	public UserMariaDBRepository(Connection connection) {
		this.connection = connection;
	}

	private String saveQuery = "INSERT INTO %s (username,password,id) VALUES (?,?,?)";
	private String userByIdQuery = "SELECT * from %s WHERE id = ?";
	private String userByUsernameQuery = "SELECT * from %s WHERE username = ?";
	private String userByUsernameAndPasswordQuery = "SELECT * from %s WHERE username = ? AND password = ?";

	void injectSaveQuery(String toInject) {
		this.saveQuery = toInject;
	}

	void injectUserByIDQuery(String toInject) {
		this.userByIdQuery = toInject;
	}

	void injectuserByUsernameQuery(String toInject) {
		this.userByUsernameQuery = toInject;
	}

	void injectuserByUsernameAndPasswordQuery(String toInject) {
		this.userByUsernameAndPasswordQuery = toInject;
	}

	private GenericRepositoryException handleDBException(SQLException ex) {
		return new GenericRepositoryException(ex.getMessage());
	}

	@Override
	public void save(User user) throws GenericRepositoryException {
		if (user == null || user.getUsername() == null || user.getPassword() == null) {
			return;
		}
		String statement = String.format(saveQuery, USER_TABLE_NAME);
		try (PreparedStatement stmt = connection.prepareStatement(statement)) {
			stmt.setString(1, user.getUsername());
			stmt.setString(2, user.getPassword());
			stmt.setInt(3, user.getId());
			stmt.executeUpdate();
		} catch (SQLException ex) {
			throw (handleDBException(ex));
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
	public User findUserById(int id) throws GenericRepositoryException {
		String statement = String.format(userByIdQuery, USER_TABLE_NAME);
		try (PreparedStatement stmt = connection.prepareStatement(statement)) {
			stmt.setInt(1, id);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				return databaseToUser(rs);
			}
		} catch (SQLException ex) {
			throw (handleDBException(ex));
		}
		return null;
	}

	private User databaseToUser(ResultSet rs) throws SQLException {
		return new User(rs.getString(USERNAME_KEY), rs.getString(PWD_DB_KEY), rs.getInt(ID_KEY));
	}

	@Override
	public User findUserByUsername(String username) throws GenericRepositoryException {
		String statement = String.format(userByUsernameQuery, USER_TABLE_NAME);
		try (PreparedStatement stmt = connection.prepareStatement(statement)) {
			stmt.setString(1, username);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				return databaseToUser(rs);
			}
		} catch (SQLException ex) {
			throw (handleDBException(ex));
		}
		return null;
	}

	@Override
	public User findUserByUsernameAndPassword(String username, String password) throws GenericRepositoryException {
		String statement = String.format(userByUsernameAndPasswordQuery, USER_TABLE_NAME);
		try (PreparedStatement stmt = connection.prepareStatement(statement)) {
			stmt.setString(1, username);
			stmt.setString(2, password);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				return databaseToUser(rs);
			}
		} catch (SQLException ex) {
			throw (handleDBException(ex));
		}
		return null;
	}
}
