package com.rosa.angelo.progetto.ast.controller;

import java.util.List;
import java.util.Objects;

import com.rosa.angelo.progetto.ast.model.Product;
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

	public void newProduct(Product productToInsert) {
		Product exists = productRepository.findProductById(productToInsert.getId());

		if (exists != null) {
			productView.showError("Product already exists with this ID ", productToInsert);
			return;
		}

		List<Product> products = productRepository.findAllProductsSentByUser(productToInsert.getSender());
		
		for (Product p : products) {
			if (Objects.equals(p, productToInsert)) {
				productView.showError("You already sent this package to that customer ", productToInsert);
				return;
			}
		}

		productRepository.save(productToInsert);
		productView.productAdded(productToInsert);
	}

	public void deleteProduct(Product productToDelete) {

		if (productRepository.findProductById(productToDelete.getId()) == null) {
			productView.showError("Product does not exists with such ID ", productToDelete);
			return;
		}

		productRepository.delete(productToDelete);
		productView.productRemoved(productToDelete);
	}
}
