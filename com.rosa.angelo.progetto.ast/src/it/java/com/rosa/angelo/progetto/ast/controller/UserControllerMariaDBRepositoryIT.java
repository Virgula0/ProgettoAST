package com.rosa.angelo.progetto.ast.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.Mock;
import org.testcontainers.containers.MariaDBContainer;

import com.rosa.angelo.progetto.ast.model.User;
import com.rosa.angelo.progetto.ast.repository.GenericRepositoryException;
import com.rosa.angelo.progetto.ast.repository.UserMariaDBRepository;
import com.rosa.angelo.progetto.ast.repository.UserMongoRepository;
import com.rosa.angelo.progetto.ast.view.LoginView;

public class UserControllerMariaDBRepositoryIT {

	@SuppressWarnings({ "resource" })
	@ClassRule
	public static final MariaDBContainer<?> mariadb = new MariaDBContainer<>(
			UserMariaDBRepository.IMAGE + ":" + UserMariaDBRepository.VERSION)
			.withDatabaseName(UserMariaDBRepository.AST_DB_NAME).withUsername(UserMariaDBRepository.DB_USERNAME)
			.withPassword(UserMariaDBRepository.DB_PASSWORD).withExposedPorts(UserMariaDBRepository.PORT);

	private static Connection connection;

	@Mock
	private LoginView loginView;
	private UserMariaDBRepository userRepository;
	private UserController userController;

	private final String validToken = UserMongoRepository.REGISTRATION_TOKEN;

	private void cleanupAndCreate() {
		try {
			try (Statement stmt = connection.createStatement()) {
				stmt.execute("DROP DATABASE IF EXISTS " + UserMariaDBRepository.AST_DB_NAME);
				stmt.execute("CREATE DATABASE " + UserMariaDBRepository.AST_DB_NAME);
				stmt.execute("USE " + UserMariaDBRepository.AST_DB_NAME);

				stmt.execute("CREATE TABLE " + UserMariaDBRepository.USER_TABLE_NAME
						+ " (id INT PRIMARY KEY,username VARCHAR(255),password VARCHAR(255))");
			}
		} catch (SQLException ex) {
			new GenericRepositoryException(ex.getMessage());
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

	@Before
	public void setup() {
		userRepository = new UserMariaDBRepository(connection);
		cleanupAndCreate();
	}

	@After
	public void after() {
		cleanupAndCreate();
	}

	@AfterClass
	public static void afterClass() throws SQLException {
		if (connection != null)
			connection.close();
	}

	@Test
	public void testLoginControllerITSuccesfullWithExistingUser() throws GenericRepositoryException {
		String password = "password1234";
		String username = "test";
		User user = new User(username, password, 1);
		userRepository.save(user);

		userController.login(username, password);

		verify(loginView, times(1)).switchPanel();
		verify(loginView, times(0)).showError(anyString());
		verify(loginView, times(0)).showError(anyString(), any());
	}

	@Test
	public void testNewUserController() throws GenericRepositoryException {
		String username = "test";
		String password = "password1234";
		User user = new User(username, password, 1);
		userController.newUser(user, validToken);

		assertThat(userRepository.findUserById(1)).isEqualTo(user);
		assertThat(userRepository.findUserByUsername(username)).isEqualTo(user);
		assertThat(userRepository.findUserByUsernameAndPassword(username, password)).isEqualTo(user);
	}
}
