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
            if (!hiHaTaules()) {
                executarSQLdArxiu("C:\\Users\\smcri\\Desktop\\CODE\\HappyFlowers\\src\\main\\java\\connexio\\crearBDD.txt");
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
                    System.out.println("Executed: " + command);
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
                System.out.println("Executat: " + sql);
            }
        } catch (SQLException e) {
            System.out.println("Error en els comandaments sql: " + e.getMessage());
        }
    }

    public List<Ticket> obtenirTotsElsTickets() {
        List<Ticket> tickets = new ArrayList<>();
        String sql = "SELECT * FROM tickets";

        try (Statement statement = getConnexio().createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String data = resultSet.getString("data");
                String hora = resultSet.getString("hora");
                double preuTotal = resultSet.getDouble("preuTotal");

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

            System.out.println("Arbres:");
            for (String arbre : arbres) {
                System.out.println(arbre);
            }

            System.out.println("\nFlors:");
            for (String flor : flors) {
                System.out.println(flor);
            }

            System.out.println("\nDecoracions:");
            for (String decoracio : decoracions) {
                System.out.println(decoracio);
            }

        } catch (SQLException e) {
            System.out.println("Error en recuperar els productes: " + e.getMessage());
        }
    }

    public double obtenirValorTotalFloristeria() {
        String sql = "SELECT SUM(preu) AS valor_total FROM Producte";
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

    public void afegirTicketAmbProductes(Ticket ticket, List<String> nomsProductes) {
        String ticketQuery = "INSERT INTO Ticket (data, total) VALUES (?, ?)";
        String retirarProducteQuery = "DELETE FROM Producte WHERE id = ?";

        try (Connection connect = getConnexio();
             PreparedStatement ticketStatement = connect.prepareStatement(ticketQuery, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement retirarProducteStatement = connect.prepareStatement(retirarProducteQuery)) {

            // Desactivar l'auto-commit per començar una transacció
            connect.setAutoCommit(false);

            // Inserir el ticket a la base de dades
            ticketStatement.setDate(1, Date.valueOf(ticket.getData()));
            ticketStatement.setDouble(2, ticket.getPreuTotal());
            ticketStatement.executeUpdate();

            // Obtenir l'ID del ticket generat
            try (ResultSet generatedKeys = ticketStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int ticketId = generatedKeys.getInt(1);
                   // ticket.getId(ticketId);

                    // Inserir cada producte al ticket i retirar-lo del stock
                    for (String nomProducte : nomsProductes) {
                        // Obtenir el producte pel nom
                        Producte producte = obtenirProductePerNom(nomProducte);
                        if (producte != null) {
                            // Afegir producte al ticket
                            ticket.afegirProducteTicket(producte);

                            // Retirar producte del stock
                            retirarProducteStatement.setInt(1, producte.getId());
                            retirarProducteStatement.executeUpdate();
                        } else {
                            System.out.println("Producte no trobat: " + nomProducte);
                        }
                    }

                    // Cometre la transacció
                    connect.commit();
                } else {
                    throw new SQLException("Error al obtenir l'ID generat del ticket.");
                }
            } catch (SQLException e) {
                // Fer rollback si hi ha algun error durant l'obtenció de claus generades o la inserció de productes
                connect.rollback();
                throw e;
            } finally {
                // Tornar a activar l'auto-commit
                connect.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.out.println("Error en afegir el ticket amb productes: " + e.getMessage());
        }
    }

    public Producte obtenirProductePerNom(String nom) {
        String query = "SELECT * FROM Producte WHERE nom = ? LIMIT 1";
        try (Connection connect = getConnexio();
             PreparedStatement statement = connect.prepareStatement(query)) {
            statement.setString(1, nom);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String tipus = resultSet.getString("tipus");
                    double preu = resultSet.getDouble("preu");
                    String atribut = resultSet.getString("atribut");

                    switch (tipus.toLowerCase()) {
                        case "arbre":
                            return new Arbre(nom, Double.parseDouble(atribut), preu);
                        case "flor":
                            return new Flor(nom, atribut, preu);
                        case "decoracio":
                            return new Decoracio(nom, Material.valueOf(atribut.toUpperCase()), preu);
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
}