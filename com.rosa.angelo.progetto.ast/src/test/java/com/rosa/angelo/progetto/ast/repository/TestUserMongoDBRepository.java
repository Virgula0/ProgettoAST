package com.rosa.angelo.progetto.ast.repository;

import static org.assertj.core.api.Assertions.*;

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
import com.rosa.angelo.progetto.ast.model.User;

import static com.mongodb.client.model.Filters.eq;

public class TestUserMongoDBRepository {

	@SuppressWarnings({ "rawtypes", "resource" })
	@ClassRule
	public static final GenericContainer mongo = new GenericContainer(
			UserMongoRepository.IMAGE + ":" + UserMongoRepository.VERSION).withExposedPorts(UserMongoRepository.PORT);

	private static MongoClient client;
	private static MongoDatabase database;

	private UserMongoRepository userRepository;
	private MongoCollection<Document> userCollection;

	private static final String TEST_USERNAME = "TEST_USERNAME";
	private static final String TEST_PASSWORD = "TEST_PASSWORD";

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

	@Test
	public void testSaveANewUserSuccesfully() {
		User user = new User(TEST_USERNAME, TEST_PASSWORD, 1);
		userRepository.save(user);

		// getUserCollection is a package private method not a repository method
		assertThat(userRepository.getUserCollection().find(eq("id", 1))
				.map(doc -> new User(doc.getString("username"), doc.getString("password"), doc.getInteger("id")))
				.into(new ArrayList<>()))
				.containsExactly(new User(TEST_USERNAME, TEST_PASSWORD, 1));
	}
	
	@Test
	public void testSaveANewUserIsNull() {
		User user = null;
		userRepository.save(user);

		assertThat(userRepository.getUserCollection()
				.find()
				.map(doc -> new User(doc.getString("username"), doc.getString("password"), doc.getInteger("id")))
				.into(new ArrayList<>()))
				.isEmpty();
	}
	
	@Test 
	public void testSaveFieldsOfUserAreNullExceptForID() {
		User userWithUsernameNull = new User(null, TEST_PASSWORD, 1);
		User userWithPasswordNull = new User(TEST_USERNAME, null, 1);
		User bothNull = new User(null, null, 1);
		
		userRepository.save(userWithUsernameNull);
		
		assertThat(userRepository.getUserCollection()
				.find()
				.map(doc -> new User(doc.getString("username"), doc.getString("password"), doc.getInteger("id")))
				.into(new ArrayList<>()))
				.isEmpty();

		userRepository.save(userWithPasswordNull);
		
		assertThat(userRepository.getUserCollection()
				.find()
				.map(doc -> new User(doc.getString("username"), doc.getString("password"), doc.getInteger("id")))
				.into(new ArrayList<>()))
				.isEmpty();

		userRepository.save(bothNull);
		
		assertThat(userRepository.getUserCollection()
				.find()
				.map(doc -> new User(doc.getString("username"), doc.getString("password"), doc.getInteger("id")))
				.into(new ArrayList<>()))
				.isEmpty();
	}
	
	@Test 
	public void testRegistrationToken() {
		assertThat(userRepository.getRegistrationToken()).isEqualTo("validToken");
	}
}
