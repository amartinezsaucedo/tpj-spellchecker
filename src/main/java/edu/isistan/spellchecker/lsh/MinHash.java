package edu.isistan.spellchecker.lsh;

import java.util.*;

public class MinHash {
    private static final int LARGEST_PRIME = 2147483647; //Mersenne
    private int numberOfPermutations;
    private int[] hashValues;
    private int[] permutationA;
    private int[] permutationB;

    public MinHash(int numberOfPermutations) {
        this.numberOfPermutations = numberOfPermutations;
        this.initializeHashValues();
        this.initializePermutations(numberOfPermutations);
    }

    private void initializeHashValues() {
        this.hashValues = new int[this.numberOfPermutations];
        Arrays.fill(this.hashValues, Integer.MAX_VALUE);
    }

    private void initializePermutations(int numberOfPermutations) {
        this.permutationA = new int[numberOfPermutations];
        this.permutationB = new int[numberOfPermutations];
        SplittableRandom random = new SplittableRandom(1);
        for (int index = 0; index < numberOfPermutations; index++) {
            this.permutationA[index] = random.nextInt(1, LARGEST_PRIME);
            this.permutationB[index] = random.nextInt(0, LARGEST_PRIME);
        }
    }

    public void update(String ngram) {
        for (int index = 0; index < this.numberOfPermutations; index++) {
            int a = this.permutationA[index];
            int b = this.permutationB[index];
            int hash = modulus(modulus((a * ngram.hashCode() + b), LARGEST_PRIME), Integer.MAX_VALUE);
            if (hash < this.hashValues[index]) {
                this.hashValues[index] = hash;
            }
        }
    }

    private int modulus(int a, int b) {
        if (((b - 1) & b) == 0) {
            return a & (b - 1);
        }
        return a % b;
    }

    public int[] getHashValues() {
        return this.hashValues;
    }
}
