package com.rosa.angelo.progetto.ast.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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
import com.rosa.angelo.progetto.ast.model.User;
import com.rosa.angelo.progetto.ast.repository.UserMongoRepository;
import com.rosa.angelo.progetto.ast.view.LoginView;

public class UserControllerMongoRepositoryIT {

	@Mock
	private LoginView loginView;

	private UserMongoRepository userRepository;

	private UserController userController;

	private AutoCloseable closeable;

	private final String validToken = UserMongoRepository.REGISTRATION_TOKEN;

	@SuppressWarnings({ "rawtypes", "resource" })
	@ClassRule
	public static final GenericContainer mongo = new GenericContainer(
			UserMongoRepository.IMAGE + ":" + UserMongoRepository.VERSION).withExposedPorts(UserMongoRepository.PORT);
	private static MongoClient client;
	private static MongoDatabase database;

	@BeforeClass
	public static void beforeClassSetup() {
		client = new MongoClient(new ServerAddress(mongo.getHost(), mongo.getMappedPort(UserMongoRepository.PORT)));
		database = client.getDatabase(UserMongoRepository.AST_DB_NAME);
	}

	@Before
	public void beforeSetup() {
		closeable = MockitoAnnotations.openMocks(this);
		userRepository = new UserMongoRepository(client);
		database.drop();
		userController = new UserController(loginView, userRepository);
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
	public void testLoginControllerITSuccesfullWithExistingUser() {
		String username = "test";
		String password = "password1234";
		User user = new User(username, password, 1);
		userRepository.save(user);

		userController.login(username, password);

		verify(loginView, times(1)).switchPanel(user);
		verify(loginView, times(0)).showError(anyString());
		verify(loginView, times(0)).showError(anyString(), any());
	}

	@Test
	public void testNewUserController() {
		String username = "test";
		String password = "password1234";
		User user = new User(username, password, 1);
		userController.newUser(user, validToken);

		assertThat(userRepository.findUserById(1)).isEqualTo(user);
		assertThat(userRepository.findUserByUsername(username)).isEqualTo(user);
		assertThat(userRepository.findUserByUsernameAndPassword(username, password)).isEqualTo(user);
	}

	@Test
	public void testNewUserControllerWithNullUser() {
		userController.newUser(null, validToken);
		verify(loginView).showError("Invalid null user passed", null);
	}

	@Test
	public void testInvalidRegistrationToken() {
		String username = "test";
		String password = "password1234";
		User user = new User(username, password, 1);
		userController.newUser(user, "invalid");

		verify(loginView).showError("Invalid registration token");
	}

	@Test
	public void testNewUserAlreadyExists() {
		String username = "test";
		String password = "password1234";
		User user = new User(username, password, 1);
		userRepository.save(user);
		userController.newUser(user, validToken);

		verify(loginView).showError("Already existing user ", user);
	}

	@Test
	public void testNewUserInvalidPasswordInRegistration() {
		String username = "test";
		String password = "pwd";
		User user = new User(username, password, 1);
		userController.newUser(user, validToken);

		verify(loginView).showError("Password must be greater or equal than 8 chars ", user);
	}

	@Test
	public void tesLoginInvalidCredentials() {
		userController.login("not valid", "not valid");
		verify(loginView).showError("Invalid credentials");
	}
}
