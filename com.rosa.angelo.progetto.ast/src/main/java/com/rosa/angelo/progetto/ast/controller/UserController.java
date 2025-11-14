package com.rosa.angelo.progetto.ast.controller;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.Objects;

import com.google.inject.BindingAnnotation;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.rosa.angelo.progetto.ast.model.User;
import com.rosa.angelo.progetto.ast.repository.GenericRepositoryException;
import com.rosa.angelo.progetto.ast.repository.UserRepository;
import com.rosa.angelo.progetto.ast.view.LoginView;

public class UserController {

	@BindingAnnotation
	@Target({ FIELD, PARAMETER, METHOD })
	@Retention(RUNTIME)
	public static @interface RepoType {
	}

	private LoginView loginView;
	private UserRepository userRepo;

	@Inject
	public UserController(@Assisted LoginView view, @RepoType UserRepository userRepo) {
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

		} catch (GenericRepositoryException ex) {
			hadleRepoException(ex);
			return;
		}

		if (found != null) {
			loginView.showError("Already existing user by id or username similarity ", found);
			return;
		}

		if (user.getPassword().length() < 8) {
			loginView.showError("Password must be greater or equal than 8 chars ", user);
			return;
		}
		try {
			userRepo.save(user);
		} catch (GenericRepositoryException ex) {
			hadleRepoException(ex);
		}
	}

	private void hadleRepoException(GenericRepositoryException ex) {
		loginView.showError("Exception occurred in repository: " + ex.getMessage());
	}

	public void login(String username, String password) {
		User user;

		try {
			user = userRepo.findUserByUsernameAndPassword(username, password);
			if (user == null) {
				loginView.showError("Invalid credentials");
				return;
			}
		} catch (GenericRepositoryException ex) {
			hadleRepoException(ex);
			return;
		}

		loginView.switchPanel(user);
	}

}
