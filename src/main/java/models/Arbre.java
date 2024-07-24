package models;

public class Arbre extends Producte {
    private double alcadaCm;

    public Arbre(String nom, double alcadaCm, double preu) {
        super(nom, preu);
        this.alcadaCm = alcadaCm;
    }

    public double getAlcadaCm() {
        return alcadaCm;
    }

    public void setAlcadaCm(double alcadaCm) {
        this.alcadaCm = alcadaCm;
    }

    public String caracteristiquesProducte() {
        return "Arbre: " + super.getNom() + ", " + alcadaCm + " cm, " + super.getPreu() + "€";
    }



}
