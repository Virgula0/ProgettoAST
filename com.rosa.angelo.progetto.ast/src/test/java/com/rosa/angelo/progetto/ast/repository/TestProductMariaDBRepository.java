package com.rosa.angelo.progetto.ast.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.testcontainers.containers.MariaDBContainer;

import com.rosa.angelo.progetto.ast.model.Product;
import com.rosa.angelo.progetto.ast.model.User;

public class TestProductMariaDBRepository {

	@SuppressWarnings({ "resource" })
	@ClassRule
	public static final MariaDBContainer<?> mariadb = new MariaDBContainer<>(
			ProductMariaDBRepository.IMAGE + ":" + ProductMariaDBRepository.VERSION)
			.withDatabaseName(ProductMariaDBRepository.AST_DB_NAME).withUsername(ProductMariaDBRepository.DB_USERNAME)
			.withPassword(ProductMariaDBRepository.DB_PASSWORD).withExposedPorts(ProductMariaDBRepository.PORT);

	private ProductMariaDBRepository productRepository;
	private static Connection connection;
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
		productRepository = new ProductMariaDBRepository(connection);
		loggedInUser = new User("test1234", "password1234", 1);
		anotherDifferentUser = new User("anotherUser", "password1234", 2);
		cleanupAndCreate();
	}

	@After
	public void after() throws GenericRepositoryException {
		cleanupAndCreate();
	}

	@AfterClass
	public static void afterClass() throws SQLException {
		if (connection != null)
			connection.close();
	}

	// -- HELPERS --

	private void addTestProductToUserToDatabase(Product p) throws GenericRepositoryException {
		String query = "INSERT INTO %s (id,sender_id,receivername,receiverusername,receiveraddress,packagetype)"
				+ " VALUES (?,?,?,?,?,?)";
		String statement = String.format(query, ProductMariaDBRepository.PRODUCT_TABLE_NAME);
		try {
			try (PreparedStatement stmt = connection.prepareStatement(statement)) {
				stmt.setInt(1, p.getId());
				stmt.setInt(2, p.getSender().getId());
				stmt.setString(3, p.getReceiverName());
				stmt.setString(4, p.getReceiverSurname());
				stmt.setString(5, p.getReceiverAddress());
				stmt.setString(6, p.getPackageType());
				stmt.executeUpdate();
			}
		} catch (SQLException ex) {
			throw new GenericRepositoryException(ex.getMessage());
		}
	}

	private Product databaseToProduct(ResultSet rs) throws SQLException {
		User senderUser = new User(rs.getString(UserMariaDBRepository.USERNAME_KEY),
				rs.getString(UserMariaDBRepository.PWD_DB_KEY),
				rs.getInt(ProductMariaDBRepository.USER_ID_FOREIGN_KEY));

		return new Product(senderUser, rs.getString(ProductMariaDBRepository.RECEIVER_NAME_KEY),
				rs.getString(ProductMariaDBRepository.RECEIVER_SURNAME_KEY),
				rs.getString(ProductMariaDBRepository.RECEIVER_ADDRESS_KEY),
				rs.getString(ProductMariaDBRepository.RECEIVER_PACKAGETYPE_KEY),
				rs.getInt(ProductMariaDBRepository.PRODUCT_ID_KEY));
	}

	private List<Product> getAllProducts() throws GenericRepositoryException {
		String query = "SELECT p.*,u.%s,u.%s from %s p JOIN %s u ON u.id=p.%s";
		List<Product> products = new ArrayList<>();

		query = String.format(query, UserMariaDBRepository.USERNAME_KEY, UserMariaDBRepository.PWD_DB_KEY,
				ProductMariaDBRepository.PRODUCT_TABLE_NAME, UserMariaDBRepository.USER_TABLE_NAME,
				ProductMariaDBRepository.USER_ID_FOREIGN_KEY);

		try {
			try (PreparedStatement stmt = connection.prepareStatement(query)) {
				ResultSet rs = stmt.executeQuery();
				while (rs.next()) {
					products.add(databaseToProduct(rs));
				}
			}
		} catch (SQLException e) {
			throw new GenericRepositoryException(e.getMessage());
		}

		return products;
	}

	// -- END OF HELPERS --

	@Test
	public void testFindAllProductsSentByUser() throws GenericRepositoryException {
		Product p1 = new Product(loggedInUser, "test", "test", "testAddress", "testPackage", 1);
		Product p2 = new Product(loggedInUser, "test2", "test2", "testAddress2", "testPackage2", 2);
		Product p3 = new Product(anotherDifferentUser, "test3", "test3", "testAddress3", "testPackage3", 3);

		addTestProductToUserToDatabase(p1);
		addTestProductToUserToDatabase(p2);
		addTestProductToUserToDatabase(p3);

		assertThat(productRepository.findAllProductsSentByUser(loggedInUser)).containsExactly(p1, p2);
	}

	@Test
	public void testWhenSQLExceptionisthrownByfindAllProductsSentByUser() {
		productRepository.injectFindAllProductsSentByUserQuery("bad query");

		assertThatThrownBy(() -> productRepository.findAllProductsSentByUser(loggedInUser))
				.isInstanceOf(GenericRepositoryException.class).extracting("message").asString().isNotEmpty();
	}

	@Test
	public void testFindAllProductsSentByUserWithNullUser() throws GenericRepositoryException {
		assertThat(productRepository.findAllProductsSentByUser(null)).isEmpty();
	}

	@Test
	public void testSaveANewProductSuccesfully() throws GenericRepositoryException {
		Product product = new Product(loggedInUser, "test", "test", "testAddress", "testPackage", 1);

		productRepository.save(product);

		assertThat(getAllProducts()).containsExactly(product);
	}

	@Test
	public void testWhenSQLExceptionisthrownBySaveProduct() {
		Product product = new Product(loggedInUser, "test", "test", "testAddress", "testPackage", 1);
		productRepository.injectSaveProductsQuery("bad query");

		assertThatThrownBy(() -> productRepository.save(product)).isInstanceOf(GenericRepositoryException.class)
				.extracting("message").asString().isNotEmpty();
	}

	@Test
	public void testDeleteAProductSuccesfully() throws GenericRepositoryException {
		Product product = new Product(loggedInUser, "test", "test", "testAddress", "testPackage", 1);
		Product product2 = new Product(loggedInUser, "test", "test", "testAddress", "testPackage", 2);
		
		addTestProductToUserToDatabase(product);
		addTestProductToUserToDatabase(product2);
		assertThat(getAllProducts()).containsExactly(product, product2);

		productRepository.delete(product);

		assertThat(getAllProducts()).containsExactly(product2);
	}
	
	@Test
	public void testWhenSQLExceptionisthrownByDeleteProduct() {
		Product product = new Product(loggedInUser, "test", "test", "testAddress", "testPackage", 1);
		productRepository.injectDeleteProductsQuery("bad query");

		assertThatThrownBy(() -> productRepository.delete(product)).isInstanceOf(GenericRepositoryException.class)
				.extracting("message").asString().isNotEmpty();
	}

}
