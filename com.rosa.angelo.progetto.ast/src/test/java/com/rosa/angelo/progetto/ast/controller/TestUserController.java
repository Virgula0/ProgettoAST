package com.rosa.angelo.progetto.ast.controller;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.sql.SQLException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.rosa.angelo.progetto.ast.model.User;
import com.rosa.angelo.progetto.ast.repository.UserRepository;
import com.rosa.angelo.progetto.ast.view.LoginView;

public class TestUserController {

	private static final String VALID_PASSWORD = "greaterThanEigth";

	@Mock
	private UserRepository userRepository;

	@Mock
	private LoginView loginView;

	@InjectMocks
	private UserController userController;

	private AutoCloseable closeable;

	private String VALID_TOKEN = System.getProperty("SIGNUP_TOKEN", "validToken");
	private String INVALID_TOKEN = "notValid";

	@Before
	public void setup() {
		closeable = MockitoAnnotations.openMocks(this);
		when(userRepository.getRegistrationToken()).thenReturn(VALID_TOKEN);
	}

	@After
	public void releaseMocks() throws Exception {
		closeable.close();
	}

	@Test
	public void testNewUserInvalidRegistrationToken() throws SQLException {
		User user = new User("test", VALID_PASSWORD, 1);

		userController.newUser(user, INVALID_TOKEN);

		verify(userRepository, times(1)).getRegistrationToken();
		verify(userRepository, times(0)).save(user);
		verify(loginView).showError("Invalid registration token");
	}

	@Test
	public void testNewUserWithNullToken() throws SQLException {
		User user = new User("test", VALID_PASSWORD, 1);

		userController.newUser(user, null);

		verify(userRepository, times(1)).getRegistrationToken();
		verify(userRepository, times(0)).save(user);
		verify(loginView).showError("Invalid registration token");
	}

	@Test
	public void testNewUserWithEmptyTokenString() throws SQLException {
		User user = new User("test", VALID_PASSWORD, 1);
		userController.newUser(user, "");

		verify(userRepository, times(1)).getRegistrationToken();
		verify(userRepository, times(0)).save(user);
		verify(loginView).showError("Invalid registration token");
	}

	@Test
	public void testNewUserWhenUserDoesNotAlreadyExistAndTokenIsCorrect() throws SQLException {
		User user = new User("test", VALID_PASSWORD, 1);

		userController.newUser(user, VALID_TOKEN);

		verify(userRepository, times(1)).findUserById(user.getId());
		verify(userRepository, times(1)).findUserByUsername(user.getUsername());

		verify(loginView, times(0)).showError(anyString());
		verify(loginView, times(0)).showError(anyString(), any());

		verify(userRepository, times(1)).getRegistrationToken();
		verify(userRepository).save(user);
	}

	@Test
	public void testInvalidNewNullUserButValidToken() {
		userController.newUser(null, VALID_TOKEN);

		verify(userRepository, times(1)).getRegistrationToken();
		verify(loginView).showError("Invalid null user passed", null);
		verifyNoMoreInteractions(ignoreStubs(userRepository));
	}

	@Test
	public void testNewUserWithSameIdAlreadyExistAndValidToken() throws SQLException {
		User existingUser = new User("test", VALID_PASSWORD, 1);
		User userToAdd = new User("test", "passwordDifferent", 1);

		when(userRepository.findUserById(1)).thenReturn(existingUser);
		userController.newUser(userToAdd, VALID_TOKEN);

		verify(userRepository, times(1)).findUserById(userToAdd.getId());
		verify(userRepository, times(1)).getRegistrationToken();
		verify(loginView).showError("Already existing user ", existingUser);
		verifyNoMoreInteractions(ignoreStubs(userRepository));
	}

	@Test
	public void testNewUserWithUsernameLessThanEigthChars() throws SQLException {
		String sevenChar = "1234567";
		User userToAdd = new User("test", sevenChar, 1);

		userController.newUser(userToAdd, VALID_TOKEN);

		verify(userRepository, times(1)).getRegistrationToken();
		verify(userRepository, times(1)).findUserById(userToAdd.getId());
		verify(userRepository, times(1)).findUserByUsername(userToAdd.getUsername());

		verify(loginView).showError("Username must be greater or equal than 8 chars ", userToAdd);
		verifyNoMoreInteractions(ignoreStubs(userRepository));
	}

	@Test
	public void testControllerSQLExceptionShowsErrorOnSave() throws SQLException {
		User userToAdd = new User("test", VALID_PASSWORD, 1);
		String exceptionMessage = "Database connection failed";

		doThrow(new SQLException(exceptionMessage)).when(userRepository).save(userToAdd);

		userController.newUser(userToAdd, VALID_TOKEN);

		verify(userRepository, times(1)).getRegistrationToken();
		verify(userRepository, times(1)).findUserById(userToAdd.getId());
		verify(userRepository, times(1)).findUserByUsername(userToAdd.getUsername());
		verify(userRepository, times(1)).save(userToAdd);
		verify(loginView).showError("Exception occurred in repository: " + exceptionMessage);
	}

	@Test
	public void testControllerSQLExceptionShowsErrorOnFindUserByID() throws SQLException {
		User userToCheck = new User("test", VALID_PASSWORD, 1);
		String exceptionMessage = "Database connection failed";

		doThrow(new SQLException(exceptionMessage)).when(userRepository).findUserById(1);

		userController.newUser(userToCheck, VALID_TOKEN);

		verify(userRepository, times(1)).getRegistrationToken();
		verify(userRepository, times(1)).findUserById(userToCheck.getId());
		verify(loginView).showError("Exception occurred in repository: " + exceptionMessage);
	}

	@Test
	public void testControllerSQLExceptionShowsErrorOnFindUserByUsername() throws SQLException {
		User userToCheck = new User("test", VALID_PASSWORD, 1);
		String exceptionMessage = "Database connection failed";

		doThrow(new SQLException(exceptionMessage)).when(userRepository).findUserByUsername(userToCheck.getUsername());

		userController.newUser(userToCheck, VALID_TOKEN);

		verify(userRepository, times(1)).getRegistrationToken();
		verify(userRepository, times(1)).findUserById(userToCheck.getId());
		verify(userRepository, times(1)).findUserByUsername(userToCheck.getUsername());
		verify(loginView).showError("Exception occurred in repository: " + exceptionMessage);
	}

	@Test
	public void testControllerSQLExceptionShowsErrorOnFindUserByUsernameAndPassword() throws SQLException {
		User userToCheck = new User("test", VALID_PASSWORD, 1);
		String exceptionMessage = "Database connection failed";

		doThrow(new SQLException(exceptionMessage)).when(userRepository)
				.findUserByUsernameAndPassword(userToCheck.getUsername(), userToCheck.getPassword());

		userController.login(userToCheck.getUsername(), userToCheck.getPassword());

		verify(userRepository, times(1)).findUserByUsernameAndPassword(userToCheck.getUsername(),
				userToCheck.getPassword());
		verify(loginView).showError("Exception occurred in repository: " + exceptionMessage);
	}

	@Test
	public void testNewUserWithUsernameWithExactlyEigthCharsMustSuceed() throws SQLException {
		String sevenChar = "12345678";
		User userToAdd = new User("test", sevenChar, 1);

		userController.newUser(userToAdd, VALID_TOKEN);

		verify(userRepository, times(1)).getRegistrationToken();
		verify(userRepository, times(1)).findUserById(userToAdd.getId());
		verify(userRepository, times(1)).findUserByUsername(userToAdd.getUsername());

		verify(loginView, times(0)).showError(anyString());
		verify(loginView, times(0)).showError(anyString(), any());

		verify(userRepository).save(userToAdd);
	}

	@Test
	public void testNewUserWithDifferentIdButSameUsernameShouldFail() throws SQLException {
		User existingUser = new User("test", VALID_PASSWORD, 1);
		User userToAdd = new User("test", "passwordDifferent", 2);

		when(userRepository.findUserByUsername("test")).thenReturn(existingUser);

		userController.newUser(userToAdd, VALID_TOKEN);

		verify(userRepository, times(1)).findUserByUsername(userToAdd.getUsername());
		verify(userRepository, times(1)).getRegistrationToken();
		verify(userRepository, times(1)).findUserById(userToAdd.getId());

		verify(loginView).showError("Already existing user ", existingUser);
		verifyNoMoreInteractions(ignoreStubs(userRepository));
	}

	@Test
	public void loginWhitNullUsername() throws SQLException {
		String password = VALID_PASSWORD;

		userController.login(null, password);
		verify(userRepository, times(1)).findUserByUsernameAndPassword(null, password);
		verify(loginView, times(0)).switchPanel();
		verify(loginView, times(1)).showError("Invalid credentials");
	}

	@Test
	public void loginWhitNullPassword() throws SQLException {
		String username = "test";

		userController.login(username, null);
		verify(userRepository, times(1)).findUserByUsernameAndPassword(username, null);
		verify(loginView, times(0)).switchPanel();
		verify(loginView, times(1)).showError("Invalid credentials");
	}

	// docs
	@Test
	public void loginWhitEmptyCredentials() throws SQLException {
		userController.login("", "");
		verify(userRepository, times(1)).findUserByUsernameAndPassword("", "");
		verify(loginView, times(0)).switchPanel();
		verify(loginView, times(1)).showError("Invalid credentials");
	}

	@Test
	public void loginSuceedsWhenCredentialsAreCorrectAndUserExists() throws SQLException {
		String username = "test";
		String password = VALID_PASSWORD;
		// mock
		when(userRepository.findUserByUsernameAndPassword(username, password))
				.thenReturn(new User(username, password, 1));

		userController.login(username, password);
		verify(userRepository, times(1)).findUserByUsernameAndPassword(username, password);
		verify(loginView, times(1)).switchPanel();
		verify(loginView, times(0)).showError(anyString());
		verify(loginView, times(0)).showError(anyString(), any());
	}

	@Test
	public void loginNotSuceedsWhenUserDoesNotExists() throws SQLException {
		String username = "test";
		String password = VALID_PASSWORD;

		userController.login(username, password);
		verify(userRepository, times(1)).findUserByUsernameAndPassword(username, password);
		verify(loginView, times(1)).showError("Invalid credentials");
		verify(loginView, times(0)).switchPanel();

	}

	@Test
	public void loginNotSuceedsWhenCredentialsAreWrong() throws SQLException {
		String username = "test";
		String password = VALID_PASSWORD;
		when(userRepository.findUserByUsernameAndPassword(username, password))
				.thenReturn(new User(username, password, 1));

		String wrongPassword = "wrongPassword";
		String wrongUsername = "wrongUsername";
		userController.login(wrongUsername, wrongPassword);
		verify(userRepository, times(1)).findUserByUsernameAndPassword(wrongUsername, wrongPassword);
		verify(loginView, times(0)).switchPanel();
		verify(loginView).showError("Invalid credentials");
	}

	@Test
	public void loginSucceedsCheckViewSwitch() throws SQLException {
		String username = "test";
		String password = VALID_PASSWORD;
		// mock
		when(userRepository.findUserByUsernameAndPassword(username, password))
				.thenReturn(new User(username, password, 1));

		userController.login(username, password);
		InOrder inOrder = inOrder(userRepository, loginView);
		inOrder.verify(userRepository, times(1)).findUserByUsernameAndPassword(username, password);
		inOrder.verify(loginView, times(1)).switchPanel();
	}
}
