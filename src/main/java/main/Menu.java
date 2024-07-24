package main;

import models.*;
import connexio.*;
import excepcions.ProducteNoTrobatBDD;

import java.sql.Array;
import java.util.Scanner;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Menu {
    private MySqlConnexio connexio;
    Scanner scanner = new Scanner(System.in);
    Floristeria floristeria = new Floristeria("Flors-Felices");
    String opcionsMenu = "Tria una opció\n" +
            "1. Crear floristeria\n" +
            "2. Afegir producte\n" +
            "3. Retirar producte\n" +
            "4. Veure estoc\n" +
            "5. Veure estoc per tipus de producte\n" +
            "6. Veure valor total estoc\n" +
            "7. Crear ticket compra (-> seleccionar productes, retirar-los de la bdd, afegir-los al ticket, imprimir ticket)\n" +
            "8. Mostrar llista de tickets\n" +
            "9. Veure total vendes\n" +
            "10. Sortir";

    String opcionsTipusProducte = "Quin és el tipus del producte?\n" +
            "1. Arbre\n" +
            "2. Flor\n" +
            "3. Decoració";

    public Menu() {
        connexio = MySqlConnexio.getInstance();
    }

    public void menuPrincipal() {
        System.out.println("Benvingut/da al gestor de Floristeries");
        System.out.println(opcionsMenu);
        int opcio = scanner.nextInt();
        scanner.nextLine(); // Afegit per consumir la nova línia
        switch (opcio) {
            case 1:
                // crearFloristeria();
                break;
            case 2:
                // Afegir Producte
                afegirProducte();
                System.out.println("Producte afegit amb èxit");
                break;
            case 3:
                // Retirar Producte
                String tipusRetirar = menuTipusProducte();
                System.out.println("Quin és el nom del producte a retirar?");
                String nomProducte = scanner.nextLine();
                try {
                    if(floristeria.trobarProducte(tipusRetirar, nomProducte)){
                        retirarProducte(tipusRetirar, nomProducte);
                        System.out.println("Producte retirat correctament.");
                    }
                } catch (ProducteNoTrobatBDD e) {
                    System.out.println(e.getMessage());
                }
                break;
            case 4:
                //Imprimir estoc
                floristeria.veureEstoc();
                break;
            case 5:
                //Veure estoc per tipus de producte
                break;
            case 6:
                //Veure valor total estoc
                break;
            case 7:
                //Crear ticket compra
                break;
            case 8:
                //Mostrar llista de tickets
                break;
            case 9:
                //Veure total vendes
                break;
            case 10:
                //Sortir
                break;
            default:
                System.out.println("Tornar a escollir, opció no vàlida");
        }
    }

    public String[] dadesProducte() {
        String[] dades = new String[4];
        String tipusAfegir = menuTipusProducte();
        System.out.println("Quin és el nom del producte?");
        String nomProducte = scanner.nextLine();
        System.out.println("Quin és el preu?");
        double preuProducte = scanner.nextDouble();
        Object atribut = null;
        switch(tipusAfegir){
            case "arbre":
                System.out.println("Quina és l'alçada de l'arbre?");
                atribut = scanner.nextDouble();
                break;
            case "flor":
                System.out.println("Quin és el color de la flor?");
                atribut = scanner.nextLine();
                break;
            case "decoració":
                System.out.println("Quin és el material de la decoració?\n" +
                        "[1. Fusta o 2. Plàstic]");
                int opcioMaterial = scanner.nextInt();
                scanner.nextLine();
                switch(opcioMaterial) {
                    case 1:
                        atribut = Material.FUSTA;
                        break;
                    case 2:
                        atribut = Material.PLASTIC;
                        break;
                    default:
                        System.out.println("Escull 1 per fusta o 2 per plàstic, siusplau");
                }
                break;
        }
        dades[0] = tipusAfegir;
        dades[1] = nomProducte;
        dades[2] = String.valueOf(preuProducte);
        dades[3] = String.valueOf(atribut);

        return dades;
    }

    public void afegirProducte() {
        Object objecteAfegir = floristeria.crearProducte(dadesProducte());
        String tipusAfegir = ((Producte) objecteAfegir).getClass().toString().replace("class models.", "");
        String sqlAfegir = floristeria.generarSQLAfegirProducte(tipusAfegir, objecteAfegir);
        connexio.executarSQL(sqlAfegir);
    }

    public String menuTipusProducte() {
        System.out.println(opcionsTipusProducte);
        int opcio = scanner.nextInt();
        scanner.nextLine();
        String tipus = "";
        switch (opcio) {
            case 1:
                tipus = "Arbre";
                break;
            case 2:
                tipus = "Flor";
                break;
            case 3:
                tipus = "Decoració";
                break;
            default:
                System.out.println("Opció no vàlida, torna a escollir");
        }
        return tipus;
    }

    public void retirarProducte(String tipus, String nom) {
        String sqlRetirar = "DELETE FROM Producte WHERE id = (SELECT id FROM (SELECT id FROM Producte WHERE tipus = '" +
                tipus + "' AND nom = '" + nom + "' LIMIT 1) as subquery)";
        connexio.executarSQL(sqlRetirar);

    }


}
