package ch.stockmanager.client.views;

import ch.stockmanager.client.util.HTTPHelper;
import ch.stockmanager.types.Order;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Font;

import java.io.IOException;
import java.util.List;

/**
 * Vue permettant de gérer les commandes de réapprovisionnement.
 * - Liste des produits à commander (quantité, coût unitaire, coût total)
 * - Possibilité d'activer/désactiver l'automatisation des commandes
 * - Formulaire pour commander manuellement un produit donné.
 */
public class VueGestionCommandes extends BorderPane {
    private final TableView<Order> tableCommandes;

	public VueGestionCommandes() throws IOException {
        // Mise en forme générale
        this.setPadding(new Insets(15));

        HBox topBar = new HBox();
        topBar.setPadding(new Insets(10));
        topBar.setSpacing(10);
        topBar.setAlignment(Pos.CENTER_LEFT);

        // Titre
        Label titre = new Label("Gestion des commandes");
        titre.setFont(new Font("Arial", 24));

        Button boutonRetour = new Button("Retour Dashboard");
        boutonRetour.setOnAction(e -> Navigator.goToDashboard());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        topBar.getChildren().addAll(titre, spacer, boutonRetour);
        this.setTop(topBar);

        BorderPane.setMargin(titre, new Insets(0, 0, 10, 0));

        // Partie centrale : tableau des produits à commander
        tableCommandes = new TableView<>();
        TableColumn<Order, String> columnProduct = new TableColumn<>("Produit");
        columnProduct.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Order, Number> columnQuantity = new TableColumn<>("Quantité");
        columnQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        // TODO unit and total price of a row

        tableCommandes.getColumns().add(columnProduct);
        tableCommandes.getColumns().add(columnQuantity);
        tableCommandes.setPrefHeight(300);

        // Section basse : formulaire pour commander manuellement
        HBox formBox = getOrderForm();

        // Mise en page verticale
        VBox centerBox = new VBox(10, tableCommandes);

        this.setCenter(centerBox);
        this.setBottom(formBox);

        // Charger la liste initiale (simulée)
        tableCommandes.getItems().setAll(fetchOrders());
    }

    private HBox getOrderForm() {
        TextField champNomProduit = new TextField();
        champNomProduit.setPromptText("Nom du produit");

        TextField champQuantite = new TextField();
        champQuantite.setPromptText("Quantité");

        TextField champPrixUnitaire = new TextField();
        champPrixUnitaire.setPromptText("Prix unitaire");

        Button boutonAjouterCommande = new Button("Commander");
        boutonAjouterCommande.setOnAction(e -> {
            // ICI, appel HTTP pour ajouter une commande
            // ex: api.ajouterCommande(nomProduit, qte, prixUnit);
            String nom = champNomProduit.getText().trim();
            String qte = champQuantite.getText().trim();

            if (!nom.isEmpty() && !qte.isEmpty()) {
                try {
                    int quantiteInt = Integer.parseInt(qte);
                    Order cp = new Order(nom, quantiteInt);
                    tableCommandes.getItems().add(cp);
                    champNomProduit.clear();
                    champQuantite.clear();
                } catch (NumberFormatException nfe) {
                    // Mauvais format pour la quantité
                    System.err.println("Quantité ou prix invalide.");
                }
            }
        });

        HBox hBoxForm = new HBox(10, champNomProduit, champQuantite, champPrixUnitaire, boutonAjouterCommande);
        hBoxForm.setPadding(new Insets(10, 0, 0, 0));
        return hBoxForm;
    }

    private List<Order> fetchOrders() {
        return HTTPHelper.getList("http://localhost:25565/api/products/orders", Order.class);
    }
}

