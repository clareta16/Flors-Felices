package factories;

import models.Flor;
import models.Material;
import models.Producte;

public class FlorFactory implements ProducteFactory {

    @Override
    public Producte crearProducte(String nom, String color, double preu) {
        return new Flor(nom, color, preu);
    }

    @Override
    public Producte crearProducte(String nom, Enum<Material> material, double preu) {
        throw new UnsupportedOperationException("El tipus d'atribut no és compatible amb Flor.");
    }

    @Override
    public Producte crearProducte(String nom, double alcada, double preu) {
        throw new UnsupportedOperationException("El tipus d'atribut no és compatible amb Flor.");
    }
}
