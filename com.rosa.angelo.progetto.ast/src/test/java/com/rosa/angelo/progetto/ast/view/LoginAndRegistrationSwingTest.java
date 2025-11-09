package com.rosa.angelo.progetto.ast.view;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.core.matcher.JLabelMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.fixture.JTextComponentFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.rosa.angelo.progetto.ast.controller.UserController;

@RunWith(GUITestRunner.class)
public class LoginAndRegistrationSwingTest extends AssertJSwingJUnitTestCase {
	private FrameFixture window;

	private LoginAndRegistrationSwingView loginView;

	@Mock
	private UserController userController;

	private AutoCloseable closeable;

	@Override
	protected void onSetUp() throws Exception {
		closeable = MockitoAnnotations.openMocks(this);
		GuiActionRunner.execute(() -> {
			loginView = new LoginAndRegistrationSwingView();
			loginView.setUserController(userController);
			return loginView;
		});
		window = new FrameFixture(super.robot(), loginView);
		window.show(); // shows the frame to test
	}

	@Override
	protected void onTearDown() throws Exception {
		closeable.close();
	}

	// docs
	@Test
	public void assertControllerIsTheRightOne() {
		assertThat(loginView.getUserController()).isSameAs(userController).isEqualTo(userController);
	}

	@Test
	@GUITest
	public void testControlsInitialStates() {
		window.label(JLabelMatcher.withText("Registration Form"));
		window.label(JLabelMatcher.withText("Login Form"));

		window.label(JLabelMatcher.withText("id"));
		window.textBox("registrationIdInputText").requireEnabled();

		window.label("registrationUsernameLabel").requireVisible();
		window.textBox("registrationUsernameInputText").requireEnabled();

		window.label("registrationPasswordLabel").requireVisible();
		window.textBox("registrationPasswordInputText").requireEnabled();

		window.button(JButtonMatcher.withText("Register")).requireDisabled();

		window.label("loginUsernameLabel").requireVisible();
		window.textBox("loginUsernameInputText").requireEnabled();

		window.label("loginPasswordLabel").requireEnabled();
		window.textBox("loginPasswordInputText").requireEnabled();

		window.label("registrationTokenLabel").requireEnabled();
		window.textBox("registrationTokenInputText").requireEnabled();

		window.button(JButtonMatcher.withText("Login")).requireDisabled();

		window.label("errorMessageLabel").requireText(" ");
	}

	@Test
	@GUITest
	public void testWhenIdAdUsernameAndPasswordAndTokenAreNonEmptyThenRegisterButtonShouldBeEnabled() {
		window.textBox("registrationIdInputText").enterText("1");
		window.textBox("registrationUsernameInputText").enterText("testUsername");
		window.textBox("registrationPasswordInputText").enterText("testPassword");
		window.textBox("registrationTokenInputText").enterText("validToken");

		window.button(JButtonMatcher.withText("Register")).requireEnabled();
		window.button(JButtonMatcher.withText("Login")).requireDisabled(); // not touched
	}

	@Test
	@GUITest
	public void testWhenUsernameAndPasswordAreNonEmptyThenLoginButtonShouldBeEnabled() {
		window.textBox("loginUsernameInputText").enterText("test123");
		window.textBox("loginPasswordInputText").enterText("testUsername");

		window.button(JButtonMatcher.withText("Login")).requireEnabled();
		window.button(JButtonMatcher.withText("Register")).requireDisabled();
	}

	@Test
	@GUITest
	public void testWhenEitherIdOrUsernameOrPasswordOrTokenAreBlankThenRegisterButtonShouldBeDisabled() {
		JTextComponentFixture idBox = window.textBox("registrationIdInputText");
		JTextComponentFixture usernameBox = window.textBox("registrationUsernameInputText");
		JTextComponentFixture passwordBox = window.textBox("registrationPasswordInputText");
		JTextComponentFixture tokenBox = window.textBox("registrationTokenInputText");

		idBox.enterText(" ");
		usernameBox.enterText("1");
		passwordBox.enterText("1");
		tokenBox.enterText("1");

		window.button(JButtonMatcher.withText("Register")).requireDisabled();

		// reset
		resetRegistrationInputs(idBox, usernameBox, passwordBox, tokenBox);

		idBox.enterText("1");
		usernameBox.enterText(" ");
		passwordBox.enterText("1");
		tokenBox.enterText("1");

		window.button(JButtonMatcher.withText("Register")).requireDisabled();

		resetRegistrationInputs(idBox, usernameBox, passwordBox, tokenBox);

		idBox.enterText("1");
		usernameBox.enterText("1");
		passwordBox.enterText(" ");
		tokenBox.enterText("1");

		window.button(JButtonMatcher.withText("Register")).requireDisabled();
		
		resetRegistrationInputs(idBox, usernameBox, passwordBox, tokenBox);
		
		idBox.enterText("1");
		usernameBox.enterText("1");
		passwordBox.enterText("1");
		tokenBox.enterText(" ");

		window.button(JButtonMatcher.withText("Register")).requireDisabled();
	}

	private void resetRegistrationInputs(JTextComponentFixture idBox, JTextComponentFixture usernameBox,
			JTextComponentFixture passwordBox, JTextComponentFixture tokenBox) {
		idBox.setText("");
		usernameBox.setText("");
		passwordBox.setText("");
		tokenBox.setText("");
	}
	
	@Test
	@GUITest
	public void testWhenEitherUsernameOrPasswordAreBlankThenLoginButtonShouldBeDisabled() {
		JTextComponentFixture usernameBox = window.textBox("loginUsernameInputText");
		JTextComponentFixture passwordBox = window.textBox("loginPasswordInputText");

		usernameBox.enterText("1");
		passwordBox.enterText(" ");
		window.button(JButtonMatcher.withText("Login")).requireDisabled();

		usernameBox.setText("");
		passwordBox.setText("");

		usernameBox.enterText(" ");
		passwordBox.enterText("test");
		window.button(JButtonMatcher.withText("Login")).requireDisabled();
	}
}
