package com.rosa.angelo.progetto.ast.controller;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.Objects;
import java.util.stream.Collectors;

import com.google.inject.BindingAnnotation;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.rosa.angelo.progetto.ast.model.Product;
import com.rosa.angelo.progetto.ast.model.User;
import com.rosa.angelo.progetto.ast.repository.GenericRepositoryException;
import com.rosa.angelo.progetto.ast.repository.ProductRepository;
import com.rosa.angelo.progetto.ast.view.ProductView;

public class ProductController {

	@BindingAnnotation
	@Target({ FIELD, PARAMETER, METHOD })
	@Retention(RUNTIME)
	public static @interface RepoType {
	}

	private ProductView productView;
	private ProductRepository productRepository;

	@Inject
	public ProductController(@Assisted ProductView productView, @RepoType ProductRepository productRepository) {
		this.productView = productView;
		this.productRepository = productRepository;
	}

	public void allProducts(User loggedIn) {
		if (loggedIn == null) {
			return;
		}
		try {
			productView.showAllProductsSentByUser(productRepository.findAllProductsSentByUser(loggedIn));
		} catch (GenericRepositoryException ex) {
			hadleRepoException(ex);
		}
	}

	private void hadleRepoException(GenericRepositoryException ex) {
		productView.showError("Exception occurred in repository: " + ex.getMessage());
	}

	public void newProduct(Product productToInsert, User loggedInUser) {

		if (productToInsert == null) {
			productView.showError("Invalid product ", productToInsert);
			return;
		}

		if (productToInsert.getSender() == null) {
			productView.showError("Invalid associated user to product ", productToInsert);
			return;
		}

		try {
			Product exists = productRepository.findProductById(productToInsert.getId());

			if (exists != null) {
				productView.showError("Product already exists with this ID ", productToInsert);
				return;
			}

			int numberOfAlreadySentSamePackages = productRepository
					.findAllProductsSentByUser(productToInsert.getSender()).stream()
					.filter(x -> Objects.equals(x, productToInsert)).collect(Collectors.counting()).intValue();

			if (numberOfAlreadySentSamePackages > 0) {
				productView.showError("You already sent this package to that customer ", productToInsert);
				return;
			}

			if (!Objects.equals(productToInsert.getSender(), loggedInUser)) {
				productView.showError("You cannot add a package to another user ", productToInsert);
				return;
			}

			productRepository.save(productToInsert);

		} catch (GenericRepositoryException ex) {
			hadleRepoException(ex);
		}

		productView.productAdded(productToInsert);
	}

	public void deleteProduct(Product productToDelete, User loggedInUser) {

		if (productToDelete == null) {
			productView.showError("Invalid product to delete ", productToDelete);
			return;
		}

		if (productToDelete.getSender() == null) {
			productView.showError("Invalid sender user for product ", productToDelete);
			return;
		}

		try {
			if (productRepository.findProductById(productToDelete.getId()) == null) {
				productView.showError("Product does not exists with such ID ", productToDelete);
				return;
			}

			int found = productRepository.findAllProductsSentByUser(loggedInUser).stream()
					.filter(p -> Objects.equals(p, productToDelete)).collect(Collectors.counting()).intValue();

			if (found < 1) {
				productView.showError("You cannot delete a package you don't own ", productToDelete);
				return;
			}

			productRepository.delete(productToDelete);

		} catch (GenericRepositoryException ex) {
			hadleRepoException(ex);
		}

		productView.productRemoved(productToDelete);
	}
}
