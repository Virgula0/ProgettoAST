package com.rosa.angelo.progetto.ast.controller;

import com.rosa.angelo.progetto.ast.view.LoginView;

public interface UserControllerFactory {
	UserController create(LoginView view);
}
