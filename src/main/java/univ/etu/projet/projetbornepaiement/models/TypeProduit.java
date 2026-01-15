package univ.etu.projet.projetbornepaiement.models;

public enum TypeProduit {
    MENU("Nos Menus"),
    PLAT("Plats Traditionnels"),
    BOISSON("Boissons & Rafraîchissements"),
    DESSERT("Desserts & Accompagnements");

    private final String label;

    TypeProduit(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}