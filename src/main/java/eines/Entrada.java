package eines;

import models.Material;

import java.util.InputMismatchException;
import java.util.Scanner;

public class Entrada {

private static final Scanner entrada = new Scanner(System.in);
private static final String MISS_ERR_INT = "Hi ha un error de format. Escriu un número enter";
private static final String MISS_ERR_DOUBLE = "Hi ha un error de format. Escriu un número decimal";
private static final String MISS_ERR_STRING_BUIDA = "Hi ha un error de format. Escriu una paraula";


public static int entradaInt(String pregunta) {
    int resposta = 0;
    boolean correcte = false;
    while (!correcte) {
            System.out.println(pregunta);
            try {
                resposta = entrada.nextInt();
                correcte = true;
        } catch (InputMismatchException e){
                entrada.nextLine();
                System.out.println(MISS_ERR_INT);
        }
    }
    return resposta;
}

    public static double entradaDouble(String pregunta) {
        double resposta = 0;
        boolean correcte = false;
        while (!correcte) {
            System.out.println(pregunta);
            try {
                resposta = entrada.nextDouble();
                correcte = true;
            } catch (InputMismatchException e){
                entrada.nextLine();
                System.out.println(MISS_ERR_DOUBLE);
            }
        }
        return resposta;
    }

    public static String entradaBuida(String pregunta) {
        String resposta = "";
        boolean correcte = false;
        while (!correcte) {
            System.out.println(pregunta);
            try {
                resposta = entrada.nextLine();
                correcte = true;
            } catch (InputMismatchException e){
                System.out.println(MISS_ERR_STRING_BUIDA);
            }
        }
        return resposta;
    }
}
