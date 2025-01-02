package app.controllers;

import app.types.Product;
import io.javalin.http.Context;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProductController extends CRUDController<Product> {
	public ProductController(DBInfo dbInfo) {
		super(dbInfo);
	}

	@Override
	public String getDataType() {
		return "product";
	}

	@Override
	protected void prepareInsertQuery(PreparedStatement statement, Product product) throws SQLException {
		statement.setInt(1, product.code());
		statement.setString(2, product.name());
		statement.setDouble(3, product.price());
		statement.setInt(4, product.supplierId());
	}

	@Override
	protected Product getResult(ResultSet resultSet) throws SQLException {
		return new Product(
			resultSet.getInt(1),
			resultSet.getString(2),
			resultSet.getDouble(3),
			resultSet.getInt(4)
		);
	}

	@Override
	protected Product extractObject(Context ctx) {
		String code = ctx.queryParam("code");
		String name = ctx.queryParam("name");
		String price = ctx.queryParam("price");
		String supplierId = ctx.queryParam("supplierId");

		if (code == null || name == null || price == null || supplierId == null)
			throw new NullPointerException();

		return new Product(
			Integer.parseInt(code),
			name,
			Double.parseDouble(price),
			Integer.parseInt(supplierId)
		);
	}

	@Override
	protected int getId(Context ctx) {
		String code = ctx.queryParam("code");
		if (code == null) throw new NullPointerException();

		try {
			return Integer.parseInt(code);
		} catch (NumberFormatException e) {
			throw new RuntimeException("Invalid product code");
		}
	}
}
