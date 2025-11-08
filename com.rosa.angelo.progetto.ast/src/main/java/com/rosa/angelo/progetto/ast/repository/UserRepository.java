package com.rosa.angelo.progetto.ast.repository;

import java.sql.SQLException;

import com.rosa.angelo.progetto.ast.model.User;

public interface UserRepository {
	// here because used both by mongo and mariadb repositories
	static final String REGISTRATION_TOKEN = "validToken";

	void save(User user) throws SQLException;

	String getRegistrationToken();

	User findUserById(int id) throws SQLException;

	User findUserByUsernameAndPassword(String username, String password);

	User findUserByUsername(String username) throws SQLException;
}
