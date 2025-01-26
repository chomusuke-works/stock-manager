package ch.stockmanager.client.views;

import ch.stockmanager.client.controllers.SuppliersController;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;

import ch.stockmanager.client.util.FXHelper;
import ch.stockmanager.types.Product;
import ch.stockmanager.types.Supplier;
import javafx.util.Callback;
import javafx.util.converter.IntegerStringConverter;

/**
 * This pane displays information about suppliers and which products they provide.
 */
public class SuppliersPane extends BorderPane {
	private final SuppliersController controller;

	public SuppliersPane(SuppliersController controller) {
		this.controller = controller;

		// Components (Structure)
		// - Title
		Label title = new Label("Fournisseurs");
		title.setFont(new Font("Arial", 24));
		BorderPane.setMargin(title, new Insets(0, 0, 20, 0));

		ListView<Supplier> suppliersList = getSuppliersList();
		ReadOnlyObjectProperty<Supplier> selectedSupplier = suppliersList.getSelectionModel().selectedItemProperty();

		Button addSupplierButton = new Button("+");
		Button removeSupplierButton = new Button("-");

		VBox suppliersListBox = new VBox(new HBox(addSupplierButton, removeSupplierButton), suppliersList);

		// - Table of the supplier's products
		TableView<Product> productsTable = FXHelper.getTable(
			new String[]{"Produit", "Prix"},
			new String[]{"name", "price"}
		);
		productsTable.setItems(controller.getSuppliedProducts());

		VBox supplierDetailsBox = new VBox(10,
			getDetailsGrid(selectedSupplier),
			productsTable
		);
		supplierDetailsBox.setPadding(new Insets(10));

		// - UI division
		SplitPane splitPane = new SplitPane(suppliersListBox, supplierDetailsBox);
		splitPane.setDividerPositions(0.3); // 30% / 70%

		// - Create supplier button action
		addSupplierButton.setOnAction(event -> {
			Supplier s = new Supplier(0, "", "", 0);
			suppliersList.getItems().add(s);
			suppliersList.getSelectionModel().select(s);
			Supplier newSupplier = showSupplierDialog(selectedSupplier.get());
			controller.addSupplier(newSupplier);
		});

		removeSupplierButton.disableProperty().bind(selectedSupplier.isNull());  // Can't delete a supplier if no selection
		removeSupplierButton.setOnAction(event ->
			controller.removeSupplier(selectedSupplier.get())
		);

		this.setTop(title);
		this.setCenter(splitPane);
	}

	private Supplier showSupplierDialog(Supplier supplier) {
		if (supplier == null) throw new NullPointerException("Supplier is null");

		// Create the dialog
		Dialog<Supplier> dialog = new Dialog<>();
		dialog.setTitle("Edit Supplier");

		// Set the button types
		dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

		// Create the fields
		TextField nameField = new TextField(supplier.getName());
		TextField contactField = new TextField(supplier.getEmail());
		TextField orderFrequencyField = new TextField();
		orderFrequencyField.setTextFormatter(new TextFormatter<>(new IntegerStringConverter()));
		orderFrequencyField.setText(Integer.toString(supplier.getOrderFrequency()));

		// Create the layout
		VBox editBox = new VBox(10,
			new Label("Name:"), nameField,
			new Label("Contact:"), contactField,
			new Label("Order Frequency:"), orderFrequencyField);
		dialog.getDialogPane().setContent(editBox);

		// Convert the result to a supplier when the save button is clicked
		dialog.setResultConverter(dialogButton -> {
			if (dialogButton != ButtonType.OK) return null;

			String newName = nameField.getText();
			String newContact = contactField.getText();
			int newOrderFrequency = Integer.parseInt(orderFrequencyField.getText());

			return new Supplier(supplier.getId(), newName, newContact, newOrderFrequency);
		});

		return dialog.showAndWait().orElse(null);
	}

	private GridPane getDetailsGrid(ReadOnlyObjectProperty<Supplier> selectedSupplier) {
		Label supplierNameLabel = new Label();
		Label supplierContactLabel = new Label();
		Label supplierOrderFrequencyLabel = new Label();
		Button editButton = new Button("Edit");

		supplierNameLabel.textProperty()
			.bind(selectedSupplier.map(Supplier::getName));
		supplierContactLabel.textProperty()
			.bind(selectedSupplier.map(Supplier::getEmail));
		supplierOrderFrequencyLabel.textProperty()
			.bind(selectedSupplier.map(s -> String.valueOf(s.getOrderFrequency())));

		GridPane grid = new GridPane(10, 10);
		grid.add(new Label("Nom : "), 0, 0);
		grid.add(new Label("E-mail : "), 0, 1);
		grid.add(new Label("Période de commande : "), 0, 2);
		grid.add(editButton, 0, 3);

		grid.add(supplierNameLabel, 1, 0);
		grid.add(supplierContactLabel, 1, 1);
		grid.add(supplierOrderFrequencyLabel, 1, 2);

		editButton.setOnAction(event -> {
			Supplier modifiedSupplier = showSupplierDialog(selectedSupplier.get());
			controller.modifySupplier(modifiedSupplier);
		});

		return grid;
	}

	private ListView<Supplier> getSuppliersList() {
		ListView<Supplier> list = new ListView<>();
		list.setCellFactory(new SupplierCellFactory());
		list.setItems(controller.getSuppliers());
		list.setPrefWidth(200);
		VBox.setVgrow(list, Priority.ALWAYS);

		list.getSelectionModel().selectedItemProperty()
			.addListener((observable, oldValue, newValue) ->
				controller.updateSuppliedProducts(newValue)
			);

		return list;
	}

	private static class SupplierCellFactory implements Callback<ListView<Supplier>, ListCell<Supplier>> {
		@Override
		public ListCell<Supplier> call(ListView<Supplier> param) {
			return new ListCell<>() {
				@Override
				protected void updateItem(Supplier item, boolean empty) {
					super.updateItem(item, empty);

					if (item == null || empty) setText(null);
					else setText(item.getName());
				}
			};
		}
	}
}