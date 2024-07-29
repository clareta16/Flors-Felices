package models;

public abstract class Producte {
    private int id;
    private String nom;
    private double preu;
    private boolean venut;

    public Producte(String nom, double preu) {
        this.nom = nom;
        this.preu = preu;
        this.venut = false;
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

    public boolean isVenut() {
        return venut;
    }

    public void setVenut(boolean venut) {
        this.venut = venut;
    }

    public void setId(int id) {
        this.id = id;
    }


    public abstract String caracteristiquesProducte();


    @Override
    public String toString() {
        return "Producte:" + "\n" +
                "nom: " + nom + "\n" +
                "preu: " + preu;
    }
}
