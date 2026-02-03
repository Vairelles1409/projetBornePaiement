package univ.etu.projet.projetbornepaiement.models;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "coupons")
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_coupon")
    private int id;

    @Column(name = "code", unique = true)
    private String code; // Le code à 3 chiffres

    @Column(name = "valeur")
    private double valeur; // Le montant de la réduction

    @Column(name = "validity_date")
    private LocalDate validityDate;

    @Column(name = "status")
    private String status; // VALIDE, UTILISE, PERIME

    public Coupon() {}

    // Getters et Setters
    public int getId() { return id; }
    public String getCode() { return code; }
    public double getValeur() { return valeur; }
    public LocalDate getValidityDate() { return validityDate; }
    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }
}