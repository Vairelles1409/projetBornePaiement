package univ.etu.projet.projetbornepaiement.controllers;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import univ.etu.projet.projetbornepaiement.SceneManager;
import univ.etu.projet.projetbornepaiement.models.Commande;
import univ.etu.projet.projetbornepaiement.models.LigneCommande;
import univ.etu.projet.projetbornepaiement.services.QR_codeService;

import java.io.IOException;


public class QRTicketController {

    @FXML
    private ImageView qrImageView;

    @FXML
    private VBox ticketBox;

    @FXML
    private StackPane stackPane;

    @FXML
    private HBox mainContent;

    @FXML
    private ProgressIndicator loader;

    @FXML
    public void initialize() {
        System.out.println("QRTicketController chargé");

        if (CommandeHolder.instance != null) {
            showWithLoader(CommandeHolder.instance);
            CommandeHolder.instance = null;
        }
    }

    /**
     * Affiche le ticket + QR code après un loader d'une seconde.
     */
    private void showWithLoader(Commande cmd) {
        // loader visible, contenu caché
        loader.setVisible(true);
        mainContent.setVisible(false);

        // Pause d'une seconde
        PauseTransition pause = new PauseTransition(Duration.seconds(1));
        pause.setOnFinished(event -> {
            loader.setVisible(false);
            mainContent.setVisible(true);
            setCommande(cmd);
        });
        pause.play();
    }

    public void setQRCode(Image qrImage) {
        if (qrImageView != null && qrImage != null) {
            qrImageView.setImage(qrImage);
        }
    }

    public void setCommande(Commande cmd) {
        if (cmd == null) {
            System.err.println("Commande null");
            return;
        }

        // Génération texte pour QR
        StringBuilder sb = new StringBuilder();
        sb.append("Commande #").append(cmd.getIdOrder())
                .append("\nClient ID: ").append(cmd.getClient().getId())
                .append("\nTotal: ").append(cmd.getTotalPrice()).append("€")
                .append("\nPlats:\n");

        cmd.getLignes().forEach(ligne -> {
            sb.append("- ").append(ligne.getPlat().getName())
                    .append(" x").append(ligne.getQty())
                    .append(" : ").append(ligne.getPriceLigne()).append("€\n");
        });

        Image qrImage = QR_codeService.generateQRCode(sb.toString(), 300);
        setQRCode(qrImage);

        // Construction ticket
        ticketBox.getChildren().clear();

        Label idCmd = new Label("Commande #" + cmd.getIdOrder());
        idCmd.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label total = new Label("Total : " + cmd.getTotalPrice() + " €");
        total.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        ticketBox.getChildren().addAll(idCmd, total, new Label(" "));

        for (LigneCommande l : cmd.getLignes()) {
            Label item = new Label(l.getPlat().getName() + "  x" + l.getQty() + " — " + l.getPriceLigne() + " €");
            item.setStyle("-fx-font-size: 15px;");
            ticketBox.getChildren().add(item);
        }
    }

    @FXML
    private void closeWindow() throws IOException {
        SceneManager.setRoot("Welcome-view.fxml");
    }
}
