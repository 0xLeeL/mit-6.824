package org.lee;

import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class Main {

    static Set<Thread> threads = new HashSet<>();
    public static void main(String[] args) throws InterruptedException {
        while (true){
            Scanner scanner = new Scanner(System.in);
            scanner.nextLine();
            for (int i = 0; i < 100000; i++) {
                Thread thread = new Thread(() -> {
                    try {
                        Thread.sleep(10000000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                },i+"xx");
                threads.add(thread);
            }
        }
    }
}
