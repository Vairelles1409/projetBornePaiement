module univ.etu.projet.projetbornepaiement {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fazecast.jSerialComm;
    requires java.smartcardio;

    // BASE DE DONNEES ---
    requires java.sql;
    requires org.hibernate.orm.core;
    requires jakarta.persistence;
    requires java.naming;
    // ---------------------------------

    opens univ.etu.projet.projetbornepaiement to javafx.fxml;
    opens univ.etu.projet.projetbornepaiement.controllers to javafx.fxml;

    // Ouvrir les models à Hibernate ---
    opens univ.etu.projet.projetbornepaiement.models to org.hibernate.orm.core, javafx.base;

    exports univ.etu.projet.projetbornepaiement;
}