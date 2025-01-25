package ch.stockmanager.client.controllers;

import ch.stockmanager.client.util.HTTPHelper;
import ch.stockmanager.types.Sale;
import javafx.beans.property.ReadOnlyListWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

public class SalesController extends Controller {
	private final ObservableList<Sale> sales = FXCollections.observableArrayList();
	private final OrdersController ordersController;

	public SalesController(String serverIp, OrdersController ordersController) {
		super(serverIp);

		this.ordersController = ordersController;
	}

	@Override
	public void update() {
		new Thread(() -> {
			List<Sale> sales = HTTPHelper.getList(getUrl("all"), Sale.class);

			this.sales.setAll(sales);
		}).start();
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

		HTTPHelper.post(getUrl(), new Sale(now, code, sold, thrown));

		update();
		ordersController.update();
	}

	public void filterSales(String searchTerm) {
		if (searchTerm == null) throw new NullPointerException("Search term must not be null");

		update();

		String treatedSearchTerm = searchTerm.toLowerCase().trim();
		if (treatedSearchTerm.isEmpty()) return;

		// TODO: database-side filtering
		new Thread(() -> {
			var filtered = sales.stream()
				.filter(s -> String.valueOf(s.code).contains(treatedSearchTerm))
				.toList();

			sales.setAll(filtered);
		}).start();
	}

	public void sell(long productCode, int quantity) {
		addSale(productCode, quantity, 0);
	}

	public void dispose(long productCode, int quantity) {
		addSale(productCode, 0, quantity);
	}
}
