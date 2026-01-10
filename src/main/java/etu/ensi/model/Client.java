package etu.ensi.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "clients")
public class Client extends Personne {
    public Client() {
        super();
        this.setRole(Role.CLIENT);
    }
}