package main;

import connexio.MySqlConnexio;
import excepcions.LlistaTicketsBuidaException;
import models.Arbre;
import models.Decoracio;
import models.Flor;
import models.Ticket;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        Map<String, Integer> comptadorArbres = new HashMap<>();
        Map<String, Integer> comptadorFlors = new HashMap<>();
        Map<String, Integer> comptadorDecoracions = new HashMap<>();

        for (Arbre arbre : stockArbres){
            comptadorArbres.put(arbre.toString(), comptadorArbres.getOrDefault(arbre.toString(), 0) + 1);
        }

        for (Flor flor : stockFlors){
            comptadorFlors.put(flor.toString(), comptadorFlors.getOrDefault(flor.toString(), 0) + 1);
        }

        for (Decoracio decoracio : stockDecoracions){
            comptadorDecoracions.put(decoracio.toString(), comptadorDecoracions.getOrDefault(decoracio.toString(), 0) + 1);
        }

        System.out.println("Stock d'Arbres: ");
        for (Map.Entry<String, Integer> entry : comptadorArbres.entrySet()) {
            System.out.println(entry.getKey() + " - Quantitat: " + entry.getValue());
        }

        System.out.println("Stock de Flors: ");
        for (Map.Entry<String, Integer> entry : comptadorFlors.entrySet()) {
            System.out.println(entry.getKey() + " - Quantitat: " + entry.getValue());
        }

        System.out.println("Stock de Decoracions: ");
        for (Map.Entry<String, Integer> entry : comptadorDecoracions.entrySet()) {
            System.out.println(entry.getKey() + " - Quantiat: " + entry.getValue());
        }

    }

    public void imprimirValorTotal() {
        double valorTotal = 0;

        for (Arbre arbre : stockArbres){
            valorTotal += arbre.getPreu();
        }

        for (Flor flor : stockFlors){
            valorTotal += flor.getPreu();
        }

        for (Decoracio decoracio : stockDecoracions){
            valorTotal += decoracio.getPreu();
        }

        System.out.println(String.format("El valor total de tot l'stock és de: %.2f euros.", valorTotal));

    }

    public void crearTicket() {
        Ticket ticket = new Ticket();
    }

    public void mostrarLlistaCompresAntigues() {
        if(tickets.isEmpty()) {
            System.out.println("No hi ha compres antigues.");
        } else {
            System.out.println("Llista de compres antigues: ");
            for (Ticket ticket : tickets){
                System.out.println(ticket);
            }
        }

    }

    public String visualitzarTotalDinersGuanyats() throws LlistaTicketsBuidaException {
        double dinersGuanyats = 0.00;

        if (tickets.isEmpty()) {
            throw new LlistaTicketsBuidaException("No hi ha cap ticket a la llista de tickets");
        }

        String query = "SELECT SUM(preuTotal) AS total FROM tickets";

        try (Connection connect = MySqlConnexio.getInstance().getConnexio();
             Statement statement = connect.createStatement();
             ResultSet resultat = statement.executeQuery(query)) {

            if (resultat.next()) {
                dinersGuanyats = resultat.getDouble("total");
            }
        } catch (SQLException e) {
            System.out.println("Error al calcular el total " + e.getMessage());
        }

        return "La floristeria Flors Felices ha guanyat en total " + dinersGuanyats + " euros";
    }
}
