package com.rosa.angelo.progetto.ast.repository;

import java.util.List;

import com.rosa.angelo.progetto.ast.model.Product;
import com.rosa.angelo.progetto.ast.model.User;

public interface ProductRepository {
	void save(Product product);

	void delete(Product product);

	Product findProductById(int id);

	List<Product> findAllProductsSentByUser(User user);
}
