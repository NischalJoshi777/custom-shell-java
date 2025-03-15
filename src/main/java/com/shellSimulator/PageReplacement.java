package com.shellSimulator;

import java.util.*;

public class PageReplacement {

    private int capacity;
    private Queue<Integer> fifoQueue;
    private LinkedHashMap<Integer, Integer> lruMap;

    public PageReplacement(int capacity) {
        this.capacity = capacity;
        this.fifoQueue = new LinkedList<>();
        this.lruMap = new LinkedHashMap<>(capacity, 0.75f, true);
    }

    // FIFO
    public void fifo(int[] pages) {
        Set<Integer> memory = new HashSet<>();
        int pageFaults = 0;

        System.out.println("\nFIFO Page Replacement:");
        for (int page : pages) {
            if (!memory.contains(page)) {
                if (memory.size() >= capacity) {
                    int removed = fifoQueue.poll();
                    memory.remove(removed);
                    System.out.println("Page " + removed + " evicted.");
                }
                memory.add(page);
                fifoQueue.add(page);
                pageFaults++;
                System.out.println("Page " + page + " loaded (Page Fault).");
            } else {
                System.out.println("Page " + page + " is already in memory.");
            }
        }
        System.out.println("Total Page Faults (FIFO): " + pageFaults);
    }

    // LRU
    public void lru(int[] pages) {
        int pageFaults = 0;

        System.out.println("\nLRU Page Replacement:");
        for (int page : pages) {
            if (!lruMap.containsKey(page)) {
                if (lruMap.size() >= capacity) {
                    int removed = lruMap.entrySet().iterator().next().getKey();
                    lruMap.remove(removed);
                    System.out.println("Page " + removed + " evicted.");
                }
                pageFaults++;
                System.out.println("Page " + page + " loaded (Page Fault).");
            } else {
                System.out.println("Page " + page + " accessed.");
            }
            lruMap.put(page, 1); //Most recent
        }
        System.out.println("Total Page Faults (LRU): " + pageFaults);
    }
}
