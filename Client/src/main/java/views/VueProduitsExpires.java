package views;

import types.Product;
import com.fasterxml.jackson.core.type.TypeReference;
import javafx.beans.property.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import util.RequestHelper;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class VueProduitsExpires extends BorderPane {

    private TableView<Product> tableProduitsExpires;
    private TableView<Product> tableProduitsBientotExpires;

    private List<Product> products;

    public VueProduitsExpires() throws IOException, URISyntaxException {
        // Marges autour de la vue
        this.setPadding(new Insets(15));

        // -----------------------------
        // Barre du haut (title + spacer + bouton retour)
        // -----------------------------
        HBox topBar = new HBox();
        topBar.setPadding(new Insets(10));
        topBar.setSpacing(10);
        topBar.setAlignment(Pos.CENTER_LEFT); // Aligne verticalement les éléments au centre, le label à gauche

        Label titre = new Label("Vue des Products expirés");
        titre.setFont(new Font("Arial", 24));

        // Bouton retour en haut à droite
        Button boutonRetour = new Button("Retour Dashboard");
        boutonRetour.setOnAction(e -> Navigator.goToDashboard());

        // "Espacer" pour pousser le boutonRetour à droite
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        topBar.getChildren().addAll(titre, spacer, boutonRetour);
        this.setTop(topBar);

        // -----------------------------
        // Création des deux tables
        // -----------------------------
        tableProduitsExpires = new TableView<>();
        tableProduitsBientotExpires = new TableView<>();

        // Exemple minimal de colonnes
        TableColumn<Product, String> colNomExpire = new TableColumn<>("Nom du Product");
        TableColumn<Product, String> colDateExpire = new TableColumn<>("Date péremption");

        colNomExpire.setCellValueFactory(param ->
               new ReadOnlyObjectWrapper<>(param.getValue().name)
        );
//        colDateExpire.setCellValueFactory(param -> //TODO
//                new ReadOnlyObjectWrapper<>(param.getValue().)
//        );

        tableProduitsExpires.getColumns().addAll(colNomExpire, colDateExpire);

        TableColumn<Product, String> colNomBientot = new TableColumn<>("Nom du Product");
        TableColumn<Product, String> colDateBientot = new TableColumn<>("Date péremption");

        colNomBientot.setCellValueFactory(param ->
                new ReadOnlyObjectWrapper<>(param.getValue().name)
        );
//        colDateBientot.setCellValueFactory(param -> //TODO
//                new ReadOnlyObjectWrapper<>(param.getValue().date)
//        );

        tableProduitsBientotExpires.getColumns().addAll(colNomBientot, colDateBientot);

        Label labelExpires = new Label("Produits expirés :");
        Label labelBientotExpires = new Label("Produits bientôt expirés :");

        VBox vboxTables = new VBox(10,
                labelExpires,
                tableProduitsExpires,
                labelBientotExpires,
                tableProduitsBientotExpires
        );
        this.setCenter(vboxTables);

        // Simuler le chargement des données
        chargerDonnees();
    }

    private void chargerDonnees() throws IOException, URISyntaxException {

        //Exemple de connection à la bd pour récupérer les Products bientôt expirés
        HttpURLConnection connexion = RequestHelper.createConnexion(
                "http://localhost:25565/api/products/soonExpired",
                "GET");
        RequestHelper.sendRequest(connexion, 200);
        products = RequestHelper.parse(RequestHelper.getAnswer(connexion), new TypeReference<List<Product>>() {});
        //List<String> nomsProducts = products.stream().map(Product::getNom).toList(); //TODO
        //List<String> datesExpProducts = products.stream().map(Product::getDatePeremption).toList(); //TODO
        tableProduitsExpires.getItems().clear();
        tableProduitsBientotExpires.getItems().clear();
        //TODO continuer



        //GET : Pour la lecture
        //POST: Création d'un nouvel élément ou paramètres complexes
        //PUT: Mise à jour d'une donnée
        //Le mieux pour simplement afficher les Products expirés est GET

        //https://www.baeldung.com/java-http-request

        // Products expirés
        List<Product> ProductsExpires = new ArrayList<>();
        //ProductsExpires.add(new Product("Yaourt nature", LocalDate.now().minusDays(1)).toString());
        //ProductsExpires.add(new Product("Fromage frais", LocalDate.now().minusDays(3)).toString());

        // Products bientôt expirés
        List<Product> ProductsBientot = new ArrayList<>();
        //ProductsBientot.add(new Product("Jus d'orange", LocalDate.now().plusDays(2)).toString());

        // On remplit les tables
        tableProduitsExpires.getItems().setAll(ProductsExpires);
        tableProduitsBientotExpires.getItems().setAll(ProductsBientot);
    }

    // -----------------------------
    // Classe interne pour la démo
    // -----------------------------


//    public static class Product {
//        public final String nom;
//        public final String date;
//        public final int quantity;
//
//        public Product(String nom, String date, int quantity) {
//            this.nom = nom;
//            this.quantity = quantity;
//            this.date = date;
//        }
//
//        public String getNom() {
//            return nom;
//        }
//
//        public String getDatePeremption() {
//            return date;
//        }
//    }
}
