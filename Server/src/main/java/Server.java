import app.controllers.Salescontroller;
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
		Javalin app = Javalin.create();

		// Products
		app.get("/api/products/soonExpired", productController::getSoonExpired)
			.get("/api/products/{code}", productController::getOne)
			.get("/api/products", productController::getAll)
			.post("/api/products", productController::insert)
			.delete("/api/products/{code}", productController::delete);

		app.get("/api/products/sell/{date}_{code}", salesController::getOne)
			.put("/api/products/sell/{date}_{code}", salesController::sell);

		//app.get("/api/products/orderList", null);



		app.start(APP_PORT);
	}
}
