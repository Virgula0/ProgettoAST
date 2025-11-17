package com.rosa.angelo.progetto.ast.view;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

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
import com.rosa.angelo.progetto.ast.model.User;
import com.rosa.angelo.progetto.ast.repository.UserMongoRepository;

@RunWith(GUITestRunner.class)
public class LoginAndRegistrationSwingViewTest extends AssertJSwingJUnitTestCase {
	private FrameFixture window;

	private LoginAndRegistrationSwingView loginView;

	@Mock
	private UserController userController;

	private AutoCloseable closeable;

	private final String VALID_TOKEN = UserMongoRepository.REGISTRATION_TOKEN;

	private static class FakePanel extends JFrame implements PanelSwitcher {
		private JPanel contentPane;
		private static final long serialVersionUID = 1L;

		@Override
		public void start(User userSession) {
			setVisible(true);
		}

		public FakePanel() {
			setTitle("FakePanel");
			setName("FakePanel");
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			setBounds(100, 100, 450, 442);
			contentPane = new JPanel();
			contentPane.setName("LoginView");
			contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
			setContentPane(contentPane);
			GridBagLayout gbl_contentPane = new GridBagLayout();
			gbl_contentPane.columnWidths = new int[] { 0, 387 };
			gbl_contentPane.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
			gbl_contentPane.columnWeights = new double[] { 0.0, 1.0 };
			gbl_contentPane.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
					0.0, 0.0, 0.0, Double.MIN_VALUE };
			contentPane.setLayout(gbl_contentPane);

			JLabel lblNewLabel = new JLabel("This is a test");
			lblNewLabel.setName("testNewWindowLabel");
			GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
			gbc_lblNewLabel.gridwidth = 2;
			gbc_lblNewLabel.insets = new Insets(0, 0, 5, 0);
			gbc_lblNewLabel.gridx = 0;
			gbc_lblNewLabel.gridy = 1;
			contentPane.add(lblNewLabel, gbc_lblNewLabel);
		}
	}

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

	private void resetRegistrationInputs(JTextComponentFixture idBox, JTextComponentFixture usernameBox,
			JTextComponentFixture passwordBox, JTextComponentFixture tokenBox) {
		idBox.setText("");
		usernameBox.setText("");
		passwordBox.setText("");
		tokenBox.setText("");
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

		window.label("registrationTokenLabel").requireVisible();
		window.textBox("registrationTokenInputText").requireEnabled();

		window.button(JButtonMatcher.withText("Register")).requireDisabled();

		window.label("loginUsernameLabel").requireVisible();
		window.textBox("loginUsernameInputText").requireEnabled();

		window.label("loginPasswordLabel").requireVisible();
		window.textBox("loginPasswordInputText").requireEnabled();

		window.button(JButtonMatcher.withText("Login")).requireDisabled();

		window.label("errorMessageLabel").requireText(" ");
	}

	@Test
	@GUITest
	public void testWhenIdAndUsernameAndPasswordAndTokenAreNonEmptyThenRegisterButtonShouldBeEnabled() {
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

	@Test
	public void testShowErrorWithUserShouldShowTheMessageInTheErrorLabel() {
		User user = new User("test1", "testpwd", 1);
		GuiActionRunner.execute(() -> loginView.showError("error message", user));
		window.label("errorMessageLabel").requireText("error message: " + user);
	}

	// docs
	@Test
	public void testShowErrorWithUserShouldShowTheMessageInTheErrorLabelNullValues() {
		GuiActionRunner.execute(() -> loginView.showError(null, null));
		window.label("errorMessageLabel").requireText(null + ": " + null);
	}

	@Test
	public void testShowErrorWithoutObjectShouldShowTheMessageInTheErrorLabel() {
		window.label("errorMessageLabel").requireText(" ");
		GuiActionRunner.execute(() -> loginView.showError("this is an error message"));
		window.label("errorMessageLabel").requireText("Error : this is an error message");
	}

	@Test
	@GUITest
	public void assertStartRunsCorrectly() throws Exception {
		SwingUtilities.invokeAndWait(() -> {
			loginView.start(null);
		});
		assertThat(loginView.isVisible()).isTrue();
		SwingUtilities.invokeAndWait(() -> loginView.dispose());
	}

	@Test
	@GUITest
	public void testswitchPanelClosesTheOldWindowAndLoadsTheNewOne() {
		List<FakePanel> fakePanels = new ArrayList<>();
		SwingUtilities.invokeLater(() -> {
			FakePanel fakePanel = new FakePanel();
			loginView.setNextPanel(fakePanel);
			fakePanels.add(fakePanel);
		});

		try {
			SwingUtilities.invokeAndWait(() -> {
				loginView.switchPanel(null);
			});
		} catch (Exception e) {
			fail(e.getMessage());
		}

		assertThat(loginView.isDisplayable()).isFalse();

		FrameFixture fakeFixture = new FrameFixture(super.robot(), fakePanels.get(0));

		fakeFixture.requireVisible();
		fakeFixture.label("testNewWindowLabel").requireVisible();

		fakeFixture.cleanUp();
	}

	@Test
	@GUITest
	public void testRegisterClickCallsUserController() {
		User user = new User("test123", "passsword1234", 1);
		JTextComponentFixture registrationIdInputText = window.textBox("registrationIdInputText");
		registrationIdInputText.enterText(String.valueOf(user.getId()));
		JTextComponentFixture registrationUsernameInputText = window.textBox("registrationUsernameInputText");
		registrationUsernameInputText.enterText(user.getUsername());
		JTextComponentFixture registrationPasswordInputText = window.textBox("registrationPasswordInputText");
		registrationPasswordInputText.enterText(user.getPassword());
		JTextComponentFixture registrationTokenInputText = window.textBox("registrationTokenInputText");
		registrationTokenInputText.enterText(VALID_TOKEN);

		window.button(JButtonMatcher.withText("Register")).click();
		verify(userController).newUser(user, VALID_TOKEN);
	}

	@Test
	@GUITest
	public void testLoginClickCallsUserController() {
		User user = new User("test123", "passsword1234", 1);
		window.textBox("loginUsernameInputText").enterText(user.getUsername());
		window.textBox("loginPasswordInputText").enterText(user.getPassword());

		window.button(JButtonMatcher.withText("Login")).click();
		verify(userController).login(user.getUsername(), user.getPassword());
	}

	@Test
	@GUITest
	public void testIdIsNotAnInteger() {
		window.textBox("registrationIdInputText").enterText("2.0"); // double
		window.textBox("registrationUsernameInputText").enterText("testUsername");
		window.textBox("registrationPasswordInputText").enterText("testPassword");
		window.textBox("registrationTokenInputText").enterText(VALID_TOKEN);
		window.button(JButtonMatcher.withText("Register")).click();
		window.label("errorMessageLabel").requireText("Error : Invalid id format");
	}

	@Test
	public void currentErrorMustBeResetWhenRegisterButtonIsClicked() {
		GuiActionRunner.execute(() -> {
			window.label("errorMessageLabel").target().setText("error set");
		});

		User user = new User("test123", "passsword1234", 1);

		doAnswer(invocation -> {
			loginView.resetErrorMessage();
			return null;
		}).when(userController).newUser(user, VALID_TOKEN);

		window.textBox("registrationIdInputText").enterText(String.valueOf(user.getId()));
		window.textBox("registrationUsernameInputText").enterText(user.getUsername());
		window.textBox("registrationPasswordInputText").enterText(user.getPassword());
		window.textBox("registrationTokenInputText").enterText(VALID_TOKEN);

		window.button(JButtonMatcher.withText("Register")).click();
		verify(userController).newUser(user, VALID_TOKEN);
		window.label("errorMessageLabel").requireText(" ");
	}
}
