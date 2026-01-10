package etu.ensi.model;

import jakarta.persistence.*;

@Entity
@Table(name = "lignes_commande")
public class Ligne_commande {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id_ligne;

    private int qty_commande;
    private double price_ligne;

    @ManyToOne
    @JoinColumn(name = "id_plat")
    private Plat plat;

    @ManyToOne
    @JoinColumn(name = "id_order")
    private Commande commande;

    public Ligne_commande() {}

    // constructor
    public Ligne_commande(Plat plat, int qty) {
        this.plat = plat;
        this.qty_commande = qty;
        this.price_ligne = plat.getUnit_price() * qty;
    }

    public int getId_ligne() { return id_ligne; }
    public void setId_ligne(int id_ligne) { this.id_ligne = id_ligne; }
    public int getQty_commande() { return qty_commande; }
    public void setQty_commande(int qty_commande) { this.qty_commande = qty_commande; }
    public double getPrice_ligne() { return price_ligne; }
    public void setPrice_ligne(double price_ligne) { this.price_ligne = price_ligne; }
    public Plat getPlat() { return plat; }
    public void setPlat(Plat plat) { this.plat = plat; }
    public Commande getCommande() { return commande; }
    public void setCommande(Commande commande) { this.commande = commande; }
}