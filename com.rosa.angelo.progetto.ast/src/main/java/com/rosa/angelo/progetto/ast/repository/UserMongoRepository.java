package com.rosa.angelo.progetto.ast.repository;

import java.util.Objects;
import java.util.stream.StreamSupport;

import org.bson.Document;

import com.google.inject.Inject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.rosa.angelo.progetto.ast.main.GuiceAnnotations.UserCollectionName;
import com.rosa.angelo.progetto.ast.main.GuiceAnnotations.UserDatabaseName;
import com.rosa.angelo.progetto.ast.model.User;

public class UserMongoRepository implements UserRepository {

	public static final String IMAGE = System.getProperty("mongo.image", "mongo");
	public static final String VERSION = System.getProperty("mongo.version", "4.4.3");
	public static final int PORT = Integer.parseInt(System.getProperty("mongo.port", "27017"));

	public static final String AST_DB_NAME = "ast";
	public static final String USER_COLLECTION_NAME = "users";
	private MongoCollection<Document> userCollection;

	public static final String ID_KEY = "id";
	public static final String USERNAME_KEY = "username";
	public static final String PASSWORD_KEY = "password";

	@Inject
	public UserMongoRepository(MongoClient client, @UserDatabaseName String databaseName,
			@UserCollectionName String collectionName) {
		userCollection = client.getDatabase(databaseName).getCollection(collectionName);
	}

	public UserMongoRepository(MongoClient client) {
		this(client, AST_DB_NAME, USER_COLLECTION_NAME);
	}

	@Override
	public void save(User user) {
		if (user == null || user.getUsername() == null || user.getPassword() == null) {
			return;
		}
		userCollection.insertOne(new Document().append(ID_KEY, user.getId()).append(USERNAME_KEY, user.getUsername())
				.append(PASSWORD_KEY, user.getPassword()));
	}

	@Override
	public String getRegistrationToken() {
		/*
		 * This is just a simplification for the purpose of the project Registration
		 * tokens should be single disposable after each registration
		 */
		return REGISTRATION_TOKEN;
	}

	private User documentToUser(Document doc) {
		return new User(doc.getString(USERNAME_KEY), doc.getString(PASSWORD_KEY), doc.getInteger(ID_KEY));
	}

	@Override
	public User findUserById(int id) {
		return StreamSupport.stream(userCollection.find().spliterator(), false).map(this::documentToUser)
				.filter(x -> Objects.equals(x.getId(), id)).findFirst().orElse(null);
	}

	@Override
	public User findUserByUsernameAndPassword(String username, String password) {
		return StreamSupport.stream(userCollection.find().spliterator(), false).map(this::documentToUser)
				.filter(x -> Objects.equals(x.getUsername(), username) && Objects.equals(password, x.getPassword()))
				.findFirst().orElse(null);
	}

	@Override
	public User findUserByUsername(String username) {
		return StreamSupport.stream(userCollection.find().spliterator(), false).map(this::documentToUser)
				.filter(x -> Objects.equals(x.getUsername(), username)).findFirst().orElse(null);
	}
}
