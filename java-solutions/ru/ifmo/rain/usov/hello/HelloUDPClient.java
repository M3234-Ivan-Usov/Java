package ru.ifmo.rain.usov.hello;

//java -cp . -p . -m info.kgeorgiy.java.advanced.hello client ru.ifmo.rain.usov.hello.HelloUDPClient
import info.kgeorgiy.java.advanced.hello.HelloClient;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class HelloUDPClient implements HelloClient {

    public static void main(String[] args) {
        new ClientMain(new HelloUDPClient()).launch(args);
    }

    private DatagramPacket getAnswer(DatagramSocket dataSocket) throws SocketException
    {
        int sizeToReceive = dataSocket.getReceiveBufferSize();
        return new DatagramPacket(new byte[sizeToReceive], sizeToReceive);
    }

    private DatagramPacket getPacket(String request, InetSocketAddress socket)
    {
        byte[] bytes = request.getBytes(StandardCharsets.UTF_8);
        return new DatagramPacket(bytes, bytes.length, socket);
    }

    private String correctReply(DatagramPacket packet, String request)
    {
        String reply = new String(packet.getData(), packet.getOffset(), packet.getLength(), StandardCharsets.UTF_8);
        return (reply.contains(request)) ? reply : "";
    }

    private void end(ExecutorService arbeiters, int extraTime)
    {
        arbeiters.shutdown();
        try {
            arbeiters.awaitTermination(extraTime, TimeUnit.SECONDS);
        } catch (InterruptedException ignored) {
        }
    }

    @Override
    public void run(String host, int port, String prefix, int threads, int requests)
    {
        final int TERMINATION_TIMEOUT = 5;
        InetSocketAddress address = new InetSocketAddress(host, port);
        ExecutorService arbeiters = Executors.newFixedThreadPool(threads);
        for (int i = 0; i < threads; ++i) {
            final int worker = i;
            arbeiters.submit(() -> {
                try (DatagramSocket dataSocket = new DatagramSocket()) {
                    dataSocket.setSoTimeout(Datagrams.TIMEOUT);
                    DatagramPacket answer = getAnswer(dataSocket);
                    for (int reqNum = 0; reqNum < requests; reqNum++) {
                        String request = Datagrams.defaultMessage(prefix, worker, reqNum);
                        System.out.println("Request: " + request);
                        DatagramPacket packet = getPacket(request, address);
                        while (!dataSocket.isClosed()) {
                            try {
                                dataSocket.send(packet);
                                dataSocket.receive(answer);
                                String reply = correctReply(answer, request);
                                if (reply.length() != 0) {
                                    System.out.println("Answer: " + reply);
                                    break;
                                }
                            } catch (IOException e) {
                                System.err.println("Failed while sending/receiving");
                            }
                        }
                    }
                } catch (SocketException e) {
                    System.err.println("Bad connection");
                }
            });
        }
        end(arbeiters,TERMINATION_TIMEOUT * requests);
    }
}
