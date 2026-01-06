package univ.etu.projet.projetbornepaiement.controllers;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import univ.etu.projet.projetbornepaiement.SceneManager;
import univ.etu.projet.projetbornepaiement.models.Carte;
import univ.etu.projet.projetbornepaiement.models.Produit;

import java.io.IOException;
import java.util.Map;

public class CarteController {

    @FXML private VBox cartContainer;
    @FXML private Label totalLabel;
    @FXML private TextField couponField;

    @FXML
    public void initialize() {
        refreshCartDisplay();
    }

    private void refreshCartDisplay() {
        cartContainer.getChildren().clear();
        Map<Produit, Integer> items = Carte.getInstance().getItems();

        if (items.isEmpty()) {
            cartContainer.getChildren().add(new Label("Votre panier est vide."));
        } else {
            for (Map.Entry<Produit, Integer> entry : items.entrySet()) {
                cartContainer.getChildren().add(createCartRow(entry.getKey(), entry.getValue()));
            }
        }
        updateTotal();
    }

    private HBox createCartRow(Produit p, int quantity) {
        HBox row = new HBox(15);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle("-fx-padding: 10; -fx-border-color: #ddd; -fx-border-width: 0 0 1 0;");

        Label nameLbl = new Label(p.getName());
        nameLbl.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

        // Spacer pour pousser le prix à droite
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label qtyLbl = new Label("x " + quantity);
        Label priceLbl = new Label(String.format("%.2f €", p.getPrice() * quantity));

        Button removeBtn = new Button("-");
        removeBtn.setOnAction(e -> {
            Carte.getInstance().removeProduct(p);
            refreshCartDisplay();
        });

        row.getChildren().addAll(nameLbl, spacer, qtyLbl, priceLbl, removeBtn);
        return row;
    }

    private void updateTotal() {
        // Formattage à 2 décimales
        totalLabel.setText(String.format("%.2f €", Carte.getInstance().getTotal()));
    }

    @FXML
    private void handleApplyCoupon() {
        String code = couponField.getText();
        if ("PROMO10".equalsIgnoreCase(code)) {
            // Logique fictive de réduction
            System.out.println("Réduction appliquée ! (À implémenter dans le calcul total)");
            couponField.setStyle("-fx-border-color: green;");
        } else {
            couponField.setStyle("-fx-border-color: red;");
        }
    }

    @FXML
    private void handleBackToMenu() throws IOException {
        SceneManager.setRoot("menu-view.fxml");
    }

    @FXML
    private void handleClearCart() {
        Carte.getInstance().clear();
        refreshCartDisplay();
    }

    @FXML
    private void handlePayment() throws IOException {
        if (Carte.getInstance().getItems().isEmpty()) return;

        System.out.println("Passage au paiement...");
        SceneManager.setRoot("emporterCommande.fxml");
        // C'est ici qu'on ira vers l'écran "Insérez Carte"
        // SceneManager.setRoot("payment-wait-view.fxml");
    }


}