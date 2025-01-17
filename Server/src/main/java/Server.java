import app.controllers.Salescontroller;
import app.controllers.ShelfController;
import app.util.DBInfo;
import app.controllers.ProductController;
import io.javalin.Javalin;

public class Server {
	// Database credentials
	private static final String DB_HOST = "localhost";
	private static final int DB_PORT = 5666;
	private static final String DB_NAME = "stoman";
	private static final String USER = "kowag";
	private static final String PASSWORD = "cjcex@&08GWzqRy6zMCqBR7E%ZYCFM5f";

	// Web app information
	private static final int APP_PORT = 25565;

	public static void main(String[] args) {

		DBInfo dbInfo = new DBInfo(
			String.format("jdbc:postgresql://%s:%d/%s",
				DB_HOST,
				DB_PORT,
				DB_NAME
			),
			USER,
			PASSWORD
		);

		var productController = new ProductController(dbInfo);
		var salesController = new Salescontroller(dbInfo);
		var shelfController = new ShelfController(dbInfo);
		Javalin app = Javalin.create();

		// Products
		app.get("/api/products/soonExpired", productController::getSoonExpired)
			.get("/api/products/{code}", productController::getOne)
			.get("/api/products", productController::getAll)
			.post("/api/products", productController::insert)
			.delete("/api/products/{code}", productController::delete);

		// Sales
		app.get("/api/products/sell/{date}_{code}", salesController::getOne)
			.put("/api/products/sell/{date}_{code}", salesController::sell);

		// Shelf
		app.post("/api/shelves/", shelfController::insert)
			.put("/api/shelves/{id}", shelfController::update)
			.get("/api/shelves/products", shelfController::getProducts)
			.get("/api/shelves", shelfController::getAll)
			.get("/api/shelves/{id}", shelfController::getOne)
			.delete("/api/shelves/{id}", shelfController::delete);

		app.start(APP_PORT);
	}
}
