package com;

import oracle.kv.KVStore;
import oracle.kv.KVStoreConfig;
import oracle.kv.KVStoreFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Database {
    private String[] helperHosts;
    private static String className;
    private static Database db;
    private KVStore kvstore;

    private Database() {
        className = "Database";
        System.out.printf("[%s] - Creating instance..\n", className);
        setHelperHosts();
        configureStore();
    }

    public static Database getInstance() {
        if(db == null) {
            db = new Database();
        }
        return db;
    }

    public String[] getHelperHosts() { return this.helperHosts; }

    public void setHelperHosts() {
        // get the helpers hosts
        System.out.printf("[%s] - Please input the helpersHosts list in a comma separated list (example: \"x.x.x.x:port, x.x.x.x:port, ...\")\n", className);
        this.helperHosts = Main.scanner.nextLine().split(",");

        // sanitize them
        for(int i = 0; i < this.helperHosts.length; ++i)
            this.helperHosts[i] = this.helperHosts[i].trim();

        // check if success or not
        if(this.helperHosts.length > 0)
            System.out.printf("[%s] - Added %d helpers to the list\n", className, this.helperHosts.length);
        else {
            System.out.printf("[%s] - Failed to add helpers to the list, exiting\n", className);
            System.exit(-1);
        }
    }

    private void configureStore() {
        KVStoreConfig kConfig = new KVStoreConfig("myStore", getHelperHosts());
        kvstore = KVStoreFactory.getStore(kConfig);
    }

    public KVStore getKvStore() {
        return this.kvstore;
    }

    public void printHelperHosts() {
        for(int i = 0; i < this.helperHosts.length; ++i)
            System.out.printf("[%s] - Helper: %s\n", className, this.helperHosts[i]);
    }


}
