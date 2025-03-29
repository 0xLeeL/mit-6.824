package org.lee.study;

public class Main {
    public static void main(String[] args) {
        MasterElection masterElection = new MasterElection();
        masterElection.startElection("线程");
        try {
            Thread.sleep(Integer.MAX_VALUE);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}