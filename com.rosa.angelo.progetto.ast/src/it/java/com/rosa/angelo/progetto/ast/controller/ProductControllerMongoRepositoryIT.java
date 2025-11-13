package com.rosa.angelo.progetto.ast.controller;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Arrays;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testcontainers.containers.GenericContainer;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import com.rosa.angelo.progetto.ast.model.Product;
import com.rosa.angelo.progetto.ast.model.User;
import com.rosa.angelo.progetto.ast.repository.ProductMongoRepository;
import com.rosa.angelo.progetto.ast.view.ProductView;

public class ProductControllerMongoRepositoryIT {

	@Mock
	private ProductView productView;

	private ProductMongoRepository productRepository;

	private ProductController productController;

	private AutoCloseable closeable;

	@SuppressWarnings({ "rawtypes", "resource" })
	@ClassRule
	public static final GenericContainer mongo = new GenericContainer(
			ProductMongoRepository.IMAGE + ":" + ProductMongoRepository.VERSION)
			.withExposedPorts(ProductMongoRepository.PORT);
	private static MongoClient client;
	private static MongoDatabase database;

	private User loggedInUser;

	@BeforeClass
	public static void beforeClassSetup() {
		client = new MongoClient(new ServerAddress(mongo.getHost(), mongo.getMappedPort(ProductMongoRepository.PORT)));
		database = client.getDatabase(ProductMongoRepository.AST_DB_NAME);
	}

	@Before
	public void beforeSetup() {
		closeable = MockitoAnnotations.openMocks(this);
		productRepository = new ProductMongoRepository(client);
		database.drop();
		productController = new ProductController(productView, productRepository);
		loggedInUser = new User("test1234", "password1234", 1);
	}

	@After
	public void afterTest() throws Exception {
		closeable.close();
		database.drop();
	}

	@AfterClass
	public static void tearDown() {
		client.close();
	}

	@Test
	public void testAllProducts() {
		Product product = new Product(loggedInUser, "receivername", "receiverurname", "address", "packagetype", 1);
		productRepository.save(product);
		productController.allProducts(loggedInUser);
		verify(productView, times(1)).showAllProductsSentByUser(Arrays.asList(product));

		// test products of other users are not showed
		User anotherUser = new User("test12", "ppp", 2);
		Product product2 = new Product(anotherUser, "receivername", "receiverurname", "address", "packagetype", 2);
		productRepository.save(product2);
		productController.allProducts(loggedInUser);
		verify(productView, times(2)).showAllProductsSentByUser(Arrays.asList(product));
	}

	@Test
	public void testNewProduct() {
		Product product = new Product(loggedInUser, "receivername", "receiverurname", "address", "packagetype", 1);
		productController.newProduct(product, loggedInUser);
		verify(productView).productAdded(product);
	}

	@Test
	public void testNewProductAlreadyExists() {
		Product product = new Product(loggedInUser, "receivername", "receiverurname", "address", "packagetype", 1);
		productRepository.save(product);

		productController.newProduct(product, loggedInUser);
		verify(productView).showError("Product already exists with this ID ", product);
	}

	@Test
	public void testNewProductAlreadySentThisPackage() {
		Product product = new Product(loggedInUser, "receivername", "receiverurname", "address", "packagetype", 1);
		Product product2 = new Product(loggedInUser, "receivername", "receiverurname", "address", "packagetype", 2);

		productRepository.save(product);

		productController.newProduct(product2, loggedInUser);
		verify(productView).showError("You already sent this package to that customer ", product2);
	}

	@Test
	public void testNewProductToAnotherUser() {
		Product product = new Product(new User("tester", "password123", 2), "receivername2", "receiverurname2",
				"address2", "packagetype2", 2);

		productController.newProduct(product, loggedInUser);
		verify(productView).showError("You cannot add a package to another user ", product);
	}

	@Test
	public void testDeletePackage() {
		Product product = new Product(loggedInUser, "receivername", "receiverurname", "address", "packagetype", 1);
		productRepository.save(product);

		productController.deleteProduct(product, loggedInUser);
		verify(productView).productRemoved(product);
	}

	@Test
	public void testDeletePackageNotExisting() {
		Product product = new Product(loggedInUser, "receivername", "receiverurname", "address", "packagetype", 1);
		productController.deleteProduct(product, loggedInUser);
		verify(productView).showError("Product does not exists with such ID ", product);
	}

	@Test
	public void testDeletePackageOfOtherUser() {
		Product product = new Product(new User("wewe", "wewe", 2), "receivername", "receiverurname", "address",
				"packagetype", 1);

		productRepository.save(product);
		productController.deleteProduct(product, loggedInUser);
		verify(productView).showError("You cannot delete a package you don't own ", product);
	}
}
