package univ.etu.projet.projetbornepaiement.controllers;

import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import univ.etu.projet.projetbornepaiement.SceneManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WelcomeController {

    @FXML
    private Label welcomeText;

    @FXML
    private StackPane carouselContainer;

    @FXML
    private ImageView carouselImage1;

    @FXML
    private ImageView carouselImage2;

    private final List<Image> images = new ArrayList<>();
    private int index = 0;
    private boolean showingFirst = true;

    @FXML
    public void initialize() {
        loadImages();
        startCarousel();
    }

    /** Charge les images du carrousel en mémoire */
    private void loadImages() {
        try {
            images.add(new Image(getClass().getResource("/img/braise.jpg").toExternalForm()));
            images.add(new Image(getClass().getResource("/img/complements.jpg").toExternalForm()));
            images.add(new Image(getClass().getResource("/img/legumes.jpg").toExternalForm()));
        } catch (Exception e) {
            System.err.println("Erreur de chargement des images du carrousel : " + e.getMessage());
        }

        if (!images.isEmpty()) {
            carouselImage1.setImage(images.get(0));
        }
    }

    /** Lance le carrousel avec cross-fade fluide */
    private void startCarousel() {
        if (images.size() < 2) return; // au moins 2 images nécessaires

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(4), event -> {
            // Définir la prochaine image
            int nextIndex = (index + 1) % images.size();
            Image nextImage = images.get(nextIndex);

            ImageView fadeOutView = showingFirst ? carouselImage1 : carouselImage2;
            ImageView fadeInView = showingFirst ? carouselImage2 : carouselImage1;

            fadeInView.setImage(nextImage);
            fadeInView.setOpacity(0);

            // Cross-fade
            FadeTransition ftOut = new FadeTransition(Duration.seconds(1), fadeOutView);
            ftOut.setToValue(0);
            FadeTransition ftIn = new FadeTransition(Duration.seconds(1), fadeInView);
            ftIn.setToValue(1);

            ParallelTransition pt = new ParallelTransition(ftOut, ftIn);
            pt.play();

            showingFirst = !showingFirst;
            index = nextIndex;
        }));

        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    @FXML
    protected void onStartButtonClick() {
        try {
            SceneManager.setRoot("menu-view.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void goToTestMode() throws IOException {
        SceneManager.setRoot("pinpad-test-view.fxml");
    }
}
