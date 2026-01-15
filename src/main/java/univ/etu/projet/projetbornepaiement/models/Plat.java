package univ.etu.projet.projetbornepaiement.models;

import jakarta.persistence.*;

@Entity
@Table(name = "plats") // Correspond au nom exact de ta table dans la BD
public class Plat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_plat")
    private int idPlat;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "unit_price", nullable = false)
    private double unitPrice;

    @Column(name = "qty_stock")
    private int qtyStock;

    @Enumerated(EnumType.STRING) // Stocke "PLAT", "BOISSON" en texte
    @Column(name = "type")
    private TypeProduit type;

    @Column(name = "image_path")
    private String imagePath;

    // --- CONSTRUCTEUR VIDE (OBLIGATOIRE POUR HIBERNATE) ---
    public Plat() {}

    // --- CONSTRUCTEUR UTILE POUR LES TESTS ---
    public Plat(String name, double unitPrice, int qtyStock, TypeProduit type, String imagePath) {
        this.name = name;
        this.unitPrice = unitPrice;
        this.qtyStock = qtyStock;
        this.type = type;
        this.imagePath = imagePath;
    }

    // --- GETTERS ---
    public int getIdPlat() { return idPlat; }
    public String getName() { return name; }

    // Astuce : On garde getPrice() pour ne pas casser ton contrôleur existant
    public double getPrice() { return unitPrice; }

    public int getQtyStock() { return qtyStock; }
    public TypeProduit getType() { return type; }
    public String getImagePath() { return imagePath; }

    // --- SETTERS (Optionnels mais utiles pour Hibernate) ---
    public void setQtyStock(int qtyStock) { this.qtyStock = qtyStock; }

    @Override
    public String toString() {
        return name + " (" + unitPrice + " €)";
    }
}