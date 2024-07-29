package main;

import connexio.MySqlConnexio;
import excepcions.LlistaTicketsBuidaException;
import excepcions.ProducteNoTrobatBDD;
import factories.*;
import models.*;

import java.sql.*;
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

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public Object crearProducte(String[] dades) {
        Object objecte = null;
        switch (dades[0]) {
            case "Arbre":
                ArbreFactory arbreFactory = new ArbreFactory();
                Arbre arbre = (Arbre) arbreFactory.crearProducte(dades[1], Double.parseDouble(dades[3]), Double.parseDouble(dades[2]));
                objecte = arbre;
                System.out.println("Arbre creat.");
                break;
            case "Flor":
                FlorFactory florFactory = new FlorFactory();
                Flor flor = (Flor) florFactory.crearProducte(dades[1], dades[3], Double.parseDouble(dades[2]));
                objecte = flor;
                System.out.println("Flor creada.");
                break;
            case "Decoracio":
                DecoracioFactory decoracioFactory = new DecoracioFactory();
                Decoracio decoracio = (Decoracio) decoracioFactory.crearProducte(dades[1], Material.valueOf(dades[3]), Double.parseDouble(dades[2]));
                objecte = decoracio;
                System.out.println("Decoració creada.");
                break;
        }
        return objecte;
    }

    public void afegirProducte(String[] dadesProducte) {
        Object objecteAfegir = crearProducte(dadesProducte);
        if (objecteAfegir != null) {
            String tipusAfegir = ((Producte) objecteAfegir).getClass().toString().replace("class models.", "");
            System.out.println("Tipus: " + tipusAfegir);
            String sqlAfegir = generarSQLAfegirProducte(tipusAfegir, objecteAfegir);
            System.out.println("SQL: " + sqlAfegir);
            MySqlConnexio.getInstance().executarSQL(sqlAfegir);
        }
    }

    public void retirarProducte(String tipusRetirar, String nomProducte) {
        try {
            if (trobarProducte(tipusRetirar, nomProducte)) {
                marcarProducteVenut(tipusRetirar, nomProducte);
                System.out.println("Producte retirat correctament.");
            }
        } catch (ProducteNoTrobatBDD e) {
            System.out.println(e.getMessage());
        }
    }

    public void marcarProducteVenut(String tipus, String nom) {
        String sqlMarcarVenut = "UPDATE Producte SET venut = TRUE WHERE id = (SELECT id FROM (SELECT id FROM Producte WHERE tipus = '" +
                tipus + "' AND nom = '" + nom + "' LIMIT 1) as subquery)";
        MySqlConnexio.getInstance().executarSQL(sqlMarcarVenut);
    }

    public boolean trobarProducte(String tipus, String nom) throws ProducteNoTrobatBDD {
        boolean trobat = false;
        String sqlCheck = "SELECT * FROM Producte WHERE tipus = '" + tipus + "' AND nom = '" + nom + "' AND venut = FALSE";
        try (Statement statement = MySqlConnexio.getInstance().getConnexio().createStatement();
             ResultSet resultSet = statement.executeQuery(sqlCheck)) {
            if (resultSet.next()) {
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
        switch (tipus.toLowerCase()) {
            case "arbre":
                Arbre arbre = (Arbre) producte;
                sql = "INSERT INTO Producte (tipus, nom, preu, atribut) VALUES ('arbre', '" + arbre.getNom() + "', " + arbre.getPreu() + ", 'alçada " + arbre.getAlcadaCm() + "cm')";
                break;
            case "flor":
                Flor flor = (Flor) producte;
                sql = "INSERT INTO Producte (tipus, nom, preu, atribut) VALUES ('flor', '" + flor.getNom() + "', " + flor.getPreu() + ", 'color " + flor.getColor() + "')";
                break;
            case "decoracio":
                Decoracio decoracio = (Decoracio) producte;
                sql = "INSERT INTO Producte (tipus, nom, preu, atribut) VALUES ('decoracio', '" + decoracio.getNom() + "', " + decoracio.getPreu() + ", 'material " + decoracio.getMaterial() + "')";
                break;
        }
        return sql;
    }

    public void veureEstoc() {
        String sql = "SELECT * FROM Producte WHERE venut = FALSE";
        try (Statement statement = MySqlConnexio.getInstance().getConnexio().createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            if (!resultSet.isBeforeFirst()) {
                System.out.println("No hi ha cap producte en estoc");
            } else {
                System.out.println("Estoc de productes:");
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String tipus = resultSet.getString("tipus");
                    String nom = resultSet.getString("nom");
                    double preu = resultSet.getDouble("preu");
                    String atribut = resultSet.getString("atribut");
                    System.out.println("ID: " + id + ", Tipus: " + tipus + ", Nom: " + nom + ", Preu: " + preu + ", Atribut: " + atribut);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error en recuperar l'estoc: " + e.getMessage());
        }
    }

    public void imprimirStockQuantitats() {
        MySqlConnexio.getInstance().imprimirStock();
    }

    public void imprimirValorTotal() {
        double valorTotal = MySqlConnexio.getInstance().obtenirValorTotalFloristeria();
        System.out.println("El valor total de la floristeria és: " + valorTotal + " euros.");
    }

    public void crearTicket(List<String> nomsProductes) {
        Ticket ticket = new Ticket();
        guardarTicket(ticket);  // Guarda el ticket primer per obtenir el seu ID
        double totalPreuTicket = 0;
        for (String nomProducte : nomsProductes) {
            Producte producte = obtenirProducteXNom(nomProducte);
            if (producte != null) {
                afegirProducteATicket(ticket, producte);
                marcarProducteComVenut(producte);
                totalPreuTicket += producte.getPreu();
            } else {
                System.out.println("Producte no trobat: " + nomProducte);
            }
        }
        ticket.setPreuTotal(totalPreuTicket);
        preuTotalTicketABaseDades(totalPreuTicket, ticket.getId());
    }

    private Producte obtenirProducteXNom(String nomProducte) {
        return MySqlConnexio.getInstance().obtenirProductePerNom(nomProducte);
    }

    private void afegirProducteATicket(Ticket ticket, Producte producte) {
        ticket.afegirProducteTicket(producte);
        MySqlConnexio.getInstance().afegirProducteATicket(ticket.getId(), producte);
    }


    private void marcarProducteComVenut(Producte producte) {
        MySqlConnexio.getInstance().marcarProducteComVenut(producte);
    }

    private void guardarTicket(Ticket ticket) {
        MySqlConnexio.getInstance().afegirTicket(ticket);
    }

    private void preuTotalTicketABaseDades(Double preuTotal, int id) {
        String sql = "UPDATE Ticket SET total = " + preuTotal + " WHERE id = " + id;
        MySqlConnexio.getInstance().executarSQL(sql);
    }

    public void mostrarLlistaCompresAntigues() {
//        String tickets = MySqlConnexio.getInstance().obtenirTotsElsTickets();
        MySqlConnexio.getInstance().obtenirTotsElsTickets();

        if (tickets.isEmpty()) {
            System.out.println("No hi ha tickets emmagatzemats.");
        } else {
            System.out.println(tickets);
        }
    }

    public String visualitzarTotalDinersGuanyats() throws LlistaTicketsBuidaException {
        Ticket ticket = new Ticket();

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
