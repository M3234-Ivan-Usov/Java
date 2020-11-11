
package ru.ifmo.rain.usov.walk;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class Visit extends SimpleFileVisitor<Path> {
    private static final int x0 = 0x811c9dc5;
    private static final int p = 0x01000193;
    int hash = 0;
    BufferedWriter ans;
    Visit(BufferedWriter out) {
       ans = out;
    }
    public FileVisitResult visitFile(String line, BasicFileAttributes attr) throws IOException {
        try (InputStream s = Files.newInputStream(Paths.get(line))) {
            int c;
            hash = x0;
            while ((c = s.read()) != -1) {
                hash = ((hash * p) ^ c);
            }
        } catch (IOException | InvalidPathException e) {
            System.err.println(line + " can not be found");
        } finally {
            ans.write(String.format("%08x %s\n", hash, line));
        }
        return FileVisitResult.CONTINUE;
    }
}

