package stockmanager.client.views;

import stockmanager.types.Product;
import com.fasterxml.jackson.core.type.TypeReference;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import stockmanager.client.util.RequestHelper;

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

    // Champ de recherche
    private TextField champRecherche;
    // Table pour afficher la liste des produits
    private TableView<Product> tableProduits;
    // Zone de saisie de quantité
    private TextField champQuantite;
    // Boutons d'action
    private Button boutonVente;
    private Button boutonDechet;

    private List<Product> produits;

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
        boutonRetour.setOnAction(e -> stockmanager.client.views.Navigator.goToDashboard());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        topBar.getChildren().addAll(titre, spacer, boutonRetour);
        this.setTop(topBar);

        BorderPane.setMargin(titre, new Insets(0, 0, 20, 0));

        // Création du champ de recherche
        champRecherche = new TextField();
        champRecherche.setPromptText("Rechercher un produit...");
        champRecherche.setPrefWidth(200);
        champRecherche.textProperty().addListener((observable, oldValue, newValue) -> {
            // Ici, vous pourrez lancer la requête HTTP pour rechercher les produits correspondants
            // ou filtrer localement si vous avez déjà chargé les produits en mémoire.
            filtrerProduits(newValue);
        });

        // Table des produits
        tableProduits = new TableView<>();
        TableColumn<Product, String> colNom = new TableColumn<>("Nom du produit");
        colNom.setCellValueFactory(param ->
                new ReadOnlyObjectWrapper<>(param.getValue().name)
        );


        TableColumn<Product, Integer> colStock = new TableColumn<>("Stock");
//        colStock.setCellValueFactory(param -> //TODO
//                new ReadOnlyObjectWrapper<>(param.getValue().stock)
//        );

        TableColumn<Product, String> colPeremption = new TableColumn<>("Date de péremption");
//        colPeremption.setCellValueFactory(param -> //TODO
//                new ReadOnlyObjectWrapper<>(param.getValue().date)
//        );

        tableProduits.getColumns().addAll(colNom, colStock, colPeremption);
        tableProduits.setPrefHeight(300);

        // Partie formulaire : saisir quantité, puis boutons Vente ou Déchet
        champQuantite = new TextField();
        champQuantite.setPromptText("Quantité");

        boutonVente = new Button("Enregistrer la vente");
        boutonVente.setOnAction(event -> {
            // Ici, vous ferez l'appel HTTP pour soustraire la quantité vendue du stock
            Product produitSelectionne = tableProduits.getSelectionModel().getSelectedItem();
            if (produitSelectionne != null) {
                try {
                    int quantiteV = Integer.parseInt(champQuantite.getText());
                    // Appel HTTP pour valider la vente
                    // ex: api.vendreProduit(produitSelectionne.getId(), quantiteV);
                    System.out.println("Vente de " + quantiteV + " unités de " + produitSelectionne.name);
                } catch (NumberFormatException e) {
                    System.err.println("Quantité invalide.");
                }
            }
        });

        boutonDechet = new Button("Signaler en déchet");
        boutonDechet.setOnAction(event -> {
            // Ici, vous ferez l'appel HTTP pour signaler la perte de cette quantité en tant que déchet
            Product produitSelectionne = tableProduits.getSelectionModel().getSelectedItem();
            if (produitSelectionne != null) {
                try {
                    int quantiteD = Integer.parseInt(champQuantite.getText());
                    // Appel HTTP pour signaler que cette quantité est jetée
                    // ex: api.jeterProduit(produitSelectionne.getId(), quantiteD);
                    System.out.println("Jeter " + quantiteD + " unités de " + produitSelectionne.name);
                } catch (NumberFormatException e) {
                    System.err.println("Quantité invalide.");
                }
            }
        });

        HBox hBoxForm = new HBox(10);
        hBoxForm.getChildren().addAll(new Label("Quantité :"), champQuantite, boutonVente, boutonDechet);
        hBoxForm.setPadding(new Insets(10, 0, 0, 0));

        // Disposition verticale : Champ recherche, Table, Formulaire
        VBox vboxCenter = new VBox(10);
        vboxCenter.getChildren().addAll(champRecherche, tableProduits, hBoxForm);

        this.setCenter(vboxCenter);

        // Chargement initial des produits (simulé ici en dur)
        chargerProduitsParDefaut();
    }

    /**
     * Méthode pour charger une liste initiale de produits (fictive).
     * À remplacer par un appel HTTP pour récupérer les produits depuis l'API.
     */
    private void chargerProduitsParDefaut() throws IOException, URISyntaxException {
        //Exemple de connection à la bd pour récupérer les produits bientôt expirés
        HttpURLConnection connexion = RequestHelper.createConnexion(
                "http://localhost:25565/api/products/all",
                "GET");
        RequestHelper.sendRequest(connexion, 200);
        produits = RequestHelper.parse(RequestHelper.getAnswer(connexion), new TypeReference<List<Product>>() {});
        tableProduits.getItems().clear();
        tableProduits.getItems().addAll(produits);
    }

    /**
     * Méthode pour filtrer les produits affichés dans le tableau,
     * selon la chaîne de recherche passée en paramètre.
     */
    private void filtrerProduits(String searchTerm) {
        // Ici, on pourrait refaire un appel HTTP pour obtenir la liste filtrée.
        // En attendant, on simule simplement un filtrage local sur la liste affichée.
        List<Product> productAmountActuels = new ArrayList<>(tableProduits.getItems());
        List<Product> resultats = new ArrayList<>();

        for (Product p : productAmountActuels) {
            if (p.name.toLowerCase().contains(searchTerm.toLowerCase())) {
                resultats.add(p);
            }
        }

        tableProduits.getItems().setAll(resultats);
    }

//    /**
//     * Classe interne représentant un produit.
//     * On utilise des propriétés JavaFX (StringProperty, IntegerProperty...) pour faciliter le binding avec la TableView.
//     */
//    public static class ProductAmount {
//        public String date;
//        public long code;
//        public int sold;
//        public int thrown;
//
//        public ProductAmount() {
//            code = 0;
//            date = null;
//            sold = 0;
//            thrown = 0;
//        }
//
//        public ProductAmount(String date, long code, int sold, int thrown) {
//            this.code = code;
//            this.date = date;
//            this.sold = sold;
//            this.thrown = thrown;
//        }
//    }

}
