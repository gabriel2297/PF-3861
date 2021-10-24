package com;

import oracle.kv.*;

import javax.xml.crypto.Data;
import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Array;
import java.sql.SQLOutput;
import java.util.*;

public class People {
    private static People people;
    private static String className;
    private KVStore kvStore;

    private People() {
        className = "People";
        System.out.printf("[%s] - Creating instance..\n", className);
        Database db = Database.getInstance();
        kvStore = db.getKvStore();
    }

    public static People getInstance() {
        if(people == null)
            people = new People();
        return people;
    }

    public void loadData() {
        Main.clearScreen();
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
                insertPerson(new Person(data[0], data[1], data[2], data[3], data[4], data[5], data[6]));
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

    public void insertPerson(Person person) {
        ArrayList<String> majorComponents = new ArrayList<String>();
        ArrayList<String> minorComponents = new ArrayList<String>();

        majorComponents.add(className);
        majorComponents.add(person.getLn());
        majorComponents.add(person.getFn());
        majorComponents.add(person.getId());

        System.out.printf("[%s] - Inserting %s to DB\n", className, majorComponents.toString());

        // insert id
        minorComponents.add("id");
        insertProperty(majorComponents, minorComponents, Value.createValue(person.getId().getBytes()));

        // insert firstname
        minorComponents.remove(0);
        minorComponents.add("fn");
        insertProperty(majorComponents, minorComponents, Value.createValue(person.getFn().getBytes()));

        // insert lastname
        minorComponents.remove(0);
        minorComponents.add("ln");
        insertProperty(majorComponents, minorComponents, Value.createValue(person.getLn().getBytes()));

        // insert gender
        insertGenderData(person);
        minorComponents.remove(0);
        minorComponents.add("gender");
        insertProperty(majorComponents, minorComponents, Value.createValue(person.getGender().getBytes()));

        // insert birthDate
        minorComponents.remove(0);
        minorComponents.add("birthDate");
        insertProperty(majorComponents, minorComponents, Value.createValue(person.getBirthDate().getBytes()));

        // insert phoneNumber
        minorComponents.remove(0);
        minorComponents.add("phoneNumber");
        insertProperty(majorComponents, minorComponents, Value.createValue(person.getPhoneNumber().getBytes()));

        // insert email
        minorComponents.remove(0);
        minorComponents.add("email");
        insertProperty(majorComponents, minorComponents, Value.createValue(person.getEmail().getBytes()));
    }

    private void insertGenderData(Person person) {
        ArrayList<String> majorComponents = new ArrayList<String>();
        ArrayList<String> minorComponents = new ArrayList<String>();

        majorComponents.add(person.getGender());
        majorComponents.add(person.getLn());
        majorComponents.add(person.getFn());
        majorComponents.add(person.getId());

        // insert name
        minorComponents.add("fullName");
        insertProperty(majorComponents, minorComponents, Value.createValue(person.getFullName().getBytes()));

        // insert phoneNumber
        minorComponents.remove(0);
        minorComponents.add("phoneNumber");
        insertProperty(majorComponents, minorComponents, Value.createValue(person.getPhoneNumber().getBytes()));

        // insert email
        minorComponents.remove(0);
        minorComponents.add("email");
        insertProperty(majorComponents, minorComponents, Value.createValue(person.getEmail().getBytes()));

    }

    private void insertProperty(ArrayList<String> major, ArrayList<String> minor, Value value) {
        Key key = Key.createKey(major, minor);
        kvStore.put(key, value);
    }

    private void deletePeople() {
        System.out.printf("[%s] - running delete people\n", className);
        ArrayList<String> major = new ArrayList<String>();
        major.add("People");
        Key key = Key.createKey(major);

        Iterator<KeyValueVersion> i = kvStore.storeIterator(Direction.UNORDERED, 0, key, null, null);

        if(!i.hasNext()) {
            System.out.printf("[%s] - No people in DB\n", className);
            return;
        }

        int counter = 0;
        while(i.hasNext()) {
            KeyValueVersion kv = i.next();
            if(kv.getKey().toString().contains("email"))
                ++counter;
            kvStore.delete(kv.getKey());
        }
        System.out.printf("[%s] - Deleted %d people records\n", className, counter);
    }

    private void deleteMales() {
        System.out.printf("[%s] - running delete males\n", className);
        ArrayList<String> major = new ArrayList<String>();
        major.add("M");
        Key key = Key.createKey(major);

        Iterator<KeyValueVersion> i = kvStore.storeIterator(Direction.UNORDERED, 0, key, null, null);

        if(!i.hasNext()) {
            System.out.printf("[%s] - No males in DB\n", className);
            return;
        }

        int counter = 0;
        while(i.hasNext()) {
            KeyValueVersion kv = i.next();
            if (kv.getKey().toString().contains("email"))
                ++counter;
            kvStore.delete(kv.getKey());
        }
        System.out.printf("[%s] - Deleted %d males records\n", className, counter);
    }

    private void deleteFemales() {
        System.out.printf("[%s] - running delete females\n", className);
        ArrayList<String> major = new ArrayList<String>();
        major.add("F");
        Key key = Key.createKey(major);

        Iterator<KeyValueVersion> i = kvStore.storeIterator(Direction.UNORDERED, 0, key, null, null);

        if(!i.hasNext()) {
            System.out.printf("[%s] - No females in DB\n", className);
            return;
        }

        int counter = 0;
        while(i.hasNext()) {
            KeyValueVersion kv = i.next();
            if (kv.getKey().toString().contains("email"))
                ++counter;
            kvStore.delete(kv.getKey());
        }
        System.out.printf("[%s] - Deleted %d female records\n", className, counter);
    }

    private void deleteGender() {
        deleteFemales();
        deleteMales();
    }

    public void deleteAll() {
        Main.clearScreen();
        String answer = "";
        do {
            System.out.printf("[%s] - Please confirm you want to delete all people (y/n): \n", className);
            answer = Main.scanner.nextLine().toLowerCase(Locale.ROOT);
            if(answer.equals("n")) {
                System.out.printf("[%s] - Not deleting records\n", className);
                return;
            }
        } while(!answer.equals("y"));

        deleteGender();
        deletePeople();
    }

    public void deleteByLastName() {
        Main.clearScreen();
        System.out.printf("[%s] - Enter person's last name: \n", className);
        String lastName = Main.scanner.nextLine();

        String answer = "";
        do {
            System.out.printf("[%s] - Please confirm you want to delete all records with lastname %s (y/n): \n", className, lastName);
            answer = Main.scanner.nextLine().toLowerCase(Locale.ROOT);
            if(answer.equals("n")) {
                System.out.printf("[%s] - Not deleting records\n", className);
                return;
            }
        } while(!answer.equals("y"));

        ArrayList<String> majorComponents = new ArrayList<String>();
        majorComponents.add(className);
        majorComponents.add(lastName);

        Key key = Key.createKey(majorComponents);

        Iterator<KeyValueVersion> i = kvStore.storeIterator(Direction.UNORDERED, 0, key, null, null);

        if(!i.hasNext()) {
            System.out.printf("[%s] - No records found with lastname %s\n", className, lastName);
            return;
        }

        int counter = 0;
        while(i.hasNext()) {
            KeyValueVersion kv = i.next();
            if (kv.getKey().toString().contains("email"))
                ++counter;
            kvStore.delete(kv.getKey());
        }
        System.out.printf("[%s] - Deleted %d records with lastname\n", className, counter, lastName);
        deleteGenderRecord(lastName);
    }


    private void deleteGenderRecord(String lastName) {
        deleteMaleRecordsByLastName(lastName);
        deleteFemaleRecordsByLastName(lastName);
    }

    private void deleteMaleRecordsByLastName(String lastName) {
        ArrayList<String> major = new ArrayList<String>();
        major.add("M");
        major.add(lastName);

        Iterator<KeyValueVersion> i = kvStore.storeIterator(Direction.UNORDERED, 0, Key.createKey(major), null, null);

        if(!i.hasNext()) {
            System.out.printf("[%s] - No associated records found with lastname %s on males\n", className, lastName);
            return;
        }

        int counter = 0;
        while(i.hasNext()) {
            KeyValueVersion kv = i.next();
            if(kv.getKey().toString().contains("email"))
                ++counter;
            kvStore.delete(kv.getKey());
        }
        System.out.printf("[%s] - Deleted %d associated male records\n", className, counter);
    }

    private void deleteFemaleRecordsByLastName(String lastName) {
        ArrayList<String> major = new ArrayList<String>();
        major.add("F");
        major.add(lastName);

        Iterator<KeyValueVersion> i = kvStore.storeIterator(Direction.UNORDERED, 0, Key.createKey(major), null, null);

        if(!i.hasNext()) {
            System.out.printf("[%s] - No associated records found with lastname %s on females\n", className, lastName);
            return;
        }

        int counter = 0;
        while(i.hasNext()) {
            KeyValueVersion kv = i.next();
            if(kv.getKey().toString().contains("email"))
                ++counter;
            kvStore.delete(kv.getKey());
        }
        System.out.printf("[%s] - Deleted %d associated female records\n", className, counter);
    }

    public void searchByFullName() {
        Main.clearScreen();
        System.out.printf("[%s] - Enter person's full name: \n", className);
        String[] fullName = Main.scanner.nextLine().split(" ");

        ArrayList<String> majorComponents = new ArrayList<String>();
        majorComponents.add(className);
        majorComponents.add(fullName[1]);
        majorComponents.add(fullName[0]);

        Key key = Key.createKey(majorComponents);

        Iterator<KeyValueVersion> i = kvStore.storeIterator(Direction.UNORDERED, 0, key, null, null);

        if(!i.hasNext()) {
            System.out.printf("[%s] - No record found with name %s %s\n", className, fullName[0], fullName[1]);
            return;
        }

        int counter = 0;
        while(i.hasNext()) {
            KeyValueVersion kv = i.next();
            if(kv.getKey().toString().contains("email"))
                ++counter;
            System.out.printf("%s : %s\n", kv.getKey().toString(), new String(kv.getValue().getValue()));
        }
        System.out.printf("[%s] - Returned %d records\n", className, counter);
    }

    public void searchByGender() {
        Main.clearScreen();
        System.out.printf("[%s] - Enter gender to search (Male/Female): \n", className);
        char gender = Main.scanner.nextLine().toUpperCase(Locale.ROOT).charAt(0);

        ArrayList<String> majorComponents = new ArrayList<String>();
        majorComponents.add("" + gender);

        Key key = Key.createKey(majorComponents);
        Iterator<KeyValueVersion> i = kvStore.storeIterator(Direction.UNORDERED, 0, key, null, null);

        if(!i.hasNext()) {
            System.out.printf("[%s] - No gender data in DB\n", className);
            return;
        }

        int counter = 0;
        while(i.hasNext()) {
            KeyValueVersion kv = i.next();
            if(kv.getKey().toString().contains("email"))
                ++counter;
            System.out.printf("%s : %s\n", kv.getKey().toString(), new String(kv.getValue().getValue()));
        }
        System.out.printf("[%s] - Returned %d records\n", className, counter);
    }

    public void searchContactInformation() {
        Main.clearScreen();
        System.out.printf("[%s] - Enter person's full name: \n", className);
        String[] fullName = Main.scanner.nextLine().split(" ");

        ArrayList<String> majorComponents = new ArrayList<String>();
        majorComponents.add(className);
        majorComponents.add(fullName[1]);
        majorComponents.add(fullName[0]);

        Key key = Key.createKey(majorComponents);

        Iterator<KeyValueVersion> i = kvStore.storeIterator(Direction.UNORDERED, 0, key, null, null);

        if(!i.hasNext()) {
            System.out.printf("[%s] - No record found with name %s %s\n", className, fullName[0], fullName[1]);
            return;
        }

        int counter = 0;
        while(i.hasNext()) {
            KeyValueVersion kv = i.next();
            if(kv.getKey().toString().contains("email")) {
                System.out.printf("%s : %s\n", kv.getKey().toString(), new String(kv.getValue().getValue()));
            }
            else if(kv.getKey().toString().contains("phone")) {
                System.out.printf("%s : %s\n", kv.getKey().toString(), new String(kv.getValue().getValue()));
                ++counter;
            }
        }
        System.out.printf("[%s] - Returned %d records\n", className, counter);
    }

    public void searchByLastname() {
        Main.clearScreen();
        System.out.printf("[%s] - Enter person's last name: \n", className);
        String lastName = Main.scanner.nextLine();

        ArrayList<String> majorComponents = new ArrayList<String>();
        majorComponents.add(className);
        majorComponents.add(lastName);

        Key key = Key.createKey(majorComponents);

        Iterator<KeyValueVersion> i = kvStore.storeIterator(Direction.UNORDERED, 0, key, null, null);

        if(!i.hasNext()) {
            System.out.printf("[%s] - No record found with lastname %s\n", className, lastName);
            return;
        }

        int counter = 0;
        while(i.hasNext()) {
            KeyValueVersion kv = i.next();
            if(kv.getKey().toString().contains("email"))
                ++counter;
            System.out.printf("%s : %s\n", kv.getKey().toString(), new String(kv.getValue().getValue()));
        }
        System.out.printf("[%s] - Returned %d records\n", className, counter);
    }

    public void searchAllPeople() {
        Main.clearScreen();
        ArrayList<String> major = new ArrayList<String>();
        major.add(className);
        Key key = Key.createKey(major);

        Iterator<KeyValueVersion> i = kvStore.storeIterator(Direction.UNORDERED, 0, key, null, null);

        if(!i.hasNext()) {
            System.out.printf("[%s] - No people in DB\n", className);
            return;
        }

        while(i.hasNext()) {
            KeyValueVersion kv = i.next();
            System.out.printf("- %s : %s\n", kv.getKey().toString(), new String(kv.getValue().getValue()));
        }
    }

}
