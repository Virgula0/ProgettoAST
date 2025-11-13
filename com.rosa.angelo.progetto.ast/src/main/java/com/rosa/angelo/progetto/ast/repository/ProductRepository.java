package com.rosa.angelo.progetto.ast.repository;

import java.util.List;

import com.rosa.angelo.progetto.ast.model.Product;
import com.rosa.angelo.progetto.ast.model.User;

public interface ProductRepository {
	void save(Product product) throws GenericRepositoryException;

	void delete(Product product) throws GenericRepositoryException;

	Product findProductById(int id) throws GenericRepositoryException;

	List<Product> findAllProductsSentByUser(User user) throws GenericRepositoryException ;
}
