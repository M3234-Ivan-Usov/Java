package ru.ifmo.rain.usov.hello;

import info.kgeorgiy.java.advanced.hello.HelloClient;

import java.io.IOException;
import java.net.*;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class HelloNonBlockingClient implements HelloClient {

    private Selector selector;
    private List<Datagrams.Context> contexts;

    public HelloNonBlockingClient() {
        contexts = new ArrayList<>();
    }

    public static void main(String[] args) {
        new ClientMain(new HelloNonBlockingClient()).launch(args);
    }


    @Override
    public void run(String host, int port, String prefix, int threadCount, int requestCount) {
        try {
            selector = Selector.open();
        } catch (IOException e) {
            System.out.println("Selector failed: " + e.getMessage());
            return;
        }
        InetSocketAddress address = new InetSocketAddress(host, port);
        for (int thread = 0; thread < threadCount; thread++) {
            try {
                setConnection(address, prefix, thread);
            } catch (IOException e) {
                System.out.println("Could not create channel: " + e.getMessage());
            }
        }
        int amount = 0;
        while (amount < threadCount) {
            try {
                selector.select(Datagrams.TIMEOUT);
                if (selector.selectedKeys().isEmpty()) {
                    for (Datagrams.Context context : contexts) {
                        if (context.channel.isConnected()) {
                            send(context, prefix, address);
                        }
                    }
                } else {
                    for (SelectionKey selectionKey : selector.selectedKeys()) {
                        Datagrams.Context context = (Datagrams.Context) selectionKey.attachment();
                        context.buffer.clear();
                        SocketAddress responseAddress = context.channel.receive(context.buffer);
                        if (responseAddress == null && context.attempts > 0) {
                            context.attempts--;
                            continue;
                        }
                        context.buffer.flip();
                        String answer = new String(context.buffer.array(), 0, context.buffer.limit(), StandardCharsets.UTF_8);
                        if (answer.contains(Datagrams.defaultMessage(prefix, context.threadId, context.requests))) {
                            System.out.println(answer);
                            context.requests++;
                        } else {
                            System.out.println("Error: " + answer);
                        }
                        if (context.requests == requestCount) {
                            context.channel.disconnect();
                            context.channel.close();
                            amount++;
                            continue;
                        }
                        context.attempts = Datagrams.Context.ATTEMPTS_COUNT;
                        send(context, prefix, address);
                    }
                }
            } catch (IOException ignored) {
            }
        }
        try {
            selector.close();
        } catch (IOException ignored) {
        }
    }

    private void setConnection(InetSocketAddress address, String prefix, int thread) throws IOException {
        DatagramChannel channel = DatagramChannel.open();
        channel.configureBlocking(false);
        Datagrams.Context context = new Datagrams.Context(channel, thread);
        refreshBuffer(context, prefix);
        contexts.add(context);
        channel.connect(address);
        channel.register(selector, SelectionKey.OP_READ, context);
        while (channel.send(context.buffer, address) == 0) {
        }
    }

    private void refreshBuffer(Datagrams.Context context, String prefix) {
        context.buffer.clear();
        context.buffer.put(Datagrams.defaultBytes(prefix, context.threadId, context.requests));
        context.buffer.flip();
    }

    private void send(Datagrams.Context context, String prefix, SocketAddress address) {
        refreshBuffer(context, prefix);
        try {
            while (context.channel.send(context.buffer, address) == 0){
            }
        } catch (IOException ignored) {
        }
    }
}
