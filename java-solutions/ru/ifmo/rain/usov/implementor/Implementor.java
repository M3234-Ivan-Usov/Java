package ru.ifmo.rain.usov.implementor;


import info.kgeorgiy.java.advanced.implementor.Impler;
import info.kgeorgiy.java.advanced.implementor.ImplerException;

import java.io.*;
import java.lang.reflect.*;
import java.nio.file.*;
import java.util.*;


public class Implementor implements Impler {
    /**
     * Creates a new instance of {@link Implementor}
     */
    public Implementor() {
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void implement(Class<?> token, Path root) throws ImplerException {
        checkArgs(token, root);
        root = convertPath(root, token, "Impl.java");
        mkdir(root.getParent());
        String source = getHeader(token) + realiseMethods(getMethods(token)) + "}";
        write(toUnicode(source), root);
    }

    /**
     * Arguments validation
     * @param token Inspected {@link Class}
     * @param root Argument path
     * @throws ImplerException If arguments is not suitable for {@link Implementor}
     */
    private void checkArgs(Class<?> token, Path root) throws ImplerException {
        if (token == null) {
            throw new ImplerException("Token is null");
        }
        if (!token.isInterface()) {
            throw new ImplerException("Token is not an interface");
        }
        if (root == null) {
            throw new ImplerException("Path is null");
        }
        if (token.isPrimitive() || token.isArray() ||
                Modifier.isFinal(token.getModifiers()) || token == Enum.class) {
            throw new ImplerException("Invalid token");
        }
    }

    /**
     * Return real path of <code>token</code>
     * @param path   {@link Path} path file with code
     * @param token  Inspected instance
     * @param suffix {@link String} ending of generated string, file extension
     * @return {@link Path} of file
     */
    Path convertPath(Path path, Class<?> token, String suffix) {
        return path.resolve(token.getPackageName().replace('.',
                File.separatorChar)).resolve(token.getSimpleName() + suffix);
    }

    /**
     * Create new directory
     * @param path Location
     * @throws ImplerException Occurs, if creation failed
     */
    private void mkdir(Path path) throws ImplerException {
        if (path != null) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                throw new ImplerException("Can`t create directories!", e);
            }
        }

    }

    /**
     * Introduce package and declaration of a <code>token</code>
     * @param token Object to declare
     * @return String with full information about <code>token</code>
     * @throws ImplerException Occurs, if <code>token</code> appears to have private {@link Modifier}
     */
    private String getHeader(Class<?> token) throws ImplerException {
        Package pack = token.getPackage();
        if (Modifier.isPrivate(token.getModifiers())) {
            throw new ImplerException("Private token");
        }
        if (pack == null) {
            return "public class " + token.getSimpleName() + "Impl " +
                    (token.isInterface() ? "implements " : "extends ") +
                    token.getCanonicalName() + " {\n";
        } else {
            return "package " + pack.getName() + ";\n" +
                    "public class " + token.getSimpleName() + "Impl " +
                    (token.isInterface() ? "implements " : "extends ") +
                    token.getCanonicalName() + " {\n";
        }
    }

    /**
     * Extract methods from <code>token</code>
     * @param token Methods holder
     * @return {@link HashSet} filled with methods in form of {@link UpgradeMethod}
     * @throws ImplerException Occurs, if it is impossible to access superclasses
     */
    private Set<UpgradeMethod> getMethods(Class<?> token) throws ImplerException {
        Set<UpgradeMethod> methods = new HashSet<>();
        for (Method method : token.getMethods()) {
            if (Modifier.isAbstract(method.getModifiers())) {
                methods.add(new UpgradeMethod(method));
            }
        }
        while (token != null) {
            if (!(Modifier.isPrivate((token.getModifiers())))) {
                for (Method method : token.getDeclaredMethods()) {
                    if (Modifier.isAbstract(method.getModifiers())) {
                        methods.add(new UpgradeMethod(method));
                    }
                }
                token = token.getSuperclass();
            } else {
                throw new ImplerException("Token has private superclass");
            }
        }
        return methods;
    }

    /**
     * Creates default realisation for each method in <code>methods</code>
     * @param methods Set of {@link UpgradeMethod}
     * @return String, containing the default realisation
     */
    private String realiseMethods(Set<UpgradeMethod> methods) {
        StringBuilder realisation = new StringBuilder();
        for (UpgradeMethod m : methods) {
            realisation.append(getMethodSignature(m.method));
            realisation.append(getExceptions(m.method));
            realisation.append(" {\n");
            realisation.append(getMethodBody(m.method));
            realisation.append("\n}\n");
        }
        return realisation.toString();
    }

    /**
     * List of method's parameters. Get types and create variable name, which is a capital English letter
     * @param method {@link Method} to search for parameters
     * @return String in format "(type A, type B , ...)" or "()"
     */
    private String getParams(Method method) {
        StringBuilder params = new StringBuilder();
        char arg = 'A';
        params.append("(");
        for (Class<?> argument : method.getParameterTypes()) {
            if (arg != 'A') {
                params.append(", ");
            }
            params.append(argument.getCanonicalName()).append(" ").append(arg);
            arg++;
        }
        params.append(")");
        return params.toString();
    }

    /**
     * List of method's exception. Get exceptions, which <code>exe</code> throws
     * @param method {@link Method} to search for exceptions
     * @return String in format "throws exception1, exception2, ... " empty {@link String}
     */
    private String getExceptions(Method method) {
        StringBuilder exceptions = new StringBuilder();
        for (Class<?> exception : method.getExceptionTypes()) {
            if (exceptions.length() != 0) {
                exceptions.append(", ");
            } else {
                exceptions.append(" throws ");
            }
            exceptions.append(exception.getCanonicalName());
        }
        return exceptions.toString();
    }

    /**
     * Declaration of Method{@link Method}. Remove abstract {@link Modifier}
     * @param m {@link Method} to declare
     * @return String in format "public int <code>exe</code>(type A, type B, ... )
     */
    private String getMethodSignature(Method m) {
        return Modifier.toString(m.getModifiers() & ((Modifier.classModifiers() ^ Modifier.ABSTRACT)))
                + " " + m.getReturnType().getCanonicalName() + " " + m.getName() + getParams(m);
    }

    /**
     * Default value. Generate a simple body for <code>m</code>
     * @param method {@link Method} to fulfil body
     * @return String in format "return 0;"
     */
    private String getMethodBody(Method method) {
        switch ((method.getReturnType().getCanonicalName())) {
            case "void": {
                return "return;";
            }
            case "boolean": {
                return "return false;";
            }
        }
        if ((method.getReturnType().isPrimitive())) {
            return "return 0;";
        }
        return "return null;";
    }

    /**
     * Outputs <code>source</code> into <code>path</code> location
     * @param source Object to write
     * @param path Destination file
     */
    private void write(String source, Path path) {
        try (BufferedWriter out = Files.newBufferedWriter(path)) {
            out.write(toUnicode(source));
        } catch (IOException e) {
            System.out.println("Error with opening output file" + e);
        }
    }

    /**
     * Convert <code>source</code> to unicode-string
     * @param source Default-format {@link String}
     * @return Unicode-format string
     */
    private String toUnicode(String source) {
        StringBuilder unicodeBuilder = new StringBuilder();
        for (char c : source.toCharArray()) {
            unicodeBuilder.append(c >= 128 ? String.format("\\u%04X", (int) c) : c);
        }
        return unicodeBuilder.toString();
    }

}