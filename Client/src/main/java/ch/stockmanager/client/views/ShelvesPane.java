package ch.stockmanager.client.views;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.*;

import ch.stockmanager.types.Product;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Font;

import ch.stockmanager.client.util.HTTPHelper;
import ch.stockmanager.types.ProductShelfQuantity;
import ch.stockmanager.types.Shelf;

/**
 * This pane displays information about the shelves on which the products are placed:
 * <p>
 * * on the left, the products associated with their shelf
 * <p>
 * * on the right, the shelves and buttons to add/rename/delete them
 */
public class ShelvesPane extends BorderPane {
    private static final String PATH_PREFIX = "http://localhost:25565/api/shelves/";

    TableView<ProductShelfQuantity> productsTableView;
    TextField champRecherche;
    ListView<Shelf> shelvesList;
    TableView<ProductShelfQuantity> latestTableViewSelected;


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

    /**
     * Simulation de chargement initial des rayons.
     * À remplacer par un appel HTTP récupérant la liste des rayons depuis l'API.
     */
    private List<Shelf> fetchShelves() {
        return HTTPHelper.getList(PATH_PREFIX + "all", Shelf.class);
    }

    private void postShelf(Shelf shelf) {
        HTTPHelper.post(PATH_PREFIX, shelf);
    }

    private void deleteShelf(Shelf shelf) {
        HTTPHelper.delete(PATH_PREFIX + shelf.getId());
    }

    private void updateShelf(Shelf shelf) {
        int id = shelf.getId();
        HTTPHelper.put(PATH_PREFIX + id, shelf);
    }

    private void addProductShelf(ProductShelfQuantity productShelfQuantity, Shelf shelf) {
        HTTPHelper.post(PATH_PREFIX + "products", new ProductShelfQuantity(productShelfQuantity.productName, 0, shelf.name, productShelfQuantity.productCode, shelf.id));
    }

    private void deleteProductShelf(ProductShelfQuantity productShelfQuantity) {
        HTTPHelper.delete(String.format(PATH_PREFIX + "products"
                + "/" + productShelfQuantity.productCode
                + "/" + productShelfQuantity.shelfId));
    }

    private List<ProductShelfQuantity> fetchProducts() {
        return HTTPHelper.getList(PATH_PREFIX + "products", ProductShelfQuantity.class);
    }

    /**
     * Filtrer les Products affichés en fonction du texte saisi dans la barre de recherche.
     * Simulation locale pour l'instant, peut être remplacée par un vrai appel HTTP.
     */
    private void filterProducts() {
        String searchedTerm = champRecherche.getText();
        if (searchedTerm == null || searchedTerm.trim().isEmpty())
            productsTableView.getItems().setAll(fetchProducts());

        String lowercaseTerm = searchedTerm.trim().toLowerCase();

        List<ProductShelfQuantity> filteredProducts =  fetchProducts().stream()
            .filter(e -> e.productName.toLowerCase().contains(lowercaseTerm))
            .toList();

        productsTableView.getItems().setAll(filteredProducts);
    }

    private VBox getLeftBox() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(10));

        productsTableView = getEmptyTableView();
        TableView<ProductShelfQuantity> store = getEmptyTableView();
        latestTableViewSelected = store;
        TableView<ProductShelfQuantity> stock = getEmptyTableView();

        BiConsumer<TableView<ProductShelfQuantity>, Boolean> getProductList =
            (tv, isStock) -> {
                LinkedList<ProductShelfQuantity> list = new LinkedList<>(productsTableView.getItems());
                LinkedList<ProductShelfQuantity> result = new LinkedList<>();
                while (!list.isEmpty()) {
                    var psq = list.pop();
                    var other = otherOrWithShelfNull(psq, list);
                    result.add(isStockFromId(psq.shelfId, isStock) ?  psq : other);
                    list.remove(other);
                }

                tv.getItems().setAll(result);
            };

        // Use the lambda in the listener
        productsTableView.getItems().addListener((ListChangeListener<ProductShelfQuantity>) c -> {
            while (c.next()) {
                if (c.wasAdded() || c.wasRemoved() || c.wasUpdated()) {
                    getProductList.accept(store, false);
                    getProductList.accept(stock, true);
                }
            }
        });
        store.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) latestTableViewSelected = store;
        });

        stock.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) latestTableViewSelected = stock;
        });

        new Thread(() -> {
            productsTableView.getItems().setAll(fetchProducts());
            getProductList.accept(store, false);
            getProductList.accept(stock, true);
        })
                .start();

        // Champ de recherche
        champRecherche = new TextField();
        champRecherche.setPromptText("Rechercher un Produit...");
        champRecherche.textProperty().addListener((obs, oldValue, newValue) -> filterProducts());

        // Bouton "Changer le rayon"
        Button changeShelfButton = getButton("Changer le rayon",
                e -> openChangeShelfDialog(latestTableViewSelected, latestTableViewSelected == stock));

        // Bouton "Enlever de l'étagère"
        Button removeFromShelfButton = getButton("Enlever de l'étagère",
                e -> {
                    ProductShelfQuantity selectedProduct = latestTableViewSelected.getSelectionModel().getSelectedItem();
                    if (selectedProduct != null) {
                        deleteProductShelf(selectedProduct);
                        new Thread(this::filterProducts).start();
                    } else {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Aucun produit sélectionné");
                        alert.setHeaderText(null);
                        alert.setContentText("Veuillez sélectionner un produit pour changer son rayon.");
                        alert.showAndWait();
                    }
                });

        HBox buttonBox = new HBox(10, changeShelfButton, removeFromShelfButton);

        // Wrap TableViews in TitledPanes
        TitledPane storePane = new TitledPane("Magasin", store);
        TitledPane stockPane = new TitledPane("Stock", stock);

        // Ajout des éléments au vbox de gauche
        box.getChildren().setAll(champRecherche, storePane, stockPane, buttonBox);

        return box;
    }

    private boolean isStockFromId (Integer id, Boolean isStock) {
        return shelvesList.getItems().filtered(s -> s.id == id).stream()
                .findFirst()
                .map(s -> s.isStock == isStock)
                .orElse(true);
    }
    //!shelvesList.getItems().filtered(s -> s.id == id && s.isStock).isEmpty();
    private ProductShelfQuantity otherOrWithShelfNull (ProductShelfQuantity psq, List<ProductShelfQuantity> list) {
        return list.stream().filter(i -> i.productCode == psq.productCode).findFirst().orElseGet(
                () -> new ProductShelfQuantity(psq.productName, psq.quantity, "", psq.productCode, 0));
    };


    private TableView<ProductShelfQuantity> getEmptyTableView() {
        LinkedList<String> columnNames = new LinkedList<>(List.of("Nom", "Stock", "Rayon"));
        TableView<ProductShelfQuantity> table = new TableView<>();

        for (String name : Arrays.stream(ProductShelfQuantity.class.getDeclaredFields()).map(Field::getName).toList()) {
            TableColumn<ProductShelfQuantity, String> column = new TableColumn<>(columnNames.pop());
            column.setCellValueFactory(new PropertyValueFactory<>(name));
            table.getColumns().add(column);
            if (columnNames.isEmpty()) break;
        }
        table.setPrefHeight(400);

        return table;
    }

    private VBox getRightBox() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(10));

        Label labelRayons = new Label("Liste des rayons :");
        labelRayons.setFont(new Font("Arial", 14));

        shelvesList = new ListView<>();
        shelvesList.setPrefHeight(200);
        VBox actionsBox = getActionsBox(shelvesList.getItems(), shelvesList.getSelectionModel().selectedItemProperty());

        box.getChildren().setAll(labelRayons, shelvesList, actionsBox);

        new Thread(() -> shelvesList.getItems().setAll(fetchShelves()))
            .start();

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
            filterProducts();

            fieldShelfRename.clear();
        });

        Button buttonShelfDelete = getButton("Supprimer", e -> {
            Shelf selectedShelf = selectedItem.getValue();
            if (selectedShelf == null) return;

            deleteShelf(selectedShelf);
            shelvesList.remove(selectedShelf);
            filterProducts();
        });

        // Disposition pour la gestion des rayons
        HBox hboxNouveauRayonStore = new HBox(5, fieldShelfStoreCreate, buttonShelfAddStore);
        HBox hboxNouveauRayonStock = new HBox(5, fieldShelfStockCreate, buttonShelfAddStock);
        HBox hboxRenommerRayon = new HBox(5, fieldShelfRename, buttonShelfRename);

        return new VBox(10, hboxNouveauRayonStore, hboxNouveauRayonStock, hboxRenommerRayon, buttonShelfDelete);
    }

    private void openChangeShelfDialog(TableView<ProductShelfQuantity> table, Boolean isStock) {
        ProductShelfQuantity selectedProduct = table.getSelectionModel().getSelectedItem();

        if (selectedProduct == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Aucun produit sélectionné");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez sélectionner un produit pour changer son rayon.");
            alert.showAndWait();

            return;
        }

        Dialog<Shelf> dialog = new Dialog<>();
        dialog.setTitle("Changer le rayon");
        dialog.setHeaderText("Sélectionnez un nouveau rayon pour le produit : " + selectedProduct.getProductName());

        ButtonType confirmButtonType = new ButtonType("Confirmer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(confirmButtonType, ButtonType.CANCEL);

        ComboBox<Shelf> shelfComboBox = new ComboBox<>();
        shelfComboBox.getItems().setAll(fetchShelves().stream().filter(s -> s.isStock == isStock).toList());

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        grid.add(new Label("Rayon :"), 0, 0);
        grid.add(shelfComboBox, 1, 0);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == confirmButtonType) {
                return shelfComboBox.getSelectionModel().getSelectedItem();
            }
            return null;
        });

        Optional<Shelf> result = dialog.showAndWait();
        result.ifPresent(newShelf -> {
            if (selectedProduct.shelfId != 0) deleteProductShelf(selectedProduct);
            addProductShelf(selectedProduct, newShelf);
            new Thread(this::filterProducts).start();
        });
    }
}

