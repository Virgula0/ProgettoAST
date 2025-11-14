package com.rosa.angelo.progetto.ast.view;

import static org.assertj.core.api.Assertions.assertThat;

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
import com.rosa.angelo.progetto.ast.controller.ProductController;
import com.rosa.angelo.progetto.ast.model.Product;
import com.rosa.angelo.progetto.ast.model.User;
import com.rosa.angelo.progetto.ast.repository.ProductMongoRepository;

@RunWith(GUITestRunner.class)
public class ITProductViewControllerMongoDBRepository extends AssertJSwingJUnitTestCase {
	private ProductSwingView productView;
	private ProductMongoRepository productRepository;
	private ProductController productController;

	private FrameFixture window;

	@SuppressWarnings({ "rawtypes", "resource" })
	@ClassRule
	public static final GenericContainer mongo = new GenericContainer(
			ProductMongoRepository.IMAGE + ":" + ProductMongoRepository.VERSION)
			.withExposedPorts(ProductMongoRepository.PORT);
	private static MongoClient client;
	private static MongoDatabase database;

	private User loggedInUser;

	@BeforeClass
	public static void beforeClassSetup() {
		client = new MongoClient(new ServerAddress(mongo.getHost(), mongo.getMappedPort(ProductMongoRepository.PORT)));
		database = client.getDatabase(ProductMongoRepository.AST_DB_NAME);
	}

	@Override
	protected void onSetUp() throws Exception {
		database.drop();
		loggedInUser = new User("test", "password1234", 1);
		GuiActionRunner.execute(() -> {
			productRepository = new ProductMongoRepository(client);
			productView = new ProductSwingView();
			productController = new ProductController(productView, productRepository);
			productView.setProductController(productController);
			productView.setLoggedInUser(loggedInUser);
			return productView;
		});
		window = new FrameFixture(robot(), productView);
		window.show();
	}

	@AfterClass
	public static void afterClass() {
		client.close();
	}

	@Test
	@GUITest
	public void testAllProducts() {
		Product product1 = new Product(loggedInUser, "test1", "test1", "test1", "test1", 1);
		Product product2 = new Product(loggedInUser, "test2", "test2", "test2", "test2", 2);
		productRepository.save(product1);
		productRepository.save(product2);
		GuiActionRunner.execute(() -> productController.allProducts(loggedInUser));
		assertThat(window.list().contents()).containsExactly(product1.toString(), product2.toString());
	}

	@Test
	@GUITest
	public void testAddProductButtonSuccess() {
		Product product1 = new Product(loggedInUser, "test1", "test1", "test1", "test1", 1);
		window.textBox("productIdInputText").enterText("1");
		window.textBox("receiverNameInputText").enterText("test1");
		window.textBox("receiverSurnameInputText").enterText("test1");
		window.textBox("receiverAddressInputText").enterText("test1");
		window.textBox("packageTypeInputText").enterText("test1");
		window.button(JButtonMatcher.withText("Add")).click();
		assertThat(window.list().contents()).containsExactly(product1.toString());
	}

	@Test
	@GUITest
	public void testAddProductButtonNotSucceedBecauseProductAlreadyExisting() {
		Product product1 = new Product(loggedInUser, "test1", "test1", "test1", "test1", 1);
		productRepository.save(product1);

		window.textBox("productIdInputText").enterText("1");
		window.textBox("receiverNameInputText").enterText("test1");
		window.textBox("receiverSurnameInputText").enterText("test1");
		window.textBox("receiverAddressInputText").enterText("test1");
		window.textBox("packageTypeInputText").enterText("test1");
		window.button(JButtonMatcher.withText("Add")).click();

		assertThat(window.list().contents()).isEmpty();
		assertThat(window.label("errorMessageLabel").text())
				.isEqualTo("Product already exists with this ID : " + product1);
	}
	
	@Test
	@GUITest
	public void testDeleteProductButtonSuccess() {
		Product product1 = new Product(loggedInUser, "test1", "test1", "test1", "test1", 1);
		GuiActionRunner.execute(() -> productController.newProduct(product1, loggedInUser));
		window.list().selectItem(0);
		window.button(JButtonMatcher.withText("Delete Product")).click();
		assertThat(window.list().contents()).isEmpty();
	}
	
	@Test
	@GUITest
	public void testDeleteProductButtonError() {
		Product product1 = new Product(loggedInUser, "test1", "test1", "test1", "test1", 1);
		GuiActionRunner.execute(() -> productView.getListProductModel().addElement(product1));
		window.list().selectItem(0);
		window.button(JButtonMatcher.withText("Delete Product")).click();
		assertThat(window.list().contents()).containsExactly(product1.toString());
		window.label("errorMessageLabel").requireText("Product does not exists with such ID : " + product1);
	}
}
