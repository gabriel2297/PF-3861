package com;

import oracle.kv.*;
import oracle.nosql.common.migrator.data.Entry;

import java.nio.charset.StandardCharsets;
import java.sql.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class Database {
    private String[] helperHosts;
    private static String className;
    private static Database db;
    private KVStore kvstore;
    private Map<Integer, KeyValue> parallelMap;
    private ArrayList<String> results;
    private int parallelCount;

    private Database() {
        className = "Database";
        System.out.printf("[%s] - Creating instance..\n", className);
        setHelperHosts();
        configureStore();
        prepareData();
        parallelCount = 0;
        results = new ArrayList<>();
    }

    private void prepareData() {
        // prepare data for quicker insertion
        System.out.println("Preparing key-value pairs in hashmap");
        parallelMap = new HashMap<>();

        for(int i = 0; i < 25000; ++i)
            parallelMap.put(i, new KeyValue(Key.fromString("/lista/"+i), Value.createValue(NumberToWords.convert(i).getBytes())));

        System.out.println("data ready");
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

    public void insertValuesIntoDb() {
        deleteValuesFromDb();
        System.out.println("Inserting data to DB");
        int counter = 0;
        long start = System.currentTimeMillis();
        for (KeyValue kv : parallelMap.values()) {
            kvstore.put(kv.getKey(), kv.getValue());
            ++counter;
        }
        long end = System.currentTimeMillis();
        String message = "[Concurrent run] - Inserted " + counter + " records in " + (end-start)  + "ms (" + ((end-start)/1000) + " seconds)";
        results.add(message);
        System.out.println(message);
    }


    public void parallelInsertValuesIntoDb() {
        deleteValuesFromDb();
        System.out.println("Inserting data to DB in parallel");
        int load = 25000;

        // prepare stream
        Integer streamParallelism = 2;
        BulkWriteOptions bulkWriteOptions = new BulkWriteOptions(null, 0, null);
        bulkWriteOptions.setStreamParallelism(streamParallelism);

        // create a list of entry streams that hold the keyvalue pair
        final List<EntryStream<KeyValue>> streams = new ArrayList<EntryStream<KeyValue>>(streamParallelism);
        final int num = (load + (streamParallelism - 1)) / streamParallelism; // == 1

        // while i < 2
        for (int i = 0; i < streamParallelism; i++) {
            final int min = num * i; // 0, 1
            final int max = Math.min((min + num), load); // 1, 2
            streams.add(new CreateStream("Thread", i, min, max)); // 0, 0, 1 | 1, 1, 2
        }

        long start = System.currentTimeMillis();
        kvstore.put(streams, bulkWriteOptions);
        long end = System.currentTimeMillis();

        String message = "[Parallel run] - Inserted " + parallelCount + " records in " + (end-start)  + "ms (" + ((end-start)/1000) + " seconds)";
        results.add(message);
        System.out.println(message);
    }

    public void deleteValuesFromDb() {
        Main.clearScreen();
        System.out.println("Deleting data from DB");
        int counter = 0;

        for (KeyValue kv : parallelMap.values())
            counter = kvstore.delete(kv.getKey()) ? ++counter : counter;

        System.out.printf("Deleted %d records from DB\n", counter);
    }

    public void showResults() {
        Main.clearScreen();
        for(String res : results)
            System.out.println(res);
    }

    private class CreateStream implements EntryStream<KeyValue> {
        private final String name;
        private final int index;
        private final int max;
        private final int min;
        private int id;
        private int count;
        private final AtomicLong keyExistsCount;

        CreateStream(String name, int index, int min, int max) {
            this.index = index;
            this.max = max;
            this.min = min;
            this.name = name;
            id = min;
            count = 0;
            keyExistsCount = new AtomicLong();
        }

        @Override
        public String name() {
            return name + "-" + index + ": " + min + "~" + max;
        }

        @Override
        public KeyValue getNext() {
            if (id++ == max) {
                return null;
            }
            count++;
            return parallelMap.get(id);
        }

        @Override
        public void completed() {
            parallelCount += count;
            System.err.println(name() + " completed, loaded: " + count);
        }

        @Override
        public void keyExists(KeyValue entry) {
            keyExistsCount.incrementAndGet();
        }

        @Override
        public void catchException (RuntimeException exception, KeyValue entry) {
            System.err.println(name() + " catch exception: " + exception.getMessage() + ": " + entry.toString());
            throw exception;
        }

        public int getCounter() { return this.count; }
    }

}