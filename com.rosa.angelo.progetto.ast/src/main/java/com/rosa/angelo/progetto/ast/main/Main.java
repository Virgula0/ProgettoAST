package com.rosa.angelo.progetto.ast.main;

import java.awt.EventQueue;
import java.util.concurrent.Callable;
import java.util.logging.Level;
// log4j
import java.util.logging.Logger;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.rosa.angelo.progetto.ast.repository.ProductMongoRepository;
import com.rosa.angelo.progetto.ast.repository.UserMariaDBRepository;
import com.rosa.angelo.progetto.ast.repository.UserMongoRepository;
import com.rosa.angelo.progetto.ast.view.LoginAndRegistrationSwingView;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(mixinStandardHelpOptions = true)
public class Main implements Callable<Void> {
	@Option(names = { "--mongo-host" }, description = "MongoDB host address")
	private String mongoHost = "localhost";
	@Option(names = { "--mongo-port" }, description = "MongoDB host port")
	private int mongoPort = 27017;
	@Option(names = { "--db-name" }, description = "Database name")
	private String databaseName = "ast";
	@Option(names = { "--db-user-collection" }, description = "User Collection Name")
	private String userCollectionName = UserMongoRepository.USER_COLLECTION_NAME;
	@Option(names = { "--db-product-collection" }, description = "Product Collection Name")
	private String productCollectionName = ProductMongoRepository.PRODUCT_COLLECTION_NAME;

	@Option(names = { "--mariadb-host" }, description = "MariaDB host address")
	private String mariaDBHost = "localhost";
	@Option(names = { "--mariadb-port" }, description = "MariaDB host port")
	private int mariaDBPort = 3306;
	@Option(names = { "--mariadb-name" }, description = "Database name")
	private String mariaDBdatabaseName = UserMariaDBRepository.AST_DB_NAME;
	@Option(names = { "--mariadb-username" }, description = "Db Username")
	private String dbUsername = UserMariaDBRepository.DB_USERNAME;
	@Option(names = { "--mariadb-password" }, description = "Db Password")
	private String dbPassword = UserMariaDBRepository.DB_PASSWORD;

	@Option(names = "--db", required = false, description = "Choose database type")
	private String databaseTypeName = "mongodb";

	private AbstractModule databaseModule;

	public static void main(String[] args) {
		new CommandLine(new Main()).execute(args);
	}

	private AbstractModule moduleChoiser() {
		databaseModule = switch (databaseTypeName.toLowerCase()) {
		case "mongodb" -> new MongoDefaultModule().mongoHost(mongoHost).mongoPort(mongoPort).databaseName(databaseName)
				.userCollectionName(userCollectionName).productCollectionName(productCollectionName);
		case "mariadb" -> new MariaDBDefaultModule().dbHost(mariaDBHost).dbPort(mariaDBPort)
				.databaseName(mariaDBdatabaseName).dbUsername(dbUsername).dbPassword(dbPassword);
		default -> throw new IllegalArgumentException("Unknown DB type: " + databaseTypeName);
		};

		return databaseModule;
	}

	@Override
	public Void call() throws Exception {
		databaseModule = moduleChoiser();

		EventQueue.invokeLater(() -> {
			try {
				// null parameter passed indicates still a null session since not logged in
				Guice.createInjector(databaseModule).getInstance(LoginAndRegistrationSwingView.class).start(null);
			} catch (Exception e) {
				Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Exception", e);
			}
		});
		return null;
	}
}
