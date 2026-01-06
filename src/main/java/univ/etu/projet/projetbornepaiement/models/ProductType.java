package univ.etu.projet.projetbornepaiement.models;

public enum ProductType {
    MENU("Nos Menus"),
    PLAT("Plats Traditionnels"),
    BOISSON("Boissons & Rafraîchissements"),
    DESSERT("Desserts & Accompagnements");

    private final String label;

    ProductType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}