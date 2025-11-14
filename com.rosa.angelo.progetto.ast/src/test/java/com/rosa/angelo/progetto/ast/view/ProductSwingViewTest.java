package com.rosa.angelo.progetto.ast.view;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.core.matcher.JLabelMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.fixture.JButtonFixture;
import org.assertj.swing.fixture.JListFixture;
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
		JListFixture list = window.list("productList");
		assertThat(list.contents().length).isZero();
		
		GuiActionRunner.execute(() -> productView.getListProductModel()
				.addElement(new Product(loggedInUser, "test", "test", "test", "test", 1)));
		window.list("productList").selectItem(0);
		JButtonFixture deleteButton = window.button(JButtonMatcher.withText("Delete Product"));
		deleteButton.requireEnabled();
		window.list("productList").clearSelection();
		deleteButton.requireDisabled();
	}
}
