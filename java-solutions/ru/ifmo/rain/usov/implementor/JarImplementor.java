package ru.ifmo.rain.usov.implementor;

//java -cp . -p . -m info.kgeorgiy.java.advanced.implementor interface ru.ifmo.rain.usov.implementor.Implementor
//java -cp . -p . -m info.kgeorgiy.java.advanced.implementor jar-interface ru.ifmo.rain.usov.implementor.JarImplementor

import info.kgeorgiy.java.advanced.implementor.ImplerException;
import info.kgeorgiy.java.advanced.implementor.JarImpler;

import java.nio.file.*;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;
import javax.tools.*;
import java.io.*;
import java.net.URISyntaxException;

/**
 * Modification for {@link Implementor}. Allows to put result into jar
 */
public class JarImplementor extends Implementor implements JarImpler {
    /**
     * Creates new instance of {@link JarImplementor}
     */
    public JarImplementor() {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void implementJar(Class<?> token, Path jarFile) throws ImplerException {
        Path tmpDir = Paths.get(".");
        implement(token, tmpDir);
        Path source = convertPath(tmpDir, token, "Impl.java");
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            throw new ImplerException("Compiler is absent");
        }
        String[] argv = {"-cp", tmpDir.toString() + File.pathSeparator + getClassPath(token), source.toString()};
        if (compiler.run(null, null, null, argv) != 0) {
            throw new ImplerException("Compilation error");
        }
        Path classPath = convertPath(tmpDir, token, "Impl.class");
        classPath.toFile().deleteOnExit();
        String className = Path.of(token.getPackageName().replace(".", "/"),
                token.getSimpleName()).toString().replace('\\', '/') + "Impl.class";

        try (JarOutputStream jar = new JarOutputStream(Files.newOutputStream(Path.of(jarFile.toString())))) {
            jar.putNextEntry(new ZipEntry(className));
            Files.copy(classPath, jar);
        } catch (IOException e) {
            throw new ImplerException("Fail to put result into jar");
        }
    }

    /**
     * Returns a classpath of a <code>token</code>
     *
     * @param token class
     * @return {@link String} <code>token</code> classpath
     */
    private String getClassPath(Class<?> token) throws ImplerException {
        try {
            return Path.of(token.getProtectionDomain().getCodeSource().getLocation().toURI()).toString();
        } catch (final URISyntaxException e) {
            throw new ImplerException(e);
        }
    }

    /**
     * Entry point, requires two or three arguments
     *
     * @param args [-jar] class path
     */
    public static void main(String[] args) {
        if (args == null || (args.length != 2 && args.length != 3)) {
            System.out.println("Two or three arguments were expected");
            return;
        }
        try {
            if (args.length == 2) {
                new Implementor().implement(Class.forName(args[0]), Paths.get(args[1]));
            } else if (args[0].equals("-jar")) {
                new JarImplementor().implementJar(Class.forName(args[1]), Paths.get(args[2]));
            } else {
                System.out.println(args[0] + " is unknown argument, only '-jar' expected");
            }
        } catch (InvalidPathException e) {
            System.out.println("Invalid path: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.out.println("Invalid token: " + e.getMessage());
        } catch (ImplerException e) {
            System.out.println("Failed while implementing: " + e.getMessage());
        }
    }
}
