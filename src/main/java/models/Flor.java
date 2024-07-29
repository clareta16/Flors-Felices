package models;

public class Flor extends Producte {
    private String color;

    public Flor(String nom, String color, double preu) {
        super(nom, preu);
        this.color = color;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String caracteristiquesProducte() {
        return "Flor: " + super.getNom() + ", color: " + this.color + ", " + super.getPreu() + "€. En estoc: " + super.isVenut();
    }
}
