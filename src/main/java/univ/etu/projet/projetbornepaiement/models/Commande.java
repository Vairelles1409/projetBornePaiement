package univ.etu.projet.projetbornepaiement.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "commandes")
public class Commande {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_order")
    private int idOrder;

    // Lien vers le Client (Clé étrangère id_client)
    @ManyToOne
    @JoinColumn(name = "id_client", nullable = false)
    private Client client;

    @Column(name = "total_price", nullable = false)
    private double totalPrice;

    @Column(name = "order_date")
    private LocalDateTime orderDate;

    // Liste des lignes de commande
    // cascade = ALL signifie : Si je sauve la commande, sauve aussi les lignes
    @OneToMany(mappedBy = "commande", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LigneCommande> lignes = new ArrayList<>();

    public Commande() {
    }

    public Commande(Client client, double totalPrice) {
        this.client = client;
        this.totalPrice = totalPrice;
        this.orderDate = LocalDateTime.now(); // Date actuelle
    }

    // Méthode utilitaire pour ajouter une ligne proprement
    public void addLigne(LigneCommande ligne) {
        lignes.add(ligne);
        ligne.setCommande(this); // On fait le lien dans les deux sens
    }

    // Getters
    public int getIdOrder() { return idOrder; }
    public Client getClient() { return client; }
    public double getTotalPrice() { return totalPrice; }
    public LocalDateTime getOrderDate() { return orderDate; }
    public List<LigneCommande> getLignes() { return lignes; }
}