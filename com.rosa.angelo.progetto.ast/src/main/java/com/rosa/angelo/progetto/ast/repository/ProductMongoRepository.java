package com.rosa.angelo.progetto.ast.repository;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.StreamSupport;

import org.bson.Document;

import com.google.inject.BindingAnnotation;
import com.google.inject.Inject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.rosa.angelo.progetto.ast.model.Product;
import com.rosa.angelo.progetto.ast.model.User;

public class ProductMongoRepository implements ProductRepository {
	@BindingAnnotation
	@Target({ FIELD, PARAMETER, METHOD })
	@Retention(RUNTIME)
	public static @interface ProductDatabaseName {
	}

	@BindingAnnotation
	@Target({ FIELD, PARAMETER, METHOD })
	@Retention(RUNTIME)
	public static @interface ProductCollectionName {
	}

	public static final String IMAGE = System.getProperty("mongo.image", "mongo");
	public static final String VERSION = System.getProperty("mongo.version", "4.4.3");
	public static final int PORT = Integer.parseInt(System.getProperty("mongo.port", "27017"));

	public static final String AST_DB_NAME = "ast";
	public static final String PRODUCT_COLLECTION_NAME = "product";
	private MongoCollection<Document> productCollection;

	@Inject
	public ProductMongoRepository(MongoClient client, @ProductDatabaseName String databaseName,
			@ProductCollectionName String collectionName) {
		productCollection = client.getDatabase(databaseName).getCollection(collectionName);
	}

	public ProductMongoRepository(MongoClient client) {
		this(client, AST_DB_NAME, PRODUCT_COLLECTION_NAME);
	}

	public static final String SENDER_ID_KEY = "senderid";
	public static final String SENDER_USERNAME_KEY = "senderusername";

	public static final String PRODUCT_ID_KEY = "id";
	public static final String RECEIVER_NAME_KEY = "receivername";
	public static final String RECEIVER_SURNAME_KEY = "receiverusername";
	public static final String RECEIVER_ADDRESS_KEY = "receiveraddress";
	public static final String RECEIVER_PACKAGETYPE_KEY = "packagetype";

	private Product documentToProduct(User user, Document doc) {
		return new Product(user, doc.getString(RECEIVER_NAME_KEY), doc.getString(RECEIVER_SURNAME_KEY),
				doc.getString(RECEIVER_ADDRESS_KEY), doc.getString(RECEIVER_PACKAGETYPE_KEY),
				doc.getInteger(PRODUCT_ID_KEY));
	}

	@Override
	public List<Product> findAllProductsSentByUser(User user) {
		if (user == null) {
			return Collections.emptyList();
		}
		return StreamSupport.stream(productCollection.find().spliterator(), false)
				.filter(x -> Objects.equals(x.getInteger(SENDER_ID_KEY), user.getId()))
				.map(d -> documentToProduct(user, d)).toList();
	}

	@Override
	public void save(Product product) {
		if (product == null || product.getSender() == null) {
			return;
		}
		productCollection
				.insertOne(new Document().append(ProductMongoRepository.SENDER_ID_KEY, product.getSender().getId())
						.append(ProductMongoRepository.SENDER_USERNAME_KEY, product.getSender().getUsername())
						.append(ProductMongoRepository.PRODUCT_ID_KEY, product.getId())
						.append(ProductMongoRepository.RECEIVER_NAME_KEY, product.getReceiverName())
						.append(ProductMongoRepository.RECEIVER_SURNAME_KEY, product.getReceiverSurname())
						.append(ProductMongoRepository.RECEIVER_ADDRESS_KEY, product.getReceiverAddress())
						.append(ProductMongoRepository.RECEIVER_PACKAGETYPE_KEY, product.getPackageType()));
	}

	@Override
	public void delete(Product product) {
		if (product == null) {
			return;
		}
		Document query = new Document(PRODUCT_ID_KEY, product.getId());
		productCollection.findOneAndDelete(query);
	}

	@Override
	public Product findProductById(int id) {
		return StreamSupport.stream(productCollection.find().spliterator(), false)
				.filter(x -> Objects.equals(x.getInteger(ProductMongoRepository.PRODUCT_ID_KEY), id))
				.map(d -> documentToFullUser(d, id)).findFirst().orElse(null);
	}

	private Product documentToFullUser(Document d, int productId) {
		return new Product(
				new User(d.getString(ProductMongoRepository.SENDER_USERNAME_KEY), null,
						d.getInteger(ProductMongoRepository.SENDER_ID_KEY)),
				d.getString(ProductMongoRepository.RECEIVER_NAME_KEY),
				d.getString(ProductMongoRepository.RECEIVER_SURNAME_KEY),
				d.getString(ProductMongoRepository.RECEIVER_ADDRESS_KEY),
				d.getString(ProductMongoRepository.RECEIVER_PACKAGETYPE_KEY), productId);
	}
}
