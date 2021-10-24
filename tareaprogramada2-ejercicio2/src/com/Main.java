package com;
import java.util.Scanner;

public class Main {
    public static final Scanner scanner = new Scanner(System.in);
    private static final String className = "Main";
    private static Database db;

    public static void main(String[] args) {
        clearScreen();
        System.out.println("TAREA PROGRAMADA 2 ejercicio 2 - PF-3861 BASES DE DATOS AVANZADAS - GABRIEL UMAÑA (C09913)");
        db = Database.getInstance();
        controlProgramFlow();
        scanner.close();
        System.out.println("Program finished");
    }

    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    private static void controlProgramFlow() {
        int option = 0;
        do {
            printMenu();
            System.out.printf("[%s] - Choose an option: \n", className);
            option = Integer.parseInt(scanner.nextLine());
            switch(option) {
                case 1:
                    db.insertValuesIntoDb();
                    break;
                case 2:
                    db.parallelInsertValuesIntoDb();
                    break;
                case 3:
                    db.deleteValuesFromDb();
                    break;
                case 4:
                    db.showResults();
                    break;
            }
        } while(option != 5);
    }

    private static void printMenu() {
        System.out.println("--------- MENU ---------");
        System.out.println("1. Load data concurrently (will first delete any data)");
        System.out.println("2. Load data in parallel (will first delete any data)");
        System.out.println("3. Delete current data");
        System.out.println("4. Show results");
        System.out.println("5. Exit program");
    }


}
