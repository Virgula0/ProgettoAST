package com.rosa.angelo.progetto.ast.repository;

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

public class TestUserMongoDBRepository {

	@SuppressWarnings({ "rawtypes", "resource" })
	@ClassRule
	public static final GenericContainer mongo = new GenericContainer(
			UserMongoRepository.IMAGE + ":" + UserMongoRepository.VERSION).withExposedPorts(UserMongoRepository.PORT);

	private static MongoClient client;
	private static MongoDatabase database;

	private UserMongoRepository userRepository;
	private MongoCollection<Document> userCollection;

	@BeforeClass
	public static void beforeClass() {
		client = new MongoClient(new ServerAddress(mongo.getHost(), mongo.getMappedPort(UserMongoRepository.PORT)));
		database = client.getDatabase(UserMongoRepository.AST_DB_NAME);
	}

	@Before
	public void setup() {
		userRepository = new UserMongoRepository(client);
		// make sure we always start with a clean database
		database.drop();
		userCollection = database.getCollection(UserMongoRepository.USER_COLLECTION_NAME);
	}

	@AfterClass
	public static void afterClass() {
		client.close();
	}

	private void addTestStudentToDatabase(int id, String username, String password) {
		userCollection.insertOne(new Document()
				.append(UserMongoRepository.ID_KEY, id)
				.append(UserMongoRepository.USERNAME_KEY, username)
				.append(UserMongoRepository.PASSWORD_KEY, password));
	}

	@Test
	public void test() {

	}
}
