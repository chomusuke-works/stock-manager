package views;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class VueProduitsExpires extends BorderPane {

    private TableView<Produit> tableProduitsExpires;
    private TableView<Produit> tableProduitsBientotExpires;

    public VueProduitsExpires() {
        // Marges autour de la vue
        this.setPadding(new Insets(15));

        // -----------------------------
        // Barre du haut (title + spacer + bouton retour)
        // -----------------------------
        HBox topBar = new HBox();
        topBar.setPadding(new Insets(10));
        topBar.setSpacing(10);
        topBar.setAlignment(Pos.CENTER_LEFT); // Aligne verticalement les éléments au centre, le label à gauche

        Label titre = new Label("Vue des produits expirés");
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
        TableColumn<Produit, String> colNomExpire = new TableColumn<>("Nom du produit");
        TableColumn<Produit, LocalDate> colDateExpire = new TableColumn<>("Date péremption");

        colNomExpire.setCellValueFactory(param -> param.getValue().nomProperty());
        colDateExpire.setCellValueFactory(param -> param.getValue().datePeremptionProperty());

        tableProduitsExpires.getColumns().addAll(colNomExpire, colDateExpire);

        TableColumn<Produit, String> colNomBientot = new TableColumn<>("Nom du produit");
        TableColumn<Produit, LocalDate> colDateBientot = new TableColumn<>("Date péremption");

        colNomBientot.setCellValueFactory(param -> param.getValue().nomProperty());
        colDateBientot.setCellValueFactory(param -> param.getValue().datePeremptionProperty());

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

    private void chargerDonnees() {
        // Produits expirés
        List<Produit> produitsExpires = new ArrayList<>();
        produitsExpires.add(new Produit("Yaourt nature", LocalDate.now().minusDays(1)));
        produitsExpires.add(new Produit("Fromage frais", LocalDate.now().minusDays(3)));

        // Produits bientôt expirés
        List<Produit> produitsBientot = new ArrayList<>();
        produitsBientot.add(new Produit("Jus d'orange", LocalDate.now().plusDays(2)));

        // On remplit les tables
        tableProduitsExpires.getItems().setAll(produitsExpires);
        tableProduitsBientotExpires.getItems().setAll(produitsBientot);
    }

    // -----------------------------
    // Classe interne pour la démo
    // -----------------------------


    public static class Produit {
        private final StringProperty nom;
        private final ObjectProperty<LocalDate> datePeremption;

        public Produit(String nom, LocalDate datePeremption) {
            this.nom = new SimpleStringProperty(nom);
            this.datePeremption = new SimpleObjectProperty<>(datePeremption);
        }

        public String getNom() {
            return nom.get();
        }
        public StringProperty nomProperty() {
            return nom;
        }

        public LocalDate getDatePeremption() {
            return datePeremption.get();
        }
        public ObjectProperty<LocalDate> datePeremptionProperty() {
            return datePeremption;
        }
    }
}
