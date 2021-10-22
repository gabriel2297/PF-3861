package com;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Database {
    private List<String> helpers;
    private static String className;
    private static Database db;

    private Database() {
        className = "Database";
        helpers = new ArrayList<String>();
        System.out.printf("[%s] - Creating instance..\n", className);
        setHelpers();
    }

    public static Database getInstance() {
        if(db == null) {
            db = new Database();
        }
        return db;
    }

    public List<String> getHelpers() {
        return this.helpers;
    }

    public void setHelpers() {
        // get the helpers hosts
        System.out.printf("[%s] - Please input the helpersHosts list in a comma separated list (example: \"x.x.x.x:port, x.x.x.x:port, ...\")\n", className);
        List<String> helpers = Arrays.asList(Main.scanner.nextLine().split(","));

        // add them to the list of hosts
        int counter = 0;
        for(int i = 0; i < helpers.size(); ++i) {
            this.helpers.add(helpers.get(i).trim());
            ++counter;
        }

        // check if success or not
        if(counter > 0)
            System.out.printf("[%s] - Added %d helpers to the list\n", className, counter);
        else {
            System.out.printf("[%s] - Failed to add helpers to the list, exiting\n", className);
            System.exit(-1);
        }
    }

    public void printHelpers() {
        for(int i = 0; i < helpers.size(); ++i) {
            System.out.printf("[%s] - Helper: %s\n", className, helpers.get(i));
        }
    }


}
