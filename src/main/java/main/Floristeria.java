package main;

import connexio.MySqlConnexio;
import excepcions.LlistaTicketsBuidaException;
import factories.*;
import models.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class Floristeria {
    private MySqlConnexio connexio;
    private String nom;
    private List<Arbre> stockArbres;
    private List<Flor> stockFlors;
    private List<Decoracio> stockDecoracions;
    private List<Ticket> tickets;

    public Floristeria(String nom) {
        this.nom = nom;
        this.stockArbres = new ArrayList<>();
        this.stockFlors = new ArrayList<>();
        this.stockDecoracions = new ArrayList<>();
        this.tickets = new ArrayList<>();
    }

    public Object crearProducte(String[] dades) {
        Object objecte = null;
        switch(dades[0]){
            case "arbre":
                ArbreFactory arbreFactory = new ArbreFactory();
                Arbre arbre = (Arbre) arbreFactory.crearProducte(dades[1], Double.parseDouble(dades[3]), Double.parseDouble(dades[2]));
                objecte = arbre;
                System.out.println("Arbre creat.");
                break;
            case "flor":
                FlorFactory florFactory = new FlorFactory();
                Flor flor = (Flor) florFactory.crearProducte(dades[1], dades[3] , Double.parseDouble(dades[2]));
                objecte = flor;
                System.out.println("Flor creada.");
                break;
            case "decoració":
                DecoracioFactory decoracioFactory = new DecoracioFactory();
                Decoracio decoracio = (Decoracio) decoracioFactory.crearProducte(dades[1], Material.valueOf(dades[3]), Double.parseDouble(dades[2]));
                objecte = decoracio;
                System.out.println("Decoració creada.");
                break;
        }
        return objecte;
    }

//    public Object crearProducte(String opcio) {
//        Object objecte = null;
//        switch (opcio) {
//            case "arbre":
//                System.out.println("Quin és el nom de l'arbre?");
//                String nomArbre = scanner.nextLine();
//                System.out.println("Quina alçada té?");
//                double alcadaCm = scanner.nextDouble();
//                System.out.println("Quin és el preu?");
//                double preuArbre = scanner.nextDouble();
//                scanner.nextLine();
//                ArbreFactory arbreFactory = new ArbreFactory();
//                Arbre arbre = (Arbre) arbreFactory.crearProducte(nomArbre, alcadaCm, preuArbre);
//                objecte = arbre;
//                System.out.println(arbre + " creat.");
//                break;
//            case "flor":
//                System.out.println("Quin és el nom de la flor?");
//                String nomFlor = scanner.nextLine();
//                System.out.println("De quin color és?");
//                String color = scanner.nextLine();
//                System.out.println("Quin és el preu?");
//                double preuFlor = scanner.nextDouble();
//                scanner.nextLine();
//                FlorFactory florFactory = new FlorFactory();
//                Flor flor = (Flor) florFactory.crearProducte(nomFlor, color, preuFlor);
//                objecte = flor;
//                System.out.println(flor + " creada.");
//                break;
//            case "decoració":
//                System.out.println("Quin és el nom de la decoració?");
//                String nomDecoracio = scanner.nextLine();
//                System.out.println("De quin material és?");
//                String material = scanner.nextLine(); // Enum
//                System.out.println("Quin és el preu?");
//                double preuDecoracio = scanner.nextDouble();
//                scanner.nextLine();
//                DecoracioFactory decoracioFactory = new DecoracioFactory();
//                Decoracio decoracio = (Decoracio) decoracioFactory.crearProducte(nomDecoracio, material, preuDecoracio);
//                objecte = decoracio;
//                System.out.println(decoracio + " creada.");
//                break;
//            default:
//                System.out.println("Opció no vàlida, torna a escollir");
//        }
//        return objecte;
//    }

    public String generarSQLAfegirProducte(String tipus, Object producte) {
        String sql = "";
        switch (tipus) {
            case "arbre":
                Arbre arbre = (Arbre) producte;
                sql = "INSERT INTO Producte (tipus, nom, preu, atribut) VALUES ('arbre', '" + arbre.getNom() + "', " + arbre.getPreu() + ", 'alçada " + arbre.getAlcadaCm() + "cm')";
                break;
            case "flor":
                Flor flor = (Flor) producte;
                sql = "INSERT INTO Producte (tipus, nom, preu, atribut) VALUES ('flor', '" + flor.getNom() + "', " + flor.getPreu() + ", 'color " + flor.getColor() + "')";
                break;
            case "decoració":
                Decoracio decoracio = (Decoracio) producte;
                sql = "INSERT INTO Producte (tipus, nom, preu, atribut) VALUES ('decoració', '" + decoracio.getNom() + "', " + decoracio.getPreu() + ", 'material " + decoracio.getMaterial() + "')";
                break;
        }
        return sql;
    }

    public void imprimirStock() {

    }

    public void imprimirStockQuantitats() {
        connexio.imprimirStock();
    }

    public void imprimirValorTotal() {
        double valorTotal = connexio.obtenirValorTotalFloristeria();
        System.out.println("El valor total de la floristeria és: " + valorTotal + " euros.");
    }

    public void crearTicket() {
        Scanner scanner = new Scanner(System.in);

        // Crear un nou objecte Ticket sense preu total inicialment
        Ticket ticket = new Ticket();

        // Llista per emmagatzemar els noms dels productes del ticket
        List<String> nomsProductes = new ArrayList<>();

        boolean afegirMesProductes;
        do {
            // Demanar informació del producte a l'usuari
            System.out.print("Introdueix el nom del producte: ");
            String nomProducte = scanner.nextLine();

            // Afegir el nom del producte a la llista
            nomsProductes.add(nomProducte);

            // Preguntar si vol afegir més productes
            System.out.print("Vols afegir més productes? (si/no): ");
            afegirMesProductes = scanner.nextLine().equalsIgnoreCase("si");

        } while (afegirMesProductes);

        // Afegir el ticket amb els productes a la base de dades
//        connexio.afegirTicketAmbProductes(ticket, nomsProductes);

        System.out.println("Ticket amb múltiples productes afegit amb ID: " + ticket.getId());
    }

    public void mostrarLlistaCompresAntigues() {
        List<Ticket> tickets = connexio.obtenirTotsElsTickets();

        if (tickets.isEmpty()) {
            System.out.println("No hi ha tickets emmagatzemats.");
        } else {
            for (Ticket ticket : tickets) {
                ticket.imprimirTicket(ticket);
                System.out.println();  // Espai per separar els tickets
            }
        }
    }

    public String visualitzarTotalDinersGuanyats(Ticket ticket) throws LlistaTicketsBuidaException {
        if (tickets.isEmpty()) {
            throw new LlistaTicketsBuidaException("No hi ha cap ticket a la llista de tickets");
        }

        String query = "SELECT SUM(preuTotal) AS total FROM tickets";

        try (Connection connect = MySqlConnexio.getInstance().getConnexio();
             Statement statement = connect.createStatement();
             ResultSet resultat = statement.executeQuery(query)) {

            if (resultat.next()) {
                ticket.calcularTotal();
            }
        } catch (SQLException e) {
            System.out.println("Error al calcular el total " + e.getMessage());
        }

        return "La floristeria Flors Felices ha guanyat en total " + ticket.calcularTotal() + " euros";
    }
}
