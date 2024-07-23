package models;

public class Decoracio extends Producte {

    private String nom;
    private Enum<Material> material;

    public Decoracio(String nom, Enum<Material> material, double preu) {
        super(preu);
        this.nom = nom;
        this.material = material;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public Enum<Material> getMaterial() {
        return material;
    }

    public void setMaterial(Enum<Material> material) {
        this.material = material;
    }

    public String caracteristiquesProducte() {
        return "Decoracio: " + nom + ", " + material + getPreu() + "€";
    }
}
