package com.rosa.angelo.progetto.ast.view;

import static org.assertj.core.api.Assertions.assertThat;

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
public class ITViewControllerMongoDBRepository extends AssertJSwingJUnitTestCase {

	private LoginAndRegistrationSwingView loginView;
	private UserMongoRepository userRepository;
	private UserController userController;

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
			return loginView;
		});
		window = new FrameFixture(robot(), loginView);
		window.show();
	}

	@AfterClass
	public static void afterClass() {
		client.close();
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
}
