package com.rosa.angelo.progetto.ast.view;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.testcontainers.containers.GenericContainer;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import com.rosa.angelo.progetto.ast.controller.UserController;
import com.rosa.angelo.progetto.ast.model.User;
import com.rosa.angelo.progetto.ast.repository.UserMongoRepository;

@RunWith(GUITestRunner.class)
public class ITLoginViewControllerMongoDBRepository extends AssertJSwingJUnitTestCase {

	private LoginAndRegistrationSwingView loginView;
	private UserMongoRepository userRepository;
	private UserController userController;
	private FakePanel fakePanel;

	private FrameFixture window;

	private final String VALID_TOKEN = UserMongoRepository.REGISTRATION_TOKEN;

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

	@Override
	protected void onSetUp() throws Exception {
		database.drop();
		GuiActionRunner.execute(() -> {
			userRepository = new UserMongoRepository(client);
			loginView = new LoginAndRegistrationSwingView();
			userController = new UserController(loginView, userRepository);
			loginView.setUserController(userController);
			fakePanel = new FakePanel();
			loginView.setNextPanel(fakePanel);
			return loginView;
		});
		window = new FrameFixture(robot(), loginView);
		window.show();
	}

	@AfterClass
	public static void afterClass() {
		client.close();
	}

	private static class FakePanel extends JFrame implements PanelSwitcher {
		private JPanel contentPane;
		private static final long serialVersionUID = 1L;

		@Override
		public void start(User sessionUser) {
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

	@Test
	public void testRegistrationViaUIAndCheckUserSaved() {
		User user = new User("test123", "password1234", 1);
		window.textBox("registrationIdInputText").enterText(String.valueOf(user.getId()));
		window.textBox("registrationUsernameInputText").enterText(user.getUsername());
		window.textBox("registrationPasswordInputText").enterText(user.getPassword());
		window.textBox("registrationTokenInputText").enterText(VALID_TOKEN);
		window.button(JButtonMatcher.withText("Register")).click();

		assertThat(userRepository.findUserById(user.getId())).isEqualTo(user);
	}

	@Test
	@GUITest
	public void testLoginViaUIWhenUserExists() {
		User user = new User("test123", "password1234", 1);

		userRepository.save(user);

		window.textBox("loginUsernameInputText").enterText(user.getUsername());
		window.textBox("loginPasswordInputText").enterText(user.getPassword());

		window.button(JButtonMatcher.withText("Login")).click();

		try {
			SwingUtilities.invokeAndWait(() -> {
			});
		} catch (Exception e) {
			fail(e.getMessage());
		}

		assertThat(loginView.isDisplayable()).isFalse();

		FrameFixture fakeFixture = new FrameFixture(super.robot(), fakePanel);

		fakeFixture.requireVisible();
		fakeFixture.label("testNewWindowLabel").requireVisible();

		fakeFixture.cleanUp();
	}

	@Test
	@GUITest
	public void testRegisterButtonError() {
		User user = new User("test123", "passwor1244", 1);
		User newUser = new User("test123", "passwor1244", 1);

		userRepository.save(user);

		window.textBox("registrationIdInputText").enterText(String.valueOf(newUser.getId()));
		window.textBox("registrationUsernameInputText").enterText(newUser.getUsername());
		window.textBox("registrationPasswordInputText").enterText(newUser.getPassword());
		window.textBox("registrationTokenInputText").enterText(VALID_TOKEN);
		window.button(JButtonMatcher.withText("Register")).click();

		window.label("errorMessageLabel").requireText("Already existing user by id or username similarity : " + newUser);
	}

	@Test
	@GUITest
	public void testLoginButtonError() {
		User user = new User("test123", "passwor1244", 1);

		window.textBox("loginUsernameInputText").enterText(user.getUsername());
		window.textBox("loginPasswordInputText").enterText(user.getPassword());
		window.button(JButtonMatcher.withText("Login")).click();

		window.label("errorMessageLabel").requireText("Invalid credentials");
	}

	@Test
	@GUITest
	public void testShowErrorInvalidTokenRegistration() {
		User user = new User("test123", "passwor1244", 1);

		window.textBox("registrationIdInputText").enterText(String.valueOf(user.getId()));
		window.textBox("registrationUsernameInputText").enterText(user.getUsername());
		window.textBox("registrationPasswordInputText").enterText(user.getPassword());
		window.textBox("registrationTokenInputText").enterText("invalid token");
		window.button(JButtonMatcher.withText("Register")).click();

		window.label("errorMessageLabel").requireText("Invalid registration token");
	}

	@Test
	@GUITest
	public void testShowErrorPasswordTooShort() {
		User user = new User("test123", "psw", 1);

		window.textBox("registrationIdInputText").enterText(String.valueOf(user.getId()));
		window.textBox("registrationUsernameInputText").enterText(user.getUsername());
		window.textBox("registrationPasswordInputText").enterText(user.getPassword());
		window.textBox("registrationTokenInputText").enterText(VALID_TOKEN);
		window.button(JButtonMatcher.withText("Register")).click();

		window.label("errorMessageLabel").requireText("Password must be greater or equal than 8 chars : " + user);
	}
}
