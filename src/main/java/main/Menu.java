package main;

import factories.*;
import models.*;
import connexio.*;

import java.util.Scanner;

public class Menu {
    private MySqlConnexio connexio;
    Scanner scanner = new Scanner(System.in);
        String opcionsMenu = "Tria una opció\n" +
		        "1. Crear floristeria\n" +
                "2. Afegir Producte\n" +
                "3. Retirar Producte\n" +
                "...";
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
        switch (opcio) {
            case 1:
//                crearFloristeria();
                break;
            case 2:
                //Afegir Producte
                int tipusAfegir = menuTipusProducte();
                Object objecteAfegir = crearProducte(tipusAfegir);
                String sqlAfegir = generarSQLAfegirProducte(tipusAfegir, objecteAfegir);
                connexio.executarSQL(sqlAfegir);
                System.out.println("Producte afegit amb èxit");
                break;
            case 3:
                //Retirar Producte
                int tipusRetirar = menuTipusProducte();
                System.out.println("Quin és el nom del producte a retirar:");
                String nom = scanner.nextLine();
                String sqlRetirar = generarSQLRetirarProducte(tipusRetirar, nom);
                connexio.executarSQL(sqlRetirar);
                break;
            default:
                System.out.println("Tornar a escollir, opció no vàlida");
        }
    }

    public int menuTipusProducte() {
        System.out.println(opcionsTipusProducte);
        int opcio = scanner.nextInt();
        scanner.nextLine();
        int tipus = 0;
        switch (opcio) {
            case 1:
                tipus = 1;
                break;
            case 2:
                tipus = 2;
                break;
            case 3:
                tipus = 3;
                break;
            default:
                System.out.println("Opció no vàlida, torna a escollir");
        }
        return tipus;
    }

    public Object crearProducte(int opcio) {
        Object objecte = null;
        switch (opcio) {
            case 1:
                System.out.println("Quin és el nom de l'arbre?");
                String nomArbre = scanner.nextLine();
                System.out.println("Quina alçada té?");
                double alcadaCm = scanner.nextDouble();
                System.out.println("Quin és el preu?");
                double preuArbre = scanner.nextDouble();
                ArbreFactory arbreFactory = new ArbreFactory();
                Arbre arbre = (Arbre) arbreFactory.crearProducte(nomArbre, alcadaCm, preuArbre);
                objecte = arbre;
                System.out.println(arbre + " creat.");
                break;
            case 2:
                System.out.println("Quin és el nom de la flor?");
                String nomFlor = scanner.nextLine();
                System.out.println("De quin color és?");
                String color = scanner.nextLine();
                System.out.println("Quin és el preu?");
                double preuFlor = scanner.nextDouble();
                FlorFactory florFactory = new FlorFactory();
                Flor flor = (Flor) florFactory.crearProducte(nomFlor, color, preuFlor);
                objecte = flor;
                System.out.println(flor + " creada.");
                break;
            case 3:
                System.out.println("Quin és el nom de la decoració?");
                String nomDecoracio = scanner.nextLine();
                System.out.println("De quin material és?");
                String material = scanner.nextLine(); //Enum
                System.out.println("Quin és el preu?");
                double preuDecoracio = scanner.nextDouble();
                DecoracioFactory decoracioFactory = new DecoracioFactory();
                Decoracio decoracio = (Decoracio) decoracioFactory.crearProducte(nomDecoracio, material, preuDecoracio);
                objecte = decoracio;
                System.out.println(decoracio + " creada.");
                break;
            default:
                System.out.println("Opció no vàlida, torna a escollir");
        }
        return objecte;
    }

    public String generarSQLAfegirProducte(int tipus, Object producte) {
        String sql = "";
        switch (tipus) {
            case 1:
                Arbre arbre = (Arbre) producte;
                sql = "INSERT INTO Producte (tipus, nom, preu, atribut) VALUES ('arbre', '" + arbre.getNom() + "', " + arbre.getPreu() + ", 'alçada " + arbre.getAlcadaCm() + "cm')";
                break;
            case 2:
                Flor flor = (Flor) producte;
                sql = "INSERT INTO Producte (tipus, nom, preu, atribut) VALUES ('flor', '" + flor.getNom() + "', " + flor.getPreu() + ", 'color " + flor.getColor() + "')";
                break;
            case 3:
                Decoracio decoracio = (Decoracio) producte;
                sql = "INSERT INTO Producte (tipus, nom, preu, atribut) VALUES ('decoració', '" + decoracio.getNom() + "', " + decoracio.getPreu() + ", 'material " + decoracio.getMaterial() + "')";
                break;
        }
        return sql;
    }

    public String generarSQLRetirarProducte(int tipus, String nom) {
        String sql = "";
        switch (tipus) {
            case 1:
                sql = "DELETE FROM Producte WHERE tipus = 'arbre' AND nom = '" + nom + "'";
                break;
            case 2:
                sql = "DELETE FROM Producte WHERE tipus = 'flor' AND nom = '" + nom + "'";
                break;
            case 3:
                sql = "DELETE FROM Producte WHERE tipus = 'decoració' AND nom = '" + nom + "'";
                break;
            default:
                System.out.println("Tipus de producte no vàlid");
                break;
        }
        return sql;
    }

}
