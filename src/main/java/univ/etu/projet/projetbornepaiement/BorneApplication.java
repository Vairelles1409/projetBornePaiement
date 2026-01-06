package univ.etu.projet.projetbornepaiement;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class BorneApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(BorneApplication.class.getResource("welcome-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Borne Paiement - Acceuil ");
        stage.setScene(scene);

        SceneManager.setStage(stage);
        SceneManager.setRoot("Welcome-view.fxml");
        stage.show();
    }

    public static void main (String[] args){
        launch();
    }
}
