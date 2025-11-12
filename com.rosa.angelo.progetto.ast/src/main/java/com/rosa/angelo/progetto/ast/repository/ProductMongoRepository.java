package com.rosa.angelo.progetto.ast.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.rosa.angelo.progetto.ast.model.Product;
import com.rosa.angelo.progetto.ast.model.User;

public class ProductMongoRepository implements ProductRepository {
	public static final String IMAGE = System.getProperty("mongo.image", "mongo");
	public static final String VERSION = System.getProperty("mongo.version", "4.4.3");
	public static final int PORT = Integer.parseInt(System.getProperty("mongo.port", "27017"));

	public static final String AST_DB_NAME = "ast";
	public static final String PRODUCT_COLLECTION_NAME = "product";
	private MongoCollection<Document> productCollection;

	public ProductMongoRepository(MongoClient client, String databaseName, String collectionName) {
		productCollection = client.getDatabase(databaseName).getCollection(collectionName);
	}

	public ProductMongoRepository(MongoClient client) {
		this(client, AST_DB_NAME, PRODUCT_COLLECTION_NAME);
	}

	public static final String SENDER_ID_KEY = "senderid";
	public static final String SENDER_USERNAME_KEY = "senderusername";

	public static final String RECEIVER_ID_KEY = "id";
	public static final String RECEIVER_NAME_KEY = "receivername";
	public static final String RECEIVER_SURNAME_KEY = "receiverusername";
	public static final String RECEIVER_ADDRESS_KEY = "receiveraddress";
	public static final String RECEIVER_PACKAGETYPE_KEY = "packagetype";

	@Override
	public List<Product> findAllProductsSentByUser(User user) {
		List<Product> products = new ArrayList<>();
		for (Document doc : productCollection.find()) {
			int userID = doc.getInteger(SENDER_ID_KEY);
			if (Objects.equals(userID, user.getId())) {
				products.add(new Product(user, doc.getString(RECEIVER_NAME_KEY), doc.getString(RECEIVER_SURNAME_KEY),
						doc.getString(RECEIVER_ADDRESS_KEY), doc.getString(RECEIVER_PACKAGETYPE_KEY),
						doc.getInteger(RECEIVER_ID_KEY)));
			}
		}
		return products;
	}

	@Override
	public void save(Product product) {
		// TODO Auto-generated method stub

	}

	@Override
	public void delete(Product product) {
		// TODO Auto-generated method stub

	}

	@Override
	public Product findProductById(int id) {
		// TODO Auto-generated method stub
		return null;
	}
}
