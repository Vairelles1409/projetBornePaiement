module univ.etu.projet.projetbornepaiement {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fazecast.jSerialComm;
    requires java.smartcardio;

    requires java.sql;
    requires org.hibernate.orm.core;
    requires jakarta.persistence;
    requires java.naming;
    // On ajoute bytebuddy au cas où, bien qu'il soit inclus dans hibernate
    requires net.bytebuddy;

    opens univ.etu.projet.projetbornepaiement to javafx.fxml;
    opens univ.etu.projet.projetbornepaiement.controllers to javafx.fxml;

    // --- CORRECTION ICI ---
    // 1. On ouvre 'models' à TOUT LE MONDE (plus de "to ...") pour que ByteBuddy puisse y accéder
    opens univ.etu.projet.projetbornepaiement.models;

    // 2. On exporte 'models' pour que les modules dynamiques puissent les utiliser
    exports univ.etu.projet.projetbornepaiement.models;

    exports univ.etu.projet.projetbornepaiement;
}