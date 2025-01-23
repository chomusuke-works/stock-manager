package ch.stockmanager.client.views.elements;

import ch.stockmanager.types.ProductShelf;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

public class ProductShelfTable extends TableView<ProductShelf> {
	public ProductShelfTable() {
		super();
	}

	public ProductShelfTable(ObservableList<ProductShelf> products) {
		super();
		this.setItems(products);

		TableColumn<ProductShelf, String> productNameColumn = new TableColumn<>("Produit");
		productNameColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));

		TableColumn<ProductShelf, String> shelfNameColumn = new TableColumn<>("Etagère");
		shelfNameColumn.setCellValueFactory(new PropertyValueFactory<>("shelfName"));

		TableColumn<ProductShelf, String> storeSectionColumn = getStoreSectionColumn();

		this.setPrefHeight(400);

		this.getColumns().add(productNameColumn);
		this.getColumns().add(shelfNameColumn);
		this.getColumns().add(storeSectionColumn);
	}

	private static TableColumn<ProductShelf, String> getStoreSectionColumn() {
		Callback<TableColumn.CellDataFeatures<ProductShelf, String>, ObservableValue<String>> storeSectionValueFactory = param -> {
			ProductShelf productShelf = param.getValue();
			if (productShelf == null) return null;

			String cellData;

			if (productShelf.getShelfName() != null) {
				cellData = productShelf.isStock ? "stock" : "magasin";
			} else {
				cellData = "";
			}

			return new ReadOnlyStringWrapper(cellData);
		};

		TableColumn<ProductShelf, String> storeSectionColumn = new TableColumn<>("section");
		storeSectionColumn.setCellValueFactory(storeSectionValueFactory);

		return storeSectionColumn;
	}
}
