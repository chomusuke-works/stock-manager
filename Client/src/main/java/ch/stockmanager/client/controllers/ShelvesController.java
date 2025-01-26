package ch.stockmanager.client.controllers;

import ch.stockmanager.client.util.HTTPHelper;
import ch.stockmanager.types.ProductShelf;
import ch.stockmanager.types.Shelf;
import javafx.beans.property.ReadOnlyListWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

public class ShelvesController extends Controller {
	private final ObservableList<ProductShelf> productsOnShelves = FXCollections.observableArrayList();
	private final ObservableList<Shelf> shelves = FXCollections.observableArrayList();

	public ShelvesController(String serverIp) {
		super(serverIp);
	}

	@Override
	public void update() {
		updateProductsOnShelves();
		updateShelves();
	}

	@Override
	public String getPathPrefix() {
		return "api/shelves";
	}

	public ObservableList<ProductShelf> getProductsOnShelves() {
		return new ReadOnlyListWrapper<>(productsOnShelves);
	}

	public ObservableList<Shelf> getShelves() {
		return new ReadOnlyListWrapper<>(shelves);
	}

	public void updateProductsOnShelves() {
		searchProducts("");
	}

	public void searchProducts(String searchTerm) {
		new Thread(() -> {
			List<ProductShelf> productsOnShelves = HTTPHelper.getList(getUrl(String.format("products?searchTerm=%s", searchTerm)), ProductShelf.class);

			this.productsOnShelves.setAll(productsOnShelves);
		}).start();
	}

	public void updateShelves() {
		new Thread(() -> {
			List<Shelf> shelves = HTTPHelper.getList(getUrl("all"), Shelf.class);

			this.shelves.setAll(shelves);
		}).start();
	}

	public void addShelf(Shelf shelf) {
		shelves.add(shelf);
		HTTPHelper.post(getUrl(), shelf);
	}

	public void removeShelf(Shelf shelf) {
		shelves.remove(shelf);
		HTTPHelper.delete(getUrl(Integer.toString(shelf.getId())));
	}

	public void updateShelf(Shelf shelf) {
		int id = shelf.getId();
		HTTPHelper.put(getUrl(Integer.toString(id)), shelf);

		updateShelves();
	}

	public void addProductToShelf(ProductShelf productShelf, Shelf shelf) {
		if (productShelf == null) throw new NullPointerException("Product shelf must not be null");
		if (shelf == null) throw new NullPointerException("Shelf must not be null");

		ProductShelf toInsert = new ProductShelf(
			productShelf.getProductCode(),
			productShelf.getProductName(),
			shelf.getId(),
			shelf.getName(),
			shelf.getIsStock() ? "stock" : "magasin"
		);

		if (productsOnShelves.contains(toInsert)) return;

		productsOnShelves.add(toInsert);
		HTTPHelper.post(getUrl("products"), toInsert);
	}

	public void removeProductFromShelf(ProductShelf productShelf) {
		if (productShelf == null) throw new NullPointerException("Product shelf must not be null");

		String path = String.format("products/%d_%d", productShelf.productCode, productShelf.shelfId);
		HTTPHelper.delete(getUrl(path));

		long count = productsOnShelves.stream()
			.filter(ps -> ps.equals(productShelf))
			.count();
		// Only the shelf part of the record is removed if it is the last record remaining
		if (count == 0) updateProductsOnShelves();
		else productsOnShelves.remove(productShelf);
	}
}
