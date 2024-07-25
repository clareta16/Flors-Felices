package main;

import connexio.MySqlConnexio;
import excepcions.LlistaTicketsBuidaException;
import excepcions.ProducteNoTrobatBDD;
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
        this.connexio = MySqlConnexio.getInstance();
    }

    public Object crearProducte(String[] dades) {
        Object objecte = null;
        switch(dades[0]){
            case "Arbre":
                ArbreFactory arbreFactory = new ArbreFactory();
                Arbre arbre = (Arbre) arbreFactory.crearProducte(dades[1], Double.parseDouble(dades[3]), Double.parseDouble(dades[2]));
                objecte = arbre;
                System.out.println("Arbre creat.");
                break;
            case "Flor":
                FlorFactory florFactory = new FlorFactory();
                Flor flor = (Flor) florFactory.crearProducte(dades[1], dades[3] , Double.parseDouble(dades[2]));
                objecte = flor;
                System.out.println("Flor creada.");
                break;
            case "Decoració":
                DecoracioFactory decoracioFactory = new DecoracioFactory();
                Decoracio decoracio = (Decoracio) decoracioFactory.crearProducte(dades[1], Material.valueOf(dades[3]), Double.parseDouble(dades[2]));
                objecte = decoracio;
                System.out.println("Decoració creada.");
                break;
        }
        return objecte;
    }

    public boolean trobarProducte(String tipus, String nom) throws ProducteNoTrobatBDD {
        boolean trobat = false;
        String sqlCheck = "SELECT * FROM Producte WHERE tipus = '" + tipus + "' AND nom = '" + nom + "'";
        try (Statement statement = connexio.getConnexio().createStatement();
             ResultSet resultSet = statement.executeQuery(sqlCheck)) {
            if(resultSet.next()){
                trobat = true;
            } else {
                throw new ProducteNoTrobatBDD("El producte no és a la base de dades");
            }
        } catch (SQLException e) {
            System.out.println("Error en la comprovació del producte: " + e.getMessage());
        }
        return trobat;
    }

    public String generarSQLAfegirProducte(String tipus, Object producte) {
        String sql = "";
        switch (tipus) {
            case "Arbre":
                Arbre arbre = (Arbre) producte;
                sql = "INSERT INTO Producte (tipus, nom, preu, atribut) VALUES ('arbre', '" + arbre.getNom() + "', " + arbre.getPreu() + ", 'alçada " + arbre.getAlcadaCm() + "cm')";
                break;
            case "Flor":
                Flor flor = (Flor) producte;
                sql = "INSERT INTO Producte (tipus, nom, preu, atribut) VALUES ('flor', '" + flor.getNom() + "', " + flor.getPreu() + ", 'color " + flor.getColor() + "')";
                break;
            case "Decoració":
                Decoracio decoracio = (Decoracio) producte;
                sql = "INSERT INTO Producte (tipus, nom, preu, atribut) VALUES ('decoració', '" + decoracio.getNom() + "', " + decoracio.getPreu() + ", 'material " + decoracio.getMaterial() + "')";
                break;
        }
        return sql;
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
