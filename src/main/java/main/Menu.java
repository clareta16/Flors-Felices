package main;

import factories.*;
import models.*;
import connexio.*;
import excepcions.ProducteNoTrobatBDD;

import java.util.Scanner;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Menu {
    private MySqlConnexio connexio;
    Scanner scanner = new Scanner(System.in);
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
                int tipusAfegir = menuTipusProducte();
                Object objecteAfegir = crearProducte(tipusAfegir);
                String sqlAfegir = generarSQLAfegirProducte(tipusAfegir, objecteAfegir);
                connexio.executarSQL(sqlAfegir);
                System.out.println("Producte afegit amb èxit");
                break;
            case 3:
                // Retirar Producte
                int tipusRetirar = menuTipusProducte();
                System.out.println("Quin és el nom del producte a retirar?");
                String nom = scanner.nextLine();
                try {
                    retirarProducte(tipusRetirar, nom);
                    System.out.println("Producte retirat correctament.");
                } catch (ProducteNoTrobatBDD e) {
                    System.out.println(e.getMessage());
                }
                break;
            case 4:
                //Imprimir estoc
                veureEstoc();
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
                scanner.nextLine(); // Afegit per consumir la nova línia
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
                scanner.nextLine(); // Afegit per consumir la nova línia
                FlorFactory florFactory = new FlorFactory();
                Flor flor = (Flor) florFactory.crearProducte(nomFlor, color, preuFlor);
                objecte = flor;
                System.out.println(flor + " creada.");
                break;
            case 3:
                System.out.println("Quin és el nom de la decoració?");
                String nomDecoracio = scanner.nextLine();
                System.out.println("De quin material és?");
                String material = scanner.nextLine(); // Enum
                System.out.println("Quin és el preu?");
                double preuDecoracio = scanner.nextDouble();
                scanner.nextLine(); // Afegit per consumir la nova línia
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

    public void retirarProducte(int tipus, String nom) throws ProducteNoTrobatBDD {
        String tipusProducte = "";
        switch (tipus) {
            case 1:
                tipusProducte = "arbre";
                break;
            case 2:
                tipusProducte = "flor";
                break;
            case 3:
                tipusProducte = "decoració";
                break;
            default:
                System.out.println("Tipus de producte no vàlid");
        }
        String sqlCheck = "SELECT COUNT(*) FROM Producte WHERE tipus = '" + tipusProducte + "' AND nom = '" + nom + "'";
        try (Statement statement = connexio.getConnexio().createStatement();
             ResultSet resultSet = statement.executeQuery(sqlCheck)) {
            resultSet.next();
            int count = resultSet.getInt(1);
            if (count == 0) {
                throw new ProducteNoTrobatBDD("No s'ha trobat cap producte amb el nom: " + nom);
            }
        } catch (SQLException e) {
            System.out.println("Error en la comprovació del producte: " + e.getMessage());
        }

        String sqlRetirar = "DELETE FROM Producte WHERE tipus = '" + tipusProducte + "' AND nom = '" + nom + "'";
        connexio.executarSQL(sqlRetirar);

    }

    public void veureEstoc() {
        String sql = "SELECT * FROM Producte";
        try (Statement statement = connexio.getConnexio().createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            System.out.println("Estoc de productes:");
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String tipus = resultSet.getString("tipus");
                String nom = resultSet.getString("nom");
                double preu = resultSet.getDouble("preu");
                String atribut = resultSet.getString("atribut");
                System.out.println("ID: " + id + ", Tipus: " + tipus + ", Nom: " + nom + ", Preu: " + preu + ", Atribut: " + atribut);
            }
        } catch (SQLException e) {
            System.out.println("Error en recuperar l'estoc: " + e.getMessage());
        }
    }


}
