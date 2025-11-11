package com.rosa.angelo.progetto.ast.controller;

import com.rosa.angelo.progetto.ast.model.User;
import com.rosa.angelo.progetto.ast.repository.ProductRepository;
import com.rosa.angelo.progetto.ast.view.ProductView;

public class ProductController {
	private ProductView productView;
	private ProductRepository productRepository;
	
	public ProductController(ProductView productView, ProductRepository productRepository) {
		this.productView = productView;
		this.productRepository = productRepository;
	}

	public void allProducts(User logggedIn) {
		productView.showAllProductsSentByUser(productRepository.findAllProductsSentByUser(logggedIn));
	}
}
