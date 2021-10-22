package com;

import java.util.Scanner;

public class Main {
    public static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        Database database = Database.getInstance();
        People people = People.getInstance();
        scanner.close();
    }


}
