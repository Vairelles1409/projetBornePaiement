package etu.ensi.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "coupons")
public class Coupon_reduction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id_coupon;
    private int valeur;
    @Temporal(TemporalType.DATE)
    private Date validity_date;

    private String status;

    public Coupon_reduction() {}

    public int getId_coupon() { return id_coupon; }
    public void setId_coupon(int id_coupon) { this.id_coupon = id_coupon; }
    public int getValeur() { return valeur; }
    public void setValeur(int valeur) { this.valeur = valeur; }
    public Date getValidity_date() { return validity_date; }
    public void setValidity_date(Date validity_date) { this.validity_date = validity_date; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}