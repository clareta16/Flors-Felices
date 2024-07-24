package main;

import excepcions.LlistaTicketsBuidaException;
import models.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Floristeria {
private String nom;
private List<Arbre> stockArbres;
private List<Flor> stockFlors;
private List<Decoracio> stockDecoracions;
private List<Ticket> tickets;


    public Floristeria(String nom, List<Arbre> stockArbres, List<Flor> stockFlors, List<Decoracio> stockDecoracions, List<Ticket> tickets) {
        this.nom = nom;
        this.stockArbres = stockArbres;
        this.stockFlors = stockFlors;
        this.stockDecoracions = stockDecoracions;
        this.tickets = tickets;
    }

    public void afegirArbre(Arbre arbre){

    }

    public void afegirFlor(Flor flor){

    }

    public void afegirDecoracio(Decoracio decoracio){

    }

    public void retirarArbre(Arbre arbre){

    }

    public void retirarFlor(Flor flor){

    }

    public void retirarDecoracio(Decoracio decoracio){

    }

    public void imprimirStock() {

    }

    public void imprimirStockQuantitats() {
        connexio.imprimirStock();
    }

    public void mostrarValorTotalFloristeria() {
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
        connexio.afegirTicketAmbProductes(ticket, nomsProductes);

        System.out.println("Ticket amb múltiples productes afegit amb ID: " + ticket.getId());
    }

    public void mostrarLlistaCompresAntigues() {
        List<Ticket> tickets = connexio.obtenirTotsElsTickets();

        if (tickets.isEmpty()) {
            System.out.println("No hi ha tickets emmagatzemats.");
        } else {
            for (Ticket ticket : tickets) {
                ticket.imprimirTicket();
                System.out.println();  // Espai per separar els tickets
            }
        }

    }

    public String visualitzarTotalDinersGuanyats() throws LlistaTicketsBuidaException {
        double dinersGuanyats = 0.00;

            if (tickets.isEmpty()) {
                throw new LlistaTicketsBuidaException("No hi ha cap ticket a la llista de tickets");
            }
            for (Ticket ticket : tickets) {
                dinersGuanyats += ticket.getPreuTotal();
            }

        return "La floristeria ha guanyat un total de " + dinersGuanyats + " euros";
    }


    }


