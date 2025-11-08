package com.rosa.angelo.progetto.ast.repository;

import com.rosa.angelo.progetto.ast.model.User;

public interface UserRepository {
	// here because used both by mongo and mariadb repositories
	static final String REGISTRATION_TOKEN = "validToken";

	void save(User user) throws GenericRepositoryException;

	String getRegistrationToken();

	User findUserById(int id) throws GenericRepositoryException;

	User findUserByUsernameAndPassword(String username, String password) throws GenericRepositoryException;

	User findUserByUsername(String username) throws GenericRepositoryException;
}
