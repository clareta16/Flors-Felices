package models;

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
    productesTicket.add(producte);
    }

    public void imprimirTicket(){
        System.out.println("Ticket de compra  " + id);
        System.out.println(data + hora);

        for (Producte producte : productesTicket) {
            System.out.println(producte.caracteristiquesProducte());
        }

        System.out.println("Preu final: " + calcularTotal() + " euros");

    }

    public double calcularTotal(){
        for (Producte producte : productesTicket) {
           this.preuTotal += producte.getPreu();
        }
        return preuTotal;
    }
}
