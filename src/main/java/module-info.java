module univ.etu.projet.projetbornepaiement {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.smartcardio;

    requires org.kordamp.bootstrapfx.core;

    requires com.fazecast.jSerialComm;

    opens univ.etu.projet.projetbornepaiement to javafx.fxml;
    exports univ.etu.projet.projetbornepaiement;
    exports univ.etu.projet.projetbornepaiement.controllers;
    opens univ.etu.projet.projetbornepaiement.controllers to javafx.fxml;
}