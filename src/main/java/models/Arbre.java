package models;

public class Arbre extends Producte {
    private String nom;
    private double alcadaCm;

    public Arbre(String nom, double alcadaCm, double preu) {
        super(preu);
        this.nom = nom;
        this.alcadaCm = alcadaCm;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public double getAlcadaCm() {
        return alcadaCm;
    }

    public void setAlcadaCm(double alcadaCm) {
        this.alcadaCm = alcadaCm;
    }

    public String caracteristiquesProducte() {
        return "Arbre: " + nom + ", " + alcadaCm + " cm, " + getPreu() + "€";
    }

}
