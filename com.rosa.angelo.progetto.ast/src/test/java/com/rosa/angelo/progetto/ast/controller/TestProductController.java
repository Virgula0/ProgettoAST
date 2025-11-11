package com.rosa.angelo.progetto.ast.controller;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.rosa.angelo.progetto.ast.model.Product;
import com.rosa.angelo.progetto.ast.model.User;
import com.rosa.angelo.progetto.ast.repository.ProductRepository;
import com.rosa.angelo.progetto.ast.repository.UserRepository;
import com.rosa.angelo.progetto.ast.view.ProductView;

public class TestProductController {
	@Mock
	private ProductRepository productRepository;

	@Mock
	private ProductView productView;

	@InjectMocks
	private ProductController productController;

	private AutoCloseable closeable;

	private User validLoggedInUser;

	@Before
	public void setup() {
		closeable = MockitoAnnotations.openMocks(this);
		validLoggedInUser = new User("test123", "password12345", 1);
	}

	@After
	public void releaseMocks() throws Exception {
		closeable.close();
	}

	@Test
	public void showAllProductsSentByUser() {
		List<Product> products = Arrays.asList(
				new Product(validLoggedInUser, "receiverName", "receiverSuername", "receiverAddress", "packageType", 1),
				new Product(validLoggedInUser, "receiverName2", "receiverSuername2", "receiverAddress2", "packageType2",
						2));

		when(productRepository.findAllProductsSentByUser(validLoggedInUser)).thenReturn(products);

		productController.allProducts(validLoggedInUser);
		verify(productView).showAllProductsSentByUser(products);
	}
}
