package univ.etu.projet.projetbornepaiement.models;

public class Produit {
    private String nomPlat;
    private double prix;
    private ProductType type;
    private String image;

    public Produit(String name, double prix, ProductType type, String imageChemin) {
        this.nomPlat = name;
        this.prix = prix;
        this.type = type;
        this.image= imageChemin;
    }

    public Produit(String nomPlat, double prix){
        this(nomPlat, prix, ProductType.PLAT, "");
    }
    public String getName() { return nomPlat; }
    public double getPrice() { return prix; }
    public ProductType getType() { return type; }
    public String getImage() {return image;}

    @Override
    public String toString() {
        return prix + " - " + prix + " €";
    }
}