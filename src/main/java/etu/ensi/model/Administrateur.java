package etu.ensi.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "administrateurs")
public class Administrateur extends Personne {
    public Administrateur() {
        super();
        this.setRole(Role.ADMIN);
    }
}