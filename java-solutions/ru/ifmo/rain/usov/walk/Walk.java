package ru.ifmo.rain.usov.walk;

import java.io.*;
import java.nio.file.*;

public class Walk {
    private static final int x0 = 0x811c9dc5;
    private static final int p = 0x01000193;

    public static void main(String[] args) {
        if ((args == null) || (args.length != 2) || (args[0] == null) || (args[1] == null)) {
            System.err.println("Invalid arguments");
            return;
        }
        try {
            Path input = Paths.get(args[0]);
            Path output = Paths.get(args[1]);
            try (BufferedReader in = Files.newBufferedReader(input)) {
                try (BufferedWriter out = Files.newBufferedWriter(output)) {
                    Visit Counting = new Visit(out);
                    String line;
                    while ((line = in.readLine()) != null) {
                        int hash = 0;
                        try (InputStream current = Files.newInputStream(Paths.get(line));) {
                            //Files.walkFileTree(Paths.get(line), Counting);
                            int c;
                            hash = x0;
                            while ((c = current.read()) != -1) {
                                hash = ((hash * p) ^ c);
                            }
                        } catch (IOException | InvalidPathException e) {
                            System.err.println(line + " can not be found");
                        } finally {
                            out.write(String.format("%08x %s\n", hash, line));
                        }
                    }
                } catch (IOException e) {
                    System.err.println("Problems with out-file");
                    return;
                }
            } catch (IOException e) {
                System.err.println("Problems with in-file");
                return;
            }
        } catch (InvalidPathException e) {
            System.err.println("Invalid IN or OUT path");
        }
    }
}
