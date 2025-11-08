package com.rosa.angelo.progetto.ast.repository;

import static org.assertj.core.api.Assertions.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.testcontainers.containers.MariaDBContainer;

import com.rosa.angelo.progetto.ast.model.User;

public class TestUserMariaDBRepository {

	@SuppressWarnings({ "resource" })
	@ClassRule
	public static final MariaDBContainer<?> mariadb = new MariaDBContainer<>(
			UserMariaDBRepository.IMAGE + ":" + UserMariaDBRepository.VERSION)
			.withDatabaseName(UserMariaDBRepository.AST_DB_NAME).withUsername(UserMariaDBRepository.DB_USERNAME)
			.withPassword(UserMariaDBRepository.DB_PASSWORD).withExposedPorts(UserMariaDBRepository.PORT);

	private static UserMariaDBRepository userRepository;
	private static Connection connection;

	private static final String TEST_USERNAME = "TEST_USERNAME";
	private static final String TEST_PASSWORD = "TEST_PASSWORD";

	private void cleanupAndCreate() throws SQLException {
		try (Statement stmt = connection.createStatement()) {
			stmt.execute("DROP DATABASE IF EXISTS " + UserMariaDBRepository.AST_DB_NAME);
			stmt.execute("CREATE DATABASE " + UserMariaDBRepository.AST_DB_NAME);
			stmt.execute("USE " + UserMariaDBRepository.AST_DB_NAME);

			stmt.execute("CREATE TABLE " + UserMariaDBRepository.USER_TABLE_NAME
					+ " (id INT PRIMARY KEY,username VARCHAR(255),password VARCHAR(255))");
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
	public void setup() throws SQLException {
		userRepository = new UserMariaDBRepository(connection);
		cleanupAndCreate();
	}

	@After
	public void after() throws SQLException {
		cleanupAndCreate();
	}

	@AfterClass
	public static void afterClass() throws SQLException {
		if (connection != null)
			connection.close();
	}

	@Test
	public void testRegistrationToken() {
		assertThat(userRepository.getRegistrationToken()).isEqualTo("validToken");
	}

	private List<User> getAllUsers() throws SQLException {
		String query = "SELECT * from users";
		List<User> users = new ArrayList<>();
		try (PreparedStatement stmt = connection.prepareStatement(query)) {
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				users.add(new User(rs.getString("username"), rs.getString("password"), rs.getInt("id")));
			}
		}
		return users;
	}

	@Test
	public void testSaveANewUserSuccesfully() throws SQLException {
		User user = new User(TEST_USERNAME, TEST_PASSWORD, 1);
		userRepository.save(user);

		assertThat(getAllUsers()).containsExactly(new User(TEST_USERNAME, TEST_PASSWORD, 1));
	}

}
