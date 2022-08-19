package edu.isistan.spellchecker.lsh;

import java.util.*;

public class MinHashLSH {
    private static final int BANDS = 4;
    private static final int ROWS = 16;
    private List<Map<Integer, List<String>>> hashTables;

    public MinHashLSH() {
        this.hashTables = new ArrayList<>();
        for (int index = 0; index < BANDS; index++) {
            this.hashTables.add(new HashMap<>(10, 0.5F));
        }
    }

    public void insert(String key, MinHash minHash) {
        int[] minHashHashValues = minHash.getHashValues();
        for (int i = 0; i < BANDS; i++) {
            Integer hashValue = getHashValue(minHashHashValues, i);
            Map<Integer, List<String>> hashTable = this.hashTables.get(i);
            List<String> values = hashTable.getOrDefault(hashValue, new ArrayList<>());
            values.add(key);
            hashTable.put(hashValue, values);
        }
    }

    private int getHashValue(int[] minHashHashValues, int index) {
        int start = ROWS * index;
        int end = ROWS * (index + 1);
        int result = 0;
        for (int j = start; j < end; j++) {
            result += minHashHashValues[j];
        }
        return result;
    }

    public Set<String> query(MinHash queryMinHash) {
        Set<String> results = new HashSet<>();
        int[] queryMinHashHashValues = queryMinHash.getHashValues();
        for (int i = 0; i < BANDS; i++) {
            Integer hashValue = getHashValue(queryMinHashHashValues, i);
            List<String> values = this.hashTables.get(i).get(hashValue);
            if (values != null) {
                results.addAll(values);
            }
        }
        return results;
    }

}
