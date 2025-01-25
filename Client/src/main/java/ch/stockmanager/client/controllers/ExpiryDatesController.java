package ch.stockmanager.client.controllers;

import ch.stockmanager.client.util.HTTPHelper;
import ch.stockmanager.types.ProductDateQuantity;
import javafx.beans.property.ReadOnlyListWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

public class ExpiryDatesController extends Controller {
	private final ObservableList<ProductDateQuantity> expiredProducts = FXCollections.observableArrayList();
	private final ObservableList<ProductDateQuantity> soonExpiredProducts = FXCollections.observableArrayList();

	public ExpiryDatesController(String serverIp) {
		super(serverIp);
	}

	@Override
	public void update() {
		updateExpiredProducts();
		updateSoonExpiredProducts();
	}

	@Override
	public String getPathPrefix() {
		return "api/products";
	}

	public void updateExpiredProducts() {
		new Thread(() -> {
			List<ProductDateQuantity> expiredProducts = HTTPHelper.getList(getUrl("expired"), ProductDateQuantity.class);

			this.expiredProducts.setAll(expiredProducts);
		}).start();
	}

	public void updateSoonExpiredProducts() {
		new Thread(() -> {
			List<ProductDateQuantity> soonExpiredProducts = HTTPHelper.getList(getUrl("soonExpired"), ProductDateQuantity.class);

			this.soonExpiredProducts.setAll(soonExpiredProducts);
		}).start();
	}

	public ObservableList<ProductDateQuantity> getExpiredProducts() {
		return new ReadOnlyListWrapper<>(expiredProducts);
	}

	public ObservableList<ProductDateQuantity> getSoonExpiredProducts() {
		return new ReadOnlyListWrapper<>(soonExpiredProducts);
	}
}
