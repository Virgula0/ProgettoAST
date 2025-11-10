package com.rosa.angelo.progetto.ast.view;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.rosa.angelo.progetto.ast.controller.UserController;
import com.rosa.angelo.progetto.ast.model.User;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.JPasswordField;
import javax.swing.JButton;
import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class LoginAndRegistrationSwingView extends JFrame implements LoginView, CommonPanel {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private transient UserController userController;
	private JTextField registrationIdInputText;
	private JTextField registrationUsernameInputText;
	private JPasswordField registrationPasswordInputText;
	private JTextField loginUsernameInputText;
	private JPasswordField loginPasswordInputText;
	private JButton registerButton;
	private JButton loginButton;
	private JTextField registrationTokenInputText;
	private JLabel errorMessageLabel;

	private transient CommonPanel nextPanel;

	public void setNextPanel(CommonPanel nextPanel) {
		this.nextPanel = nextPanel;
	}

	@Override
	public void showError(String message, User user) {
		errorMessageLabel.setText(message + ": " + user);
	}

	@Override
	public void showError(String message) {
		errorMessageLabel.setText(message);
	}

	@Override
	public void start() {
		setVisible(true);
	}

	@Override
	public void switchPanel() {
		this.dispose();
		nextPanel.start();
	}

	public UserController getUserController() {
		return userController;
	}

	public void setUserController(UserController userController) {
		this.userController = userController;
	}

	/**
	 * Create the frame.
	 */
	public LoginAndRegistrationSwingView() {
		String viewName = "LoginView";
		setTitle(viewName);
		setName(viewName);
		int exitOnClose = WindowConstants.EXIT_ON_CLOSE;
		setDefaultCloseOperation(exitOnClose);
		setBounds(100, 100, 450, 442);
		contentPane = new JPanel();
		contentPane.setName(viewName);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[] { 0, 387 };
		gbl_contentPane.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		gbl_contentPane.columnWeights = new double[] { 0.0, 1.0 };
		gbl_contentPane.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
				0.0, 0.0, 0.0, Double.MIN_VALUE };
		contentPane.setLayout(gbl_contentPane);

		JLabel lblNewLabel = new JLabel("Registration Form");
		lblNewLabel.setName("registrationLabel");
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.gridwidth = 2;
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 0);
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 1;
		contentPane.add(lblNewLabel, gbc_lblNewLabel);

		JLabel lblNewLabel_1 = new JLabel("id");
		lblNewLabel_1.setName("registrationIdLabel");
		GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
		gbc_lblNewLabel_1.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_1.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_1.gridx = 0;
		gbc_lblNewLabel_1.gridy = 3;
		contentPane.add(lblNewLabel_1, gbc_lblNewLabel_1);

		registrationIdInputText = new JTextField();
		registrationIdInputText.setName("registrationIdInputText");
		GridBagConstraints gbc_registrationIDInputText = new GridBagConstraints();
		gbc_registrationIDInputText.insets = new Insets(0, 0, 5, 0);
		gbc_registrationIDInputText.fill = GridBagConstraints.HORIZONTAL;
		gbc_registrationIDInputText.gridx = 1;
		gbc_registrationIDInputText.gridy = 3;
		contentPane.add(registrationIdInputText, gbc_registrationIDInputText);
		registrationIdInputText.setColumns(10);

		JLabel lblNewLabel_2 = new JLabel("Username");
		lblNewLabel_2.setName("registrationUsernameLabel");
		GridBagConstraints gbc_lblNewLabel_2 = new GridBagConstraints();
		gbc_lblNewLabel_2.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_2.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_2.gridx = 0;
		gbc_lblNewLabel_2.gridy = 4;
		contentPane.add(lblNewLabel_2, gbc_lblNewLabel_2);

		registrationUsernameInputText = new JTextField();
		registrationUsernameInputText.setName("registrationUsernameInputText");
		GridBagConstraints gbc_textField_1 = new GridBagConstraints();
		gbc_textField_1.insets = new Insets(0, 0, 5, 0);
		gbc_textField_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_1.gridx = 1;
		gbc_textField_1.gridy = 4;
		contentPane.add(registrationUsernameInputText, gbc_textField_1);
		registrationUsernameInputText.setColumns(10);

		JLabel lblNewLabel_3 = new JLabel("Password");
		lblNewLabel_3.setName("registrationPasswordLabel");
		GridBagConstraints gbc_lblNewLabel_3 = new GridBagConstraints();
		gbc_lblNewLabel_3.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_3.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_3.gridx = 0;
		gbc_lblNewLabel_3.gridy = 5;
		contentPane.add(lblNewLabel_3, gbc_lblNewLabel_3);

		registrationPasswordInputText = new JPasswordField();
		registrationPasswordInputText.setName("registrationPasswordInputText");
		GridBagConstraints gbc_passwordField = new GridBagConstraints();
		gbc_passwordField.insets = new Insets(0, 0, 5, 0);
		gbc_passwordField.fill = GridBagConstraints.HORIZONTAL;
		gbc_passwordField.gridx = 1;
		gbc_passwordField.gridy = 5;
		contentPane.add(registrationPasswordInputText, gbc_passwordField);

		JLabel lblNewLabel_8 = new JLabel("Token");
		lblNewLabel_8.setName("registrationTokenLabel");
		GridBagConstraints gbc_lblNewLabel_8 = new GridBagConstraints();
		gbc_lblNewLabel_8.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_8.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_8.gridx = 0;
		gbc_lblNewLabel_8.gridy = 6;
		contentPane.add(lblNewLabel_8, gbc_lblNewLabel_8);

		registrationTokenInputText = new JTextField();
		registrationTokenInputText.setName("registrationTokenInputText");
		GridBagConstraints gbc_registrationTokenInputText = new GridBagConstraints();
		gbc_registrationTokenInputText.insets = new Insets(0, 0, 5, 0);
		gbc_registrationTokenInputText.fill = GridBagConstraints.HORIZONTAL;
		gbc_registrationTokenInputText.gridx = 1;
		gbc_registrationTokenInputText.gridy = 6;
		contentPane.add(registrationTokenInputText, gbc_registrationTokenInputText);
		registrationTokenInputText.setColumns(10);

		registerButton = new JButton("Register");
		registerButton.setEnabled(false);
		registerButton.setName("registerButton");
		GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
		gbc_btnNewButton.gridwidth = 2;
		gbc_btnNewButton.insets = new Insets(0, 0, 5, 0);
		gbc_btnNewButton.gridx = 0;
		gbc_btnNewButton.gridy = 7;
		contentPane.add(registerButton, gbc_btnNewButton);

		JLabel lblNewLabel_4 = new JLabel("Login Form");
		GridBagConstraints gbc_lblNewLabel_4 = new GridBagConstraints();
		gbc_lblNewLabel_4.gridwidth = 2;
		gbc_lblNewLabel_4.insets = new Insets(0, 0, 5, 0);
		gbc_lblNewLabel_4.gridx = 0;
		gbc_lblNewLabel_4.gridy = 9;
		contentPane.add(lblNewLabel_4, gbc_lblNewLabel_4);

		JLabel lblNewLabel_5 = new JLabel("Username");
		lblNewLabel_5.setName("loginUsernameLabel");
		GridBagConstraints gbc_lblNewLabel_5 = new GridBagConstraints();
		gbc_lblNewLabel_5.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_5.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_5.gridx = 0;
		gbc_lblNewLabel_5.gridy = 11;
		contentPane.add(lblNewLabel_5, gbc_lblNewLabel_5);

		loginUsernameInputText = new JTextField();
		loginUsernameInputText.setName("loginUsernameInputText");
		GridBagConstraints gbc_textField_2 = new GridBagConstraints();
		gbc_textField_2.insets = new Insets(0, 0, 5, 0);
		gbc_textField_2.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_2.gridx = 1;
		gbc_textField_2.gridy = 11;
		contentPane.add(loginUsernameInputText, gbc_textField_2);
		loginUsernameInputText.setColumns(10);

		JLabel lblNewLabel_6 = new JLabel("Password");
		lblNewLabel_6.setName("loginPasswordLabel");
		GridBagConstraints gbc_lblNewLabel_6 = new GridBagConstraints();
		gbc_lblNewLabel_6.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_6.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_6.gridx = 0;
		gbc_lblNewLabel_6.gridy = 12;
		contentPane.add(lblNewLabel_6, gbc_lblNewLabel_6);

		loginPasswordInputText = new JPasswordField();
		loginPasswordInputText.setName("loginPasswordInputText");
		GridBagConstraints gbc_passwordField_1 = new GridBagConstraints();
		gbc_passwordField_1.insets = new Insets(0, 0, 5, 0);
		gbc_passwordField_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_passwordField_1.gridx = 1;
		gbc_passwordField_1.gridy = 12;
		contentPane.add(loginPasswordInputText, gbc_passwordField_1);

		loginButton = new JButton("Login");
		loginButton.setEnabled(false);
		GridBagConstraints gbc_btnNewButton_1 = new GridBagConstraints();
		gbc_btnNewButton_1.gridwidth = 2;
		gbc_btnNewButton_1.insets = new Insets(0, 0, 5, 0);
		gbc_btnNewButton_1.gridx = 0;
		gbc_btnNewButton_1.gridy = 13;
		contentPane.add(loginButton, gbc_btnNewButton_1);

		errorMessageLabel = new JLabel(" ");
		errorMessageLabel.setForeground(new Color(165, 29, 45));
		errorMessageLabel.setName("errorMessageLabel");
		GridBagConstraints gbc_lblNewLabel_7 = new GridBagConstraints();
		gbc_lblNewLabel_7.insets = new Insets(0, 0, 5, 0);
		gbc_lblNewLabel_7.gridwidth = 2;
		gbc_lblNewLabel_7.gridx = 0;
		gbc_lblNewLabel_7.gridy = 14;
		contentPane.add(errorMessageLabel, gbc_lblNewLabel_7);

		eventsHandler();
	}

	private void eventsHandler() {
		KeyAdapter registerCheckerAdapter = new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				char[] passwordChars = registrationPasswordInputText.getPassword();
				String password = new String(passwordChars);

				registerButton.setEnabled(!registrationIdInputText.getText().isBlank()
						&& !registrationUsernameInputText.getText().isBlank() && !password.isBlank()
						&& !registrationTokenInputText.getText().isBlank());
			}
		};
		registrationIdInputText.addKeyListener(registerCheckerAdapter);
		registrationUsernameInputText.addKeyListener(registerCheckerAdapter);
		registrationPasswordInputText.addKeyListener(registerCheckerAdapter);
		registrationTokenInputText.addKeyListener(registerCheckerAdapter);

		registerButton.addActionListener(e -> userController.newUser(
				new User(registrationUsernameInputText.getText(),
						new String(registrationPasswordInputText.getPassword()), 1),
				registrationTokenInputText.getText()));

		KeyAdapter loginCheckerAdapter = new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				char[] passwordChars = loginPasswordInputText.getPassword();
				String password = new String(passwordChars);

				loginButton.setEnabled(!loginUsernameInputText.getText().isBlank() && !password.isBlank());
			}
		};
		loginUsernameInputText.addKeyListener(loginCheckerAdapter);
		loginPasswordInputText.addKeyListener(loginCheckerAdapter);
	}
}
