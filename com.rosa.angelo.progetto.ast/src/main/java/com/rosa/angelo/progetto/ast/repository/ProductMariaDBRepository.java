package com.rosa.angelo.progetto.ast.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.rosa.angelo.progetto.ast.model.Product;
import com.rosa.angelo.progetto.ast.model.User;

public class ProductMariaDBRepository implements ProductRepository {

	public static final String USER_ID_FOREIGN_KEY = "sender_id";
	public static final String PRODUCT_ID_KEY = "id";
	public static final String RECEIVER_NAME_KEY = "receivername";
	public static final String RECEIVER_SURNAME_KEY = "receiverusername";
	public static final String RECEIVER_ADDRESS_KEY = "receiveraddress";
	public static final String RECEIVER_PACKAGETYPE_KEY = "packagetype";

	public static final String IMAGE = System.getProperty("mariadb.image", "mariadb");
	public static final String VERSION = System.getProperty("mariadb.version", "10.9");
	public static final int PORT = Integer.parseInt(System.getProperty("mariadb.port", "3306"));

	public static final String AST_DB_NAME = System.getProperty("mariadb.dbname", "testdb");
	public static final String DB_USERNAME = System.getProperty("mariadb.user", "testuser");
	public static final String DB_PASSWORD = System.getProperty("mariadb.password", "password");
	public static final String PRODUCT_TABLE_NAME = "products";

	private Connection connection;

	private String findAllProductsSentByUserQuery = "SELECT product.*, u.id as %s, u.username "
			+ "FROM %s product JOIN %s u ON product.%s=u.%s WHERE u.id=?";
	
	private String saveProductQuery = "INSERT INTO %s (id,sender_id,receivername,receiverusername,receiveraddress,packagetype)"
			+ " VALUES(?,?,?,?,?,?)";

	public ProductMariaDBRepository(Connection connection) {
		this.connection = connection;
	}

	void injectFindAllProductsSentByUserQuery(String string) {
		this.findAllProductsSentByUserQuery = string;
	}
	
	void injectSaveProductsQuery(String string) {
		this.saveProductQuery = string;
	}

	private GenericRepositoryException handleDBException(SQLException ex) {
		return new GenericRepositoryException(ex.getMessage());
	}

	private Product databaseToProduct(ResultSet rs) throws SQLException {
		User senderUser = new User(rs.getString(UserMariaDBRepository.USERNAME_KEY), null,
				rs.getInt(USER_ID_FOREIGN_KEY));
		return new Product(senderUser, rs.getString(RECEIVER_NAME_KEY), rs.getString(RECEIVER_SURNAME_KEY),
				rs.getString(RECEIVER_ADDRESS_KEY), rs.getString(RECEIVER_PACKAGETYPE_KEY), rs.getInt(PRODUCT_ID_KEY));
	}

	@Override
	public List<Product> findAllProductsSentByUser(User user) throws GenericRepositoryException {
		if (user == null) {
			return Collections.emptyList();
		}

		String statement = String.format(findAllProductsSentByUserQuery, USER_ID_FOREIGN_KEY, PRODUCT_TABLE_NAME,
				UserMariaDBRepository.USER_TABLE_NAME, USER_ID_FOREIGN_KEY, UserMariaDBRepository.ID_KEY);
		List<Product> products = new ArrayList<>();
		try (PreparedStatement stmt = connection.prepareStatement(statement)) {
			stmt.setInt(1, user.getId());
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				products.add(databaseToProduct(rs));
			}
		} catch (SQLException ex) {
			throw (handleDBException(ex));
		}
		return products;
	}

	@Override
	public void save(Product product) throws GenericRepositoryException {
		String statement = String.format(saveProductQuery, PRODUCT_TABLE_NAME);
		try (PreparedStatement stmt = connection.prepareStatement(statement)) {
			stmt.setInt(1, product.getId());
			stmt.setInt(2, product.getSender().getId());
			stmt.setString(3, product.getReceiverName());
			stmt.setString(4, product.getReceiverSurname());
			stmt.setString(5, product.getReceiverAddress());
			stmt.setString(6, product.getPackageType());
			stmt.executeUpdate();
		} catch (SQLException ex) {
			throw (handleDBException(ex));
		}
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
