package com.rosa.angelo.progetto.ast.repository;

import java.sql.Connection;

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
	public void save(User user) {
		// TODO Auto-generated method stub

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
	public User findUserById(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public User findUserByUsernameAndPassword(String username, String password) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public User findUserByUsername(String username) {
		// TODO Auto-generated method stub
		return null;
	}

}
