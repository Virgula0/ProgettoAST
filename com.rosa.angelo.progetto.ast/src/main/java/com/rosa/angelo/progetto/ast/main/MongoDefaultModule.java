package com.rosa.angelo.progetto.ast.main;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.google.inject.AbstractModule;
import com.google.inject.BindingAnnotation;
import com.google.inject.Provides;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.mongodb.MongoClient;
import com.rosa.angelo.progetto.ast.controller.ControllerFactory;
import com.rosa.angelo.progetto.ast.controller.ProductController;
import com.rosa.angelo.progetto.ast.controller.UserController;
import com.rosa.angelo.progetto.ast.repository.ProductMongoRepository;
import com.rosa.angelo.progetto.ast.repository.ProductRepository;
import com.rosa.angelo.progetto.ast.repository.UserMongoRepository;
import com.rosa.angelo.progetto.ast.repository.UserRepository;
import com.rosa.angelo.progetto.ast.view.LoginAndRegistrationSwingView;
import com.rosa.angelo.progetto.ast.view.ProductSwingView;

public class MongoDefaultModule extends AbstractModule {

	@BindingAnnotation
	@Target({ FIELD, PARAMETER, METHOD })
	@Retention(RUNTIME)
	public @interface MongoHost {
	}

	@BindingAnnotation
	@Target({ FIELD, PARAMETER, METHOD })
	@Retention(RUNTIME)
	public @interface MongoPort {
	}

	@BindingAnnotation
	@Target({ FIELD, PARAMETER, METHOD })
	@Retention(RUNTIME)
	public static @interface RepoType {
	}

	private String mongoHost;
	private int mongoPort;
	private String databaseName;
	private String userCollectionName;
	private String productCollectionName;

	public MongoDefaultModule defaultParams() {
		this.mongoHost = "localhost";
		this.mongoPort = UserMongoRepository.PORT;
		this.databaseName = UserMongoRepository.AST_DB_NAME;
		this.userCollectionName = UserMongoRepository.USER_COLLECTION_NAME;
		this.productCollectionName = ProductMongoRepository.PRODUCT_COLLECTION_NAME;
		return this;
	}

	public MongoDefaultModule mongoHost(String mongoHost) {
		this.mongoHost = mongoHost;
		return this;
	}

	public MongoDefaultModule mongoPort(int mongoPort) {
		this.mongoPort = mongoPort;
		return this;
	}

	public MongoDefaultModule databaseName(String databaseName) {
		this.databaseName = databaseName;
		return this;
	}

	public MongoDefaultModule userCollectionName(String collectionName) {
		this.userCollectionName = collectionName;
		return this;
	}

	public MongoDefaultModule productCollectionName(String collectionName) {
		this.productCollectionName = collectionName;
		return this;
	}

	@Override
	protected void configure() {
		// bind repositories to controllers
		bind(UserRepository.class).annotatedWith(RepoType.class).to(UserMongoRepository.class);
		bind(ProductRepository.class).annotatedWith(RepoType.class).to(ProductMongoRepository.class);

		// inject user repository first
		bind(String.class).annotatedWith(MongoHost.class).toInstance(mongoHost);
		bind(Integer.class).annotatedWith(MongoPort.class).toInstance(mongoPort);
		bind(String.class).annotatedWith(UserMongoRepository.UserDatabaseName.class).toInstance(databaseName);
		bind(String.class).annotatedWith(UserMongoRepository.UserCollectionName.class).toInstance(userCollectionName);

		// inject product repository then
		bind(String.class).annotatedWith(MongoHost.class).toInstance(mongoHost);
		bind(Integer.class).annotatedWith(MongoPort.class).toInstance(mongoPort);
		bind(String.class).annotatedWith(ProductMongoRepository.ProductDatabaseName.class).toInstance(databaseName);
		bind(String.class).annotatedWith(ProductMongoRepository.ProductCollectionName.class)
				.toInstance(productCollectionName);

		bind(UserRepository.class).to(UserMongoRepository.class); // Whenever something requires UserRepository, provide
																	// a UserMongoRepository.

		bind(ProductRepository.class).to(ProductMongoRepository.class); // Whenever something requires
																		// ProductRepository, provide a
																		// ProductMongoRepository.

		// Whenever a UserController is needed, use UserControllerFactory to build it
		install(new FactoryModuleBuilder().implement(UserController.class, UserController.class)
				.implement(ProductController.class, ProductController.class).build(ControllerFactory.class));
	}

	@Provides
	public MongoClient mongoClient(@MongoHost String host, @MongoPort int port) {
		return new MongoClient(host, port);
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
