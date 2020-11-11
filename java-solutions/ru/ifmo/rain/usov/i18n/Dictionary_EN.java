package ru.ifmo.rain.usov.i18n;

import java.util.ListResourceBundle;

public class Dictionary_EN extends ListResourceBundle {
    @Override
    protected Object[][] getContents() {
        return contents;
    }
    private static final Object[][] contents = {
            {"pageTitle", "Text Analyzer"},
            {"file", "Analyzed file: "},
            {"summary", "Summary: "},

            {"amountSentences", "Number of sentences: "},
            {"uniqueSentences", "Number of unique sentences: "},
            {"minSentences", "Minimal sentence: "},
            {"maxSentences", "Maximal sentence: "},
            {"minLenSentences", "Minimal length of a sentence: "},
            {"maxLenSentences", "Maximal length of a sentence: "},
            {"averageSentences", "Average length of sentence: "},

            {"amountStrings", "Number of strings: "},
            {"uniqueStrings", "Number of unique strings: "},
            {"minStrings", "Minimal string: "},
            {"maxStrings", "Maximal string: "},
            {"minLenStrings", "Minimal length of a string: "},
            {"maxLenStrings", "Maximal length of a string: "},
            {"averageStrings", "Average length of string: "},

            {"amountWords", "Number of words: "},
            {"uniqueWords", "Number of unique words: "},
            {"minWords", "Minimal word: "},
            {"maxWords", "Maximal word: "},
            {"minLenWords", "Minimal length of a word: "},
            {"maxLenWords", "Maximal length of a word: "},
            {"averageWords", "Average length of word: "},

            {"amountNumbers", "Number of numbers: "},
            {"uniqueNumbers", "Number of unique numbers: "},
            {"minNumbers", "Minimal number: "},
            {"maxNumbers", "Maximal number: "},
            {"minLenNumbers", "Minimal length of a number: "},
            {"maxLenNumbers", "Maximal length of a number: "},
            {"averageNumbers", "Numbers average: "},

            {"amountCurrencies", "Number of currencies: "},
            {"uniqueCurrencies", "Number of unique currencies: "},
            {"minCurrencies", "Minimal currency: "},
            {"maxCurrencies", "Maximal currency: "},
            {"minLenCurrencies", "Minimal length of a currency: "},
            {"maxLenCurrencies", "Maximal length of a currency: "},
            {"averageCurrencies", "Currency average: "},

            {"amountDates", "Number of dates: "},
            {"uniqueDates", "Number of unique dates: "},
            {"minDates", "Minimal date: "},
            {"maxDates", "Maximal date: "},
            {"minLenDates", "Minimal length of a date: "},
            {"maxLenDates", "Maximal length of a date: "},
            {"averageDates", "Average date: "},

            {"SentencesStat", "Statistics for sentences: "},
            {"StringsStat", "Statistics for strings: "},
            {"WordsStat", "Statistics for words"},
            {"NumbersStat", "Statistics for numbers"},
            {"CurrenciesStat", "Statistics for currencies"},
            {"DatesStat", "Statistics for dates"}
    };
}
