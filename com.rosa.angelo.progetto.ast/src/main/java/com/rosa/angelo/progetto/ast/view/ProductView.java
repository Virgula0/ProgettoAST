package com.rosa.angelo.progetto.ast.view;

import java.util.List;

import com.rosa.angelo.progetto.ast.model.Product;

public interface ProductView {
	void showAllProductsSentByUser(List<Product> toDisplay);

	void showError(String message, Product product);

	void productAdded(Product product);

	void productRemoved(Product product);
}
