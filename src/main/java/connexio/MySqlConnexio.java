package connexio;

import models.*;

import java.io.*;
import java.nio.file.Paths;
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
            System.out.println("Connectat a la base de dades");
            String camiContentRoot = "src" + File.separator + "main" + File.separator + "java" + File.separator + "connexio" + File.separator + "crearBDD.txt";
//            Paths path = Paths.get(camiContentRoot);
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

    public static synchronized MySqlConnexio getInstance() {
        if (instancia == null) {
            instancia = new MySqlConnexio();
        }
        return instancia;
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

    public List<Ticket> obtenirTotsElsTickets() {
        List<Ticket> tickets = new ArrayList<>();
        String sql = "SELECT * FROM Ticket";

        try (Statement statement = getConnexio().createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String data = resultSet.getString("data");
                double preuTotal = resultSet.getDouble("total");

                Ticket ticket = new Ticket();
                tickets.add(ticket);
            }
        } catch (SQLException e) {
            System.out.println("Error en recuperar els tickets: " + e.getMessage());
        }
        return tickets;
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
//            try (ResultSet resultSet = statement.executeQuery()) {
//                if (resultSet.next()) {
//                    // Crear objecte Producte a partir de les dades del ResultSet
//                    return new Producte(resultSet.getInt("id"), resultSet.getString("nom"), resultSet.getDouble("preu"));
//                }
//            }
        } catch (SQLException e) {
            System.out.println("Error en obtenir el producte per nom: " + e.getMessage());
        }
        return null;
    }

//    public void afegirTicketAmbProductes(Ticket ticket, List<String> nomsProductes) {
//        String ticketQuery = "INSERT INTO Ticket (data, total) VALUES (?, ?)";
//        String retirarProducteQuery = "UPDATE Producte SET venut = TRUE WHERE id = ?";
//        String afegirProducteATicketQuery = "INSERT INTO TicketProducte (ticket_id, producte_id) VALUES (?, ?)";
//
//        try (Connection connect = getConnexio();
//             PreparedStatement ticketStatement = connect.prepareStatement(ticketQuery, Statement.RETURN_GENERATED_KEYS);
//             PreparedStatement retirarProducteStatement = connect.prepareStatement(retirarProducteQuery);
//             PreparedStatement afegirProducteATicketStatement = connect.prepareStatement(afegirProducteATicketQuery)) {
//
//            // Desactivar l'auto-commit per començar una transacció
//            connect.setAutoCommit(false);
//
//            // Inserir el ticket a la base de dades
//            ticketStatement.setDate(1, Date.valueOf(ticket.getData()));
//            ticketStatement.setDouble(2, ticket.getPreuTotal());
//            ticketStatement.executeUpdate();
//
//            // Obtenir l'ID del ticket generat
//            try (ResultSet generatedKeys = ticketStatement.getGeneratedKeys()) {
//                if (generatedKeys.next()) {
//                    int ticketId = generatedKeys.getInt(1);
//                    ticket.setId(ticketId);
//
//                    // Inserir cada producte al ticket i marcar-lo com venut
//                    for (String nomProducte : nomsProductes) {
//                        // Obtenir el producte pel nom
//                        Producte producte = obtenirProductePerNom(nomProducte);
//                        if (producte != null) {
//                            // Afegir producte al ticket
//                            ticket.afegirProducteTicket(producte);
//
//                            // Marcar producte com venut
//                            retirarProducteStatement.setInt(1, producte.getId());
//                            retirarProducteStatement.executeUpdate();
//
//                            // Afegir el producte a la taula TicketProducte
//                            afegirProducteATicketStatement.setInt(1, ticketId);
//                            afegirProducteATicketStatement.setInt(2, producte.getId());
//                            afegirProducteATicketStatement.executeUpdate();
//                        } else {
//                            System.out.println("Producte no trobat: " + nomProducte);
//                        }
//                    }
//
//                    // Cometre la transacció
//                    connect.commit();
//                } else {
//                    throw new SQLException("Error al obtenir l'ID generat del ticket.");
//                }
//            } catch (SQLException e) {
//                // Fer rollback si hi ha algun error durant l'obtenció de claus generades o la inserció de productes
//                connect.rollback();
//                throw e;
//            } finally {
//                // Tornar a activar l'auto-commit
//                connect.setAutoCommit(true);
//            }
//        } catch (SQLException e) {
//            System.out.println("Error en afegir el ticket amb productes: " + e.getMessage());
//        }
//    }


//    public Producte obtenirProductePerNom(String nom) {
//        String query = "SELECT * FROM Producte WHERE nom = ? AND venut = FALSE LIMIT 1";
//        try (Connection connect = getConnexio();
//             PreparedStatement statement = connect.prepareStatement(query)) {
//            statement.setString(1, nom);
//            try (ResultSet resultSet = statement.executeQuery()) {
//                if (resultSet.next()) {
//                    int id = resultSet.getInt("id");
//                    String tipus = resultSet.getString("tipus");
//                    double preu = resultSet.getDouble("preu");
//                    String atribut = resultSet.getString("atribut");
//
//                    switch (tipus.toLowerCase()) {
//                        case "arbre":
//                            Arbre arbre = new Arbre(nom, Double.parseDouble(atribut.replace("alçada ", "").replace("cm", "")), preu);
//                            arbre.setId(id);
//                            return arbre;
//                        case "flor":
//                            Flor flor = new Flor(nom, atribut.replace("color ", ""), preu);
//                            flor.setId(id);
//                            return flor;
//                        case "decoracio":
//                            Decoracio decoracio = new Decoracio(nom, Material.valueOf(atribut.replace("material ", "").toUpperCase()), preu);
//                            decoracio.setId(id);
//                            return decoracio;
//                        default:
//                            throw new IllegalArgumentException("Tipus de producte desconegut: " + tipus);
//                    }
//                }
//            }
//        } catch (SQLException e) {
//            System.out.println("Error en obtenir el producte per nom: " + e.getMessage());
//        }
//        return null;
//    }

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