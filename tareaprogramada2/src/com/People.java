package com;

import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class People {
    private static People people;
    private List<Person> peopleList;
    private static String className;

    private People() {
        className = "People";
        peopleList = new ArrayList<Person>();
        System.out.printf("[%s] - Creating instance..\n", className);
        loadData();
    }

    public static People getInstance() {
        if(people == null)
            people = new People();
        return people;
    }

    private void loadData() {
        // get file path
        System.out.printf("[%s] - Please specify the path to the csv file: \n", className);
        String pathFromInput = Main.scanner.nextLine();
        Path path = Paths.get(pathFromInput);

        // try reading it
        try (BufferedReader br = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            System.out.printf("[%s] - Trying to load data from file at %s\n", className, pathFromInput);
            // read line
            String line = br.readLine();
            line = br.readLine(); // skip header
            int counter = 0;

            // read while we haven't reached a null line
            while(line != null) {
                String[] data = line.split(",");
                peopleList.add(new Person(data[0], data[1], data[2], data[3], data[4], data[5], data[6]));
                line = br.readLine();
                ++counter;
            }

            // check if success or not
            if(counter > 0)
                System.out.printf("[%s] - Added %d people to the list\n", className, counter);
            else {
                System.out.printf("[%s] - Failed to add people to the list, exiting\n", className);
                System.exit(-2);
            }
        } catch(Exception ex) {
            System.out.printf("[%s] - An exception occurred while loading the CSV data. Details: %s\n", className, ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void printPeopleList() {
        for(int i = 0; i < peopleList.size(); ++i) {
            peopleList.get(i).printPersonInformation();
        }
    }

}
