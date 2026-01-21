package univ.etu.projet.projetbornepaiement.models;

import jakarta.persistence.*;

@Entity
@Table(name = "lignes_commande")
public class LigneCommande {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ligne")
    private int idLigne;

    // Lien vers la Commande
    @ManyToOne
    @JoinColumn(name = "id_order", nullable = false)
    private Commande commande;

    // Lien vers le Plat
    @ManyToOne
    @JoinColumn(name = "id_plat", nullable = false)
    private Plat plat;

    @Column(name = "price_ligne", nullable = false)
    private double priceLigne;

    @Column(name = "qty_commande", nullable = false)
    private int qty;

    public LigneCommande() {
    }

    public LigneCommande(Plat plat, int qty) {
        this.plat = plat;
        this.qty = qty;
        this.priceLigne = plat.getPrice() * qty; // Calcul automatique du prix total de la ligne
    }

    // Setters
    public void setCommande(Commande commande) {
        this.commande = commande;
    }

    // Getters
    public int getIdLigne() { return idLigne; }
    public Commande getCommande() { return commande; }
    public Plat getPlat() { return plat; }
    public double getPriceLigne() { return priceLigne; }
    public int getQty() { return qty; }
}