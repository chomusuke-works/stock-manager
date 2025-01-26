package ch.stockmanager.client.views;

import java.util.*;

import ch.stockmanager.client.controllers.ShelvesController;
import ch.stockmanager.client.util.JavaFxHelper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.value.ObservableStringValue;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;

import ch.stockmanager.types.ProductShelf;
import ch.stockmanager.types.Shelf;

/**
 * This pane displays information about the shelves on which the products are placed:
 * <p>
 * * on the left, the products associated with their shelf
 * <p>
 * * on the right, the shelves and buttons to add/rename/delete them
 */
public class ShelvesPane extends BorderPane {
	ObservableValue<ProductShelf> lastSelectedProduct;
	ObservableStringValue searchFieldContent;
	private final ShelvesController controller;

	public ShelvesPane(ShelvesController controller) {
		this.controller = controller;

		this.setPadding(new Insets(15));

		Label title = new Label("Gestion des rayons");
		title.setFont(new Font("Arial", 20));
		BorderPane.setMargin(title, new Insets(0, 0, 20, 0));

		VBox leftBox = getLeftBox();
		VBox rightBox = getRightBox();

		SplitPane splitPane = new SplitPane();
		splitPane.getItems().setAll(leftBox, rightBox);
		splitPane.setDividerPositions(0.6);  // 60% / 40%

		this.setTop(title);
		this.setCenter(splitPane);
	}

	private VBox getLeftBox() {
		TableView<ProductShelf> productsTable = JavaFxHelper.getTable(
			new String[]{"Produit", "Rayon", "Secteur"},
			new String[]{"productName", "shelfName", "sector"}
		);

		productsTable.setItems(controller.getProductsOnShelves());
		lastSelectedProduct = productsTable.getSelectionModel().selectedItemProperty();

		TextField searchField = new TextField();
		searchFieldContent = searchField.textProperty();
		searchField.setPromptText("Rechercher un Produit...");
		searchField.textProperty().addListener((obs, oldValue, searchTerm) ->
			controller.searchProducts(searchTerm)
		);

		String addToShelfTitle = "Ajouter au rayon";
		Button addToShelfButton = getButton(addToShelfTitle, e -> {
			Shelf newShelf = shelfSelectionDialog(addToShelfTitle);
			if (newShelf == null) return;

			controller.addProductToShelf(lastSelectedProduct.getValue(), newShelf);
		});

		String changeShelfTitle = "Changer le rayon";
		Button changeShelfButton = getButton(changeShelfTitle, e -> {
			Shelf newShelf = shelfSelectionDialog(changeShelfTitle);

			controller.addProductToShelf(lastSelectedProduct.getValue(), newShelf);
			controller.removeProductFromShelf(lastSelectedProduct.getValue());
		});

		Button removeFromShelfButton = getButton("Enlever du rayon",
			e -> {
				if (lastSelectedProduct != null) {
					controller.removeProductFromShelf(lastSelectedProduct.getValue());
				} else {
					Alert alert = new Alert(Alert.AlertType.INFORMATION);
					alert.setTitle("Aucun produit sélectionné");
					alert.setHeaderText(null);
					alert.setContentText("Veuillez sélectionner un produit pour changer son rayon.");
					alert.showAndWait();
				}
			});

		HBox buttonBox = new HBox(10, addToShelfButton, changeShelfButton, removeFromShelfButton);

		return new VBox(10, searchField, productsTable, buttonBox);
	}

	private VBox getRightBox() {
		Label labelRayons = new Label("Liste des rayons :");
		labelRayons.setFont(new Font("Arial", 14));

		ListView<Shelf> shelvesList = new ListView<>();
		shelvesList.setItems(controller.getShelves());
		shelvesList.setPrefHeight(200);

		VBox actionsBox = getActionsBox(shelvesList.getSelectionModel().selectedItemProperty());

		return new VBox(10, labelRayons, shelvesList, actionsBox);
	}

	private Button getButton(String buttonText, EventHandler<ActionEvent> event) {
		Button button = new Button(buttonText);
		button.setOnAction(event);

		return button;
	}

	private VBox getActionsBox(ReadOnlyObjectProperty<Shelf> selectedItem) {
		TextField shelfNameField = new TextField();
		TextField fieldShelfStockCreate = new TextField();
		TextField fieldShelfRename = new TextField();

		shelfNameField.setPromptText("Nouveau rayon");
		fieldShelfStockCreate.setPromptText("Nouveau rayon");
		fieldShelfRename.setPromptText("Nouveau nom pour le rayon sélectionné");

		CheckBox isShelfInStockCheckbox = new CheckBox();
		isShelfInStockCheckbox.setTooltip(new Tooltip("Cocher si le rayon est dans le stock."));

		Button addShelfButton = getButton("Ajouter au magasin", e -> {
			Shelf newShelf = new Shelf(0, shelfNameField.getText().trim(), isShelfInStockCheckbox.selectedProperty().getValue());
			if (newShelf.getName().isEmpty()) return;

			controller.addShelf(newShelf);
			shelfNameField.clear();
			isShelfInStockCheckbox.setSelected(false);
		});

		Button buttonShelfRename = getButton("Renommer", e -> {
			Shelf oldShelf = selectedItem.getValue();
			Shelf newShelf = Shelf.of(oldShelf);
			newShelf.name = fieldShelfRename.getText().trim();
			if (oldShelf.getName() == null || newShelf.getName().isEmpty()) return;

			controller.updateShelf(newShelf);

			fieldShelfRename.clear();
		});

		Button buttonShelfDelete = getButton("Supprimer", e -> {
			Shelf selectedShelf = selectedItem.getValue();
			if (selectedShelf == null) return;

			controller.removeShelf(selectedShelf);
		});

		HBox newShelfControls = new HBox(5, shelfNameField, isShelfInStockCheckbox, addShelfButton);
		HBox renameControls = new HBox(5, fieldShelfRename, buttonShelfRename);

		return new VBox(10, newShelfControls, renameControls, buttonShelfDelete);
	}

	private Shelf shelfSelectionDialog(String dialogTitle) {
		if (lastSelectedProduct.getValue() == null) {
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setTitle("Aucun produit sélectionné");
			alert.setHeaderText(null);
			alert.setContentText("Veuillez sélectionner un produit pour changer son rayon.");
			alert.showAndWait();

			return null;
		}

		Dialog<Shelf> dialog = new Dialog<>();
		dialog.setTitle(dialogTitle);
		dialog.setHeaderText("Sélectionnez un nouveau rayon pour le produit : " + lastSelectedProduct.getValue().getProductName());

		dialog.getDialogPane().getButtonTypes().setAll(ButtonType.APPLY, ButtonType.CANCEL);

		ComboBox<Shelf> shelfComboBox = new ComboBox<>();
		shelfComboBox.setItems(controller.getShelves());

		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));

		grid.add(new Label("Rayon :"), 0, 0);
		grid.add(shelfComboBox, 1, 0);

		dialog.getDialogPane().setContent(grid);

		dialog.setResultConverter(dialogButton -> {
			if (dialogButton == ButtonType.APPLY) {
				return shelfComboBox.getSelectionModel().getSelectedItem();
			}

			return null;
		});

		Optional<Shelf> result = dialog.showAndWait();
		return result.orElse(null);
	}
}

