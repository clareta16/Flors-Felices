package models;

import connexio.MySqlConnexio;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Ticket {
    private int id;
    private String data;
    private String hora;
    private double preuTotal;
    private List<Producte> productesTicket;

    public Ticket(int id, String data, String hora, double preuTotal) {
        this.id = id;
        this.data = data;
        this.hora = hora;
        this.preuTotal = 0.00;
        this.productesTicket = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
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

    public void setProductesTicket(List<Producte> productesTicket) {
        this.productesTicket = productesTicket;
    }

    public void afegirProducte(Producte producte){
        productesTicket.add(producte); //cal afegir-lo o només guardant-ho a guardarProducte a la db ja està?
        guardarProducte(producte);
    }

    private void guardarProducte(Producte producte) {
        String query = "INSERT INTO productes (ticket_id, nom, tipus, preu, caracteristiques) VALUES (?, ?)";

        try (Connection connect = MySqlConnexio.getInstance().getConnexio();
             PreparedStatement statement = connect.prepareStatement(query)) {
            statement.setInt(1, this.id);
            statement.setDouble(2, producte.getPreu());

            statement.executeUpdate();

        } catch (SQLException e) {
            System.out.println("S'ha produit un error a l'intentar guardar el producte " + e.getMessage());
        }
    }

    public void imprimirTicket(Ticket ticket){
        System.out.println("Ticket de la compra " + ticket.getId());
        System.out.println(ticket.getData() + ticket.getHora());

        String query = "SELECT * FROM productes WHERE ticket_id = ?";

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

        String query = "SELECT SUM(preu) AS total FROM productes WHERE ticket_id = ?";

        try (Connection connect = MySqlConnexio.getInstance().getConnexio();
             PreparedStatement statement = connect.prepareStatement(query)) {
            statement.setInt(1, this.id);
            ResultSet resultat = statement.executeQuery();

            if (resultat.next()) { // el .next és per passar a la següent fila de resultats
                total = resultat.getDouble("total");
            }
        } catch (SQLException e) {
            System.out.println("Error al calcular el total " + e.getMessage());
        }
        return total;
    }

}