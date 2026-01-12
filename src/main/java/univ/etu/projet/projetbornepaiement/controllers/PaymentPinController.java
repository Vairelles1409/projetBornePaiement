package univ.etu.projet.projetbornepaiement.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import univ.etu.projet.projetbornepaiement.SceneManager;
import univ.etu.projet.projetbornepaiement.models.Carte;
import univ.etu.projet.projetbornepaiement.utils.PinPadService;

import java.io.IOException;

public class PaymentPinController {

    @FXML private Label amountLabel;
    @FXML private Label pinDisplayLabel;
    @FXML private Label statusLabel;

    private StringBuilder currentPin = new StringBuilder();

    @FXML
    public void initialize() {
        // 1. Récupérer et afficher le montant du panier
        double total = Carte.getInstance().getTotal();
        amountLabel.setText(String.format("%.2f €", total));

        // 2. Démarrer l'écoute du PinPad (COM5)
        boolean started = PinPadService.getInstance().startListening("COM5", this::handlePinInput);

        if (!started) {
            statusLabel.setText("ERREUR : PinPad non détecté !");
            statusLabel.setStyle("-fx-text-fill: red;");
        }

        updatePinDisplay();
    }

    /*// Méthode appelée quand le PinPad envoie une touche
    private void handlePinInput(String rawData) {
        // --- DEBUT DEBUG ---
        System.out.print("Reçu brut : '");
        for (char c : rawData.toCharArray()) {
            System.out.print(c + "' (ASCII: " + (int)c + ") ");
        }
        System.out.println("");
        // --- FIN DEBUG ---
        String key = rawData.trim();
        if (key.isEmpty()) return;

        // --- Logique Métier ---

        // 1. CHIFFRES (0-9)
        if (key.matches("[0-9]")) {
            if (currentPin.length() < 4) {
                currentPin.append(key);
                updatePinDisplay();
                statusLabel.setText("Saisie en cours...");
            }
        }
        // 2. CORRECTION (Touche Jaune / Backspace)
        // Vérifie si ton PinPad envoie '\b' ou autre chose pour la correction
        else if (key.equals("\b") || key.contains("CORR")) {
            if (currentPin.length() > 0) {
                currentPin.deleteCharAt(currentPin.length() - 1);
                updatePinDisplay();
            }
        }
        // 3. VALIDATION (Touche Verte / Entrée)
        else if (key.equals("\r") || key.equals("\n")) {
            if (currentPin.length() == 4) {
                processPayment();
            } else {
                Platform.runLater(() -> {
                    statusLabel.setText("Code incomplet (4 chiffres requis)");
                    statusLabel.setStyle("-fx-text-fill: red;");
                });
            }
        }
    } */
    // --- LA MÉTHODE FINALE ET TESTÉE ---
    private void handlePinInput(String rawData) {
        if (rawData == null || rawData.isEmpty()) return;

        // On récupère le code ASCII du premier caractère
        char firstChar = rawData.charAt(0);
        int ascii = (int) firstChar;

        // 1. CHIFFRES (0-9)
        if (Character.isDigit(firstChar)) {
            String digit = rawData.trim();
            if (currentPin.length() < 4) {
                currentPin.append(digit);
                updatePinDisplay();
                Platform.runLater(() -> statusLabel.setText("Saisie en cours..."));
            }
        }

        // 2. CORRIGER (Touche Jaune : ASCII 8)
        else if (ascii == 8) {
            if (currentPin.length() > 0) {
                currentPin.deleteCharAt(currentPin.length() - 1);
                updatePinDisplay();
            }
        }

        // 3. VALIDER (Touche Verte : ASCII 13)
        else if (ascii == 13) {
            if (currentPin.length() == 4) {
                processPayment();
            } else {
                Platform.runLater(() -> {
                    statusLabel.setText("Code incomplet (4 chiffres requis)");
                    statusLabel.setStyle("-fx-text-fill: red;");
                });
            }
        }

        // 4. ANNULER (Touche Rouge : ASCII 27)
        else if (ascii == 27) {
            Platform.runLater(() -> {
                try {
                    handleCancel(); // Quitte l'écran de paiement
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }
    private void updatePinDisplay() {
        // Affiche des ronds noirs
        StringBuilder visual = new StringBuilder();
        for (int i = 0; i < currentPin.length(); i++) {
            visual.append("●");
        }
        Platform.runLater(() -> pinDisplayLabel.setText(visual.toString()));
    }

    private void processPayment() {
        // Arrêt de l'écoute du clavier
        PinPadService.getInstance().stop();

        Platform.runLater(() -> {
            statusLabel.setText("Vérification du code PIN...");
            statusLabel.setStyle("-fx-text-fill: blue;");
        });

        String finalPin = currentPin.toString();
        System.out.println("PIN saisi pour transaction de " + amountLabel.getText() + " : " + finalPin);

        // boolean success = JavaCardService.verifyPin(finalPin);

        // Simulation pour l'instant :
        new Thread(() -> {
            try { Thread.sleep(1500); } catch (InterruptedException e) {}
            Platform.runLater(() -> {
                // Si Code Bon :
                System.out.println("Paiement Accepté !");
                // SceneManager.setRoot("ticket-view.fxml");
                statusLabel.setText("PAIEMENT ACCEPTÉ !");
                statusLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold; -fx-font-size: 18px;");
            });
        }).start();
    }

    @FXML
    private void handleCancel() throws IOException {
        PinPadService.getInstance().stop();
        SceneManager.setRoot("welcome-view.fxml");
    }
}