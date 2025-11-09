package com.rosa.angelo.progetto.ast.view;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.rosa.angelo.progetto.ast.model.User;

import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.JButton;
import java.awt.Color;

public class RegistrationAndLoginView extends JFrame implements LoginView{

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField idRegistrationInputText;
	private JTextField usernameRegistrationInputText;
	private JPasswordField registrationPasswordField;
	private JTextField usernameLoginInputText;
	private JPasswordField passwordLoginInputText;
	private JLabel registrationFormLabel;
	private JLabel idLabel;
	private JLabel registrationUsernaleLabel;
	private JLabel registrationPasswordLabel;
	private JButton registerButton;
	private JLabel loginFormLabel;
	private JLabel loginUsernameLabel;
	private JLabel loginPasswordLabel;
	private JButton loginButton;
	private JLabel errorMessageLabel;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					RegistrationAndLoginView frame = new RegistrationAndLoginView();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public RegistrationAndLoginView() {
		setTitle("LoginView");
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 617, 455);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[] { 100, 300 };
		gbl_contentPane.rowHeights = new int[] { 30, 30, 30, 30, 40, 30, 30, 30, 30, 40, 30 };
		gbl_contentPane.columnWeights = new double[] { 0.0, 1.0 };
		gbl_contentPane.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 };
		contentPane.setLayout(gbl_contentPane);

		registrationFormLabel = new JLabel("Registration Form");
		registrationFormLabel.setName("registrationLabel");
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.gridwidth = 2;
		gbc_lblNewLabel.insets = new Insets(10, 0, 10, 0);
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 0;
		contentPane.add(registrationFormLabel, gbc_lblNewLabel);

		idLabel = new JLabel("id");
		idLabel.setName("idLabel");
		GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
		gbc_lblNewLabel_1.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_1.insets = new Insets(5, 10, 5, 10);
		gbc_lblNewLabel_1.gridx = 0;
		gbc_lblNewLabel_1.gridy = 1;
		contentPane.add(idLabel, gbc_lblNewLabel_1);

		idRegistrationInputText = new JTextField();
		idRegistrationInputText.setName("idRegistrationInputText");
		GridBagConstraints gbc_textField = new GridBagConstraints();
		gbc_textField.insets = new Insets(5, 0, 5, 10);
		gbc_textField.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField.gridx = 1;
		gbc_textField.gridy = 1;
		contentPane.add(idRegistrationInputText, gbc_textField);
		idRegistrationInputText.setColumns(10);

		registrationUsernaleLabel = new JLabel("Username");
		registrationUsernaleLabel.setName("usernameLabel");
		GridBagConstraints gbc_lblNewLabel_2 = new GridBagConstraints();
		gbc_lblNewLabel_2.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_2.insets = new Insets(5, 10, 5, 10);
		gbc_lblNewLabel_2.gridx = 0;
		gbc_lblNewLabel_2.gridy = 2;
		contentPane.add(registrationUsernaleLabel, gbc_lblNewLabel_2);

		usernameRegistrationInputText = new JTextField();
		usernameRegistrationInputText.setName("usernameRegistrationInputText");
		GridBagConstraints gbc_textField_1 = new GridBagConstraints();
		gbc_textField_1.insets = new Insets(5, 0, 5, 10);
		gbc_textField_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_1.gridx = 1;
		gbc_textField_1.gridy = 2;
		contentPane.add(usernameRegistrationInputText, gbc_textField_1);
		usernameRegistrationInputText.setColumns(10);

		registrationPasswordLabel = new JLabel("Password");
		registrationPasswordLabel.setName("passwordLabel");
		GridBagConstraints gbc_lblNewLabel_3 = new GridBagConstraints();
		gbc_lblNewLabel_3.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_3.insets = new Insets(5, 10, 5, 10);
		gbc_lblNewLabel_3.gridx = 0;
		gbc_lblNewLabel_3.gridy = 3;
		contentPane.add(registrationPasswordLabel, gbc_lblNewLabel_3);

		registrationPasswordField = new JPasswordField();
		registrationPasswordField.setName("passwordRegistrationInputText");
		GridBagConstraints gbc_passwordField = new GridBagConstraints();
		gbc_passwordField.insets = new Insets(5, 0, 5, 10);
		gbc_passwordField.fill = GridBagConstraints.HORIZONTAL;
		gbc_passwordField.gridx = 1;
		gbc_passwordField.gridy = 3;
		contentPane.add(registrationPasswordField, gbc_passwordField);

		registerButton = new JButton("Register");
		registerButton.setName("registerButton");
		GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
		gbc_btnNewButton.gridwidth = 2;
		gbc_btnNewButton.insets = new Insets(10, 0, 20, 0);
		gbc_btnNewButton.gridx = 0;
		gbc_btnNewButton.gridy = 4;
		contentPane.add(registerButton, gbc_btnNewButton);

		loginFormLabel = new JLabel("LoginForm");
		loginFormLabel.setName("loginFormLabel");
		GridBagConstraints gbc_lblNewLabel_4 = new GridBagConstraints();
		gbc_lblNewLabel_4.gridwidth = 2;
		gbc_lblNewLabel_4.insets = new Insets(10, 0, 10, 0);
		gbc_lblNewLabel_4.gridx = 0;
		gbc_lblNewLabel_4.gridy = 5;
		contentPane.add(loginFormLabel, gbc_lblNewLabel_4);

		loginUsernameLabel = new JLabel("Username");
		loginUsernameLabel.setName("uernameLoginLabel");
		GridBagConstraints gbc_lblNewLabel_5 = new GridBagConstraints();
		gbc_lblNewLabel_5.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_5.insets = new Insets(5, 10, 5, 10);
		gbc_lblNewLabel_5.gridx = 0;
		gbc_lblNewLabel_5.gridy = 6;
		contentPane.add(loginUsernameLabel, gbc_lblNewLabel_5);

		usernameLoginInputText = new JTextField();
		usernameLoginInputText.setName("usernameLoginInputText");
		GridBagConstraints gbc_textField_2 = new GridBagConstraints();
		gbc_textField_2.insets = new Insets(5, 0, 5, 10);
		gbc_textField_2.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_2.gridx = 1;
		gbc_textField_2.gridy = 6;
		contentPane.add(usernameLoginInputText, gbc_textField_2);
		usernameLoginInputText.setColumns(10);

		loginPasswordLabel = new JLabel("Password");
		loginPasswordLabel.setName("passwordLabel");
		GridBagConstraints gbc_lblNewLabel_6 = new GridBagConstraints();
		gbc_lblNewLabel_6.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_6.insets = new Insets(5, 10, 5, 10);
		gbc_lblNewLabel_6.gridx = 0;
		gbc_lblNewLabel_6.gridy = 7;
		contentPane.add(loginPasswordLabel, gbc_lblNewLabel_6);

		passwordLoginInputText = new JPasswordField();
		passwordLoginInputText.setName("passwordLoginInputText");
		GridBagConstraints gbc_passwordField_1 = new GridBagConstraints();
		gbc_passwordField_1.insets = new Insets(5, 0, 5, 10);
		gbc_passwordField_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_passwordField_1.gridx = 1;
		gbc_passwordField_1.gridy = 7;
		contentPane.add(passwordLoginInputText, gbc_passwordField_1);

		loginButton = new JButton("Login");
		loginButton.setName("loginButton");
		GridBagConstraints gbc_btnNewButton_1 = new GridBagConstraints();
		gbc_btnNewButton_1.gridwidth = 2;
		gbc_btnNewButton_1.insets = new Insets(10, 0, 10, 0);
		gbc_btnNewButton_1.gridx = 0;
		gbc_btnNewButton_1.gridy = 8;
		contentPane.add(loginButton, gbc_btnNewButton_1);

		errorMessageLabel = new JLabel("");
		errorMessageLabel.setName("errorMessageLabel");
		errorMessageLabel.setVisible(false);
		errorMessageLabel.setForeground(new Color(192, 28, 40));
		GridBagConstraints gbc_lblNewLabel_7 = new GridBagConstraints();
		gbc_lblNewLabel_7.gridwidth = 2;
		gbc_lblNewLabel_7.insets = new Insets(10, 0, 0, 0);
		gbc_lblNewLabel_7.gridx = 0;
		gbc_lblNewLabel_7.gridy = 9;
		contentPane.add(errorMessageLabel, gbc_lblNewLabel_7);
	}

	@Override
	public void showError(String message, User user) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void showError(String message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void switchPanel() {
		// TODO Auto-generated method stub
		
	}

}
