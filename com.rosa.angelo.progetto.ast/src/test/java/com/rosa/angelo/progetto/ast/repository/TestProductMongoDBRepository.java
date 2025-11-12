package com.rosa.angelo.progetto.ast.repository;

import static com.mongodb.client.model.Filters.eq;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;

import org.bson.Document;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.testcontainers.containers.GenericContainer;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.rosa.angelo.progetto.ast.model.Product;
import com.rosa.angelo.progetto.ast.model.User;

public class TestProductMongoDBRepository {

	@SuppressWarnings({ "rawtypes", "resource" })
	@ClassRule
	public static final GenericContainer mongo = new GenericContainer(
			ProductMongoRepository.IMAGE + ":" + ProductMongoRepository.VERSION)
			.withExposedPorts(ProductMongoRepository.PORT);

	private static MongoClient client;
	private static MongoDatabase database;

	private ProductMongoRepository productRepository;
	private MongoCollection<Document> productCollection;

	private User loggedInUser;

	@BeforeClass
	public static void beforeClass() {
		client = new MongoClient(new ServerAddress(mongo.getHost(), mongo.getMappedPort(ProductMongoRepository.PORT)));
		database = client.getDatabase(ProductMongoRepository.AST_DB_NAME);
	}

	@Before
	public void setup() {
		// make sure we always start with a clean database
		database.drop();
		loggedInUser = new User("test", "password1234", 1);
		productRepository = new ProductMongoRepository(client);
		productCollection = database.getCollection(ProductMongoRepository.PRODUCT_COLLECTION_NAME);
	}

	private void addTestProductToDatabase(Product p) {
		productCollection.insertOne(new Document().append(ProductMongoRepository.SENDER_ID_KEY, p.getSender().getId())
				.append(ProductMongoRepository.SENDER_USERNAME_KEY, p.getSender().getUsername())
				.append(ProductMongoRepository.RECEIVER_ID_KEY, p.getId())
				.append(ProductMongoRepository.RECEIVER_NAME_KEY, p.getReceiverName())
				.append(ProductMongoRepository.RECEIVER_SURNAME_KEY, p.getReceiverSurname())
				.append(ProductMongoRepository.RECEIVER_ADDRESS_KEY, p.getReiceiverAddress())
				.append(ProductMongoRepository.RECEIVER_PACKAGETYPE_KEY, p.getPackageType()));
	}

	private Product documentToProduct(User user, Document doc) {
		return new Product(user, doc.getString(ProductMongoRepository.RECEIVER_NAME_KEY),
				doc.getString(ProductMongoRepository.RECEIVER_SURNAME_KEY),
				doc.getString(ProductMongoRepository.RECEIVER_ADDRESS_KEY),
				doc.getString(ProductMongoRepository.RECEIVER_PACKAGETYPE_KEY),
				doc.getInteger(ProductMongoRepository.RECEIVER_ID_KEY));
	}

	@AfterClass
	public static void afterClass() {
		client.close();
	}

	@Test
	public void testFindAllProductsSentByUser() {
		Product p1 = new Product(loggedInUser, "test", "test", "testAddress", "testPackage", 1);
		Product p2 = new Product(loggedInUser, "test2", "test2", "testAddress2", "testPackage2", 2);
		Product p3 = new Product(new User("anotherUser", "password1234", 2), "test2", "test2", "testAddress2", "testPackage2", 2);

		addTestProductToDatabase(p1);
		addTestProductToDatabase(p2);
		addTestProductToDatabase(p3);

		assertThat(productRepository.findAllProductsSentByUser(loggedInUser)).containsExactly(p1, p2);
	}

	@Test
	public void testFindAllProductsSentByUserWhenUserIsNull() {
		Product p1 = new Product(loggedInUser, "test", "test", "testAddress", "testPackage", 1);
		Product p2 = new Product(loggedInUser, "test2", "test2", "testAddress2", "testPackage2", 2);

		addTestProductToDatabase(p1);
		addTestProductToDatabase(p2);
		assertThat(productRepository.findAllProductsSentByUser(null)).isEmpty();
	}

	@Test
	public void testSaveNewProductInDBIsSuccesfull() {
		Product p1 = new Product(loggedInUser, "test", "test", "testAddress", "testPackage", 1);
		productRepository.save(p1);

		assertThat(productCollection.find(eq(ProductMongoRepository.RECEIVER_ID_KEY, p1.getId()))
				.map(doc -> documentToProduct(loggedInUser, doc)).into(new ArrayList<>())).containsExactly(p1);
	}
	
	@Test
	public void testSaveNewProductInDBIsNotSuccesfullWhenEmbeddedUserIsNull() {
		Product p1 = new Product(null, "test", "test", "testAddress", "testPackage", 1);
		productRepository.save(p1);

		assertThat(productCollection.find(eq(ProductMongoRepository.RECEIVER_ID_KEY, p1.getId()))
				.map(doc -> documentToProduct(loggedInUser, doc)).into(new ArrayList<>())).isEmpty();
	}
}
