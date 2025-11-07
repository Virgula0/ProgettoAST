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
		// make sure we always start with a clean database
		database.drop();
		userRepository = new UserMongoRepository(client);
		userCollection = database.getCollection(UserMongoRepository.USER_COLLECTION_NAME);
	}

	@AfterClass
	public static void afterClass() {
		client.close();
	}

	private void addTestUserToDatabase(int id, String username, String password) {
		userCollection
				.insertOne(new Document().append("id", id).append("username", username).append("password", password));
	}

	@Test
	public void testSaveANewUserSuccesfully() {
		User user = new User(TEST_USERNAME, TEST_PASSWORD, 1);
		userRepository.save(user);

		assertThat(userCollection.find(eq("id", 1))
				.map(doc -> new User(doc.getString("username"), doc.getString("password"), doc.getInteger("id")))
				.into(new ArrayList<>())).containsExactly(new User(TEST_USERNAME, TEST_PASSWORD, 1));
	}

	@Test
	public void testSaveANewUserIsNull() {
		User user = null;
		userRepository.save(user);

		assertThat(userCollection.find()
				.map(doc -> new User(doc.getString("username"), doc.getString("password"), doc.getInteger("id")))
				.into(new ArrayList<>())).isEmpty();
	}

	@Test
	public void testSaveFieldsOfUserAreNullExceptForID() {
		User userWithUsernameNull = new User(null, TEST_PASSWORD, 1);
		User userWithPasswordNull = new User(TEST_USERNAME, null, 1);
		User bothNull = new User(null, null, 1);

		userRepository.save(userWithUsernameNull);

		assertThat(userCollection.find()
				.map(doc -> new User(doc.getString("username"), doc.getString("password"), doc.getInteger("id")))
				.into(new ArrayList<>())).isEmpty();

		userRepository.save(userWithPasswordNull);

		assertThat(userCollection.find()
				.map(doc -> new User(doc.getString("username"), doc.getString("password"), doc.getInteger("id")))
				.into(new ArrayList<>())).isEmpty();

		userRepository.save(bothNull);

		assertThat(userCollection.find()
				.map(doc -> new User(doc.getString("username"), doc.getString("password"), doc.getInteger("id")))
				.into(new ArrayList<>())).isEmpty();
	}

	@Test
	public void testRegistrationToken() {
		assertThat(userRepository.getRegistrationToken()).isEqualTo("validToken");
	}

	@Test
	public void testFindUserByIdWhenUserExists() {
		addTestUserToDatabase(1, TEST_USERNAME, TEST_PASSWORD);
		addTestUserToDatabase(2, TEST_USERNAME, TEST_PASSWORD);

		User user = userRepository.findUserById(2);

		assertThat(user).isEqualTo(new User(TEST_USERNAME, TEST_PASSWORD, 2));
	}

	@Test
	public void testFindUserByIdWhenUserDoesNotExistsAndCollectionIsEmpty() {
		User user = userRepository.findUserById(1);

		assertThat(user).isEqualTo(null);
	}

	@Test
	public void testfindUserByUsernameAndPasswordOnExistingUser() {
		String username = "user1";
		String password = "password1";
		addTestUserToDatabase(1, username, password);
		String username2 = "user2";
		String password2 = "password2";
		addTestUserToDatabase(2, username2, password2);

		User user = userRepository.findUserByUsernameAndPassword(username2, password2);

		assertThat(user).isEqualTo(new User(username2, password2, 2));
		assertThat(user.getPassword()).isEqualTo(password2);
		assertThat(userRepository.findUserByUsernameAndPassword("", "")).isNull();
	}

	@Test
	public void testfindUserByUsernameAndPasswordWhenOrUsernameOrPasswordIsWrong() {
		String username = "user1";
		String password = "password1";
		addTestUserToDatabase(1, username, password);
		String username2 = "user2";
		String password2 = "password2";
		addTestUserToDatabase(2, username2, password2);

		assertThat(userRepository.findUserByUsernameAndPassword("wrongusername", password2)).isNull();
		assertThat(userRepository.findUserByUsernameAndPassword(username2, "wrongPassword")).isNull();
		assertThat(userRepository.findUserByUsernameAndPassword("wrongUsername", "wrongPassword")).isNull();

	}

	@Test
	public void testfindUserByUsernameAndPasswordWithFuzzedNulls() {
		String username = "user1";
		String password = "password1";
		addTestUserToDatabase(1, username, password);
		String username2 = "user2";
		String password2 = "password2";
		addTestUserToDatabase(2, username2, password2);

		assertThat(userRepository.findUserByUsernameAndPassword(null, password2)).isNull();
		assertThat(userRepository.findUserByUsernameAndPassword(username2, null)).isNull();
		assertThat(userRepository.findUserByUsernameAndPassword(null, null)).isNull();
	}

	@Test
	public void testFindUserByUsernameOnExistingUser() {
		String username = "user1";
		String password = "password1";
		addTestUserToDatabase(1, username, password);
		String username2 = "user2";
		String password2 = "password2";
		addTestUserToDatabase(2, username2, password2);

		User user = userRepository.findUserByUsername(username2);

		assertThat(user).isEqualTo(new User(username2, password2, 2));
		assertThat(user.getPassword()).isEqualTo(password2);
		assertThat(userRepository.findUserByUsername("")).isNull();
	}

	@Test
	public void testFindUserByUsernameWithNull() {
		String username = "user1";
		String password = "password1";
		addTestUserToDatabase(1, username, password);

		assertThat(userRepository.findUserByUsername(null)).isNull();
	}
}
