package factories;

import models.Material;
import models.Producte;

public interface ProducteFactory {
    Producte crearProducte(String nom, String color, double preu);
    Producte crearProducte(String nom, Enum<Material> material, double preu);
    Producte crearProducte(String nom, double alcada, double preu);
}
