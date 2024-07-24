package models;

public class Decoracio extends Producte {
    private Enum<Material> material;

    public Decoracio(String nom, Enum<Material> material, double preu) {
        super(nom, preu);
        this.material = material;
    }

    public Enum<Material> getMaterial() {
        return material;
    }

    public void setMaterial(Enum<Material> material) {
        this.material = material;
    }

    public String caracteristiquesProducte() {
        return "Decoracio: " + super.getNom() + ", material: " + this.material + ", " + super.getPreu() + "€";
    }
}
