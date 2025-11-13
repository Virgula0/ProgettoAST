package com.rosa.angelo.progetto.ast.controller;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testcontainers.containers.MariaDBContainer;

import com.rosa.angelo.progetto.ast.model.Product;
import com.rosa.angelo.progetto.ast.model.User;
import com.rosa.angelo.progetto.ast.repository.GenericRepositoryException;
import com.rosa.angelo.progetto.ast.repository.ProductMariaDBRepository;
import com.rosa.angelo.progetto.ast.repository.UserMariaDBRepository;
import com.rosa.angelo.progetto.ast.view.ProductView;

public class ProductControllerMariaDBRepositoryIT {

	@SuppressWarnings({ "resource" })
	@ClassRule
	public static final MariaDBContainer<?> mariadb = new MariaDBContainer<>(
			ProductMariaDBRepository.IMAGE + ":" + ProductMariaDBRepository.VERSION)
			.withDatabaseName(ProductMariaDBRepository.AST_DB_NAME).withUsername(ProductMariaDBRepository.DB_USERNAME)
			.withPassword(ProductMariaDBRepository.DB_PASSWORD).withExposedPorts(ProductMariaDBRepository.PORT);

	private static Connection connection;

	private AutoCloseable closeable;

	@Mock
	private ProductView productView;
	private ProductMariaDBRepository productRepository;
	private ProductController productController;

	private User loggedInUser;
	private User anotherDifferentUser;

	private void cleanupAndCreate() throws GenericRepositoryException {
		try {
			try (Statement stmt = connection.createStatement()) {
				stmt.execute("DROP DATABASE IF EXISTS " + ProductMariaDBRepository.AST_DB_NAME);
				stmt.execute("CREATE DATABASE " + ProductMariaDBRepository.AST_DB_NAME);
				stmt.execute("USE " + ProductMariaDBRepository.AST_DB_NAME);

				stmt.execute("CREATE TABLE " + UserMariaDBRepository.USER_TABLE_NAME
						+ " (id INT PRIMARY KEY,username VARCHAR(255),password VARCHAR(255))");

				stmt.execute("CREATE TABLE " + ProductMariaDBRepository.PRODUCT_TABLE_NAME
						+ " (id INT PRIMARY KEY, sender_id int, receivername VARCHAR(255), receiverusername VARCHAR(255), "
						+ "receiveraddress VARCHAR(255),packagetype VARCHAR(255), FOREIGN KEY (sender_id) REFERENCES "
						+ UserMariaDBRepository.USER_TABLE_NAME + "(id))");

				// initialize user table with a valid user
				stmt.execute("INSERT INTO " + UserMariaDBRepository.USER_TABLE_NAME + " VALUES (" + loggedInUser.getId()
						+ ", '" + loggedInUser.getUsername() + "', '" + loggedInUser.getPassword() + "')");

				// initialize user table with another user
				stmt.execute("INSERT INTO " + UserMariaDBRepository.USER_TABLE_NAME + " VALUES ("
						+ anotherDifferentUser.getId() + ", '" + anotherDifferentUser.getUsername() + "', '"
						+ anotherDifferentUser.getPassword() + "')");
			}
		} catch (SQLException ex) {
			throw new GenericRepositoryException(ex.getMessage());
		}
	}

	@BeforeClass
	public static void beforeClass() throws SQLException {
		String host = mariadb.getHost();
		Integer port = mariadb.getMappedPort(ProductMariaDBRepository.PORT);
		String jdbcUrl = String.format("jdbc:mariadb://%s:%d/%s", host, port, ProductMariaDBRepository.AST_DB_NAME);
		String username = mariadb.getUsername();
		String password = mariadb.getPassword();
		connection = DriverManager.getConnection(jdbcUrl, username, password);
	}

	@Before
	public void setup() throws GenericRepositoryException {
		closeable = MockitoAnnotations.openMocks(this);
		productRepository = new ProductMariaDBRepository(connection);
		productController = new ProductController(productView, productRepository);
		loggedInUser = new User("test", "password1234", 1);
		anotherDifferentUser = new User("test2", "password1234", 2);
		cleanupAndCreate();
	}

	@After
	public void after() throws Exception {
		closeable.close();
		cleanupAndCreate();
	}

	@AfterClass
	public static void afterClass() throws SQLException {
		if (connection != null)
			connection.close();
	}

	@Test
	public void testAllProducts() throws GenericRepositoryException {
		Product product = new Product(loggedInUser, "receivername", "receiverurname", "address", "packagetype", 1);
		productRepository.save(product);
		productController.allProducts(loggedInUser);
		verify(productView, times(1)).showAllProductsSentByUser(Arrays.asList(product));

		// test products of other users are not showed
		Product product2 = new Product(anotherDifferentUser, "receivername", "receiverurname", "address", "packagetype",
				2);
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
	public void testNewProductAlreadyExists() throws GenericRepositoryException {
		Product product = new Product(loggedInUser, "receivername", "receiverurname", "address", "packagetype", 1);
		productRepository.save(product);

		productController.newProduct(product, loggedInUser);
		verify(productView).showError("Product already exists with this ID ", product);
	}

	@Test
	public void testNewProductAlreadySentThisPackage() throws GenericRepositoryException {
		Product product = new Product(loggedInUser, "receivername", "receiverurname", "address", "packagetype", 1);
		Product product2 = new Product(loggedInUser, "receivername", "receiverurname", "address", "packagetype", 2);

		productRepository.save(product);

		productController.newProduct(product2, loggedInUser);
		verify(productView).showError("You already sent this package to that customer ", product2);
	}

	@Test
	public void testNewProductToAnotherUser() {
		Product product = new Product(anotherDifferentUser, "receivername2", "receiverurname2", "address2",
				"packagetype2", 2);

		productController.newProduct(product, loggedInUser);
		verify(productView).showError("You cannot add a package to another user ", product);
	}

	@Test
	public void testDeletePackage() throws GenericRepositoryException {
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
	public void testDeletePackageOfOtherUser() throws GenericRepositoryException {
		Product product = new Product(anotherDifferentUser, "receivername", "receiverurname", "address", "packagetype",
				1);

		productRepository.save(product);
		productController.deleteProduct(product, loggedInUser);
		verify(productView).showError("You cannot delete a package you don't own ", product);
	}

}
