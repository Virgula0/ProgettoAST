package com.rosa.angelo.progetto.ast.view;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Arrays;

import javax.swing.DefaultListModel;
import javax.swing.SwingUtilities;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.core.matcher.JLabelMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.fixture.JButtonFixture;
import org.assertj.swing.fixture.JTextComponentFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.rosa.angelo.progetto.ast.controller.ProductController;
import com.rosa.angelo.progetto.ast.model.Product;
import com.rosa.angelo.progetto.ast.model.User;

@RunWith(GUITestRunner.class)
public class ProductSwingViewTest extends AssertJSwingJUnitTestCase {
	private FrameFixture window;

	private ProductSwingView productView;

	@Mock
	private ProductController productController;

	private AutoCloseable closeable;

	private User loggedInUser;

	@Override
	protected void onSetUp() throws Exception {
		closeable = MockitoAnnotations.openMocks(this);
		GuiActionRunner.execute(() -> {
			productView = new ProductSwingView();
			productView.setProductController(productController);
			return productView;
		});
		window = new FrameFixture(super.robot(), productView);
		window.show(); // shows the frame to test
		loggedInUser = new User("Test", "password1234", 1);
	}

	@Override
	protected void onTearDown() throws Exception {
		closeable.close();
	}

	// docs
	@Test
	public void assertControllerIsTheRightOne() {
		assertThat(productView.getProductController()).isSameAs(productController).isEqualTo(productController);
	}

	private void resetInputsStatus() {
		window.textBox("productIdInputText").setText("");
		window.textBox("receiverNameInputText").setText("");
		window.textBox("receiverSurnameInputText").setText("");
		window.textBox("receiverAddressInputText").setText("");
		window.textBox("packageTypeInputText").setText("");
	}

	@Test
	@GUITest
	public void testControlInitialStates() {
		window.label(JLabelMatcher.withText("id"));
		window.textBox("productIdInputText").requireEnabled();

		window.label("productAdditionLabel").requireVisible();
		window.label("receiverNameLabel").requireVisible();
		window.textBox("receiverNameInputText").requireEnabled();

		window.label("receiverSurnameLabel").requireVisible();
		window.textBox("receiverSurnameInputText").requireEnabled();

		window.label("receiverAddressLabel").requireVisible();
		window.textBox("receiverAddressInputText").requireEnabled();

		window.label("packageTypeLabel").requireVisible();
		window.textBox("packageTypeInputText").requireEnabled();

		window.button(JButtonMatcher.withText("Add")).requireDisabled();

		window.list("productList");
		window.button(JButtonMatcher.withText("Delete Product")).requireDisabled();

		window.label("errorMessageLabel").requireText(" ");
	}

	@Test
	@GUITest
	public void testWhenProductInputTextAreNonEmptyThenAddButtonShouldBeEnabled() {
		window.textBox("productIdInputText").enterText("1");
		window.textBox("receiverNameInputText").enterText("test");
		window.textBox("receiverSurnameInputText").enterText("test");
		window.textBox("receiverAddressInputText").enterText("test");
		window.textBox("packageTypeInputText").enterText("test");
		window.button(JButtonMatcher.withText("Add")).requireEnabled();
	}

	@Test
	@GUITest
	public void testWhenProductInputTextAreFuzzedShouldBeDisabled() {
		window.textBox("receiverNameInputText").enterText("test");
		window.textBox("receiverSurnameInputText").enterText("test");
		window.textBox("receiverAddressInputText").enterText("test");
		window.textBox("packageTypeInputText").enterText("test");
		window.button(JButtonMatcher.withText("Add")).requireDisabled();

		resetInputsStatus();

		window.textBox("productIdInputText").enterText("1");
		window.textBox("receiverSurnameInputText").enterText("test");
		window.textBox("receiverAddressInputText").enterText("test");
		window.textBox("packageTypeInputText").enterText("test");
		window.button(JButtonMatcher.withText("Add")).requireDisabled();

		resetInputsStatus();

		window.textBox("productIdInputText").enterText("1");
		window.textBox("receiverNameInputText").enterText("test");
		window.textBox("receiverAddressInputText").enterText("test");
		window.textBox("packageTypeInputText").enterText("test");
		window.button(JButtonMatcher.withText("Add")).requireDisabled();
		resetInputsStatus();

		window.textBox("productIdInputText").enterText("1");
		window.textBox("receiverNameInputText").enterText("test");
		window.textBox("receiverSurnameInputText").enterText("test");
		window.textBox("packageTypeInputText").enterText("test");
		window.button(JButtonMatcher.withText("Add")).requireDisabled();

		resetInputsStatus();

		window.textBox("productIdInputText").enterText("1");
		window.textBox("receiverNameInputText").enterText("test");
		window.textBox("receiverSurnameInputText").enterText("test");
		window.textBox("receiverAddressInputText").enterText("test");
		window.button(JButtonMatcher.withText("Add")).requireDisabled();
	}

	@Test
	@GUITest
	public void testDeleteButtonShouldBeEnabledOnlyWhenAProductIsSelected() {
		String[] listContents = window.list().contents();
		assertThat(listContents).isEmpty();

		GuiActionRunner.execute(() -> productView.getListProductModel()
				.addElement(new Product(loggedInUser, "test", "test", "test", "test", 1)));
		window.list("productList").selectItem(0);
		JButtonFixture deleteButton = window.button(JButtonMatcher.withText("Delete Product"));
		deleteButton.requireEnabled();
		window.list("productList").clearSelection();
		deleteButton.requireDisabled();
	}

	@Test
	@GUITest
	public void testsShowAllProductsShouldAddProductDescriptionsToTheList() {
		Product product1 = new Product(loggedInUser, "test", "test", "test", "test", 1);
		Product product2 = new Product(loggedInUser, "test", "test", "test", "test", 2);

		GuiActionRunner.execute(() -> productView.showAllProductsSentByUser(Arrays.asList(product1, product2)));
		String[] listContents = window.list().contents();
		assertThat(listContents).containsExactly(product1.toString(), product2.toString());
	}

	@Test
	@GUITest
	public void testShowErrorShouldShowTheMessageInTheErrorLabel() {
		Product product = new Product(loggedInUser, "test", "test", "test", "test", 1);
		GuiActionRunner.execute(() -> productView.showError("error message", product));
		window.label("errorMessageLabel").requireText("error message: " + product);

		GuiActionRunner.execute(() -> {
			window.label("errorMessageLabel").target().setText(" ");
		});

		// message
		GuiActionRunner.execute(() -> productView.showError("error message"));
		window.label("errorMessageLabel").requireText("error message");
	}

	@Test
	@GUITest
	public void testProductAddedShouldAddTheProductToTheListAndResetTheErrorLabel() {
		Product product = new Product(loggedInUser, "test", "test", "test", "test", 1);

		GuiActionRunner.execute(() -> productView.productAdded(product));
		String[] listContents = window.list().contents();
		assertThat(listContents).containsExactly(product.toString());
		window.label("errorMessageLabel").requireText(" ");
	}

	@Test
	@GUITest
	public void testProductDeletedShouldDeleteTheProductFromTheListAndResetTheErrorLabel() {
		// setup
		Product product = new Product(loggedInUser, "test", "test", "test", "test", 1);
		Product product2 = new Product(loggedInUser, "test", "test", "test", "test", 2);

		GuiActionRunner.execute(() -> {
			productView.getListProductModel().addElement(product);
			productView.getListProductModel().addElement(product2);
		});

		String[] listContents = window.list().contents();
		String[] productStrings = new String[] { product.toString(), product2.toString() };
		assertThat(listContents).containsExactly(productStrings);

		// execute
		window.list("productList").selectItem(0);
		GuiActionRunner.execute(() -> productView.productRemoved(product)); // remove

		// verify
		listContents = window.list().contents();
		assertThat(listContents).containsExactly(product2.toString());
		window.label("errorMessageLabel").requireText(" ");
	}

	@Test
	@GUITest
	public void assertStartRunsCorrectly() throws Exception {
		// logged in not initialized!
		SwingUtilities.invokeAndWait(() -> {
			productView.start(null);
		});

		verify(productController, times(0)).allProducts(any());

		SwingUtilities.invokeAndWait(() -> {
			productView.start(loggedInUser);
		});

		verify(productController, times(1)).allProducts(loggedInUser);
		assertThat(productView.isVisible()).isTrue();
		SwingUtilities.invokeAndWait(() -> productView.dispose());
	}

	@Test
	@GUITest
	public void testAddButtonShouldDelegateToProductControllerNewProduct() {
		productView.setLoggedInUser(loggedInUser);
		window.textBox("productIdInputText").enterText("1");
		window.textBox("receiverNameInputText").enterText("test");
		window.textBox("receiverSurnameInputText").enterText("test");
		window.textBox("receiverAddressInputText").enterText("test");
		window.textBox("packageTypeInputText").enterText("test");
		window.button(JButtonMatcher.withText("Add")).requireEnabled();
		window.button(JButtonMatcher.withText("Add")).click();

		verify(productController).newProduct(new Product(loggedInUser, "test", "test", "test", "test", 1),
				loggedInUser);
	}

	@Test
	@GUITest
	public void testIdIsNotAnInteger() {
		JTextComponentFixture productIdInputText = window.textBox("productIdInputText");
		productIdInputText.enterText("WORD");
		JTextComponentFixture nameInputText = window.textBox("receiverNameInputText");
		nameInputText.enterText("test");
		JTextComponentFixture surnameInputText = window.textBox("receiverSurnameInputText");
		surnameInputText.enterText("test");
		JTextComponentFixture addressInputText = window.textBox("receiverAddressInputText");
		addressInputText.enterText("test");
		JTextComponentFixture packageInputText = window.textBox("packageTypeInputText");
		packageInputText.enterText("test");
		window.button(JButtonMatcher.withText("Add")).click();

		window.label("errorMessageLabel").requireText("Invalid id format");

		resetInputsStatus();

		window.textBox("productIdInputText").enterText("2.0"); // double
		window.textBox("receiverNameInputText").enterText("test");
		window.textBox("receiverSurnameInputText").enterText("test");
		window.textBox("receiverAddressInputText").enterText("test");
		window.textBox("packageTypeInputText").enterText("test");

		window.label("errorMessageLabel").requireText("Invalid id format");
	}

	@Test
	@GUITest
	public void testDeleteButtonShouldDelegateToProductControllerDeleteproduct() {
		productView.setLoggedInUser(loggedInUser);
		Product product = new Product(loggedInUser, "test", "test", "test", "test", 1);
		Product product2 = new Product(loggedInUser, "test", "test", "test", "test", 2);

		GuiActionRunner.execute(() -> {
			DefaultListModel<Product> productListModel = productView.getListProductModel();
			productListModel.addElement(product);
			productListModel.addElement(product2);
		});

		window.list("productList").selectItem(1);
		window.button(JButtonMatcher.withText("Delete Product")).click();
		verify(productController).deleteProduct(product2, loggedInUser);
		GuiActionRunner.execute(() -> productView.showAllProductsSentByUser(Arrays.asList(product)));
	}
}
