package ch.stockmanager.client.controllers;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyListWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import ch.stockmanager.client.util.HTTPHelper;
import ch.stockmanager.client.util.JavaFxHelper;
import ch.stockmanager.types.Product;
import ch.stockmanager.types.Supplier;

public class SuppliersController extends Controller {
	private final ObservableList<Supplier> suppliers = FXCollections.observableArrayList();
	private final ObservableList<Product> suppliedProducts = FXCollections.observableArrayList();

	public SuppliersController(String serverIp) {
		super(serverIp);
	}

	@Override
	public void update() {
		JavaFxHelper.ObservableListUpdaterTask
			.run(getUrl("all"), suppliers, Supplier.class);
	}

	@Override
	public String getPathPrefix() {
		return "api/suppliers";
	}

	public ObservableList<Supplier> getSuppliers() {
		return new ReadOnlyListWrapper<>(suppliers);
	}

	public ObservableList<Product> getSuppliedProducts() {
		return new ReadOnlyListWrapper<>(suppliedProducts);
	}

	public void addSupplier(Supplier supplier) {
		if (supplier == null) return;

		HTTPHelper.post(getUrl(), supplier);

		update();
	}

	public void modifySupplier(Supplier supplier) {
		if (supplier == null) return;

		HTTPHelper.put(getUrl(Integer.toString(supplier.getId())), supplier);

		update();
	}

	public void removeSupplier(Supplier supplier) {
		if (supplier == null) return;

		HTTPHelper.delete(getUrl(Integer.toString(supplier.getId())));
		update();
	}

	public void updateSuppliedProducts(Supplier supplier) {
		if (supplier == null) {
			Platform.runLater(suppliedProducts::clear);

			return;
		}

		JavaFxHelper.ObservableListUpdaterTask
			.run(getUrl(supplier.getId() + "/products"), suppliedProducts, Product.class);
	}
}
