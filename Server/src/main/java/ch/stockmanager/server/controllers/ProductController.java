package ch.stockmanager.server.controllers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import io.javalin.http.Context;
import io.javalin.http.HttpStatus;

import ch.stockmanager.types.Order;
import ch.stockmanager.types.ProductDateQuantity;
import ch.stockmanager.server.util.*;
import ch.stockmanager.types.Product;

public class ProductController extends Controller {
	private final DBInfo dbInfo;

	private final String
		QUERY_GET_SOON_EXPIRED,
		QUERY_GET_EXPIRED,
		QUERY_GET_ORDERS;

	private static final int EXPIRY_THRESHOLD = 7;

	public ProductController(DBInfo dbInfo) {
		super();

		this.dbInfo = dbInfo;

		List<String> extraQueries = getExtraQueries();
		QUERY_GET_SOON_EXPIRED = extraQueries.get(0);
		QUERY_GET_EXPIRED = extraQueries.get(1);
		QUERY_GET_ORDERS = extraQueries.get(2);
		System.out.println("done");
	}

	@Override
	public String getDataType() {
		return "product";
	}

	@Override
	public void insert(Context context) {
		try (
			var connection = dbInfo.getConnection();
			var statement = connection.prepareStatement(QUERY_INSERT)
		) {
			Product product = context.bodyAsClass(Product.class);

			statement.setLong(1, product.getCode());
			statement.setString(2, product.getName());
			statement.setDouble(3, product.getPrice());
			statement.setInt(4, product.getSupplierId());

			statement.executeUpdate();
			context.status(201);
		} catch (SQLException e) {
			context.status(500);
			context.result("Database error");
		}
	}

	@Override
	public void getOne(Context context) {
		try (
			var connection = dbInfo.getConnection();
			var statement = connection.prepareStatement(QUERY_GET)
		) {
			long code = ContextHelper.getLongPathParam(context, "code");
			statement.setLong(1, code);

			ResultSet results = statement.executeQuery();
			if (!results.next()) {
				context.status(404);

				return;
			}

			context.status(200);
			context.json(getProduct(results));

			results.close();
		} catch (SQLException e) {
			context.status(500);
			context.result("Database error");
		}
	}

	public void getAll(Context context) {
		try (
			var connection = dbInfo.getConnection();
			var statement = connection.prepareStatement(QUERY_GETALL)
		) {
			List<Product> products = new LinkedList<>();
			ResultSet results = statement.executeQuery();
			while (results.next()) {
				products.add(getProduct(results));
			}

			context.status(HttpStatus.OK);
			context.json(products);

			results.close();
		} catch (SQLException e) {
			context.status(500);
			context.result("Database error");
		}
	}

	@Override
	public void delete(Context context) {
		try (
			var connection = dbInfo.getConnection();
			var statement = connection.prepareStatement(QUERY_DELETE)
		) {
			long code = ContextHelper.getLongPathParam(context, "code");
			statement.setLong(1, code);
			statement.executeUpdate();

			context.status(200);
		} catch (SQLException e) {
			context.status(500);
			context.result("Database error");
		}
	}

	public void getSoonExpired(Context context) {
		List<ProductDateQuantity> expired = new LinkedList<>();

		try (
			var connection = dbInfo.getConnection();
			var statement = connection.prepareStatement(QUERY_GET_SOON_EXPIRED)
		) {
			statement.setInt(1, EXPIRY_THRESHOLD);

			ResultSet results = statement.executeQuery();
			while (results.next()) {
				ProductDateQuantity row = getExpiredProductQuantity(results);

				expired.add(row);
			}
			results.close();

			context.status(HttpStatus.OK);
			context.json(expired);
		} catch (SQLException e) {
			context.status(HttpStatus.INTERNAL_SERVER_ERROR);
			context.result("Database error");
		}
	}

	public void getExpired(Context context) {
		List<ProductDateQuantity> expired = new LinkedList<>();

		try (
			var connection = dbInfo.getConnection();
			var statement = connection.prepareStatement(QUERY_GET_EXPIRED)
		) {
			ResultSet results = statement.executeQuery();
			while (results.next()) {
				ProductDateQuantity row = getExpiredProductQuantity(results);

				expired.add(row);
			}
			results.close();

			context.status(HttpStatus.OK);
			context.json(expired);
		} catch (SQLException e) {
			context.status(HttpStatus.INTERNAL_SERVER_ERROR);
			context.result("Database error");
		}
	}

	public void getOrders(Context context) {
		List<Order> orders = new LinkedList<>();

		try (
			var connection = dbInfo.getConnection();
			var statement = connection.prepareStatement(QUERY_GET_ORDERS)
		) {
			ResultSet results = statement.executeQuery();
			while (results.next()) {
				Order row = getOrder(results);

				orders.add(row);
			}
			results.close();

			context.status(HttpStatus.OK);
			context.json(orders);
		} catch (SQLException e) {
			context.status(HttpStatus.INTERNAL_SERVER_ERROR);
			context.result("Database error");
		}
	}

	private Product getProduct(ResultSet resultSet) throws SQLException {
		int supplierId = resultSet.getInt(4);

		return new Product(
			resultSet.getLong(1),
			resultSet.getString(2),
			resultSet.getDouble(3),
			supplierId
		);
	}

	private ProductDateQuantity getExpiredProductQuantity(ResultSet resultSet) throws SQLException {
		return new ProductDateQuantity(
			resultSet.getString(1),
			resultSet.getDate(2),
			resultSet.getInt(3)
		);
	}

	private Order getOrder(ResultSet resultSet) throws SQLException {
		return new Order(
			resultSet.getString(1),
			resultSet.getInt(2)
		);
	}
}
