package ch.stockmanager.server.controllers;

import java.sql.SQLException;

import io.javalin.http.Context;

import ch.stockmanager.server.util.ContextHelper;
import ch.stockmanager.server.util.DBInfo;
import ch.stockmanager.types.ProductShelfQuantity;


public class ProductShelfController extends Controller {
    private final DBInfo dbInfo;

    public ProductShelfController(DBInfo dbInfo) {
        super();
        this.dbInfo = dbInfo;
    }

    @Override
    public void insert(Context context) {
        try (
                var connection = dbInfo.getConnection();
                var statement = connection.prepareStatement(QUERY_INSERT)
        ) {
            ProductShelfQuantity productShelf = context.bodyAsClass(ProductShelfQuantity.class);

            statement.setLong(1, productShelf.productCode);
            statement.setInt(2, productShelf.shelfId);

            statement.executeUpdate();
            context.status(201);
        } catch (SQLException e) {
            context.status(500);
            context.result("Database error" + e);
        }
    }

    @Override
    public void getOne(Context context) {
        // TODO: define ?
    }

    @Override
    public void delete(Context context) {
        try (
                var connection = dbInfo.getConnection();
                var statement = connection.prepareStatement(QUERY_DELETE)
        ) {
            long productCode = ContextHelper.getLongPathParam(context, "productCode");
            int shelfId = ContextHelper.getIntPathParam(context, "shelfId");

            statement.setLong(1, productCode);
            statement.setInt(2, shelfId);

            statement.executeUpdate();

            context.status(200);
        } catch (SQLException e) {
            context.status(500);
            context.result("Database error" + e);
        }
    }

    @Override
    protected String getDataType() {
        return "product_shelf";
    }
}
