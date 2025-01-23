package ch.stockmanager.client.views;

import java.util.*;

import ch.stockmanager.client.views.elements.ProductShelfTable;
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

import ch.stockmanager.client.util.HTTPHelper;
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

    private List<Shelf> fetchShelves() {
        return HTTPHelper.getList(String.format("%s/all", PATH_PREFIX), Shelf.class);
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

    private void addProductShelf(ProductShelf productShelf, Shelf shelf) {
        ProductShelf toInsert = new ProductShelf(
            productShelf.getProductCode(),
            productShelf.getProductName(),
            shelf.getId(),
            shelf.getName(),
            shelf.getIsStock()
        );

        HTTPHelper.post(String.format("%s/products", PATH_PREFIX), toInsert);
    }

    private void deleteProductShelf(ProductShelf productShelfQuantity) {
        HTTPHelper.delete(String.format("%s/products/%d_%d",
            PATH_PREFIX,
            productShelfQuantity.productCode,
            productShelfQuantity.shelfId
        ));
    }

    private List<ProductShelf> fetchProducts() {
        return HTTPHelper.getList(String.format("%s/products", PATH_PREFIX), ProductShelf.class);
    }

    /**
     * Filtrer les Products affichés en fonction du texte saisi dans la barre de recherche.
     * Simulation locale pour l'instant, peut être remplacée par un vrai appel HTTP.
     */
    private void applySearch() {
        String searchTerm = searchFieldContent.getValue().trim().toLowerCase();
        products.setAll(fetchProducts());
        if (searchTerm.isEmpty()) return;

        List<ProductShelf> filteredProducts = products.stream()
            .filter(e -> e.productName.toLowerCase().contains(searchTerm))
            .toList();

        products.setAll(filteredProducts);
    }

    private VBox getLeftBox() {
        TableView<ProductShelf> productsTable = new ProductShelfTable(products);

        // Get references to items lists of the tables
        productsTable.setItems(this.products);
        updateProducts();
        lastSelectedProduct = productsTable.getSelectionModel().selectedItemProperty();

        TextField searchField = new TextField();
        searchFieldContent = searchField.textProperty();
        searchField.setPromptText("Rechercher un Produit...");
        searchField.textProperty().addListener((obs, oldValue, newValue) -> applySearch());

        Button changeShelfButton = getButton("Changer le rayon", e -> {
            Shelf newShelf = askNewShelfDialog();
            if (newShelf == null) return;

            addProductShelf(lastSelectedProduct.getValue(), newShelf);
            deleteProductShelf(lastSelectedProduct.getValue());
            
            products.setAll(fetchProducts());
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

        HBox buttonBox = new HBox(10, changeShelfButton, removeFromShelfButton);

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
        TextField fieldShelfStoreCreate = new TextField();
        TextField fieldShelfStockCreate = new TextField();
        TextField fieldShelfRename = new TextField();

        fieldShelfStoreCreate.setPromptText("Nouveau rayon");
        fieldShelfStockCreate.setPromptText("Nouveau rayon");
        fieldShelfRename.setPromptText("Nouveau nom pour le rayon sélectionné");

        Button buttonShelfAddStore = getButton("Ajouter au magasin", e -> {
            Shelf newShelf = new Shelf(0, fieldShelfStoreCreate.getText().trim(), false);
            if (newShelf.getName().isEmpty()) return;

            postShelf(newShelf);
            shelvesList.setAll(fetchShelves());
            fieldShelfStoreCreate.clear();
        });

        Button buttonShelfAddStock = getButton("Ajouter au stock", e -> {
            Shelf newShelf = new Shelf(0, fieldShelfStockCreate.getText().trim(), true);
            if (newShelf.getName().isEmpty()) return;

            postShelf(newShelf);
            shelvesList.setAll(fetchShelves());
            fieldShelfStockCreate.clear();
        });

        Button buttonShelfRename = getButton("Renommer", e -> {
            Shelf oldShelf = selectedItem.getValue();
            Shelf newShelf = Shelf.of(oldShelf);
            newShelf.name = fieldShelfRename.getText().trim();
            if (oldShelf.getName() == null || newShelf.getName().isEmpty()) return;

            updateShelf(newShelf);
            shelvesList.setAll(fetchShelves());
            applySearch();

            fieldShelfRename.clear();
        });

        Button buttonShelfDelete = getButton("Supprimer", e -> {
            Shelf selectedShelf = selectedItem.getValue();
            if (selectedShelf == null) return;

            deleteShelf(selectedShelf);
            shelvesList.remove(selectedShelf);
            applySearch();
        });

        // Disposition pour la gestion des rayons
        HBox hboxNouveauRayonStore = new HBox(5, fieldShelfStoreCreate, buttonShelfAddStore);
        HBox hboxNouveauRayonStock = new HBox(5, fieldShelfStockCreate, buttonShelfAddStock);
        HBox hboxRenommerRayon = new HBox(5, fieldShelfRename, buttonShelfRename);

        return new VBox(10, hboxNouveauRayonStore, hboxNouveauRayonStock, hboxRenommerRayon, buttonShelfDelete);
    }

    private Shelf askNewShelfDialog() {
        if (lastSelectedProduct.getValue() == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Aucun produit sélectionné");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez sélectionner un produit pour changer son rayon.");
            alert.showAndWait();

            return null;
        }

        Dialog<Shelf> dialog = new Dialog<>();
        dialog.setTitle("Changer le rayon");
        dialog.setHeaderText("Sélectionnez un nouveau rayon pour le produit : " + lastSelectedProduct.getValue().getProductName());

        dialog.getDialogPane().getButtonTypes().setAll(ButtonType.APPLY, ButtonType.CANCEL);

        ComboBox<Shelf> shelfComboBox = new ComboBox<>();
        shelfComboBox.getItems().setAll(fetchShelves());

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
        new Thread(() -> products.setAll(fetchProducts()))
            .start();
    }
    
    public void updateShelves() {
        new Thread(() -> shelves.setAll(fetchShelves()))
            .start();
    }
}

