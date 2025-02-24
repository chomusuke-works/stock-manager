package ch.stockmanager.server.controllers;

import ch.stockmanager.server.util.ContextHelper;
import ch.stockmanager.server.util.DBInfo;
import ch.stockmanager.types.Product;
import ch.stockmanager.types.Supplier;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class SupplierController extends Controller {
	private final DBInfo dbInfo;
	private final String QUERY_UPDATE;
	private final String QUERY_SUPPLIER_PRODUCTS;

	public SupplierController (DBInfo dbInfo) {
		super();
		this.dbInfo = dbInfo;
		List<String> extraQueries = getExtraQueries();
		QUERY_UPDATE = extraQueries.getFirst();
		QUERY_SUPPLIER_PRODUCTS = extraQueries.getLast();
	}

	@Override
	protected String getDataType() {
		return "supplier";
	}

	@Override
	public void insert(Context context) {
		try (
			var connection = dbInfo.getConnection();
			var statement = connection.prepareStatement(QUERY_INSERT)
		) {
			Supplier supplier = context.bodyAsClass(Supplier.class);

			statement.setString(1, supplier.name);
			statement.setString(2, supplier.email);
			statement.setInt(3, supplier.orderFrequency);

			statement.executeUpdate();
			context.status(201);
		} catch (SQLException e) {
			context.status(500);
			context.result("Database error" + e);
		}
	}

	public void getAll(Context context) {
		try (
			var connection = dbInfo.getConnection();
			var statement = connection.prepareStatement(QUERY_GETALL)
		) {
			List<Supplier> suppliers = new LinkedList<>();
			ResultSet results = statement.executeQuery();
			while (results.next()) {
				suppliers.add(getSupplier(results));
			}

			context.status(HttpStatus.OK);
			context.json(suppliers);

			results.close();
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
			int id = ContextHelper.getIntPathParam(context, "id");
			statement.setInt(1, id);

			ResultSet results = statement.executeQuery();
			if (!results.next()) {
				context.status(HttpStatus.NOT_FOUND);
				return;
			}

			context.status(HttpStatus.OK);
			context.json(getSupplier(results));

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
			int id = ContextHelper.getIntPathParam(context, "id");
			statement.setInt(1, id);
			statement.executeUpdate();

			context.status(HttpStatus.OK);
		} catch (SQLException e) {
			context.status(HttpStatus.INTERNAL_SERVER_ERROR);
			context.result("Database error");
		}
	}

	public void update(Context context) {
		System.out.println("update..." + QUERY_UPDATE);
		try (
			var connection = dbInfo.getConnection();
			var statement = connection.prepareStatement(QUERY_UPDATE)
		) {
			Supplier supplier = context.bodyAsClass(Supplier.class);
			int id = ContextHelper.getIntPathParam(context, "id");

			statement.setString(1, supplier.name);
			statement.setString(2, supplier.email);
			statement.setInt(3, supplier.orderFrequency);
			statement.setInt(4, id);

			statement.executeUpdate();
			context.status(200);
		} catch (SQLException e) {
			context.status(500);
			context.result("Database error" + e);
		}
	}

	public void getSupplierProducts(Context context) {
		try (
			var connection = dbInfo.getConnection();
			var statement = connection.prepareStatement(QUERY_SUPPLIER_PRODUCTS)
		) {
			int supplierId = ContextHelper.getIntPathParam(context, "id");
			statement.setInt(1, supplierId);

			ResultSet results = statement.executeQuery();

			List<Product> products = new LinkedList<>();
			while (results.next()) {
				products.add(ProductController.getProduct(results));
			}

			context.status(HttpStatus.OK);
			context.json(products);

			results.close();
		} catch (SQLException e) {
			context.status(500);
			context.result("Database error");
		}
	}

	private Supplier getSupplier(ResultSet resultSet) throws SQLException {
		return new Supplier(
			resultSet.getInt(1),
			resultSet.getString(2),
			resultSet.getString(3),
			resultSet.getInt(4)
		);
	}
}
