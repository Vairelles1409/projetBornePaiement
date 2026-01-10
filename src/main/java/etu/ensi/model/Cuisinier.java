package etu.ensi.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "cuisiniers")
public class Cuisinier extends Personne {
    public Cuisinier() {
        super();
        this.setRole(Role.CUISINIER);
    }
}