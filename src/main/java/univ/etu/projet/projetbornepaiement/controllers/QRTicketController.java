package univ.etu.projet.projetbornepaiement.controllers;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import univ.etu.projet.projetbornepaiement.SceneManager;
import univ.etu.projet.projetbornepaiement.models.Commande;
import univ.etu.projet.projetbornepaiement.models.LigneCommande;
import univ.etu.projet.projetbornepaiement.services.QR_codeService;

import java.io.IOException;

public class QRTicketController {

    @FXML private ImageView qrImageView;
    @FXML private VBox ticketBox;
    @FXML private StackPane stackPane;
    @FXML private HBox mainContent;
    @FXML private ProgressIndicator loader;

    // NOUVEAU : Label pour afficher le solde restant (à ajouter dans le FXML)
    @FXML private Label soldeLabel;

    // Variable pour le compte à rebours
    private PauseTransition autoCloseTimer;

    @FXML
    public void initialize() {
        System.out.println("QRTicketController chargé");

        // 1. Affichage du Solde Restant (si disponible)
        if (soldeLabel != null) {
            if (CommandeHolder.soldeRestant != -1) {
                soldeLabel.setText("Solde carte restant : " + CommandeHolder.soldeRestant + " €");
            } else {
                soldeLabel.setText("Paiement validé");
            }
        }

        // 2. Chargement de la commande
        if (CommandeHolder.instance != null) {
            showWithLoader(CommandeHolder.instance);
            // On ne vide pas l'instance tout de suite au cas où on voudrait rafraîchir
        }
    }

    /**
     * Affiche le ticket + QR code après un loader d'une seconde.
     */
    private void showWithLoader(Commande cmd) {
        loader.setVisible(true);
        mainContent.setVisible(false);

        PauseTransition pause = new PauseTransition(Duration.seconds(1));
        pause.setOnFinished(event -> {
            loader.setVisible(false);
            mainContent.setVisible(true);
            setCommande(cmd);

            // 3. LANCEMENT DU TIMER AUTOMATIQUE (30 SECONDES)
            startAutoRedirect(30);
        });
        pause.play();
    }

    /**
     * Gère la redirection automatique vers l'accueil
     */
    private void startAutoRedirect(int seconds) {
        autoCloseTimer = new PauseTransition(Duration.seconds(seconds));
        autoCloseTimer.setOnFinished(event -> {
            try {
                System.out.println("Timeout : Retour accueil automatique.");
                closeWindow();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        autoCloseTimer.play();
    }

    public void setQRCode(Image qrImage) {
        if (qrImageView != null && qrImage != null) {
            qrImageView.setImage(qrImage);
        }
    }

    public void setCommande(Commande cmd) {
        if (cmd == null) return;

        // Génération texte pour QR
        StringBuilder sb = new StringBuilder();
        sb.append("AFOUM TCHOP\nCmd #: ").append(cmd.getIdOrder())
                .append("\nTotal: ").append(cmd.getTotalPrice()).append("€\n");

        cmd.getLignes().forEach(ligne -> {
            sb.append("- ").append(ligne.getQty()).append("x ").append(ligne.getPlat().getName()).append("\n");
        });

        // Génération image
        // Attention : la méthode generateQRCode prend souvent width ET height (300, 300)
        // Si ton service n'en prend qu'un, garde juste 300.
        try {
            Image qrImage = QR_codeService.generateQRCode(sb.toString(), 300);
            setQRCode(qrImage);
        } catch (Exception e) {
            System.err.println("Erreur QR: " + e.getMessage());
        }

        // Construction ticket visuel
        ticketBox.getChildren().clear();

        Label idCmd = new Label("Commande #" + cmd.getIdOrder());
        idCmd.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label total = new Label("Total : " + String.format("%.2f", cmd.getTotalPrice()) + " €");
        total.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        ticketBox.getChildren().addAll(idCmd, total, new Label("----------------"));

        for (LigneCommande l : cmd.getLignes()) {
            String ligneTxt = String.format("%s x%d — %.2f €", l.getPlat().getName(), l.getQty(), l.getPriceLigne());
            Label item = new Label(ligneTxt);
            item.setStyle("-fx-font-size: 14px;");
            ticketBox.getChildren().add(item);
        }
    }

    @FXML
    private void closeWindow() throws IOException {
        // IMPORTANT : Arrêter le timer si l'utilisateur clique manuellement
        if (autoCloseTimer != null) {
            autoCloseTimer.stop();
        }

        // Nettoyage des données statiques pour la prochaine commande
        CommandeHolder.instance = null;
        CommandeHolder.soldeRestant = -1;

        // Retour à l'accueil (Attention à la minuscule pour le JAR !)
        SceneManager.setRoot("welcome-view.fxml");
    }
}