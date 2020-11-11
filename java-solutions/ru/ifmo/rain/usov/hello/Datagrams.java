package ru.ifmo.rain.usov.hello;

import java.net.DatagramPacket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.charset.StandardCharsets;

public class Datagrams {
    public static final int TIMEOUT = 50;

    public static String defaultMessage(String prefix, int firstIndex, int secondIndex) {
        return prefix + firstIndex + "_" + secondIndex;
    }
    public static byte[] defaultBytes(String prefix, int firstIndex, int secondIndex) {
        return defaultMessage(prefix, firstIndex, secondIndex).getBytes(StandardCharsets.UTF_8);
    }

    public static byte[] defaultAnswer() {
        return "Hello, ".getBytes(StandardCharsets.UTF_8);
    }
    public static class Context {
        public static final int ATTEMPTS_COUNT = 5;

        public DatagramChannel channel;
        public final ByteBuffer buffer;
        public int threadId;
        public int requests;
        public int attempts;

        public Context(DatagramChannel channel, int threadId) throws SocketException {
            this.channel = channel;
            this.buffer = ByteBuffer.allocate(channel.socket().getReceiveBufferSize());
            this.threadId = threadId;
            this.requests = 0;
            this.attempts = ATTEMPTS_COUNT;
        }

    }
}
