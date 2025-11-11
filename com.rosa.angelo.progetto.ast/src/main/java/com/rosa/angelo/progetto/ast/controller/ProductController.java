package com.rosa.angelo.progetto.ast.controller;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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

		int numberOfAlreadySentSamePackages = productRepository.findAllProductsSentByUser(productToInsert.getSender())
				.stream().filter(x -> Objects.equals(x, productToInsert)).collect(Collectors.counting()).intValue();

		if (numberOfAlreadySentSamePackages > 0) {
			productView.showError("You already sent this package to that customer ", productToInsert);
			return;
		}

		productRepository.save(productToInsert);
		productView.productAdded(productToInsert);
	}

	public void deleteProduct(Product productToDelete, User loggedInUser) {

		if (productRepository.findProductById(productToDelete.getId()) == null) {
			productView.showError("Product does not exists with such ID ", productToDelete);
			return;
		}
		
		List<Product> products = productRepository.findAllProductsSentByUser(loggedInUser);
		
		int found = 0;
		for (Product p : products) {
			if (Objects.equals(p, productToDelete)) {
				found++;
				break;
			}
		}
		
		if (found < 1) {
			productView.showError("You cannot delete a package you don't own ", productToDelete);
			return;
		}

		productRepository.delete(productToDelete);
		productView.productRemoved(productToDelete);
	}
}
