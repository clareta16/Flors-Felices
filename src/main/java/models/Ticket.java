package models;

import connexio.MySqlConnexio;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Ticket {
    private int id;
    private LocalDate data;
    private double preuTotal;
    private List<Producte> productesTicket;

    public Ticket() {
        this.data = LocalDate.now();
        this.productesTicket = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id  = id;
    }

    public LocalDate getData() {
        return this.data;
    }

    public double getPreuTotal() {
        return preuTotal;
    }

    public void setPreuTotal(double preuTotal) {
        this.preuTotal = preuTotal;
    }

    public List<Producte> getProductesTicket() {
        return productesTicket;
    }

    public void afegirProducteTicket(Producte producte) {
        this.productesTicket.add(producte);
        this.preuTotal += producte.getPreu();
    }

    private void guardarProducteTicket(Producte producte) {
        String query = "INSERT INTO TicketProducte (ticket_id, producte_id) VALUES (?, ?)";
        try (Connection connect = MySqlConnexio.getInstance().getConnexio();
             PreparedStatement statement = connect.prepareStatement(query)) {
            statement.setInt(1, this.id);
            statement.setInt(2, producte.getId());  // Això ha de ser getId()

            statement.executeUpdate();  // Crida el mètode sense paràmetres
        } catch (SQLException e) {
            System.out.println("S'ha produit un error a l'intentar guardar el producte " + e.getMessage());
        }
    }


    public void imprimirTicket(Ticket ticket){
        System.out.println("Ticket de la compra " + ticket.getId());
        System.out.println(ticket.getData());

        String query = "SELECT * FROM producte WHERE id = ?";

        try (Connection connect = MySqlConnexio.getInstance().getConnexio();
             PreparedStatement statement = connect.prepareStatement(query)) {
             statement.setInt(1, this.id);
            ResultSet resultat = statement.executeQuery();

            while (resultat.next()) {
                double preu = resultat.getDouble("preu");
                String nom = resultat.getString("nom");
                System.out.println("El producte " +nom + " té un preu de " + preu + " euros");
            }
        } catch (SQLException e) {
            System.out.println("Hi ha hagut un error a l'imprimir el ticket " + e.getMessage());
        }

        System.out.println("El preu final és de " + calcularTotal() + " euros");
    }

    public double calcularTotal(){
        double total = 0.0;

        String query = "SELECT SUM(preu) AS total FROM producte WHERE id = ?";

        try (Connection connect = MySqlConnexio.getInstance().getConnexio();
             PreparedStatement statement = connect.prepareStatement(query)) {
            statement.setInt(1, this.id);
            ResultSet resultat = statement.executeQuery();

            if (resultat.next()) { // el .next és per passar a la següent fila de resultats
                total = resultat.getDouble("total");
            }
        } catch (SQLException e) {
            System.out.println("Error en calcular el total " + e.getMessage());
        }
        return total;
    }

}