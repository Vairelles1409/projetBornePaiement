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

// Classe utilitaire pour passer les données à la vue suivante
class CommandeHolder {
    public static Commande instance;
    public static double soldeRestant = -1;
}

public class PaymentPinController {

    @FXML private Label amountLabel;
    @FXML private Label pinDisplayLabel;
    @FXML private Label statusLabel;

    private StringBuilder currentPin = new StringBuilder();

    @FXML
    public void initialize() {
        double total = Carte.getInstance().getTotal();
        amountLabel.setText(String.format("%.2f €", total));

        // Démarrage PinPad
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

        Platform.runLater(() -> {
            statusLabel.setText("Traitement bancaire en cours...");
            statusLabel.setStyle("-fx-text-fill: blue;");
        });

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
                    // On relance l'écoute
                    PinPadService.getInstance().startListening("COM5", this::handlePinInput);
                });
                return;
            }

            // 2. DEBIT
            boolean debitOk = CardCommand.getInstance().debiterCarte(Carte.getInstance().getTotal());

            if (!debitOk) {
                Platform.runLater(() -> {
                    statusLabel.setText("Solde insuffisant !");
                    statusLabel.setStyle("-fx-text-fill: red;");
                    // On relance l'écoute si tu veux permettre de réessayer (optionnel ici car échec bancaire)
                    PinPadService.getInstance().startListening("COM5", this::handlePinInput);
                });
                return;
            }

            // 3. RECUPERATION DU SOLDE (IMPORTANT : AVANT de déconnecter la carte)
            short soldeShort = CardCommand.getInstance().getSoldeCarte();

            // 4. SAUVEGARDE BDD (Et déconnexion carte)
            Commande commandeValidee = saveToDB();

            if (commandeValidee != null) {
                // Stockage dans le Holder pour l'écran suivant
                CommandeHolder.instance = commandeValidee;
                CommandeHolder.soldeRestant = (double) soldeShort;

                Platform.runLater(() -> {
                    // Mise à jour visuelle succès
                    String msg = "PAIEMENT VALIDÉ !";
                    if (soldeShort != -1) {
                        msg += "\nSolde restant : " + soldeShort + " €";
                    }
                    statusLabel.setText(msg);
                    statusLabel.setStyle("-fx-text-fill: green; -fx-font-size: 16px; -fx-font-weight: bold; -fx-text-alignment: center;");

                    // Délai de 2 secondes avant changement de page pour lire le message
                    new Thread(() -> {
                        try { Thread.sleep(2000); } catch (InterruptedException e) {}
                        Platform.runLater(() -> {
                            try {
                                // Navigation vers l'écran QR / Ticket
                                SceneManager.setRoot("qr_ticket_generation.fxml");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
                    }).start();
                });
            } else {
                Platform.runLater(() -> {
                    statusLabel.setText("Erreur système (Sauvegarde)");
                    statusLabel.setStyle("-fx-text-fill: red;");
                });
            }

        }).start();
    }

    /**
     * Retourne l'objet Commande créé, ou null si erreur
     */
    private Commande saveToDB() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();

            Client client = session.get(Client.class, 3); // Kelly
            if (client == null) {
                client = new Client(3);
                // session.persist(client); // Attention : persist seulement si l'ID n'existe pas du tout
            }

            Commande cmd = new Commande(client, Carte.getInstance().getTotal());
            session.persist(cmd);

            for (Map.Entry<Plat, Integer> entry : Carte.getInstance().getItems().entrySet()) {
                LigneCommande ligne = new LigneCommande(entry.getKey(), entry.getValue());
                cmd.addLigne(ligne);
            }

            // Gestion consommation coupon
            Coupon couponApplique = Carte.getInstance().getAppliedCoupon();
            if (couponApplique != null) {
                Coupon c = session.get(Coupon.class, couponApplique.getId());
                if (c != null) {
                    c.setStatus("UTILISE");
                    session.merge(c);
                }
            }

            tx.commit();

            // Nettoyage final
            Carte.getInstance().clear();
            CardCommand.getInstance().disconnect(); // C'est ici qu'on coupe la carte

            return cmd; // On retourne l'objet
        } catch (Exception e) {
            e.printStackTrace();
            // En cas d'erreur BDD, on déconnecte quand même la carte
            CardCommand.getInstance().disconnect();
            return null;
        }
    }

    @FXML
    private void handleCancel() throws IOException {
        PinPadService.getInstance().stop();
        CardCommand.getInstance().disconnect();
        SceneManager.setRoot("welcome-view.fxml");
    }
}