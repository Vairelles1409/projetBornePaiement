package univ.etu.projet.projetbornepaiement;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class BorneApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        stage.setTitle("Borne Paiement - AF'OUM TCHOP");
        FXMLLoader fxmlLoader = new FXMLLoader(BorneApplication.class.getResource("welcome-view.fxml"));
        stage.setFullScreen(true);
        stage.setFullScreenExitHint("");
        stage.setResizable(false);
        SceneManager.setStage(stage);
        SceneManager.setRoot("welcome-view.fxml");
        stage.show();
    }

    public static void main (String[] args){
        launch();
    }
}
