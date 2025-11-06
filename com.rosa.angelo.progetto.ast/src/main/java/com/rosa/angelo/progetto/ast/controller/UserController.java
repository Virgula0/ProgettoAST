package com.rosa.angelo.progetto.ast.controller;

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

	public void newUser(User user) {
		if (user == null) {
			loginView.showError("Invalid null user passed", null);
			return;
		}
		
		User checkUser = userRepo.findUserById(user.getId());
		
		if (checkUser != null) {
			loginView.showError("Already existing user ", checkUser);
			return;
		}
		
		userRepo.save(user);
	}

	public void login(String username, String password) {
		if (userRepo.findUserByUsernameAndPassword(username, password) == null){
			loginView.showError("Invalid credentials");
			return;
		}
		
		loginView.switchPanel();
	}
	
}
