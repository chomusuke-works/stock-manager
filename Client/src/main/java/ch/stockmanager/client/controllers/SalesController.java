package ch.stockmanager.client.controllers;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.Instant;

import javafx.beans.property.ReadOnlyListWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import ch.stockmanager.types.Product;
import ch.stockmanager.types.Sale;
import ch.stockmanager.client.util.HTTPHelper;
import ch.stockmanager.client.util.JavaFxHelper;

public class SalesController extends Controller {
	private final ObservableList<Sale> sales = FXCollections.observableArrayList();
	private final OrdersController ordersController;

	public SalesController(String serverIp, OrdersController ordersController) {
		super(serverIp);

		this.ordersController = ordersController;
	}

	@Override
	public void update() {
		searchSale("");
	}

	@Override
	public String getPathPrefix() {
		return "api/sales";
	}

	public ObservableList<Sale> getSales() {
		return new ReadOnlyListWrapper<>(sales);
	}

	private void addSale(long code, int sold, int thrown) {
		if (sold < 0 || thrown < 0 || (sold == 0 && thrown == 0))
			throw new IllegalArgumentException("Bad input: quantity must be positive");

		Timestamp now = Timestamp.from(Instant.now());

		String productUrl = String.format("http://%s/api/products/%d", getServerIp(), code);
		Product soldProduct = HTTPHelper.get(productUrl, Product.class);
		if (soldProduct == null) throw new NullPointerException("No corresponding product found");

		HTTPHelper.post(getUrl(), new Sale(now, soldProduct.getCode(), soldProduct.getName(), sold, thrown));

		update();
		ordersController.update();
	}

	public void searchSale(String searchTerm) {
		String encodedSearchTerm = URLEncoder.encode(searchTerm, StandardCharsets.UTF_8);
		String url = getUrl(String.format("all?searchTerm=%s", encodedSearchTerm));
		JavaFxHelper.ObservableListUpdaterTask
			.run(url, sales, Sale.class);
	}

	public void sell(long productCode, int quantity) {
		addSale(productCode, quantity, 0);
	}

	public void dispose(long productCode, int quantity) {
		addSale(productCode, 0, quantity);
	}
}
