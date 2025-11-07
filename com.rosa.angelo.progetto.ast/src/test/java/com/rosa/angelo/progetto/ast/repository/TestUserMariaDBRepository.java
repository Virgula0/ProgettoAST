package com.rosa.angelo.progetto.ast.repository;

import static org.assertj.core.api.Assertions.assertThat;

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
import org.testcontainers.containers.MariaDBContainer;

public class TestUserMariaDBRepository {

	@SuppressWarnings({ "resource" })
	@ClassRule
	public static final MariaDBContainer<?> mariadb = new MariaDBContainer<>(
			UserMariaDBRepository.IMAGE + ":" + UserMariaDBRepository.VERSION)
			.withDatabaseName(UserMariaDBRepository.AST_DB_NAME).withUsername(UserMariaDBRepository.DB_USERNAME)
			.withPassword(UserMariaDBRepository.DB_PASSWORD).withExposedPorts(UserMariaDBRepository.PORT);

	private static UserMariaDBRepository userRepository;
	private static Connection connection;

	private void cleanupAndCreate() throws SQLException {
		try (Statement stmt = connection.createStatement()) {
			stmt.execute("DROP DATABASE IF EXISTS " + UserMariaDBRepository.AST_DB_NAME);
			stmt.execute("CREATE DATABASE " + UserMariaDBRepository.AST_DB_NAME);
			stmt.execute("USE " + UserMariaDBRepository.AST_DB_NAME);

			stmt.execute("CREATE TABLE " + UserMariaDBRepository.USER_TABLE_NAME
					+ " (id INT PRIMARY KEY AUTO_INCREMENT,username VARCHAR(255),password VARCHAR(255))");
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
}
