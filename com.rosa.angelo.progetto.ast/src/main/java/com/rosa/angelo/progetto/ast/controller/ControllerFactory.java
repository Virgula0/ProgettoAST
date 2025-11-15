package com.rosa.angelo.progetto.ast.controller;

import com.rosa.angelo.progetto.ast.view.LoginView;
import com.rosa.angelo.progetto.ast.view.ProductView;

public interface ControllerFactory {
	UserController create(LoginView view);

	ProductController create(ProductView view);
}
