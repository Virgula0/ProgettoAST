package com.rosa.angelo.progetto.ast.controller;

import com.rosa.angelo.progetto.ast.model.User;
import com.rosa.angelo.progetto.ast.repository.UserRepository;
import com.rosa.angelo.progetto.ast.view.LoginView;

public class UserController {
	private LoginView view;
	private UserRepository userRepo; 
	
	public UserController(LoginView view, UserRepository userRepo) {
		this.view = view;
		this.userRepo = userRepo;
	}

	public void newUser(User user) {
		if (user == null) {
			view.showError("Invalid null user passed", null);
			return;
		}
		
		User checkUser = userRepo.findUserById(user.getId());
		
		if (checkUser != null) {
			view.showError("Already existing user ", user);
			return;
		}
		
		userRepo.save(user);
	}

	public void login(String username, String password) {
		if (userRepo.findUserByUsernameAndPassword(username, password) == null){
			view.showError("Invalid credentials");
			return;
		}
		
		view.switchPanel();
	}
	
}
