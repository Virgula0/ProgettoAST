package com.rosa.angelo.progetto.ast.repository;

import java.util.Iterator;
import java.util.Objects;
import java.util.stream.StreamSupport;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
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

	public static final String REGISTRATION_TOKEN = "validToken";

	public UserMongoRepository(MongoClient client, String databaseName, String collectionName) {
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
		return StreamSupport.stream(userCollection.find().spliterator(), false).map(d -> documentToUser(d))
				.filter(x -> Objects.equals(x.getId(), id)).findFirst().orElse(null);
	}

	@Override
	public User findUserByUsernameAndPassword(String username, String password) {
		for (Document doc : userCollection.find()) {
			if (Objects.equals(username, doc.getString(USERNAME_KEY))
					&& Objects.equals(password, doc.getString(PASSWORD_KEY))) {
				return documentToUser(doc);
			}
		}
		return null;
	}

	@Override
	public User findUserByUsername(String username) {
		// TODO Auto-generated method stub
		return null;
	}

	MongoCollection<Document> getUserCollection() {
		return userCollection;
	}
}
