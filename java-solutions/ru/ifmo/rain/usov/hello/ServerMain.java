package ru.ifmo.rain.usov.hello;

import info.kgeorgiy.java.advanced.hello.HelloServer;

public class ServerMain {
    private final HelloServer server;

    public ServerMain(HelloServer server) {
        this.server = server;
    }

    public void launch(String [] args) {
        if (args == null || args.length != 2) {
            System.err.println("Invalid args");
        }
        try {
            int port = Integer.parseInt(args[0]);
            int threads = Integer.parseInt(args[1]);
            if (threads < 0 || port < 0) {
                System.out.println("Incorrect args");
                return;
            }
            server.start(port, threads);
        } catch (NumberFormatException e) {
            System.err.println("Non-numeric args");
        }
    }
}
