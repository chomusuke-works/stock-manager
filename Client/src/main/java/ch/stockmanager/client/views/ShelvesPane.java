package ch.stockmanager.client.views;

import java.util.*;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.value.ObservableStringValue;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;

import ch.stockmanager.types.ProductShelf;
import ch.stockmanager.types.Shelf;
import ch.stockmanager.client.util.HTTPHelper;
import ch.stockmanager.client.views.elements.ProductShelfTable;

/**
 * This pane displays information about the shelves on which the products are placed:
 * <p>
 * * on the left, the products associated with their shelf
 * <p>
 * * on the right, the shelves and buttons to add/rename/delete them
 */
public class ShelvesPane extends BorderPane {
    private static final String PATH_PREFIX = "http://localhost:25565/api/shelves";

    ObservableList<ProductShelf> products = FXCollections.observableArrayList();
    ObservableList<Shelf> shelves = FXCollections.observableArrayList();
    ObservableValue<ProductShelf> lastSelectedProduct;
    ObservableStringValue searchFieldContent;


    public ShelvesPane() {
        this.setPadding(new Insets(15));

        Label title = new Label("Localisation des Products & Gestion des rayons");
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

    private void postShelf(Shelf shelf) {
        HTTPHelper.post(PATH_PREFIX, shelf);
    }

    private void deleteShelf(Shelf shelf) {
        HTTPHelper.delete(String.format("%s/%d", PATH_PREFIX, shelf.getId()));
    }

    private void updateShelf(Shelf shelf) {
        int id = shelf.getId();
        HTTPHelper.put(String.format("%s/%d", PATH_PREFIX, id), shelf);
    }

    private void addProductToShelf(ProductShelf productShelf, Shelf shelf) {
        ProductShelf toInsert = new ProductShelf(
            productShelf.getProductCode(),
            productShelf.getProductName(),
            shelf.getId(),
            shelf.getName(),
            shelf.getIsStock()
        );

        if (products.contains(toInsert)) return;

        HTTPHelper.post(String.format("%s/products", PATH_PREFIX), toInsert);
    }

    private void deleteProductShelf(ProductShelf productShelfQuantity) {
        HTTPHelper.delete(String.format("%s/products/%d_%d",
            PATH_PREFIX,
            productShelfQuantity.productCode,
            productShelfQuantity.shelfId
        ));
    }

    private void searchProduct(String searchTerm) {
        String treatedSearchTerm = searchTerm.trim().toLowerCase();
        if (treatedSearchTerm.isEmpty()) return;

        updateProducts();

        List<ProductShelf> filteredProducts = products.stream()
            .filter(e -> e.productName.toLowerCase().contains(treatedSearchTerm))
            .toList();

        products.setAll(filteredProducts);
    }

    private VBox getLeftBox() {
        TableView<ProductShelf> productsTable = new ProductShelfTable(products);

        productsTable.setItems(this.products);
        updateProducts();
        lastSelectedProduct = productsTable.getSelectionModel().selectedItemProperty();

        TextField searchField = new TextField();
        searchFieldContent = searchField.textProperty();
        searchField.setPromptText("Rechercher un Produit...");
        searchField.textProperty().addListener((obs, oldValue, searchTerm) -> searchProduct(searchTerm));

        String addToShelfTitle = "Ajouter au rayon";
        Button addToShelfButton = getButton(addToShelfTitle, e -> {
            Shelf newShelf = shelfSelectionDialog(addToShelfTitle);
            if (newShelf == null) return;

            addProductToShelf(lastSelectedProduct.getValue(), newShelf);

            updateProducts();
        });

        Button removeFromShelfButton = getButton("Enlever de l'étagère",
                e -> {
                    if (lastSelectedProduct != null) {
                        deleteProductShelf(lastSelectedProduct.getValue());
                        updateProducts();
                    } else {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Aucun produit sélectionné");
                        alert.setHeaderText(null);
                        alert.setContentText("Veuillez sélectionner un produit pour changer son rayon.");
                        alert.showAndWait();
                    }
                });

        HBox buttonBox = new HBox(10, addToShelfButton, removeFromShelfButton);

        VBox box = new VBox(10);
        box.setPadding(new Insets(10));
        box.getChildren().setAll(searchField, productsTable, buttonBox);

        return box;
    }

    private VBox getRightBox() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(10));

        Label labelRayons = new Label("Liste des rayons :");
        labelRayons.setFont(new Font("Arial", 14));

        ListView<Shelf> shelvesList = new ListView<>();
        shelvesList.setItems(this.shelves);
        shelvesList.setPrefHeight(200);

        VBox actionsBox = getActionsBox(shelvesList.getItems(), shelvesList.getSelectionModel().selectedItemProperty());

        box.getChildren().setAll(labelRayons, shelvesList, actionsBox);

        updateShelves();

        return box;
    }

    private Button getButton(String buttonText, EventHandler<ActionEvent> event) {
        Button button = new Button(buttonText);
        button.setOnAction(event);

        return button;
    }

    private VBox getActionsBox(ObservableList<Shelf> shelvesList, ReadOnlyObjectProperty<Shelf> selectedItem) {
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

            postShelf(newShelf);
            updateShelves();
            shelfNameField.clear();
            isShelfInStockCheckbox.setSelected(false);
        });

        Button buttonShelfRename = getButton("Renommer", e -> {
            Shelf oldShelf = selectedItem.getValue();
            Shelf newShelf = Shelf.of(oldShelf);
            newShelf.name = fieldShelfRename.getText().trim();
            if (oldShelf.getName() == null || newShelf.getName().isEmpty()) return;

            updateShelf(newShelf);
            //updateProducts();

            fieldShelfRename.clear();
        });

        Button buttonShelfDelete = getButton("Supprimer", e -> {
            Shelf selectedShelf = selectedItem.getValue();
            if (selectedShelf == null) return;

            deleteShelf(selectedShelf);
            shelvesList.remove(selectedShelf);
            updateProducts();
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
        shelfComboBox.getItems().setAll(shelves);

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
    
    public void updateProducts() {
        List<ProductShelf> updatedProducts = HTTPHelper.getList(String.format("%s/products", PATH_PREFIX), ProductShelf.class);

        new Thread(() -> products.setAll(updatedProducts))
            .start();
    }
    
    public void updateShelves() {
        List<Shelf> updatedShelves = HTTPHelper.getList(String.format("%s/all", PATH_PREFIX), Shelf.class);

        new Thread(() -> shelves.setAll(updatedShelves))
            .start();
    }
}

