package ru.ifmo.rain.usov.hello;

import info.kgeorgiy.java.advanced.hello.HelloServer;
//java -cp . -p . -m info.kgeorgiy.java.advanced.hello server ru.ifmo.rain.usov.hello.HelloUDPServer
import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class HelloUDPServer implements HelloServer {

    private DatagramSocket dataSocket;
    private ExecutorService arbeiters;
    private ExecutorService listener;
    private int sizeToReceive;

    public static void main(String[] args) {
        new ServerMain(new HelloUDPServer()).launch(args);
    }

    public DatagramPacket getPacket(int size) {
        return new DatagramPacket(new byte[size], size);
    }

    public String parsePacket(DatagramPacket packet) {
        return new String(packet.getData(), packet.getOffset(), packet.getLength(), StandardCharsets.UTF_8);
    }

    public DatagramPacket getAnswer(String reply, DatagramPacket packet) {
        byte[] bytes = reply.getBytes(StandardCharsets.UTF_8);
        return new DatagramPacket(bytes, bytes.length, packet.getSocketAddress());
    }

    @Override
    public void start(int port, int threads) {
        try {
            dataSocket = new DatagramSocket(port);
            sizeToReceive = dataSocket.getReceiveBufferSize();
            arbeiters = Executors.newFixedThreadPool(threads);
            listener = Executors.newSingleThreadExecutor();
            listener.submit(() -> {
                try {
                    while (!dataSocket.isClosed()) {
                        DatagramPacket packet = getPacket(sizeToReceive);
                        dataSocket.receive(packet);
                        arbeiters.submit(() -> {
                            String request = parsePacket(packet);
                            String reply = "Hello, " + request;
                            DatagramPacket answer = getAnswer(reply, packet);
                            try {
                                dataSocket.send(answer);
                            } catch (IOException e) {
                                System.err.println("Failed to send");
                            }
                        });
                    }
                } catch (IOException e) {
                    System.err.println("Failed to receive");
                }
            });
        } catch (SocketException e) {
            System.err.println("Bad connection");
        }
    }

    @Override
    public void close() {
        final int TERMINATION_TIMEOUT = 5;
        dataSocket.close();
        listener.shutdown();
        arbeiters.shutdown();
        try {
            listener.awaitTermination(TERMINATION_TIMEOUT, TimeUnit.SECONDS);
            arbeiters.awaitTermination(TERMINATION_TIMEOUT, TimeUnit.SECONDS);
        } catch (InterruptedException ignored) {
        }
    }
}

