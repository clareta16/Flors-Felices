package models;

public class Flor extends Producte {
    private String nom;
    private String color;

    public Flor(String nom, String color, double preu) {
        super(preu);
        this.nom = nom;
        this.color = color;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String caracteristiquesProducte() {
        return "Flor: " + nom + ", " + color + getPreu() + "€";
    }
}
