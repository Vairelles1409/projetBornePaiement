package univ.etu.projet.projetbornepaiement.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.hibernate.Session;
import org.hibernate.Transaction;
import univ.etu.projet.projetbornepaiement.SceneManager;
import univ.etu.projet.projetbornepaiement.models.*;
import univ.etu.projet.projetbornepaiement.services.CardCommand;
import univ.etu.projet.projetbornepaiement.utils.HibernateUtil;
import univ.etu.projet.projetbornepaiement.utils.PinPadService;

import java.io.IOException;
import java.util.Map;

public class PaymentPinController {

    @FXML private Label amountLabel;
    @FXML private Label pinDisplayLabel;
    @FXML private Label statusLabel;
    private StringBuilder currentPin = new StringBuilder();

    @FXML
    public void initialize() {
        double total = Carte.getInstance().getTotal();
        amountLabel.setText(String.format("%.2f €", total));

        PinPadService.getInstance().startListening("COM5", this::handlePinInput);
        updatePinDisplay();
    }

    private void handlePinInput(String rawData) {
        if (rawData == null || rawData.isEmpty()) return;
        int ascii = (int) rawData.charAt(0);

        if (Character.isDigit(rawData.charAt(0))) {
            if (currentPin.length() < 4) {
                currentPin.append(rawData.trim());
                updatePinDisplay();
            }
        } else if (ascii == 8) { // Corriger
            if (currentPin.length() > 0) {
                currentPin.deleteCharAt(currentPin.length() - 1);
                updatePinDisplay();
            }
        } else if (ascii == 13) { // Valider
            if (currentPin.length() == 4) {
                processPayment();
            }
        } else if (ascii == 27) { // Annuler
            Platform.runLater(() -> { try { handleCancel(); } catch(Exception e){} });
        }
    }

    private void updatePinDisplay() {
        Platform.runLater(() -> {
            StringBuilder sb = new StringBuilder();
            for(int i=0; i<currentPin.length(); i++) sb.append("● ");
            pinDisplayLabel.setText(sb.toString());
        });
    }

    private void processPayment() {
        PinPadService.getInstance().stop(); // Stop Clavier

        Platform.runLater(() -> statusLabel.setText("Vérification Carte en cours..."));

        new Thread(() -> {
            // 1. VERIFY PIN
            boolean pinOk = CardCommand.getInstance().verifierPin(currentPin.toString());

            if (!pinOk) {
                int essais = CardCommand.getInstance().seuilPin;
                Platform.runLater(() -> {
                    statusLabel.setText("PIN Faux ! Reste " + essais + " essais.");
                    statusLabel.setStyle("-fx-text-fill: red;");
                    currentPin.setLength(0);
                    updatePinDisplay();
                    PinPadService.getInstance().startListening("COM5", this::handlePinInput);
                });
                return;
            }

            // 2. DEBIT
            boolean debitOk = CardCommand.getInstance().debiterCarte(Carte.getInstance().getTotal());

            if (!debitOk) {
                Platform.runLater(() -> statusLabel.setText("Solde insuffisant !"));
                return;
            }

            // 3. HIBERNATE SAVE
            boolean saved = saveToDB();

            Platform.runLater(() -> {
                if (saved) {
                    statusLabel.setText("PAIEMENT VALIDÉ !");
                    statusLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                }
            });

        }).start();
    }

    private boolean saveToDB() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();

            Client client = session.get(Client.class, 3); // Kelly
            if (client == null) client = new Client(3);

            Commande cmd = new Commande(client, Carte.getInstance().getTotal());
            session.persist(cmd);

            for (Map.Entry<Plat, Integer> entry : Carte.getInstance().getItems().entrySet()) {
                LigneCommande ligne = new LigneCommande(entry.getKey(), entry.getValue());
                cmd.addLigne(ligne);
            }

            tx.commit();

            // Nettoyage final
            Carte.getInstance().clear();
            CardCommand.getInstance().disconnect();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @FXML
    private void handleCancel() throws IOException {
        PinPadService.getInstance().stop();
        CardCommand.getInstance().disconnect();
        SceneManager.setRoot("welcome-view.fxml");
    }
}