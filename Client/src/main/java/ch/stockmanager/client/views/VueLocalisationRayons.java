package ch.stockmanager.client.views;

import com.fasterxml.jackson.core.type.TypeReference;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import ch.stockmanager.client.util.RequestHelper;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
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
    private List<Rayon> listeRayons;
    private ListView<String> listeViewRayons;
    private TextField champNouveauRayon;
    private TextField champRenommerRayon;

    public VueLocalisationRayons() throws IOException {
        // Mise en page
        this.setPadding(new Insets(15));

        HBox topBar = new HBox();
        topBar.setPadding(new Insets(10));
        topBar.setSpacing(10);
        topBar.setAlignment(Pos.CENTER_LEFT);
        // -----------------------------
        // Titre principal
        // -----------------------------
        Label titre = new Label("Localisation des produits & Gestion des rayons");
        titre.setFont(new Font("Arial", 20));

        Button boutonRetour = new Button("Retour Dashboard");
        boutonRetour.setOnAction(e -> Navigator.goToDashboard());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        topBar.getChildren().addAll(titre, spacer, boutonRetour);
        this.setTop(topBar);

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

        listeViewRayons = new ListView<>();
        listeViewRayons.setPrefHeight(200);

        // Champ pour ajouter un nouveau rayon
        champNouveauRayon = new TextField();
        champNouveauRayon.setPromptText("Nouveau rayon");
        Button boutonAjouterRayon = new Button("Ajouter Rayon");
        boutonAjouterRayon.setOnAction(e -> {
            String nouveauRayon = champNouveauRayon.getText().trim();
            if (!nouveauRayon.isEmpty()) {
                createRayon(nouveauRayon);
                champNouveauRayon.clear();
            }
        });

        // Champ pour renommer un rayon existant
        champRenommerRayon = new TextField();
        champRenommerRayon.setPromptText("Nouveau nom pour le rayon sélectionné");
        Button boutonRenommerRayon = new Button("Renommer Rayon");
        boutonRenommerRayon.setOnAction(e -> {
            String rayonSelectionne = listeViewRayons.getSelectionModel().getSelectedItem();
            String nouveauNomRayon = champRenommerRayon.getText().trim();
            if (rayonSelectionne != null && !nouveauNomRayon.isEmpty()) {
                int index = listeViewRayons.getSelectionModel().getSelectedIndex();
                updateRayon(rayonSelectionne, nouveauNomRayon);
                listeViewRayons.getItems().set(index, nouveauNomRayon);
                champRenommerRayon.clear();
            }
        });

        // Bouton pour supprimer un rayon
        Button boutonSupprimerRayon = new Button("Supprimer Rayon");
        boutonSupprimerRayon.setOnAction(e -> {
            String rayonSelectionne = listeViewRayons.getSelectionModel().getSelectedItem();
            if (rayonSelectionne != null) {
                // ICI, appel HTTP pour supprimer le rayon dans la BD
                // ex: api.supprimerRayon(rayonSelectionne);
                deleteRayon(rayonSelectionne);
            }
        });

        // Disposition pour la gestion des rayons
        HBox hboxNouvelleRayon = new HBox(5, champNouveauRayon, boutonAjouterRayon);
        HBox hboxRenommerRayon = new HBox(5, champRenommerRayon, boutonRenommerRayon);
        VBox vboxActionsRayons = new VBox(10, hboxNouvelleRayon, hboxRenommerRayon, boutonSupprimerRayon);

        vboxDroite.getChildren().addAll(labelRayons, listeViewRayons, vboxActionsRayons);

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
        try {
            chargerRayons();
            chargerProduitsParDefaut();
        } catch (URISyntaxException e) {
            //TODO: handle connexion errors with the server
            System.out.println("Error while contacting the server : " + e.getMessage());
        }
    }

    /**
     * Simulation de chargement initial des rayons.
     * À remplacer par un appel HTTP récupérant la liste des rayons depuis l'API.
     */
    private void chargerRayons() throws IOException, URISyntaxException {
        HttpURLConnection connexion = RequestHelper.createConnexion(
                "http://localhost:25565/api/shelves",
                "GET");
        RequestHelper.sendRequest(connexion, 200);
        listeRayons = RequestHelper.parse(RequestHelper.getAnswer(connexion), new TypeReference<List<Rayon>>() {});
        List<String> nomsRayons = listeRayons.stream().map(Rayon::getNom).toList();
        listeViewRayons.getItems().clear();
        listeViewRayons.getItems().addAll(nomsRayons);
    }

    private void createRayon(String nom) {
        try {
            HttpURLConnection connexion = RequestHelper.createConnexion(
                    "http://localhost:25565/api/shelves",
                    "POST");
            Rayon r = Rayon.forInsertion(nom);
            RequestHelper.loadJson(connexion, r);
            RequestHelper.sendRequest(connexion, 201);
            chargerRayons(); // to have the new rayon with his index
        }
        catch (Exception e) {
            // TODO: handle the exception here ?
            System.out.println("Error in rayon creation : " + e);
            for (var a : e.getStackTrace()) { System.out.println(a); }
        }
    }

    private void deleteRayon(String nom) {
        try {
            int id = getId(nom);
            HttpURLConnection connexion = RequestHelper.createConnexion(
                    "http://localhost:25565/api/shelves/" + id,
                    "DELETE");
            RequestHelper.sendRequest(connexion, 200);
            listeRayons.removeIf(r -> r.getId() == id);
            listeViewRayons.getItems().remove(nom); // only if code is 200, else an Exception has been thrown
        }
        catch (Exception e) {
            // TODO: handle the exception here ?
            System.out.println("Error in rayon creation : " + e);
            for (var a : e.getStackTrace()) { System.out.println(a); }
        }
    }

    private void updateRayon(String oldName, String newName) {
        try {
            HttpURLConnection connexion = RequestHelper.createConnexion(
                    "http://localhost:25565/api/shelves/"+getId(oldName),
                    "PUT");
            Rayon r = Rayon.forInsertion(newName);
            RequestHelper.loadJson(connexion, r);
            RequestHelper.sendRequest(connexion, 200);

        }
        catch (Exception e) {
            // TODO: handle the exception here ?
            System.out.println("Error in rayon creation : " + e);
            for (var a : e.getStackTrace()) { System.out.println(a); }
        }
    }

    private int getId(String nom) {
        return listeRayons.stream()
            .filter(r -> r.getNom().equals(nom))
            .map(Rayon::getId)
            .findFirst().orElseThrow();
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

    public static class Rayon {
        private final SimpleIntegerProperty id;
        private final SimpleStringProperty nom;
        private final SimpleBooleanProperty estStock;

        // Default constructor for Jackson
        public Rayon() {
            this.id = new SimpleIntegerProperty();
            this.nom = new SimpleStringProperty();
            this.estStock = new SimpleBooleanProperty();
        }

        public Rayon(int id, String nom, boolean estStock) {
            this.id = new SimpleIntegerProperty(id);
            this.nom = new SimpleStringProperty(nom);
            this.estStock = new SimpleBooleanProperty(estStock);
        }

        public static Rayon forInsertion (String nom) { return new Rayon(0, nom, false); }

        public int getId() { return id.get(); }
        //public boolean isEstStock() { return estStock.get(); }
        public String getNom() { return nom.get(); }
        public void setId(int id) { this.id.set(id); }
        public void setNom(String nom) { this.nom.set(nom); }
        public void setEstStock(boolean estStock) { this.estStock.set(estStock); }
        //public SimpleIntegerProperty idProperty() { return id; }
        //public SimpleStringProperty nomProperty() { return nom; }
        //public SimpleBooleanProperty estStockProperty() { return estStock; }

        @Override
        public String toString() {
            return id.get() + " : " + nom.get() + ((estStock.get()) ? " (stock)" : "");
        }
    }

}

