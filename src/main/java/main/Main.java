package main;

public class Main {
    public static void main(String[] args) {
        Floristeria floristeria = new Floristeria("Flors-Felices");
        Menu menu = new Menu(floristeria);
        boolean exit;
        do {
            exit = menu.menuPrincipal();
        } while (!exit);
    }
}
