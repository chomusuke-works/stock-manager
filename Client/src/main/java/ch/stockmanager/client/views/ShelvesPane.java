package ch.stockmanager.client.views;

import java.lang.reflect.Field;
import java.util.*;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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

    public ShelvesPane() {
        HBox topBar = getTopBar();

        VBox leftBox = getLeftBox();
        VBox rightBox = getRightBox();

        SplitPane splitPane = new SplitPane();
        splitPane.getItems().setAll(leftBox, rightBox);
        splitPane.setDividerPositions(0.6);  // 60% / 40%

        this.setPadding(new Insets(15));
        BorderPane.setMargin(topBar, new Insets(0, 0, 20, 0));

        this.setTop(topBar);
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
        HTTPHelper.delete(String.format("%s/products/%d_%d", PATH_PREFIX, productShelfQuantity.productCode, productShelfQuantity.shelfId));
    }

    private List<ProductShelfQuantity> fetchProducts() {
        return HTTPHelper.getList(PATH_PREFIX + "products", ProductShelfQuantity.class);
    }

    /**
     * Filtrer les Products affichés en fonction du texte saisi dans la barre de recherche.
     * Simulation locale pour l'instant, peut être remplacée par un vrai appel HTTP.
     */
    private void filterProducts(String searchedTerm, ObservableList<ProductShelfQuantity> dest) {
        if (searchedTerm == null || searchedTerm.trim().isEmpty()) return;
        String lowercaseTerm = searchedTerm.trim().toLowerCase();

        List<ProductShelfQuantity> filteredProducts =  fetchProducts().stream()
            .filter(e -> e.productName.toLowerCase().contains(lowercaseTerm))
            .toList();
        
        dest.setAll(filteredProducts);
    }

    private HBox getTopBar() {
        HBox topBar = new HBox();
        topBar.setPadding(new Insets(10));
        topBar.setSpacing(10);
        topBar.setAlignment(Pos.CENTER_LEFT);
        // -----------------------------
        // Titre principal
        // -----------------------------
        Label title = new Label("Localisation des Products & Gestion des rayons");
        title.setFont(new Font("Arial", 20));

        Button backButton = new Button("<--");
        backButton.setOnAction(e -> Navigator.goToDashboard());

        topBar.getChildren().addAll(backButton, title);

        return topBar;
    }

    private VBox getLeftBox() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(10));

        TableView<ProductShelfQuantity> table = getTable();

        // Champ de recherche
        TextField champRecherche = new TextField();
        champRecherche.setPromptText("Rechercher un Produit...");
        champRecherche.textProperty().addListener((obs, oldValue, newValue) ->
            filterProducts(newValue, table.getItems())
        );

        // Bouton "Changer le rayon"
        Button changeShelfButton = getButton("Changer le rayon",
                e -> openChangeShelfDialog(table));

        // Ajout des éléments au vbox de gauche
        box.getChildren().setAll(champRecherche, table, changeShelfButton);

        return box;
    }

    private TableView<ProductShelfQuantity> getTable() {
        LinkedList<String> columnNames = new LinkedList<>(List.of("Nom", "Stock", "Rayon"));
        TableView<ProductShelfQuantity> table = new TableView<>();

        for (String name : Arrays.stream(ProductShelfQuantity.class.getDeclaredFields()).map(Field::getName).toList()) {
            TableColumn<ProductShelfQuantity, String> column = new TableColumn<>(columnNames.pop());
            column.setCellValueFactory(new PropertyValueFactory<>(name));
            table.getColumns().add(column);
            if (columnNames.isEmpty()) break;
        }
        table.setPrefHeight(400);

        new Thread(() -> table.getItems().setAll(fetchProducts()))
            .start();

        return table;
    }

    private VBox getRightBox() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(10));

        Label labelRayons = new Label("Liste des rayons :");
        labelRayons.setFont(new Font("Arial", 14));

        ListView<Shelf> shelvesList = new ListView<>();
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
        TextField fieldShelfCreate = new TextField();
        TextField fieldShelfRename = new TextField();

        fieldShelfCreate.setPromptText("Nouveau rayon");
        fieldShelfRename.setPromptText("Nouveau nom pour le rayon sélectionné");

        Button buttonShelfAdd = getButton("Ajouter", e -> {
            Shelf newShelf = new Shelf(0, fieldShelfCreate.getText().trim(), false);
            if (newShelf.getName().isEmpty()) return;

            postShelf(newShelf);
            shelvesList.setAll(fetchShelves());
            fieldShelfCreate.clear();
        });

        Button buttonShelfRename = getButton("Renommer", e -> {
            Shelf oldShelf = selectedItem.getValue();
            Shelf newShelf = Shelf.of(oldShelf);
            newShelf.name = fieldShelfRename.getText().trim();
            if (oldShelf.getName() == null || newShelf.getName().isEmpty()) return;

            updateShelf(newShelf);
            shelvesList.setAll(fetchShelves());
            fieldShelfRename.clear();
        });

        Button buttonShelfDelete = getButton("Supprimer", e -> {
            Shelf selectedShelf = selectedItem.getValue();
            if (selectedShelf == null) return;

            deleteShelf(selectedShelf);
            shelvesList.remove(selectedShelf);
        });

        // Disposition pour la gestion des rayons
        HBox hboxNouvelleRayon = new HBox(5, fieldShelfCreate, buttonShelfAdd);
        HBox hboxRenommerRayon = new HBox(5, fieldShelfRename, buttonShelfRename);

        return new VBox(10, hboxNouvelleRayon, hboxRenommerRayon, buttonShelfDelete);
    }

    private void openChangeShelfDialog(TableView<ProductShelfQuantity> table) {
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
        shelfComboBox.getItems().setAll(fetchShelves());

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
            deleteProductShelf(selectedProduct);
            addProductShelf(selectedProduct, newShelf);
            new Thread(() -> table.getItems().setAll(fetchProducts()))
                    .start();
        });
    }
}

