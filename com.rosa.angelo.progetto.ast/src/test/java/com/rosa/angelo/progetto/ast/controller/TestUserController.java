package com.rosa.angelo.progetto.ast.controller;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;

import com.rosa.angelo.progetto.ast.model.User;
import com.rosa.angelo.progetto.ast.repository.UserRepository;
import com.rosa.angelo.progetto.ast.view.LoginView;

public class TestUserController {

	@Mock
	private UserRepository userRepository;

	@Mock
	private LoginView loginView;

	@InjectMocks
	private UserController userController;

	private AutoCloseable closeable;

	@Before
	public void setup() {
		closeable = MockitoAnnotations.openMocks(this);
	}

	@After
	public void releaseMocks() throws Exception {
		closeable.close();
	}

	@Test
	public void testNewUserWhenUserDoesNotAlreadyExist() {
		User user = new User("test", "test", 1);
		userController.newUser(user);
		verify(userRepository).save(user);
	}

	@Test
	public void testInvalidNewNullUser() {
		userController.newUser(null);
		verify(loginView).showError("Invalid null user passed", null);
		verifyNoMoreInteractions(ignoreStubs(userRepository));
	}

	@Test
	public void testNewUserWhenUserDoesAlreadyExist() {
		User existingUser = new User("test", "password1", 1);
		User userToAdd = new User("test", "passwordDifferent", 1);
		when(userRepository.findUserById(1)).thenReturn(existingUser);
		userController.newUser(userToAdd);
		verify(loginView).showError("Already existing user ", userToAdd);
		verifyNoMoreInteractions(ignoreStubs(userRepository));
	}

	@Test
	public void loginSuceedsWhenCredentialsAreCorrectAndUserExists() {
		String username = "test";
		String password = "password";
		// mock
		when(userRepository.findUserByUsernameAndPassword(username, password))
				.thenReturn(new User(username, password, 1));

		userController.login(username, password);
		verify(userRepository, times(1)).findUserByUsernameAndPassword(username, password);
		verify(loginView, times(1)).switchPanel();
	}

	@Test
	public void loginNotSuceedsWhenUserDoesNotExists() {
		String username = "test";
		String password = "password";
		
		userController.login(username, password);
		verify(userRepository, times(1)).findUserByUsernameAndPassword(username, password);
		verify(loginView, times(0)).switchPanel();
	}

	@Test
	public void loginNotSuceedsWhenCredentialsAreWrong() {
		String username = "test";
		String password = "password";
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
	public void loginSucceedsCheckViewSwitch() {
		String username = "test";
		String password = "password";
		// mock
		when(userRepository.findUserByUsernameAndPassword(username, password))
				.thenReturn(new User(username, password, 1));

		userController.login(username, password);
		InOrder inOrder = inOrder(userRepository, loginView);
		inOrder.verify(userRepository, times(1)).findUserByUsernameAndPassword(username, password);
		inOrder.verify(loginView, times(1)).switchPanel();
	}
}
