package connexio;

import java.io.*;
import java.sql.*;
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
}