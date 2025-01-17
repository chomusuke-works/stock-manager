package ch.stockmanager.client.views;

import ch.stockmanager.types.Sale;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import ch.stockmanager.client.util.RequestHelper;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * Vue permettant d'enregistrer les ventes du jour et de signaler des produits comme déchets.
 * Elle inclut un champ de recherche de produits, un tableau pour lister les produits filtrés
 * et un formulaire pour saisir la quantité vendue ou jetée.
 */
public class VueVentesDechets extends BorderPane {

    private final TableView<Sale> salesTable;
    private final TextField champQuantite;

    private final ObservableList<Sale> sales = FXCollections.observableArrayList();

	public VueVentesDechets() throws IOException, URISyntaxException {
        // Padding autour de la vue
        this.setPadding(new Insets(15));

        HBox topBar = new HBox();
        topBar.setPadding(new Insets(10));
        topBar.setSpacing(10);
        topBar.setAlignment(Pos.CENTER_LEFT);

        // Titre
        Label titre = new Label("Ventes & Gestion de Déchets");
        titre.setFont(new Font("Arial", 24));

        Button boutonRetour = new Button("Retour Dashboard");
        boutonRetour.setOnAction(e -> Navigator.goToDashboard());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        topBar.getChildren().addAll(titre, spacer, boutonRetour);
        this.setTop(topBar);

        BorderPane.setMargin(titre, new Insets(0, 0, 20, 0));

        // Création du champ de recherche
        var champRecherche = new TextField();
        champRecherche.setPromptText("Rechercher un produit...");
        champRecherche.setPrefWidth(200);
        champRecherche.textProperty().addListener((observable, oldValue, newValue) -> {
            // Ici, vous pourrez lancer la requête HTTP pour rechercher les produits correspondants
            // ou filtrer localement si vous avez déjà chargé les produits en mémoire.
            filtrerProduits(newValue);
        });

        // Table des produits
        salesTable = new TableView<>();

        TableColumn<Sale, String> columnDate = new TableColumn<>("Date de péremption");
        TableColumn<Sale, Long> columnCode = new TableColumn<>("produit");
        TableColumn<Sale, Integer> columnSold = new TableColumn<>("Vendus");
        TableColumn<Sale, Integer> columnThrown = new TableColumn<>("Jetés");

        columnCode.setCellValueFactory(new PropertyValueFactory<>("code"));
        columnDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        columnSold.setCellValueFactory(new PropertyValueFactory<>("sold"));
        columnThrown.setCellValueFactory(new PropertyValueFactory<>("thrown"));

        salesTable.getColumns().add(columnDate);
        salesTable.getColumns().add(columnCode);
        salesTable.getColumns().add(columnSold);
        salesTable.getColumns().add(columnThrown);
        salesTable.setPrefHeight(300);

        champQuantite = new TextField();
        champQuantite.setPromptText("Quantité");

        Button boutonVente = getButton("Enregistrer la vente");

        Button boutonDechet = getButton("Signaler en déchet");

        HBox hBoxForm = new HBox(10);
        hBoxForm.getChildren().addAll(new Label("Quantité :"), champQuantite, boutonVente, boutonDechet);
        hBoxForm.setPadding(new Insets(10, 0, 0, 0));

        // Disposition verticale : Champ recherche, Table, Formulaire
        VBox vboxCenter = new VBox(10);
        vboxCenter.getChildren().addAll(champRecherche, salesTable, hBoxForm);

        this.setCenter(vboxCenter);

        // Chargement initial des produits (simulé ici en dur)
        chargerProduitsParDefaut();
    }

    private Button getButton(String buttonText) {
        Button boutonVente = new Button(buttonText);
        boutonVente.setOnAction(event -> {
            Sale selectedSale = salesTable.getSelectionModel().getSelectedItem();
            if (selectedSale != null) {
                try {
                    Integer.parseInt(champQuantite.getText());
                    // TODO: API call
                } catch (NumberFormatException e) {
                    System.err.println("Quantité invalide.");
                }
            }
        });
        return boutonVente;
    }

    /**
     * Méthode pour charger une liste initiale de produits (fictive).
     * À remplacer par un appel HTTP pour récupérer les produits depuis l'API.
     */
    private void chargerProduitsParDefaut() throws IOException, URISyntaxException {
        //Exemple de connection à la bd pour récupérer les produits bientôt expirés
        HttpURLConnection connexion = RequestHelper.createConnexion(
                "http://localhost:25565/api/sales/all",
                "GET");
        RequestHelper.sendRequest(connexion, HttpURLConnection.HTTP_OK);
        String answer = RequestHelper.getAnswer(connexion);
        var objectMapper = new ObjectMapper();
        List<Sale> deserializedSales = objectMapper.readValue(answer, objectMapper.getTypeFactory().constructCollectionType(ArrayList.class, Sale.class));
        sales.setAll(deserializedSales);
        salesTable.setItems(sales);
    }

    /**
     * Performs a search through all the sales.
     *
     * @param searchTerm the term to search for
     */
    private void filtrerProduits(String searchTerm) {
        if (searchTerm == null || searchTerm.isEmpty()) {
            salesTable.setItems(sales);
        } else {
			ObservableList<Sale> filteredSales = FXCollections.observableArrayList(sales.stream()
				.filter(s -> String.valueOf(s.getCode()).contains(searchTerm))
				.toList());

            salesTable.setItems(filteredSales);
        }
    }
}
