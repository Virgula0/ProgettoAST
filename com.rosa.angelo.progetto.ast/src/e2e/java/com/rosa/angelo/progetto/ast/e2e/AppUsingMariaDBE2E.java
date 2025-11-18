package com.rosa.angelo.progetto.ast.e2e;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.swing.launcher.ApplicationLauncher.application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.regex.Pattern;

import javax.swing.JFrame;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.GenericTypeMatcher;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.finder.WindowFinder;
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

import com.rosa.angelo.progetto.ast.model.Product;
import com.rosa.angelo.progetto.ast.model.User;
import com.rosa.angelo.progetto.ast.repository.GenericRepositoryException;
import com.rosa.angelo.progetto.ast.repository.ProductMariaDBRepository;
import com.rosa.angelo.progetto.ast.repository.UserMariaDBRepository;
import com.rosa.angelo.progetto.ast.repository.UserRepository;

@RunWith(GUITestRunner.class)
public class AppUsingMariaDBE2E extends AssertJSwingJUnitTestCase {
	private FrameFixture window;
	private FrameFixture managerWindow;

	@SuppressWarnings({ "resource" })
	@ClassRule
	public static final MariaDBContainer<?> mariadb = new MariaDBContainer<>(
			UserMariaDBRepository.IMAGE + ":" + UserMariaDBRepository.VERSION)
			.withDatabaseName(UserMariaDBRepository.AST_DB_NAME).withUsername(UserMariaDBRepository.DB_USERNAME)
			.withPassword(UserMariaDBRepository.DB_PASSWORD).withExposedPorts(UserMariaDBRepository.PORT);

	private static Connection connection;

	private static final String DB_NAME = "testdb";
	private static final String DB_USERNAME = "testuser";
	private static final String DB_PASSWORD = "password";
	private final String VALID_TOKEN = UserRepository.REGISTRATION_TOKEN;
	private final User userFixture = new User("test1234", "password1234", 1);
	private Product productOneFixture;
	private Product productTwoFixture;

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
		cleanupAndCreate();

		String containerIpAddress = mariadb.getHost();
		Integer mappedPort = mariadb.getMappedPort(UserMariaDBRepository.PORT);

		addTestUserToDatabase(userFixture);
		productOneFixture = new Product(userFixture, "test1Name", "test1Surname", "test1Address", "test1Package", 1);
		productTwoFixture = new Product(userFixture, "test2Name", "test2Surname", "test2Address", "test2Package", 2);
		addTestProductToDatabase(productOneFixture);
		addTestProductToDatabase(productTwoFixture);

		application("com.rosa.angelo.progetto.ast.main.Main").withArgs("--mariadb-host=" + containerIpAddress,
				"--mariadb-port=" + mappedPort, "--mariadb-name=" + DB_NAME, "--db=mariadb",
				"--mariadb-username=" + DB_USERNAME, "--mariadb-password=" + DB_PASSWORD).start();

		// get a reference of its JFrame
		window = WindowFinder.findFrame(new GenericTypeMatcher<JFrame>(JFrame.class) {
			@Override
			protected boolean isMatching(JFrame frame) {
				return "LoginView".equals(frame.getTitle()) && frame.isShowing();
			}
		}).using(robot());
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
	// -- START OF HELPERS

	private void addTestUserToDatabase(User user) throws GenericRepositoryException {
		String query = "INSERT INTO %s (username,password,id) VALUES (?,?,?)";
		String statement = String.format(query, UserMariaDBRepository.USER_TABLE_NAME);
		try {
			try (PreparedStatement stmt = connection.prepareStatement(statement)) {
				stmt.setString(1, user.getUsername());
				stmt.setString(2, user.getPassword());
				stmt.setInt(3, user.getId());
				stmt.executeUpdate();
			}
		} catch (SQLException ex) {
			throw new GenericRepositoryException(ex.getMessage());
		}
	}

	private void addTestProductToDatabase(Product p) throws GenericRepositoryException {
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

	private void removeTestProductFromDatabase(Product p) throws GenericRepositoryException {
		String statement = String.format("DELETE FROM %s WHERE %s=?", ProductMariaDBRepository.PRODUCT_TABLE_NAME,
				ProductMariaDBRepository.PRODUCT_ID_KEY);
		try (PreparedStatement stmt = connection.prepareStatement(statement)) {
			stmt.setInt(1, p.getId());
			stmt.execute();
		} catch (SQLException ex) {
			throw new GenericRepositoryException(ex.getMessage());
		}
	}

	private void performLoginWithUserFixtureAndSwitchProductView() {
		window.textBox("loginUsernameInputText").enterText(userFixture.getUsername());
		window.textBox("loginPasswordInputText").enterText(userFixture.getPassword());
		window.button(JButtonMatcher.withText("Login")).click();
		managerWindow = WindowFinder.findFrame("ManagerView").using(window.robot());
	}

	// -- END OF HELPERS

	@Test
	@GUITest
	public void testRegistrationWithAnotherUserIsOK() {
		window.textBox("registrationIdInputText").enterText("2");
		window.textBox("registrationUsernameInputText").enterText("testUsername");
		window.textBox("registrationPasswordInputText").enterText("testPassword");
		window.textBox("registrationTokenInputText").enterText(VALID_TOKEN);
		window.button(JButtonMatcher.withText("Register")).click();

		window.label("errorMessageLabel").requireText(" ");
	}

	@Test
	@GUITest
	public void testRegistrationWithSAmeIdOfUserFixtureIsNotOk() {
		window.textBox("registrationIdInputText").enterText("1");
		window.textBox("registrationUsernameInputText").enterText("testUsername");
		window.textBox("registrationPasswordInputText").enterText("testPassword");
		window.textBox("registrationTokenInputText").enterText(VALID_TOKEN);
		window.button(JButtonMatcher.withText("Register")).click();

		window.label("errorMessageLabel")
				.requireText("Already existing user by id or username similarity : " + userFixture);
	}

	@Test
	@GUITest
	public void testLoginWithUserFixtureIsOk() {
		window.textBox("loginUsernameInputText").enterText(userFixture.getUsername());
		window.textBox("loginPasswordInputText").enterText(userFixture.getPassword());
		window.button(JButtonMatcher.withText("Login")).click();

		managerWindow = WindowFinder.findFrame("ManagerView").using(window.robot());
		managerWindow.requireVisible();
	}

	@Test
	@GUITest
	public void testUserFixtureProductsAreCorrectlyShown() {
		performLoginWithUserFixtureAndSwitchProductView();
		assertThat(managerWindow.list().contents())
				.anySatisfy(e -> assertThat(e).contains(String.valueOf(productOneFixture.getId()),
						productOneFixture.getReceiverName(), productOneFixture.getReceiverSurname(),
						productOneFixture.getReceiverAddress(), productOneFixture.getPackageType()))
				.anySatisfy(e -> assertThat(e).contains(String.valueOf(productTwoFixture.getId()),
						productTwoFixture.getReceiverName(), productTwoFixture.getReceiverSurname(),
						productTwoFixture.getReceiverAddress(), productTwoFixture.getPackageType()));
	}

	@Test
	@GUITest
	public void testAddProductSuccess() {
		performLoginWithUserFixtureAndSwitchProductView();
		managerWindow.textBox("productIdInputText").enterText("3");
		managerWindow.textBox("receiverNameInputText").enterText("test3Name");
		managerWindow.textBox("receiverSurnameInputText").enterText("test3Surname");
		managerWindow.textBox("receiverAddressInputText").enterText("test3Address");
		managerWindow.textBox("packageTypeInputText").enterText("test3Package");
		managerWindow.button(JButtonMatcher.withText("Add")).click();
		assertThat(managerWindow.list().contents()).anySatisfy(
				e -> assertThat(e).contains("3", "test3Name", "test3Surname", "test3Address", "test3Package"));
	}

	@Test
	@GUITest
	public void testAddProductError() {
		performLoginWithUserFixtureAndSwitchProductView();
		managerWindow.textBox("productIdInputText").enterText("1");
		managerWindow.textBox("receiverNameInputText").enterText(productOneFixture.getReceiverName());
		managerWindow.textBox("receiverSurnameInputText").enterText(productOneFixture.getReceiverSurname());
		managerWindow.textBox("receiverAddressInputText").enterText(productOneFixture.getReceiverAddress());
		managerWindow.textBox("packageTypeInputText").enterText(productOneFixture.getPackageType());
		managerWindow.button(JButtonMatcher.withText("Add")).click();

		managerWindow.label("errorMessageLabel")
				.requireText("Product already exists with this ID : " + productOneFixture);
	}

	@Test
	@GUITest
	public void testDeleteProductWhenExists() {
		performLoginWithUserFixtureAndSwitchProductView();
		managerWindow.list("productList")
				.selectItem(Pattern.compile(".*" + productOneFixture.getReceiverName() + ".*"));
		managerWindow.button(JButtonMatcher.withText("Delete Product")).click();
		assertThat(managerWindow.list().contents()).noneMatch(e -> e.contains(productOneFixture.getReceiverName()));
	}

	@Test
	@GUITest
	public void testDeleteProductWhenDoesNotExist() throws GenericRepositoryException {
		performLoginWithUserFixtureAndSwitchProductView();
		managerWindow.list("productList")
				.selectItem(Pattern.compile(".*" + productOneFixture.getReceiverName() + ".*"));
		removeTestProductFromDatabase(productOneFixture);
		managerWindow.button(JButtonMatcher.withText("Delete Product")).click();
		managerWindow.label("errorMessageLabel")
				.requireText("Product does not exists with such ID : " + productOneFixture);
	}
}
