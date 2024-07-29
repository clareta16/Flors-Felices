package main;

import excepcions.LlistaTicketsBuidaException;
import models.*;

import static eines.Entrada.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Menu {
    private Floristeria floristeria;
    private String opcionsMenu = "Tria una opció\n" +
            "1. Afegir producte\n" +
            "2. Retirar producte\n" +
            "3. Veure estoc\n" +
            "4. Veure estoc per tipus de producte\n" +
            "5. Veure valor total estoc\n" +
            "6. Crear ticket compra\n" +
            "7. Mostrar llista de tickets\n" +
            "8. Veure total vendes\n" +
            "9. Sortir";

    private String opcionsTipusProducte = "Quin és el tipus del producte?\n" +
            "1. Arbre\n" +
            "2. Flor\n" +
            "3. Decoració";

    public Menu(Floristeria floristeria) {
        this.floristeria = floristeria;
    }

    public boolean menuPrincipal() {
        System.out.println("Gestor de " + floristeria.getNom());
        int opcio = entradaInt(opcionsMenu);
        boolean exit = false;

        switch (opcio) {
            case 1:
                // Afegir Producte
                floristeria.afegirProducte(dadesProducte());
                break;
            case 2:
                // Retirar Producte
                floristeria.retirarProducte(menuTipusProducte(), entradaBuida("Nom del producte?"));
                break;
            case 3:
                //Imprimir estoc
                floristeria.veureEstoc();
                break;
            case 4:
                floristeria.imprimirStockQuantitats();
                break;
            case 5:
                floristeria.imprimirValorTotal();
                break;
            case 6:
                gestionarCreacioTicket();
                break;
            case 7:
                floristeria.mostrarLlistaCompresAntigues();
                break;
            case 8:
                try {
                    floristeria.visualitzarTotalDinersGuanyats();
                } catch (LlistaTicketsBuidaException e) {
                    System.out.println(e.getMessage());
                }

                //Veure total vendes
                break;
            case 9:
                //Sortir
                System.out.println("Gràcies per fer servir el gestor de floristeria");
                exit = true;
                break;
            default:
                System.out.println("Tornar a escollir, opció no vàlida");
        }
        return exit;
    }

    public String menuTipusProducte() {
        int opcio = entradaInt(opcionsTipusProducte);

        String tipus = "";
        switch (opcio) {
            case 1:
                tipus = "Arbre";
                break;
            case 2:
                tipus = "Flor";
                break;
            case 3:
                tipus = "Decoracio";
                break;
            default:
                System.out.println("Opció no vàlida, torna a escollir");
        }
        return tipus;
    }


    public String[] dadesProducte() {
        String[] dades = new String[4];
        String tipusAfegir = menuTipusProducte();

        String nomProducte = entradaBuida("Quin és el nom del producte?");
        double preuProducte = entradaDouble("Quin és el preu?");
        Object atribut = null;

        switch (tipusAfegir) {
            case "Arbre":
                atribut = entradaDouble("Quina és l'alçada de l'arbre?");
                break;
            case "Flor":
                atribut = entradaBuida("Quin és el color de la flor?");
                break;
            case "Decoracio":
                int opcioMaterial = entradaInt("Quin és el material de la decoració?\n" +
                        "[1. Fusta o 2. Plàstic]");

                switch (opcioMaterial) {
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

    private void gestionarCreacioTicket() {
        List<String> nomsProductes = new ArrayList<>();
        boolean afegirMesProductes;

        do {
            String nomProducte = obtenirNomProducte();
            nomsProductes.add(nomProducte);

            afegirMesProductes = demanarSiAfegirMesProductes();
        } while (afegirMesProductes);

        floristeria.crearTicket(nomsProductes);
    }

    private String obtenirNomProducte() {
        return entradaBuida("Introdueix el nom del producte: ");
    }

    private boolean demanarSiAfegirMesProductes() {
        String resposta = entradaBuida("Vols afegir més productes? (si/no): ");
        return resposta.equalsIgnoreCase("si");
    }

}
