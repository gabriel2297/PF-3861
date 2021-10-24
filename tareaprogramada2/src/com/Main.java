package com;

import java.util.Scanner;

public class Main {
    public static final Scanner scanner = new Scanner(System.in);
    private static final String className = "Main";
    private static People people;

    public static void main(String[] args) {
        clearScreen();
        System.out.println("TAREA PROGRAMADA 2 - PF-3861 BASES DE DATOS AVANZADAS - GABRIEL UMAÑA (C09913)");
        people = People.getInstance();
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
                    people.loadData();
                    break;
                case 2:
                    people.searchByFullName();
                    break;
                case 3:
                    people.searchContactInformation();
                    break;
                case 4:
                    people.searchByGender();
                    break;
                case 5:
                    people.searchByLastname();
                    break;
                case 6:
                    people.deleteByLastName();
                    break;
                case 7:
                    people.deleteAll();
                    break;
            }
        } while(option != 8);
    }

    private static void printMenu() {
        System.out.println("--------- MENU ---------");
        System.out.println("1. Load data from CSV");
        System.out.println("2. Search user by name and lastname");
        System.out.println("3. Search user contact information");
        System.out.println("4. Search contact information by gender");
        System.out.println("5. Search users by lastname");
        System.out.println("6. Delete users by lastname");
        System.out.println("7. Delete all users");
        System.out.println("8. Exit program");
    }

}
