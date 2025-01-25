package ch.stockmanager.client.controllers;

import ch.stockmanager.client.util.HTTPHelper;
import ch.stockmanager.types.Product;
import ch.stockmanager.types.Supplier;
import javafx.beans.property.ReadOnlyListWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

public class SuppliersController extends Controller {
	private final ObservableList<Supplier> suppliers = FXCollections.observableArrayList();
	private final ObservableList<Product> suppliedProducts = FXCollections.observableArrayList();

	public SuppliersController(String serverIp) {
		super(serverIp);
	}

	@Override
	public void update() {
		new Thread(() -> {
			List<Supplier> suppliers = HTTPHelper.getList(getUrl("all"), Supplier.class);

			this.suppliers.setAll(suppliers);
		}).start();
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
	}

	public void modifySupplier(Supplier supplier) {
		if (supplier == null) return;

		HTTPHelper.put(getUrl(Integer.toString(supplier.getId())), supplier);

		update();
	}

	public void removeSupplier(int id) {
		HTTPHelper.delete(getUrl(Integer.toString(id)));
		update();
	}

	public void updateSuppliedProducts(int id) {
		new Thread(() -> {
			List<Product> suppliedProducts = HTTPHelper.getList(getUrl(id + "/products"), Product.class);

			this.suppliedProducts.setAll(suppliedProducts);
		}).start();
	}
}
