package factories;

import models.Decoracio;
import models.Material;
import models.Producte;

public class DecoracioFactory implements ProducteFactory{

    @Override
    public Producte crearProducte(String nom, String atribut, double preu) {
        throw new UnsupportedOperationException("El tipus d'atribut no és compatible amb Decoració.");
    }

    @Override
    public Producte crearProducte(String nom, Enum<Material> material, double preu) {
        return new Decoracio(nom, material, preu);
    }

    @Override
    public Producte crearProducte(String nom, double alcada, double preu) {
        throw new UnsupportedOperationException("El tipus d'atribut no és compatible amb Decoració.");
    }
}
