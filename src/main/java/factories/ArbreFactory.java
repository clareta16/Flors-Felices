package factories;

import models.Arbre;
import models.Material;
import models.Producte;

public class ArbreFactory implements ProducteFactory{

    @Override
    public Producte crearProducte(String nom, String color, double preu) {
        throw new UnsupportedOperationException("El tipus d'atribut no és compatible amb Arbre.");
    }

    @Override
    public Producte crearProducte(String nom, Enum<Material> material, double preu) {
        throw new UnsupportedOperationException("El tipus d'atribut no és compatible amb Arbre.");
    }

    @Override
    public Producte crearProducte(String nom, double alcada, double preu) {
        return new Arbre(nom, alcada, preu);
    }
}
