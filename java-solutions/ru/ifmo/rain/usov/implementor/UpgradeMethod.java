package ru.ifmo.rain.usov.implementor;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Wrapper for {@link Method}.
 * Defines custom hashcode to put methods properly into {@link java.util.HashSet}
 */
public class UpgradeMethod {
    /**
     * Wrapped {@link Method} instance
     */
    public final Method method;

    /**
     * Constructs wrapper
     *
     * @param method {@link Method} to wrap
     */
    public UpgradeMethod(Method method) {
        this.method = method;
    }

    /**
     * Overrides {@link Object} equals to suitable version
     *
     * @param o expected {@link UpgradeMethod}
     * @return True, if both hashcodes match
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof UpgradeMethod) {
            UpgradeMethod obj = (UpgradeMethod) o;
            return hashCode() == obj.hashCode();
        }
        return false;
    }

    /**
     * Define another hash function for {@link Method}
     *
     * @return Some number, which helps to define, if two {@link UpgradeMethod} are equal
     */
    @Override
    public int hashCode() {
        return method.getDeclaringClass().hashCode() +
                method.getReturnType().hashCode() +
                method.getName().hashCode() +
                Arrays.hashCode(method.getParameterTypes());
    }
}
