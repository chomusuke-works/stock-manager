package views;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;

import java.util.ArrayList;
import java.util.List;

/**
 * Vue pour afficher la localisation des produits et gérer les rayons.
 * - À gauche : recherche + tableau des produits (nom, stock, rayon).
 * - À droite : liste des rayons, avec possibilité d'en ajouter, de renommer ou de supprimer.
 */
public class VueLocalisationRayons extends BorderPane {

    // -----------------------------
    // SECTION GAUCHE : Produits
    // -----------------------------
    private TextField champRecherche;
    private TableView<Produit> tableProduits;

    // -----------------------------
    // SECTION DROITE : Rayons
    // -----------------------------
    private ListView<String> listeRayons;
    private TextField champNouveauRayon;
    private TextField champRenommerRayon;

    public VueLocalisationRayons() {
        // Mise en page
        this.setPadding(new Insets(15));

        // -----------------------------
        // Titre principal
        // -----------------------------
        Label titre = new Label("Localisation des produits & Gestion des rayons");
        titre.setFont(new Font("Arial", 20));

        Button boutonRetour = new Button("Retour Dashboard");
        boutonRetour.setOnAction(e -> Navigator.goToDashboard());

        this.setTop(titre);
        BorderPane.setMargin(titre, new Insets(0, 0, 10, 0));

        // -----------------------------
        // SECTION GAUCHE
        // -----------------------------
        VBox vboxGauche = new VBox(10);
        vboxGauche.setPadding(new Insets(10));

        // Champ de recherche
        champRecherche = new TextField();
        champRecherche.setPromptText("Rechercher un produit...");
        champRecherche.textProperty().addListener((obs, oldValue, newValue) -> {
            // ICI, vous ferez votre appel HTTP ou filtrage local
            filtrerProduits(newValue);
        });

        // TableView des produits
        tableProduits = new TableView<>();
        TableColumn<Produit, String> colNom = new TableColumn<>("Nom");
        colNom.setCellValueFactory(cel -> cel.getValue().nomProperty());

        TableColumn<Produit, Number> colStock = new TableColumn<>("Stock");
        colStock.setCellValueFactory(cel -> cel.getValue().stockProperty());

        TableColumn<Produit, String> colRayon = new TableColumn<>("Rayon");
        colRayon.setCellValueFactory(cel -> cel.getValue().rayonProperty());

        tableProduits.getColumns().addAll(colNom, colStock, colRayon);
        tableProduits.setPrefHeight(400);

        // Ajout des éléments au vbox de gauche
        vboxGauche.getChildren().addAll(champRecherche, tableProduits);

        // -----------------------------
        // SECTION DROITE
        // -----------------------------
        VBox vboxDroite = new VBox(10);
        vboxDroite.setPadding(new Insets(10));

        Label labelRayons = new Label("Liste des rayons :");
        labelRayons.setFont(new Font("Arial", 14));

        listeRayons = new ListView<>();
        listeRayons.setPrefHeight(200);

        // Champ pour ajouter un nouveau rayon
        champNouveauRayon = new TextField();
        champNouveauRayon.setPromptText("Nouveau rayon");
        Button boutonAjouterRayon = new Button("Ajouter Rayon");
        boutonAjouterRayon.setOnAction(e -> {
            String nouveauRayon = champNouveauRayon.getText().trim();
            if (!nouveauRayon.isEmpty()) {
                // ICI, on pourrait faire un appel HTTP pour ajouter le rayon à la base
                // ex: api.ajouterRayon(nouveauRayon);
                // On met ensuite à jour la liste localement
                listeRayons.getItems().add(nouveauRayon);
                champNouveauRayon.clear();
            }
        });

        // Champ pour renommer un rayon existant
        champRenommerRayon = new TextField();
        champRenommerRayon.setPromptText("Nouveau nom pour le rayon sélectionné");
        Button boutonRenommerRayon = new Button("Renommer Rayon");
        boutonRenommerRayon.setOnAction(e -> {
            String rayonSelectionne = listeRayons.getSelectionModel().getSelectedItem();
            String nouveauNomRayon = champRenommerRayon.getText().trim();
            if (rayonSelectionne != null && !nouveauNomRayon.isEmpty()) {
                // ICI, appel HTTP pour renommer le rayon côté backend
                // ex: api.renommerRayon(rayonSelectionne, nouveauNomRayon);
                // Mise à jour dans la ListView
                int index = listeRayons.getSelectionModel().getSelectedIndex();
                listeRayons.getItems().set(index, nouveauNomRayon);
                champRenommerRayon.clear();
            }
        });

        // Bouton pour supprimer un rayon
        Button boutonSupprimerRayon = new Button("Supprimer Rayon");
        boutonSupprimerRayon.setOnAction(e -> {
            String rayonSelectionne = listeRayons.getSelectionModel().getSelectedItem();
            if (rayonSelectionne != null) {
                // ICI, appel HTTP pour supprimer le rayon dans la BD
                // ex: api.supprimerRayon(rayonSelectionne);
                listeRayons.getItems().remove(rayonSelectionne);
            }
        });

        // Disposition pour la gestion des rayons
        HBox hboxNouvelleRayon = new HBox(5, champNouveauRayon, boutonAjouterRayon);
        HBox hboxRenommerRayon = new HBox(5, champRenommerRayon, boutonRenommerRayon);
        VBox vboxActionsRayons = new VBox(10, hboxNouvelleRayon, hboxRenommerRayon, boutonSupprimerRayon);

        vboxDroite.getChildren().addAll(labelRayons, listeRayons, vboxActionsRayons, boutonRetour);

        // -----------------------------
        // Placement final
        // -----------------------------
        SplitPane splitPane = new SplitPane();
        splitPane.getItems().addAll(vboxGauche, vboxDroite);
        splitPane.setDividerPositions(0.6);  // 60% / 40%

        this.setCenter(splitPane);

        // -----------------------------
        // Initialisation de la vue
        // -----------------------------
        chargerRayonsParDefaut();
        chargerProduitsParDefaut();
    }

    /**
     * Simulation de chargement initial des rayons.
     * À remplacer par un appel HTTP récupérant la liste des rayons depuis l'API.
     */
    private void chargerRayonsParDefaut() {
        List<String> rayonsExemple = new ArrayList<>();
        rayonsExemple.add("Frais");
        rayonsExemple.add("Fruits & Légumes");
        rayonsExemple.add("Boissons");
        rayonsExemple.add("Épicerie");
        rayonsExemple.add("Surgelés");

        listeRayons.getItems().addAll(rayonsExemple);
    }

    /**
     * Simulation de chargement initial des produits.
     * À remplacer par un appel HTTP pour récupérer la liste des produits depuis l'API.
     */
    private void chargerProduitsParDefaut() {
        List<Produit> produits = new ArrayList<>();
        produits.add(new Produit("Yaourt nature", 80, "Frais"));
        produits.add(new Produit("Pomme", 120, "Fruits & Légumes"));
        produits.add(new Produit("Coca-Cola", 50, "Boissons"));
        produits.add(new Produit("Pâtes", 200, "Épicerie"));
        produits.add(new Produit("Glace au chocolat", 30, "Surgelés"));

        tableProduits.getItems().setAll(produits);
    }

    /**
     * Filtrer les produits affichés en fonction du texte saisi dans la barre de recherche.
     * Simulation locale pour l'instant, peut être remplacée par un vrai appel HTTP.
     */
    private void filtrerProduits(String termeRecherche) {
        List<Produit> listeComplete = new ArrayList<>(tableProduits.getItems());
        // On pourrait aussi stocker la liste initiale en variable globale pour filtrer correctement
        // Dans cet exemple, on la recharge pour simplifier la logique
        chargerProduitsParDefaut();
        List<Produit> produitsActuels = new ArrayList<>(tableProduits.getItems());

        if (termeRecherche == null || termeRecherche.trim().isEmpty()) {
            return;
        }
        termeRecherche = termeRecherche.toLowerCase();

        List<Produit> resultats = new ArrayList<>();
        for (Produit p : produitsActuels) {
            if (p.getNom().toLowerCase().contains(termeRecherche)) {
                resultats.add(p);
            }
        }
        tableProduits.getItems().setAll(resultats);
    }

    /**
     * Classe interne représentant un produit avec :
     * - nom
     * - stock
     * - nom du rayon
     */
    public static class Produit {
        private final SimpleStringProperty nom;
        private final SimpleIntegerProperty stock;
        private final SimpleStringProperty rayon;

        public Produit(String nom, int stock, String rayon) {
            this.nom = new SimpleStringProperty(nom);
            this.stock = new SimpleIntegerProperty(stock);
            this.rayon = new SimpleStringProperty(rayon);
        }

        public String getNom() {
            return nom.get();
        }
        public SimpleStringProperty nomProperty() {
            return nom;
        }

        public int getStock() {
            return stock.get();
        }
        public SimpleIntegerProperty stockProperty() {
            return stock;
        }

        public String getRayon() {
            return rayon.get();
        }
        public SimpleStringProperty rayonProperty() {
            return rayon;
        }
    }
}
