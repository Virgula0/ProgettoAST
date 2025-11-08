package com.rosa.angelo.progetto.ast.controller;

import java.sql.SQLException;
import java.util.Objects;
import java.util.Optional;

import com.rosa.angelo.progetto.ast.model.User;
import com.rosa.angelo.progetto.ast.repository.UserRepository;
import com.rosa.angelo.progetto.ast.view.LoginView;

public class UserController {
	private LoginView loginView;
	private UserRepository userRepo;

	public UserController(LoginView view, UserRepository userRepo) {
		this.loginView = view;
		this.userRepo = userRepo;
	}

	public void newUser(User user, String token) {

		if (!Objects.equals(token, userRepo.getRegistrationToken())) {
			loginView.showError("Invalid registration token");
			return;
		}

		if (user == null) {
			loginView.showError("Invalid null user passed", null);
			return;
		}

		User found = null;
		try {
			found = userRepo.findUserById(user.getId());

			if (found == null) {
				found = userRepo.findUserByUsername(user.getUsername());
			}

		} catch (SQLException ex) {
			handleSQLException(ex);
			return;
		}

		if (found != null) {
			loginView.showError("Already existing user ", found);
			return;
		}

		if (user.getPassword().length() < 8) {
			loginView.showError("Username must be greater or equal than 8 chars ", user);
			return;
		}
		try {
			userRepo.save(user);
		} catch (SQLException ex) {
			handleSQLException(ex);
		}
	}

	private void handleSQLException(SQLException ex) {
		loginView.showError("Exception occurred in repository: " + ex.getMessage());
	}

	public void login(String username, String password) {

		try {
			if (userRepo.findUserByUsernameAndPassword(username, password) == null) {
				loginView.showError("Invalid credentials");
				return;
			}
		} catch (SQLException ex) {
			handleSQLException(ex);
			return;
		}

		loginView.switchPanel();
	}

}
