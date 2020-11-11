package ru.ifmo.rain.usov.hello;

import info.kgeorgiy.java.advanced.hello.HelloServer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.net.InetSocketAddress;
import java.net.SocketAddress;


public class HelloNonBlockingServer implements HelloServer {
    private DatagramChannel channel;
    private ByteBuffer buffer;
    private Thread executor;

    public static void main(String[] args) {
        new ServerMain(new HelloNonBlockingServer()).launch(args);
    }

    @Override
    public void start(int port, int threads) {
        try {
            channel = DatagramChannel.open();
            channel.bind(new InetSocketAddress(port));
            buffer = ByteBuffer.allocate(channel.socket().getReceiveBufferSize());
            executor = new Thread(() -> {
                try {
                    while (!Thread.interrupted()) {
                        try {
                            buffer.clear();
                            buffer.put(Datagrams.defaultAnswer());
                            SocketAddress address = channel.receive(buffer);
                            buffer.flip();
                            channel.send(buffer, address);
                        } catch (IOException ignored) {
                        }
                    }
                } finally {
                    Thread.currentThread().interrupt();
                }
            });
            executor.start();
        } catch (IOException e) {
            System.out.print("Failed to open channel: " + e.getMessage());
        }
    }

    @Override
    public void close() {
        try {
            channel.close();
            executor.interrupt();
            executor.join();
        } catch (InterruptedException ignored) {
        } catch (IOException e) {
            System.out.println("Failed to close channel: " + e.getMessage());
        }
    }
}
