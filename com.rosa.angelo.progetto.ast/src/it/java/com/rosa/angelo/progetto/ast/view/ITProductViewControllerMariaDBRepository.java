package com.rosa.angelo.progetto.ast.view;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.testcontainers.containers.MariaDBContainer;

import com.rosa.angelo.progetto.ast.controller.ProductController;
import com.rosa.angelo.progetto.ast.model.Product;
import com.rosa.angelo.progetto.ast.model.User;
import com.rosa.angelo.progetto.ast.repository.GenericRepositoryException;
import com.rosa.angelo.progetto.ast.repository.ProductMariaDBRepository;
import com.rosa.angelo.progetto.ast.repository.UserMariaDBRepository;

@RunWith(GUITestRunner.class)
public class ITProductViewControllerMariaDBRepository extends AssertJSwingJUnitTestCase {

	private ProductSwingView productView;
	private ProductMariaDBRepository productRepository;
	private ProductController productController;

	private FrameFixture window;
	private User loggedInUser;

	@SuppressWarnings({ "resource" })
	@ClassRule
	public static final MariaDBContainer<?> mariadb = new MariaDBContainer<>(
			UserMariaDBRepository.IMAGE + ":" + UserMariaDBRepository.VERSION)
			.withDatabaseName(UserMariaDBRepository.AST_DB_NAME).withUsername(UserMariaDBRepository.DB_USERNAME)
			.withPassword(UserMariaDBRepository.DB_PASSWORD).withExposedPorts(UserMariaDBRepository.PORT);

	private static Connection connection;

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
			}
		} catch (SQLException ex) {
			throw new GenericRepositoryException(ex.getMessage());
		}
	}

	@BeforeClass
	public static void beforeClass() throws SQLException {
		String host = mariadb.getHost();
		Integer port = mariadb.getMappedPort(UserMariaDBRepository.PORT);
		String jdbcUrl = String.format("jdbc:mariadb://%s:%d/%s", host, port, UserMariaDBRepository.AST_DB_NAME);
		String username = mariadb.getUsername();
		String password = mariadb.getPassword();
		connection = DriverManager.getConnection(jdbcUrl, username, password);
	}

	@Override
	protected void onSetUp() throws Exception {
		loggedInUser = new User("test", "password1234", 1);
		cleanupAndCreate();
		GuiActionRunner.execute(() -> {
			productRepository = new ProductMariaDBRepository(connection);
			productView = new ProductSwingView();
			productController = new ProductController(productView, productRepository);
			productView.setProductController(productController);
			productView.setLoggedInUser(loggedInUser);
			return productView;
		});
		window = new FrameFixture(robot(), productView);
		window.show();
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

	@Test
	@GUITest
	public void testAllProducts() throws GenericRepositoryException {
		Product product1 = new Product(loggedInUser, "test1", "test1", "test1", "test1", 1);
		Product product2 = new Product(loggedInUser, "test2", "test2", "test2", "test2", 2);
		productRepository.save(product1);
		productRepository.save(product2);
		GuiActionRunner.execute(() -> productController.allProducts(loggedInUser));
		assertThat(window.list().contents()).containsExactly(product1.toString(), product2.toString());
	}

	@Test
	@GUITest
	public void testAddProductButtonSuccess() {
		Product product1 = new Product(loggedInUser, "test1", "test1", "test1", "test1", 1);
		window.textBox("productIdInputText").enterText("1");
		window.textBox("receiverNameInputText").enterText("test1");
		window.textBox("receiverSurnameInputText").enterText("test1");
		window.textBox("receiverAddressInputText").enterText("test1");
		window.textBox("packageTypeInputText").enterText("test1");
		window.button(JButtonMatcher.withText("Add")).click();
		assertThat(window.list().contents()).containsExactly(product1.toString());
	}

	@Test
	@GUITest
	public void testAddProductButtonNotSucceedBecauseProductAlreadyExisting() throws GenericRepositoryException {
		Product product1 = new Product(loggedInUser, "test1", "test1", "test1", "test1", 1);
		productRepository.save(product1);

		window.textBox("productIdInputText").enterText("1");
		window.textBox("receiverNameInputText").enterText("test1");
		window.textBox("receiverSurnameInputText").enterText("test1");
		window.textBox("receiverAddressInputText").enterText("test1");
		window.textBox("packageTypeInputText").enterText("test1");
		window.button(JButtonMatcher.withText("Add")).click();

		assertThat(window.list().contents()).isEmpty();
		assertThat(window.label("errorMessageLabel").text())
				.isEqualTo("Product already exists with this ID : " + product1);
	}

	@Test
	@GUITest
	public void testDeleteProductButtonSuccess() {
		Product product1 = new Product(loggedInUser, "test1", "test1", "test1", "test1", 1);
		GuiActionRunner.execute(() -> productController.newProduct(product1, loggedInUser));
		window.list().selectItem(0);
		window.button(JButtonMatcher.withText("Delete Product")).click();
		assertThat(window.list().contents()).isEmpty();
	}

	@Test
	@GUITest
	public void testDeleteProductButtonError() {
		Product product1 = new Product(loggedInUser, "test1", "test1", "test1", "test1", 1);
		GuiActionRunner.execute(() -> productView.getListProductModel().addElement(product1));
		window.list().selectItem(0);
		window.button(JButtonMatcher.withText("Delete Product")).click();
		assertThat(window.list().contents()).containsExactly(product1.toString());
		window.label("errorMessageLabel").requireText("Product does not exists with such ID : " + product1);
	}
}
