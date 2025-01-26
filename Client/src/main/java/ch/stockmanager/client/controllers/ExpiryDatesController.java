package ch.stockmanager.client.controllers;

import ch.stockmanager.client.util.FXHelper;
import ch.stockmanager.types.ProductDateQuantity;
import javafx.beans.property.ReadOnlyListWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

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
		FXHelper.ObservableListUpdaterTask
			.run(getUrl("expired"), expiredProducts, ProductDateQuantity.class);
	}

	public void updateSoonExpiredProducts() {
		FXHelper.ObservableListUpdaterTask
			.run(getUrl("soonExpired"), soonExpiredProducts, ProductDateQuantity.class);
	}

	public ObservableList<ProductDateQuantity> getExpiredProducts() {
		return new ReadOnlyListWrapper<>(expiredProducts);
	}

	public ObservableList<ProductDateQuantity> getSoonExpiredProducts() {
		return new ReadOnlyListWrapper<>(soonExpiredProducts);
	}
}
