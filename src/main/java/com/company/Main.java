package com.company;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        Thread mainStream = new Thread(new MainStream());
        Thread ne = new Thread(new NeZastavlyate());
        ne.start();
        mainStream.start();


    }

}
