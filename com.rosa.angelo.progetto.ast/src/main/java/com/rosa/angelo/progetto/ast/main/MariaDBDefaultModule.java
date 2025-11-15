package com.rosa.angelo.progetto.ast.main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.rosa.angelo.progetto.ast.controller.ControllerFactory;
import com.rosa.angelo.progetto.ast.controller.ProductController;
import com.rosa.angelo.progetto.ast.controller.UserController;
import com.rosa.angelo.progetto.ast.main.GuiceAnnotations.RepoType;
import com.rosa.angelo.progetto.ast.repository.ProductMariaDBRepository;
import com.rosa.angelo.progetto.ast.repository.ProductRepository;
import com.rosa.angelo.progetto.ast.repository.UserMariaDBRepository;
import com.rosa.angelo.progetto.ast.repository.UserRepository;
import com.rosa.angelo.progetto.ast.view.LoginAndRegistrationSwingView;
import com.rosa.angelo.progetto.ast.view.ProductSwingView;

public class MariaDBDefaultModule extends AbstractModule {

	private String dbHost;
	private int dbPort;
	private String databaseName;
	private String dbUsername;
	private String dbPassword;

	public MariaDBDefaultModule dbHost(String mariaDBHost) {
		this.dbHost = mariaDBHost;
		return this;
	}

	public MariaDBDefaultModule dbPort(int mariaDBPort) {
		this.dbPort = mariaDBPort;
		return this;
	}

	public MariaDBDefaultModule databaseName(String mariaDBdatabaseName) {
		this.databaseName = mariaDBdatabaseName;
		return this;
	}

	public MariaDBDefaultModule dbUsername(String dbUsername) {
		this.dbUsername = dbUsername;
		return this;
	}

	public MariaDBDefaultModule dbPassword(String dbPassword) {
		this.dbPassword = dbPassword;
		return this;
	}

	@Override
	protected void configure() {
		// inject controllers using RepoType annotation
		bind(UserRepository.class).annotatedWith(RepoType.class).to(UserMariaDBRepository.class);
		bind(ProductRepository.class).annotatedWith(RepoType.class).to(ProductMariaDBRepository.class);

		bind(UserRepository.class).to(UserMariaDBRepository.class);
		bind(ProductRepository.class).to(ProductMariaDBRepository.class);

		install(new FactoryModuleBuilder().implement(UserController.class, UserController.class)
				.implement(ProductController.class, ProductController.class).build(ControllerFactory.class));
	}

	@Provides
	public Connection connection() throws SQLException {
		String jdbcUrl = String.format("jdbc:mariadb://%s:%d/%s", dbHost, dbPort, databaseName);
		return DriverManager.getConnection(jdbcUrl, dbUsername, dbPassword);
	}

	@Provides
	LoginAndRegistrationSwingView loginView(ControllerFactory userControllerFactory,
			ControllerFactory productControllerFactory) {

		LoginAndRegistrationSwingView loginView = new LoginAndRegistrationSwingView();
		ProductSwingView productView = new ProductSwingView();

		loginView.setNextPanel(productView);
		loginView.setUserController(userControllerFactory.create(loginView)); // login
		productView.setProductController(productControllerFactory.create(productView));// product

		return loginView;
	}

}
