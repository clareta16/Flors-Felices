package models;

public abstract class Producte {
    private int id;
    private String nom;
    private double preu;

    public Producte(String nom, double preu) {
        this.nom = nom;
        this.preu = preu;
    }

    public int getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public double getPreu() {
        return preu;
    }

    public void setPreu(double preu) {
        this.preu = preu;
    }

    public abstract String caracteristiquesProducte();


    @Override
    public String toString() {
        return "Producte:" + "\n" +
                "nom: " + nom + "\n" +
                "preu: " + preu;
    }
}
