package com.rosa.angelo.progetto.ast.view;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.core.matcher.JLabelMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
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

		window.button(JButtonMatcher.withText("Login")).requireDisabled();
		
		window.label("errorMessageLabel").requireText(" ");
	}
}
