package com.rosa.angelo.progetto.ast.repository;

import com.rosa.angelo.progetto.ast.model.User;

public interface UserRepository {
	void save(User user);
	String getRegistrationToken();
	User findUserById(int id);
	User findUserByUsernameAndPassword(String username, String password);
}
