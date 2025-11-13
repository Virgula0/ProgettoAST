package com.rosa.angelo.progetto.ast.controller;

import static org.mockito.Mockito.ignoreStubs;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
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

	@Test
	public void testNewProductWhenProductDoesNotExists() {
		Product product = new Product(validLoggedInUser, "receiverName", "receiverSuername", "receiverAddress",
				"packageType", 1);

		when(productRepository.findProductById(product.getId())).thenReturn(null);

		productController.newProduct(product, validLoggedInUser);

		InOrder inOrder = Mockito.inOrder(productRepository, productView);
		inOrder.verify(productRepository).save(product);
		inOrder.verify(productView).productAdded(product);
	}

	@Test
	public void testNewProductWhenUserAlreadyHasOtherProducts() {
		Product product = new Product(validLoggedInUser, "receiverName", "receiverSuername", "receiverAddress",
				"packageType", 1);
		Product productToAdd = new Product(validLoggedInUser, "receiverName2", "receiverSuername2", "receiverAddress2",
				"packageType", 2);

		when(productRepository.findProductById(product.getId())).thenReturn(product);
		when(productRepository.findAllProductsSentByUser(product.getSender())).thenReturn(Arrays.asList(product));

		productController.newProduct(productToAdd, validLoggedInUser);

		InOrder inOrder = Mockito.inOrder(productRepository, productView);
		inOrder.verify(productRepository).save(productToAdd);
		inOrder.verify(productView).productAdded(productToAdd);

	}

	@Test
	public void testNewProductWhenProductAlreadyExists() {
		Product product = new Product(validLoggedInUser, "receiverName", "receiverSuername", "receiverAddress",
				"packageType", 1);
		Product product2SameId = new Product(validLoggedInUser, "receiverName2", "receiverSuername2",
				"receiverAddress2", "packageType", 1);

		when(productRepository.findProductById(product.getId())).thenReturn(product);

		productController.newProduct(product2SameId, validLoggedInUser);

		verify(productView).showError("Product already exists with this ID ", product2SameId);
		verifyNoMoreInteractions(ignoreStubs(productRepository));
	}

	@Test
	public void testDeleteProductWhenItExists() {
		Product productToDelete = new Product(validLoggedInUser, "receiverName", "receiverSuername", "receiverAddress",
				"packageType", 1);

		when(productRepository.findProductById(productToDelete.getId())).thenReturn(productToDelete);
		when(productRepository.findAllProductsSentByUser(productToDelete.getSender()))
				.thenReturn(Arrays.asList(productToDelete));

		productController.deleteProduct(productToDelete, validLoggedInUser);

		InOrder inOrder = Mockito.inOrder(productRepository, productView);
		inOrder.verify(productRepository).delete(productToDelete);
		inOrder.verify(productView).productRemoved(productToDelete);
	}

	@Test
	public void testDeleteProductWhenItDoesNotExists() {
		Product productToDelete = new Product(validLoggedInUser, "receiverName", "receiverSuername", "receiverAddress",
				"packageType", 1);

		when(productRepository.findProductById(productToDelete.getId())).thenReturn(null);

		productController.deleteProduct(productToDelete, validLoggedInUser);

		verify(productView).showError("Product does not exists with such ID ", productToDelete);
		verifyNoMoreInteractions(ignoreStubs(productRepository));
	}

	@Test
	public void testNewProductErrorIfUserAlreadySentSuchPackageEvenIfIdIsDifferent() {
		Product product = new Product(validLoggedInUser, "receiverName", "receiverSurname", "receiverAddress",
				"samePackageType", 1);
		Product product2SameReceiver = new Product(validLoggedInUser, "receiverName", "receiverSurname",
				"receiverAddress", "samePackageType", 2);
		Product notRelevantProduct = new Product(validLoggedInUser, "receiverName2", "receiverSurname2",
				"receiverAddress2", "samePackageTyp2e", 3);

		when(productRepository.findAllProductsSentByUser(product2SameReceiver.getSender()))
				.thenReturn(Arrays.asList(notRelevantProduct, product));

		InOrder inOrder = Mockito.inOrder(productRepository, productRepository, productView);

		productController.newProduct(product2SameReceiver, validLoggedInUser);

		inOrder.verify(productRepository).findProductById(product2SameReceiver.getId());
		inOrder.verify(productRepository).findAllProductsSentByUser(product2SameReceiver.getSender());
		inOrder.verify(productView).showError("You already sent this package to that customer ", product2SameReceiver);
		verifyNoMoreInteractions(ignoreStubs(productRepository));
	}

	@Test
	public void testDeleteProductShouldNotAllowToDeleteProductOfAnotherUser() {
		Product productUser1 = new Product(validLoggedInUser, "receiverName", "receiverSurname", "receiverAddress",
				"samePackageType", 1);
		Product notRelevantProduct = new Product(validLoggedInUser, "receiverName2", "receiverSurname2",
				"receiverAddress2", "samePackageTyp2e", 3);

		User user2 = new User("test12345", "password1234", 2);
		Product productUser2 = new Product(user2, "receiverName", "receiverSurname", "receiverAddress",
				"samePackageType", 1);

		when(productRepository.findAllProductsSentByUser(validLoggedInUser))
				.thenReturn(Arrays.asList(notRelevantProduct, productUser1));
		when(productRepository.findProductById(productUser2.getId())).thenReturn(productUser2);

		InOrder inOrder = Mockito.inOrder(productRepository, productRepository, productView);

		productController.deleteProduct(productUser2, validLoggedInUser);

		inOrder.verify(productRepository).findProductById(productUser2.getId());
		inOrder.verify(productRepository).findAllProductsSentByUser(validLoggedInUser);
		inOrder.verify(productView).showError("You cannot delete a package you don't own ", productUser2);
		verifyNoMoreInteractions(ignoreStubs(productRepository));
	}

	@Test
	public void testNewProductOwnedByDifferentUserShouldNotSucceed() {
		User user2 = new User("user2", "password1234", 2);
		Product product = new Product(user2, "receiverName", "receiverSuername", "receiverAddress", "packageType", 1);

		when(productRepository.findProductById(product.getId())).thenReturn(null);

		productController.newProduct(product, validLoggedInUser);

		InOrder inOrder = Mockito.inOrder(productRepository, productRepository, productView);
		inOrder.verify(productRepository).findProductById(product.getId());
		inOrder.verify(productRepository).findAllProductsSentByUser(user2);
		inOrder.verify(productView).showError("You cannot add a package to another user ", product);
		verifyNoMoreInteractions(ignoreStubs(productRepository));
	}
	
	@Test
	public void testAllProductsWithNullUser() {
		productController.allProducts(null);
		verifyNoMoreInteractions(ignoreStubs(productRepository));
		verifyNoMoreInteractions(ignoreStubs(productView));
	}
	
	@Test
	public void testNewProductWithNullProduct() {
		Product product = null;
		productController.newProduct(product, validLoggedInUser);
		verify(productView).showError("Invalid product ", product);
		verifyNoMoreInteractions(ignoreStubs(productRepository));
		verifyNoMoreInteractions(ignoreStubs(productView));
	}
}
