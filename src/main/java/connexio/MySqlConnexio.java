package connexio;

import models.*;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

public class MySqlConnexio {
    private static MySqlConnexio instancia;
    private static String BASEDADES;
    private static String URL;
    private static String USUARI;
    private static String CONTRASENYA;
    private Connection connexio;

    private MySqlConnexio() {
        carregarPropietats();
        connectarMySql();
    }

    public Connection getConnexio() {
        try {
            if (connexio == null || connexio.isClosed()) {
                connectarMySql();
            }
        } catch (SQLException e) {
            System.out.println("Error en comprovar la connexió: " + e.getMessage());
        }
        return connexio;
    }

    public static synchronized MySqlConnexio getInstance() {
        if (instancia == null) {
            instancia = new MySqlConnexio();
        }
        return instancia;
    }

    private void carregarPropietats() {
        Properties properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("db.properties")) {
            if (input == null) {
                System.out.println("Ho sento, no hem pogut trobar l'arxiu db.properties");
                return;
            }

            properties.load(input);

            // Get the property values
            BASEDADES = properties.getProperty("BDD");
            URL = properties.getProperty("URL");
            USUARI = properties.getProperty("Usuari");
            CONTRASENYA = properties.getProperty("Contrasenya");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void connectarMySql() {
        try {
            connexio = DriverManager.getConnection(URL + "/" + BASEDADES + "?useSSL=true", USUARI, CONTRASENYA);
            String camiContentRoot = "src" + File.separator + "main" + File.separator + "java" + File.separator + "connexio" + File.separator + "crearBDD.txt";
            if (!hiHaTaules()) {
                executarSQLdArxiu(camiContentRoot);
            }
        } catch (SQLException e) {
            System.out.println("Error en connectar a la base de dades: " + e.getMessage());
        }
    }

    private boolean hiHaTaules() {
        boolean taulesExisteixen = false;
        String query = "SELECT COUNT(*) AS table_count FROM information_schema.tables WHERE table_schema = '" + BASEDADES + "'";
        try (Statement statement = connexio.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            if (resultSet.next()) {
                int tableCount = resultSet.getInt("table_count");
                taulesExisteixen = tableCount > 0;
            }
        } catch (SQLException e) {
            System.out.println("Error en comprovar si hi ha taules: " + e.getMessage());
        }
        return taulesExisteixen;
    }

    public void executarSQLdArxiu(String filePath) {
        try (Scanner scanner = new Scanner(new File(filePath));
             Statement statement = getConnexio().createStatement()) {
            scanner.useDelimiter(";");
            while (scanner.hasNext()) {
                String command = scanner.next().trim();
                if (!command.isEmpty()) {
                    statement.execute(command);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Error en obrir l'arixu: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Error en els comandaments sql: " + e.getMessage());
        }
    }

    public void executarSQL(String sql) {
        try (Statement statement = getConnexio().createStatement()) {
            if (!sql.isEmpty()) {
                statement.execute(sql);
            }
        } catch (SQLException e) {
            System.out.println("Error en els comandaments sql: " + e.getMessage());
        }
    }

    public String sqlQuery = "SELECT " +
            "t.id AS ticket_id, " +
            "t.data AS ticket_data, " +
            "t.total AS ticket_total, " +
            "tp.producte_id AS producte_id, " +
            "p.nom AS producte_nom, " +
            "p.preu AS producte_preu, " +
            "p.tipus AS producte_tipus, " +
            "p.atribut AS producte_atribut " +
            "FROM " +
            "Ticket t " +
            "LEFT JOIN " +
            "TicketProducte tp " +
            "ON " +
            "t.id = tp.ticket_id " +
            "LEFT JOIN " +
            "Producte p " +
            "ON " +
            "tp.producte_id = p.id;";

    public void obtenirTotsElsTickets() {
        String ticket;
        String liniesTicket = "";
        String ticketSencer;
        try (Statement statement = getConnexio().createStatement();
             ResultSet resultSet = statement.executeQuery(sqlQuery)) {
            while (resultSet.next()) {
                int ticketId = resultSet.getInt("ticket_id");
                Date ticketData = resultSet.getDate("ticket_data");
                double ticketTotal = resultSet.getDouble("ticket_total");

                int producteId = resultSet.getInt("producte_id");
                String producteNom = resultSet.getString("producte_nom");
                double productePreu = resultSet.getDouble("producte_preu");
                String producteTipus = resultSet.getString("producte_tipus");
                String producteAtribut = resultSet.getString("producte_atribut");

                ticket = "Ticket ID " + ticketId + ", Data: " + ticketData + ", Total: " + ticketTotal;

                if (producteId > 0) {
                    liniesTicket = "  - Producte ID: " + producteId +
                            "    Nom: " + producteNom +
                            "    Preu: " + productePreu +
                            "    Tipus: " + producteTipus +
                            "    Atribut: " + producteAtribut;
                } else {
                    System.out.println("  - No hi ha productes associats a aquest tiquet.");
                }
                ticketSencer = ticket + liniesTicket;
                System.out.println(ticketSencer);
            }
        } catch (SQLException e) {
            System.out.println("Error en recuperar els tickets: " + e.getMessage());
        }
    }

    public void imprimirStock() {
        String sql = "SELECT * FROM Producte";
        try (Statement statement = getConnexio().createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            List<String> arbres = new ArrayList<>();
            List<String> flors = new ArrayList<>();
            List<String> decoracions = new ArrayList<>();

            while (resultSet.next()) {
                boolean venut = resultSet.getBoolean("venut");
                if (!venut) {
                    int id = resultSet.getInt("id");
                    String tipus = resultSet.getString("tipus");
                    String nom = resultSet.getString("nom");
                    double preu = resultSet.getDouble("preu");
                    String atribut = resultSet.getString("atribut");

                    String producteInfo = "ID: " + id + ", Nom: " + nom + ", Preu: " + preu + ", Atribut: " + atribut;

                    switch (tipus.toLowerCase()) {
                        case "arbre":
                            arbres.add(producteInfo);
                            break;
                        case "flor":
                            flors.add(producteInfo);
                            break;
                        case "decoració":
                            decoracions.add(producteInfo);
                            break;
                        default:
                            System.out.println("Tipus de producte desconegut: " + tipus);
                    }
                }

            }

            System.out.println("Arbres:");
            if (arbres.isEmpty()) {
                System.out.println("No hi ha cap arbre en estoc.");
            } else {
                for (String arbre : arbres) {
                    System.out.println(arbre);
                }
            }

            System.out.println("\nFlors:");
            if (flors.isEmpty()) {
                System.out.println("No hi ha cap flor en estoc.");
            } else {
                for (String flor : flors) {
                    System.out.println(flor);
                }
            }

            System.out.println("\nDecoracions:");
            if (decoracions.isEmpty()) {
                System.out.println("No hi ha cap decoració en estoc.");
            } else {
                for (String decoracio : decoracions) {
                    System.out.println(decoracio);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error en recuperar els productes: " + e.getMessage());
        }
    }

    public double obtenirValorTotalFloristeria() {
        String sql = "SELECT SUM(preu) AS valor_total FROM Producte WHERE venut = FALSE";
        double valorTotal = 0;

        try (Statement statement = getConnexio().createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            if (resultSet.next()) {
                valorTotal = resultSet.getDouble("valor_total");
            }
        } catch (SQLException e) {
            System.out.println("Error en calcular el valor total de la floristeria: " + e.getMessage());
        }
        return valorTotal;
    }

    public double obtenirTotalVendes() {
        String sql = "SELECT SUM(preu) AS valor_total FROM Producte WHERE venut = TRUE";
        double valorTotal = 0;

        try (Statement statement = getConnexio().createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            if (resultSet.next()) {
                valorTotal = resultSet.getDouble("valor_total");
            }
        } catch (SQLException e) {
            System.out.println("Error en calcular el valor total de vendes de la floristeria: " + e.getMessage());
        }
        return valorTotal;
    }

    public void afegirTicket(Ticket ticket) {
        String sql = "INSERT INTO Ticket (data, total) VALUES (?, ?)";
        try (Connection connect = getConnexio();
             PreparedStatement statement = connect.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setDate(1, Date.valueOf(ticket.getData()));
            statement.setDouble(2, ticket.getPreuTotal());
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    ticket.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("No s'ha pogut obtenir l'ID generat per al ticket.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error en afegir el ticket: " + e.getMessage());
        }
    }


    public void marcarProducteComVenut(Producte producte) {
        String sql = "UPDATE Producte SET venut = 1 WHERE id = ?";
        try (Connection connect = getConnexio();
             PreparedStatement statement = connect.prepareStatement(sql)) {
            statement.setInt(1, producte.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error en marcar el producte com a venut: " + e.getMessage());
        }
    }

    public Producte obtenirProductePerNom(String nom) {
        String sql = "SELECT * FROM Producte WHERE nom = ? AND venut = 0";
        try (Connection connect = getConnexio();
             PreparedStatement statement = connect.prepareStatement(sql)) {
            statement.setString(1, nom);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String tipus = resultSet.getString("tipus");
                    double preu = resultSet.getDouble("preu");
                    String atribut = resultSet.getString("atribut");

                    switch (tipus.toLowerCase()) {
                        case "arbre":
                            Arbre arbre = new Arbre(nom, Double.parseDouble(atribut.replace("alçada ", "").replace("cm", "")), preu);
                            arbre.setId(id);
                            return arbre;
                        case "flor":
                            Flor flor = new Flor(nom, atribut.replace("color ", ""), preu);
                            flor.setId(id);
                            return flor;
                        case "decoracio":
                            Decoracio decoracio = new Decoracio(nom, Material.valueOf(atribut.replace("material ", "").toUpperCase()), preu);
                            decoracio.setId(id);
                            return decoracio;
                        default:
                            throw new IllegalArgumentException("Tipus de producte desconegut: " + tipus);
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error en obtenir el producte per nom: " + e.getMessage());
        }
        return null;
    }

    public void afegirProducteATicket(int ticketId, Producte producte) {
        String sql = "INSERT INTO TicketProducte (ticket_id, producte_id) VALUES (?, ?)";
        try (Connection connect = getConnexio();
             PreparedStatement statement = connect.prepareStatement(sql)) {
            statement.setInt(1, ticketId);
            statement.setInt(2, producte.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error en afegir el producte al ticket: " + e.getMessage());
        }
    }

}