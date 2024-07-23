package main;

import Excepcions.LlistaTicketsBuidaException;
import models.Ticket;

import java.util.List;

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

    public void afegirFlor(Flor flor{

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

    }

    public void imprimirValorTotal() {

    }

    public void crearTicket() {

    }

    public void mostrarLlistaCompresAntigues() {

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
