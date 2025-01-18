package ch.stockmanager.client.views;

import ch.stockmanager.types.ProductShelfQuantity;
import ch.stockmanager.types.Shelf;
import com.fasterxml.jackson.core.JsonProcessingException;
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

import java.util.ArrayList;
import java.util.List;

/**
 * Vue pour afficher la localisation des Products et gérer les rayons.
 * - À gauche : recherche + tableau des Products (nom, stock, rayon).
 * - À droite : liste des rayons, avec possibilité d'en ajouter, de renommer ou de supprimer.
 */
public class VueLocalisationRayons extends BorderPane {

	private final TableView<ProductShelfQuantity> tableProducts;

    private final ListView<Shelf> shelvesList;


    public VueLocalisationRayons() {
        // Mise en page
        this.setPadding(new Insets(15));

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
        this.setTop(topBar);

        BorderPane.setMargin(title, new Insets(0, 0, 10, 0));

        // -----------------------------
        // SECTION GAUCHE
        // -----------------------------
        VBox leftVBox = new VBox(10);
        leftVBox.setPadding(new Insets(10));

        // Products table
        tableProducts = new TableView<>();
        TableColumn<ProductShelfQuantity, String> nameColumn = new TableColumn<>("Nom");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<ProductShelfQuantity, Number> columnStock = new TableColumn<>("Stock");
        columnStock.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        TableColumn<ProductShelfQuantity, String> columnShelf = new TableColumn<>("Rayon");
        columnShelf.setCellValueFactory(new PropertyValueFactory<>("shelf"));

        tableProducts.getColumns().add(nameColumn);
        tableProducts.getColumns().add(columnStock);
        tableProducts.getColumns().add(columnShelf);
        tableProducts.setPrefHeight(400);

        // Champ de recherche
        TextField champRecherche = new TextField();
        champRecherche.setPromptText("Rechercher un Produit...");
        champRecherche.textProperty().addListener((obs, oldValue, newValue) ->
            filterProducts(newValue, tableProducts.getItems())
        );

        // Ajout des éléments au vbox de gauche
        leftVBox.getChildren().addAll(champRecherche, tableProducts);

        // -----------------------------
        // SECTION DROITE
        // -----------------------------
        VBox vboxDroite = new VBox(10);
        vboxDroite.setPadding(new Insets(10));

        Label labelRayons = new Label("Liste des rayons :");
        labelRayons.setFont(new Font("Arial", 14));

        shelvesList = new ListView<>();
        shelvesList.setPrefHeight(200);


        vboxDroite.getChildren().addAll(labelRayons, shelvesList, getActionsBox());

        // -----------------------------
        // Placement final
        // -----------------------------
        SplitPane splitPane = new SplitPane();
        splitPane.getItems().addAll(leftVBox, vboxDroite);
        splitPane.setDividerPositions(0.6);  // 60% / 40%

        this.setCenter(splitPane);

        shelvesList.getItems().setAll(fetchShelves());
        tableProducts.getItems().setAll(fetchProducts());
    }

    /**
     * Simulation de chargement initial des rayons.
     * À remplacer par un appel HTTP récupérant la liste des rayons depuis l'API.
     */
    private List<Shelf> fetchShelves() {
        try {
            return HTTPHelper.getList("http://localhost:25565/api/shelves/all", Shelf.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private void postShelf(Shelf shelf) {
        try {
            HTTPHelper.post("http://localhost:25565/api/shelves", shelf);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void deleteShelf(Shelf shelf) {
        HTTPHelper.delete("http://localhost:25565/api/shelves/" + shelf.getId());
        shelvesList.getItems().remove(shelf);
    }

    private void updateShelf(Shelf shelf) {
        try {
            int id = shelf.getId();
            HTTPHelper.put("http://localhost:25565/api/shelves/" + id, shelf);
        }
        catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * TODO: API call
     */
    private List<ProductShelfQuantity> fetchProducts() {
        return new ArrayList<>();
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

    private Button getButton(String buttonText, EventHandler<ActionEvent> event) {
        Button button = new Button(buttonText);
        button.setOnAction(event);

        return button;
    }

    private VBox getActionsBox() {
        TextField fieldShelfCreate = new TextField();
        TextField fieldShelfRename = new TextField();

        fieldShelfCreate.setPromptText("Nouveau rayon");
        fieldShelfRename.setPromptText("Nouveau nom pour le rayon sélectionné");

        Button buttonShelfAdd = getButton("Ajouter", e -> {
            Shelf newShelf = new Shelf(0, fieldShelfCreate.getText().trim(), false);
            if (newShelf.getName().isEmpty()) return;

            postShelf(newShelf);
            shelvesList.getItems().setAll(fetchShelves());
            fieldShelfCreate.clear();
        });

        Button buttonShelfRename = getButton("Renommer", e -> {
            Shelf oldShelf = shelvesList.getSelectionModel().getSelectedItem();
            Shelf newShelf = Shelf.of(oldShelf);
            newShelf.name = fieldShelfRename.getText().trim();
            if (oldShelf.getName() == null || newShelf.getName().isEmpty()) return;

            updateShelf(newShelf);
            shelvesList.getItems().setAll(fetchShelves());
            fieldShelfRename.clear();
        });

        Button buttonShelfDelete = getButton("Supprimer", e -> {
            Shelf selectedShelf = shelvesList.getSelectionModel().getSelectedItem();
            if (selectedShelf == null) return;

            deleteShelf(selectedShelf);
        });

        // Disposition pour la gestion des rayons
        HBox hboxNouvelleRayon = new HBox(5, fieldShelfCreate, buttonShelfAdd);
        HBox hboxRenommerRayon = new HBox(5, fieldShelfRename, buttonShelfRename);

        return new VBox(10, hboxNouvelleRayon, hboxRenommerRayon, buttonShelfDelete);
    }
}

