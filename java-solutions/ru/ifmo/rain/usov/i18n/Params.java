package ru.ifmo.rain.usov.i18n;

public class Params {
    int amount = 0;
    int uniqueAmount = 0;

    String minValue = null;
    String maxValue = null;

    int minLength = 0;
    String minLengthValue = null;

    int maxLength = 0;
    String maxLengthValue = null;

    boolean hasAverage = true;

    double sum = 0;

    public Params() {
    }
    public Params(boolean full) {
        hasAverage = full;
    }

    public double getAverage() {
        return (amount != 0) ? (double)sum/amount : 0;
    }
}
