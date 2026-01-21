package univ.etu.projet.projetbornepaiement.models;

import jakarta.persistence.*;

@Entity
@Table(name = "clients")
public class Client {

    @Id
    @Column(name = "id_personne")
    private int id;

    // Constructeur vide obligatoire pour Hibernate
    public Client() {
    }

    // Constructeur pour créer un client avec un ID connu (ex: 3)
    public Client(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}