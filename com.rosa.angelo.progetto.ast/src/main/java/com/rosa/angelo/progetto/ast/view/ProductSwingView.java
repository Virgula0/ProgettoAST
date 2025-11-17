package com.rosa.angelo.progetto.ast.view;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import com.rosa.angelo.progetto.ast.controller.ProductController;
import com.rosa.angelo.progetto.ast.model.Product;
import com.rosa.angelo.progetto.ast.model.User;

public class ProductSwingView extends JFrame implements ProductView, PanelSwitcher {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;

	private transient ProductController productController;
	private JTextField productIdInputText;
	private JTextField receiverNameInputText;
	private JTextField receiverSurnameInputText;
	private JTextField receiverAddressInputText;
	private JTextField packageTypeInputText;
	private JLabel additionFormLabel;
	private JList<Product> productList;
	private JScrollPane scrollPane;
	private JButton deleteButton;
	private JLabel errorMessageLabel;
	private JButton addButton;

	private transient User loggedInUser;

	private DefaultListModel<Product> listProductModel;

	// package private method used fot testing purpose only
	DefaultListModel<Product> getListProductModel() {
		return listProductModel;
	}

	void setLoggedInUser(User loggedInUser) {
		this.loggedInUser = loggedInUser;
	}

	public void setProductController(ProductController productController) {
		this.productController = productController;
	}

	public ProductController getProductController() {
		return productController;
	}

	@Override
	public void start(User sessionUser) {
		setVisible(true);
		if (sessionUser == null) {
			return;
		}
		this.loggedInUser = sessionUser;
		productController.allProducts(loggedInUser);
	}

	/**
	 * Create the frame.
	 */
	public ProductSwingView() {
		listProductModel = new DefaultListModel<>();
		productList = new JList<>(listProductModel);

		String viewName = "ManagerView";
		setTitle(viewName);
		setName(viewName);
		int exitOnClose = WindowConstants.EXIT_ON_CLOSE;
		setDefaultCloseOperation(exitOnClose);
		setBounds(100, 100, 986, 462);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[] { 0, 0, 0 };
		gbl_contentPane.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		gbl_contentPane.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		gbl_contentPane.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0,
				Double.MIN_VALUE };
		contentPane.setLayout(gbl_contentPane);

		additionFormLabel = new JLabel("Addition form");
		additionFormLabel.setName("productAdditionLabel");
		GridBagConstraints gbc_lblNewLabel_5 = new GridBagConstraints();
		gbc_lblNewLabel_5.gridwidth = 2;
		gbc_lblNewLabel_5.insets = new Insets(0, 0, 5, 0);
		gbc_lblNewLabel_5.gridx = 0;
		gbc_lblNewLabel_5.gridy = 0;
		contentPane.add(additionFormLabel, gbc_lblNewLabel_5);

		JLabel idLabel = new JLabel("id");
		idLabel.setName("productId");
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 1;
		contentPane.add(idLabel, gbc_lblNewLabel);

		productIdInputText = new JTextField();
		productIdInputText.setName("productIdInputText");
		GridBagConstraints gbc_textField = new GridBagConstraints();
		gbc_textField.insets = new Insets(0, 0, 5, 0);
		gbc_textField.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField.gridx = 1;
		gbc_textField.gridy = 1;
		contentPane.add(productIdInputText, gbc_textField);
		productIdInputText.setColumns(10);

		JLabel receiverNameLabel = new JLabel("Receiver Name");
		receiverNameLabel.setName("receiverNameLabel");
		GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
		gbc_lblNewLabel_1.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_1.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_1.gridx = 0;
		gbc_lblNewLabel_1.gridy = 2;
		contentPane.add(receiverNameLabel, gbc_lblNewLabel_1);

		receiverNameInputText = new JTextField();
		receiverNameInputText.setName("receiverNameInputText");
		GridBagConstraints gbc_textField_1 = new GridBagConstraints();
		gbc_textField_1.insets = new Insets(0, 0, 5, 0);
		gbc_textField_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_1.gridx = 1;
		gbc_textField_1.gridy = 2;
		contentPane.add(receiverNameInputText, gbc_textField_1);
		receiverNameInputText.setColumns(10);

		JLabel lblNewLabel_2 = new JLabel("Receiver Surname");
		lblNewLabel_2.setName("receiverSurnameLabel");
		GridBagConstraints gbc_lblNewLabel_2 = new GridBagConstraints();
		gbc_lblNewLabel_2.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_2.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_2.gridx = 0;
		gbc_lblNewLabel_2.gridy = 3;
		contentPane.add(lblNewLabel_2, gbc_lblNewLabel_2);

		receiverSurnameInputText = new JTextField();
		receiverSurnameInputText.setName("receiverSurnameInputText");
		GridBagConstraints gbc_textField_2 = new GridBagConstraints();
		gbc_textField_2.insets = new Insets(0, 0, 5, 0);
		gbc_textField_2.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_2.gridx = 1;
		gbc_textField_2.gridy = 3;
		contentPane.add(receiverSurnameInputText, gbc_textField_2);
		receiverSurnameInputText.setColumns(10);

		JLabel receiverAddressLabel = new JLabel("Receiver Address");
		receiverAddressLabel.setName("receiverAddressLabel");
		GridBagConstraints gbc_lblNewLabel_3 = new GridBagConstraints();
		gbc_lblNewLabel_3.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_3.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_3.gridx = 0;
		gbc_lblNewLabel_3.gridy = 4;
		contentPane.add(receiverAddressLabel, gbc_lblNewLabel_3);

		receiverAddressInputText = new JTextField();
		receiverAddressInputText.setName("receiverAddressInputText");
		GridBagConstraints gbc_textField_3 = new GridBagConstraints();
		gbc_textField_3.insets = new Insets(0, 0, 5, 0);
		gbc_textField_3.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_3.gridx = 1;
		gbc_textField_3.gridy = 4;
		contentPane.add(receiverAddressInputText, gbc_textField_3);
		receiverAddressInputText.setColumns(10);

		JLabel packageTypeLabel = new JLabel("Package Type");
		packageTypeLabel.setName("packageTypeLabel");
		GridBagConstraints gbc_lblNewLabel_4 = new GridBagConstraints();
		gbc_lblNewLabel_4.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_4.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_4.gridx = 0;
		gbc_lblNewLabel_4.gridy = 5;
		contentPane.add(packageTypeLabel, gbc_lblNewLabel_4);

		packageTypeInputText = new JTextField();
		packageTypeInputText.setName("packageTypeInputText");
		GridBagConstraints gbc_textField_4 = new GridBagConstraints();
		gbc_textField_4.insets = new Insets(0, 0, 5, 0);
		gbc_textField_4.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_4.gridx = 1;
		gbc_textField_4.gridy = 5;
		contentPane.add(packageTypeInputText, gbc_textField_4);
		packageTypeInputText.setColumns(10);

		addButton = new JButton("Add");
		addButton.setEnabled(false);
		addButton.setName("addButton");
		GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
		gbc_btnNewButton.insets = new Insets(0, 0, 5, 0);
		gbc_btnNewButton.gridwidth = 2;
		gbc_btnNewButton.gridx = 0;
		gbc_btnNewButton.gridy = 6;
		contentPane.add(addButton, gbc_btnNewButton);

		scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.insets = new Insets(0, 0, 5, 0);
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridwidth = 2;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 7;
		contentPane.add(scrollPane, gbc_scrollPane);

		scrollPane.setViewportView(productList);
		productList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		productList.setName("productList");

		deleteButton = new JButton("Delete Product");
		deleteButton.setEnabled(false);
		deleteButton.setName("deleteButton");
		GridBagConstraints gbc_btnNewButton_1 = new GridBagConstraints();
		gbc_btnNewButton_1.insets = new Insets(0, 0, 5, 0);
		gbc_btnNewButton_1.gridwidth = 2;
		gbc_btnNewButton_1.gridx = 0;
		gbc_btnNewButton_1.gridy = 8;
		contentPane.add(deleteButton, gbc_btnNewButton_1);

		errorMessageLabel = new JLabel(" ");
		errorMessageLabel.setName("errorMessageLabel");
		errorMessageLabel.setForeground(new Color(165, 29, 45));
		GridBagConstraints gbc_lblNewLabel_6 = new GridBagConstraints();
		gbc_lblNewLabel_6.gridwidth = 2;
		gbc_lblNewLabel_6.gridx = 0;
		gbc_lblNewLabel_6.gridy = 9;
		contentPane.add(errorMessageLabel, gbc_lblNewLabel_6);

		eventHandler();
	}

	private void eventHandler() {
		KeyAdapter keyAdapter = new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				addButton.setEnabled(!productIdInputText.getText().isEmpty()
						&& !receiverNameInputText.getText().isEmpty() && !receiverSurnameInputText.getText().isEmpty()
						&& !receiverAddressInputText.getText().isEmpty() && !packageTypeInputText.getText().isEmpty());
			}
		};

		productIdInputText.addKeyListener(keyAdapter);
		receiverNameInputText.addKeyListener(keyAdapter);
		receiverSurnameInputText.addKeyListener(keyAdapter);
		receiverAddressInputText.addKeyListener(keyAdapter);
		packageTypeInputText.addKeyListener(keyAdapter);

		productList.addListSelectionListener(e -> deleteButton.setEnabled(productList.getSelectedIndex() != -1));

		addButton.addActionListener(e -> {
			int parsedId;
			try {
				parsedId = Integer.parseInt(productIdInputText.getText());
			} catch (NumberFormatException ex) {
				this.showError("Invalid id format");
				return;
			}
			productController.newProduct(
					new Product(loggedInUser, receiverNameInputText.getText(), receiverSurnameInputText.getText(),
							receiverAddressInputText.getText(), packageTypeInputText.getText(), parsedId),
					loggedInUser);
			return;
		});

		deleteButton
				.addActionListener(e -> productController.deleteProduct(productList.getSelectedValue(), loggedInUser));
	}

	@Override
	public void showAllProductsSentByUser(List<Product> toDisplay) {
		toDisplay.stream().forEach(listProductModel::addElement);
	}

	@Override
	public void showError(String message, Product product) {
		errorMessageLabel.setText(message + ": " + product);
	}

	@Override
	public void productAdded(Product product) {
		listProductModel.addElement(product);
		errorMessageLabel.setText(" ");
	}

	@Override
	public void productRemoved(Product product) {
		listProductModel.removeElement(product);
		errorMessageLabel.setText(" ");
	}

	@Override
	public void showError(String message) {
		errorMessageLabel.setText(message);
	}
}
