package ru.ifmo.rain.usov.hello;

import info.kgeorgiy.java.advanced.hello.HelloClient;

public class ClientMain {
    private final HelloClient client;

    public ClientMain(HelloClient client) {
        this.client = client;
    }

    public void launch(String[] args) {
        if ((args == null) || (args.length != 5)) {
            System.err.println("Number of arguments should be 5");
            return;
        }
        try {
            String host = args[0];
            int port = Integer.parseInt(args[1]);
            String prefix = args[2];
            int threads = Integer.parseInt(args[3]);
            int requests = Integer.parseInt(args[4]);
            if (threads < 0 || requests < 0) {
                System.out.println("Incorrect args");
                return;
            }
            client.run(host, port, prefix, threads, requests);
        } catch (NumberFormatException e) {
            System.err.println("Some arguments must be numeric");
        }
    }
}
