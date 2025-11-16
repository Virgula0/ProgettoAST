package com.rosa.angelo.progetto.ast.e2e;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.swing.launcher.ApplicationLauncher.application;

import java.util.regex.Pattern;

import javax.swing.JFrame;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.GenericTypeMatcher;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.finder.WindowFinder;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.bson.Document;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.testcontainers.containers.GenericContainer;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import com.rosa.angelo.progetto.ast.model.Product;
import com.rosa.angelo.progetto.ast.model.User;
import com.rosa.angelo.progetto.ast.repository.ProductMongoRepository;
import com.rosa.angelo.progetto.ast.repository.UserMongoRepository;
import com.rosa.angelo.progetto.ast.repository.UserRepository;

@RunWith(GUITestRunner.class)
public class AppUsingMongoDBE2E extends AssertJSwingJUnitTestCase {
	private FrameFixture window;
	private FrameFixture managerWindow;

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

	private static final String DB_NAME = "ast";
	private static final String USER_COLLECTION = "users";
	private static final String PRODUCT_COLLECTION = "products";
	private final String VALID_TOKEN = UserRepository.REGISTRATION_TOKEN;
	private final User userFixture = new User("test1234", "password1234", 1);
	private Product productOneFixture;
	private Product productTwoFixture;

	@Override
	protected void onSetUp() throws Exception {
		database.drop();
		String containerIpAddress = mongo.getHost();
		Integer mappedPort = mongo.getFirstMappedPort();

		addTestUserToDatabase(userFixture);
		productOneFixture = new Product(userFixture, "test1Name", "test1Surname", "test1Address", "test1Package", 1);
		productTwoFixture = new Product(userFixture, "test2Name", "test2Surname", "test2Address", "test2Package", 2);
		addTestProductToDatabase(productOneFixture);
		addTestProductToDatabase(productTwoFixture);

		application("com.rosa.angelo.progetto.ast.main.Main").withArgs("--mongo-host=" + containerIpAddress,
				"--mongo-port=" + mappedPort.toString(), "--db-name=" + DB_NAME,
				"--db-user-collection=" + USER_COLLECTION, "--db-product-collection=" + PRODUCT_COLLECTION).start();

		// get a reference of its JFrame
		window = WindowFinder.findFrame(new GenericTypeMatcher<JFrame>(JFrame.class) {
			@Override
			protected boolean isMatching(JFrame frame) {
				return "LoginView".equals(frame.getTitle()) && frame.isShowing();
			}
		}).using(robot());
	}

	@AfterClass
	public static void afterClass() {
		client.close();
	}

	// -- START OF HELPERS

	private void addTestUserToDatabase(User user) {
		client.getDatabase(DB_NAME).getCollection(USER_COLLECTION).insertOne(new Document().append("id", user.getId())
				.append("username", user.getUsername()).append("password", user.getPassword()));
	}

	private void addTestProductToDatabase(Product p) {
		client.getDatabase(DB_NAME).getCollection(PRODUCT_COLLECTION)
				.insertOne(new Document().append(ProductMongoRepository.SENDER_ID_KEY, p.getSender().getId())
						.append(ProductMongoRepository.SENDER_USERNAME_KEY, p.getSender().getUsername())
						.append(ProductMongoRepository.PRODUCT_ID_KEY, p.getId())
						.append(ProductMongoRepository.RECEIVER_NAME_KEY, p.getReceiverName())
						.append(ProductMongoRepository.RECEIVER_SURNAME_KEY, p.getReceiverSurname())
						.append(ProductMongoRepository.RECEIVER_ADDRESS_KEY, p.getReceiverAddress())
						.append(ProductMongoRepository.RECEIVER_PACKAGETYPE_KEY, p.getPackageType()));
	}

	private void removeTestProductFromDatabase(Product p) {
		Document query = new Document(ProductMongoRepository.PRODUCT_ID_KEY, p.getId());
		client.getDatabase(DB_NAME).getCollection(PRODUCT_COLLECTION).findOneAndDelete(query);
	}

	// -- END OF HELPERS

	private void performLoginWithUserFixtureAndSwitchProductView() {
		window.textBox("loginUsernameInputText").enterText(userFixture.getUsername());
		window.textBox("loginPasswordInputText").enterText(userFixture.getPassword());
		window.button(JButtonMatcher.withText("Login")).click();
		managerWindow = WindowFinder.findFrame("ManagerView").using(window.robot());
	}

	@Test
	@GUITest
	public void testRegistrationWithAnotherUserIsOK() {
		window.textBox("registrationIdInputText").enterText("2");
		window.textBox("registrationUsernameInputText").enterText("testUsername");
		window.textBox("registrationPasswordInputText").enterText("testPassword");
		window.textBox("registrationTokenInputText").enterText(VALID_TOKEN);
		window.button(JButtonMatcher.withText("Register")).click();

		window.label("errorMessageLabel").requireText(" ");
	}

	@Test
	@GUITest
	public void testRegistrationWithSAmeIdOfUserFixtureIsNotOk() {
		window.textBox("registrationIdInputText").enterText("1");
		window.textBox("registrationUsernameInputText").enterText("testUsername");
		window.textBox("registrationPasswordInputText").enterText("testPassword");
		window.textBox("registrationTokenInputText").enterText(VALID_TOKEN);
		window.button(JButtonMatcher.withText("Register")).click();

		window.label("errorMessageLabel")
				.requireText("Already existing user by id or username similarity : " + userFixture);
	}

	@Test
	@GUITest
	public void testLoginWithUserFixtureIsOk() {
		window.textBox("loginUsernameInputText").enterText(userFixture.getUsername());
		window.textBox("loginPasswordInputText").enterText(userFixture.getPassword());
		window.button(JButtonMatcher.withText("Login")).click();

		FrameFixture managerWindow = WindowFinder.findFrame("ManagerView").using(window.robot());
		managerWindow.requireVisible();
	}

	@Test
	@GUITest
	public void testUserFixtureProductsAreCorrectlyShown() {
		performLoginWithUserFixtureAndSwitchProductView();
		assertThat(managerWindow.list().contents())
				.anySatisfy(e -> assertThat(e).contains(String.valueOf(productOneFixture.getId()),
						productOneFixture.getReceiverName(), productOneFixture.getReceiverSurname(),
						productOneFixture.getReceiverAddress(), productOneFixture.getPackageType()))
				.anySatisfy(e -> assertThat(e).contains(String.valueOf(productTwoFixture.getId()),
						productTwoFixture.getReceiverName(), productTwoFixture.getReceiverSurname(),
						productTwoFixture.getReceiverAddress(), productTwoFixture.getPackageType()));
	}

	@Test
	@GUITest
	public void testAddProductFixtureSuccess() {
		performLoginWithUserFixtureAndSwitchProductView();
		managerWindow.textBox("productIdInputText").enterText("3");
		managerWindow.textBox("receiverNameInputText").enterText("test3Name");
		managerWindow.textBox("receiverSurnameInputText").enterText("test3Surname");
		managerWindow.textBox("receiverAddressInputText").enterText("test3Address");
		managerWindow.textBox("packageTypeInputText").enterText("test3Package");
		managerWindow.button(JButtonMatcher.withText("Add")).click();
		assertThat(managerWindow.list().contents()).anySatisfy(
				e -> assertThat(e).contains("3", "test3Name", "test3Surname", "test3Address", "test3Package"));
	}

	@Test
	@GUITest
	public void testAddProductError() {
		performLoginWithUserFixtureAndSwitchProductView();
		managerWindow.textBox("productIdInputText").enterText("1");
		managerWindow.textBox("receiverNameInputText").enterText(productOneFixture.getReceiverName());
		managerWindow.textBox("receiverSurnameInputText").enterText(productOneFixture.getReceiverSurname());
		managerWindow.textBox("receiverAddressInputText").enterText(productOneFixture.getReceiverAddress());
		managerWindow.textBox("packageTypeInputText").enterText(productOneFixture.getPackageType());
		managerWindow.button(JButtonMatcher.withText("Add")).click();

		managerWindow.label("errorMessageLabel")
				.requireText("Product already exists with this ID : " + productOneFixture);
	}

	@Test
	@GUITest
	public void testDeleteProductWhenExists() {
		performLoginWithUserFixtureAndSwitchProductView();
		managerWindow.list("productList")
				.selectItem(Pattern.compile(".*" + productOneFixture.getReceiverName() + ".*"));
		managerWindow.button(JButtonMatcher.withText("Delete Product")).click();
		assertThat(managerWindow.list().contents()).noneMatch(e -> e.contains(productOneFixture.getReceiverName()));
	}

	@Test
	@GUITest
	public void testDeleteProductWhenDoesNotExist() {
		performLoginWithUserFixtureAndSwitchProductView();
		managerWindow.list("productList")
				.selectItem(Pattern.compile(".*" + productOneFixture.getReceiverName() + ".*"));
		removeTestProductFromDatabase(productOneFixture);
		managerWindow.button(JButtonMatcher.withText("Delete Product")).click();
		managerWindow.label("errorMessageLabel")
				.requireText("Product does not exists with such ID : " + productOneFixture);
	}
}
