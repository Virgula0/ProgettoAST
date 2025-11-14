package com.rosa.angelo.progetto.ast.controller;

import com.rosa.angelo.progetto.ast.view.ProductView;

public interface ProductControllerFactory {
	ProductController create(ProductView view);
}
