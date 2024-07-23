package models;

public abstract class Producte {
    private double preu;

    public Producte(double preu) {
        this.preu = preu;

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
        return "Producte{" +
                "preu=" + preu +
                '}';
    }
}
