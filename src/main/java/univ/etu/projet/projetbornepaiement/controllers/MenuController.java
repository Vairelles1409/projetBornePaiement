package univ.etu.projet.projetbornepaiement.controllers;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.hibernate.Session;
import univ.etu.projet.projetbornepaiement.BorneApplication;
import univ.etu.projet.projetbornepaiement.SceneManager;
import univ.etu.projet.projetbornepaiement.models.Carte;
import univ.etu.projet.projetbornepaiement.models.Plat;       // Notre Entité Hibernate
import univ.etu.projet.projetbornepaiement.models.TypeProduit; // Notre Enum
import univ.etu.projet.projetbornepaiement.utils.HibernateUtil; // Notre utilitaire BDD

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class MenuController {

    @FXML private VBox menuContainer; // Assure-toi que c'est bien une VBox dans le FXML

    // Éléments du Mini-Panier
    @FXML private VBox miniCartContainer;
    @FXML private Label miniTotalLabel;

    // On utilise maintenant une liste de "Plat"
    private final List<Plat> productList = new ArrayList<>();

    @FXML
    public void initialize() {
        // 1. Chargement depuis la Base de Données (Hibernate)
        loadProductsFromDB();

        // 2. Génération de l'affichage
        generateMenuBlocks();

        // 3. Affichage du panier existant
        refreshMiniCart();
    }

    private void loadProductsFromDB() {
        productList.clear();
        System.out.println("Connexion à la base de données...");

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Requête HQL pour récupérer tous les plats
            List<Plat> platsBDD = session.createQuery("from Plat", Plat.class).list();

            if (platsBDD.isEmpty()) {
                System.out.println("⚠️ Attention : La table 'plats' est vide.");
            } else {
                productList.addAll(platsBDD);
                System.out.println("✅ " + platsBDD.size() + " plats chargés depuis la BDD.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("❌ Erreur critique : Impossible de charger le menu depuis la base.");
        }
    }

    /**
     * Génère l'affichage dynamique avec des Titres et des Grilles par catégorie.
     */
    private void generateMenuBlocks() {
        menuContainer.getChildren().clear();

        // 1. Grouper les plats par Type (MENU, PLAT, BOISSON...)
        Map<TypeProduit, List<Plat>> productsByCategory = productList.stream()
                .collect(Collectors.groupingBy(Plat::getType));

        // 2. Parcourir l'Enum pour garder un ordre logique d'affichage
        for (TypeProduit type : TypeProduit.values()) {

            if (productsByCategory.containsKey(type)) {
                List<Plat> productsInThisCategory = productsByCategory.get(type);

                // A. Titre de section
                Label sectionTitle = new Label(type.getLabel()); // Utilise le libellé de l'Enum (ex: "Nos Menus")
                sectionTitle.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #333; -fx-padding: 0 0 10 0;");

                // B. Grille pour cette section
                GridPane sectionGrid = new GridPane();
                sectionGrid.setHgap(15);
                sectionGrid.setVgap(15);

                int col = 0;
                int row = 0;
                for (Plat p : productsInThisCategory) {
                    sectionGrid.add(createProductBox(p), col, row);
                    col++;
                    if (col == 5) { // x colonnes par ligne
                        col = 0;
                        row++;
                    }
                }

                // C. Ajout au conteneur principal
                VBox sectionBox = new VBox(5);
                sectionBox.getChildren().addAll(sectionTitle, sectionGrid);
                menuContainer.getChildren().add(sectionBox);

                // Espacement visuel
                Region separator = new Region();
                separator.setPrefHeight(20);
                menuContainer.getChildren().add(separator);
            }
        }
    }

    private VBox createProductBox(Plat plat) {
        VBox box = new VBox(10);
        // Style de base : Blanc, Ombre légère, Curseur main
        box.setStyle("-fx-background-color: white; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 0); -fx-background-radius: 10; -fx-padding: 15; -fx-cursor: hand;");
        box.setAlignment(Pos.CENTER);
        box.setPrefWidth(200);

        // 1. Nom
        Label nameLabel = new Label(plat.getName());
        nameLabel.setWrapText(true);
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-alignment: center;");

        // 2. Image
        ImageView imageView = new ImageView();
        imageView.setFitHeight(120);
        imageView.setFitWidth(160);
        imageView.setPreserveRatio(true);

        try {
            // Chargement robuste de l'image
            String imagePath = "/img/" + plat.getImagePath().trim();
            URL url = BorneApplication.class.getResource(imagePath); // Utilise ta classe Main ici

            if (url != null) {
                imageView.setImage(new Image(url.toExternalForm()));
            } else {
                // Image par défaut si introuvable
                // System.err.println("Image introuvable: " + imagePath);
            }
        } catch (Exception e) { e.printStackTrace(); }

        // 3. Prix
        Label priceLabel = new Label(String.format("%.2f €", plat.getPrice()));
        priceLabel.setStyle("-fx-text-fill: #E91E63; -fx-font-weight: bold; -fx-font-size: 16px;");

        // 4. ACTION AU CLIC (Animation + Ajout)
        box.setOnMouseClicked(event -> {
            System.out.println("Ajout : " + plat.getName());

            // Lancer l'animation
            animateProductSelection(box);

            // Logique métier
            Carte.getInstance().addProduct(plat);
            refreshMiniCart();
        });

        box.getChildren().addAll(nameLabel, imageView, priceLabel);
        return box;
    }
    /*private VBox createProductBox(Plat plat) {
        VBox box = new VBox(10);
        box.setStyle("-fx-background-color: white; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 0); -fx-background-radius: 10; -fx-padding: 15;");
        box.setAlignment(Pos.CENTER);
        box.setPrefWidth(200);

        // NOM
        Label nameLabel = new Label(plat.getName());
        nameLabel.setWrapText(true);
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-alignment: center;");

        // IMAGE
        ImageView imageView = new ImageView();
        imageView.setFitHeight(100);
        imageView.setFitWidth(150);
        imageView.setPreserveRatio(true);

        // Chargement de l'image
        try {
            // On récupère le nom depuis la BDD
            String fileName = plat.getImagePath();

            // On construit le chemin relatif vers le dossier resources/images
            String fullPath = "/img/" + fileName;
            java.net.URL imageURL = getClass().getResource(fullPath);

            if (imageURL != null) {
                imageView.setImage(new Image(imageURL.toExternalForm()));
            } else {
                System.err.println("Image introuvable : " + fullPath);
                java.net.URL fallback = getClass().getResource("/img/placeholder.png");
                if (fallback != null) {
                    imageView.setImage(new Image(fallback.toExternalForm()));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        // PRIX
        Label priceLabel = new Label(String.format("%.2f €", plat.getPrice()));
        priceLabel.setStyle("-fx-text-fill: #E91E63; -fx-font-weight: bold; -fx-font-size: 14px;");

        // BOUTON AJOUTER
        Button addButton = new Button("Ajouter");
        addButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-cursor: hand;");
        addButton.setOnAction(e -> {
            Carte.getInstance().addProduct(plat);
            refreshMiniCart();
        });

        box.getChildren().addAll(nameLabel, imageView, priceLabel, addButton);
        return box;
    }*/

    // --- LOGIQUE DU MINI PANIER ---

    private void refreshMiniCart() {
        miniCartContainer.getChildren().clear();
        Map<Plat, Integer> items = Carte.getInstance().getItems();

        for (Map.Entry<Plat, Integer> entry : items.entrySet()) {
            miniCartContainer.getChildren().add(createMiniCartRow(entry.getKey(), entry.getValue()));
        }

        miniTotalLabel.setText(String.format("%.2f €", Carte.getInstance().getTotal()));
    }

    private HBox createMiniCartRow(Plat p, int qty) {
        HBox row = new HBox(5);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle("-fx-border-color: #eee; -fx-border-width: 0 0 1 0; -fx-padding: 5;");

        // Infos
        VBox infoBox = new VBox(2);
        Label nameLbl = new Label(p.getName());
        nameLbl.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");
        nameLbl.setMaxWidth(120);
        Label priceLbl = new Label(String.format("%.2f €", p.getPrice() * qty));
        priceLbl.setStyle("-fx-font-size: 10px; -fx-text-fill: gray;");
        infoBox.getChildren().addAll(nameLbl, priceLbl);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // --- BOUTON MOINS (Gère la suppression) ---
        Button minusBtn = new Button("-");
        minusBtn.setStyle("-fx-font-size: 10px; -fx-min-width: 25px; -fx-cursor: hand;");
        minusBtn.setOnAction(e -> {
            // La méthode removeProduct de Carte gère déjà : si qty=1 -> supprime
            Carte.getInstance().removeProduct(p);
            refreshMiniCart(); // Rafraîchit l'affichage (la ligne disparaîtra si qty était 1)
        });

        Label qtyLbl = new Label(String.valueOf(qty));
        qtyLbl.setMinWidth(20);
        qtyLbl.setAlignment(Pos.CENTER);
        qtyLbl.setStyle("-fx-font-size: 12px;");

        // --- BOUTON PLUS ---
        Button plusBtn = new Button("+");
        plusBtn.setStyle("-fx-font-size: 10px; -fx-min-width: 25px; -fx-cursor: hand;");
        plusBtn.setOnAction(e -> {
            Carte.getInstance().addProduct(p);
            refreshMiniCart();
        });

        // ON NE MET PLUS LE BOUTON DELETE (deleteBtn)

        row.getChildren().addAll(infoBox, spacer, minusBtn, qtyLbl, plusBtn);
        return row;
    }

    @FXML
    private void handleBack() throws IOException {
        SceneManager.setRoot("welcome-view.fxml");
    }

    @FXML
    private void handleGoToCart() throws IOException {
        SceneManager.setRoot("carte-view.fxml"); // Je remets cart-view (nom standard)
    }

    /**
     * Anime la boîte produit : petit effet de recul + lueur verte.
     */
    private void animateProductSelection(VBox box) {
        // 1. Création de l'effet "Lumière" (Glow)
        DropShadow glow = new DropShadow();
        glow.setColor(Color.LIMEGREEN); // Couleur de la lumière
        glow.setRadius(0); // Au début, pas de rayon
        glow.setSpread(0.5); // Intensité de la lumière

        // On applique l'effet à la boîte
        box.setEffect(glow);

        // 2. Création de la Timeline pour animer les propriétés
        Timeline timeline = new Timeline();

        // Étape A : État initial (0ms)
        KeyFrame kf0 = new KeyFrame(Duration.ZERO,
                new KeyValue(box.scaleXProperty(), 1.0),
                new KeyValue(box.scaleYProperty(), 1.0),
                new KeyValue(glow.radiusProperty(), 0)
        );

        // Étape B : Enfoncement + Lumière max (100ms)
        KeyFrame kf1 = new KeyFrame(Duration.millis(100),
                new KeyValue(box.scaleXProperty(), 0.95), // Rétrécit légèrement (clic)
                new KeyValue(box.scaleYProperty(), 0.95),
                new KeyValue(glow.radiusProperty(), 30)   // La lumière brille fort
        );

        // Étape C : Retour à la normale (300ms)
        KeyFrame kf2 = new KeyFrame(Duration.millis(300),
                new KeyValue(box.scaleXProperty(), 1.0),
                new KeyValue(box.scaleYProperty(), 1.0),
                new KeyValue(glow.radiusProperty(), 0)    // La lumière s'éteint
        );

        timeline.getKeyFrames().addAll(kf0, kf1, kf2);

        // 3. À la fin, on remet l'ombre portée grise d'origine pour le style
        timeline.setOnFinished(event -> {
            box.setEffect(new DropShadow(BlurType.THREE_PASS_BOX, Color.rgb(0,0,0,0.1), 5, 0, 0, 0));
        });

        timeline.play();
    }
}