package ru.ifmo.rain.usov.i18n;

import java.util.ListResourceBundle;

public class Dictionary_RU extends ListResourceBundle {
    @Override
    protected Object[][] getContents() {
        return contents;
    }
    private static final Object[][] contents = {
            {"pageTitle", "Анализатор текста"},
            {"file", "Анализируемый файл: "},
            {"summary", "Сводная статистика: "},

            {"amountSentences", "Количество предложений: "},
            {"uniqueSentences", "Из них уникальных: "},
            {"minSentences", "Минимальное предложение: "},
            {"maxSentences", "Максимальное предложение: "},
            {"minLenSentences", "Минимальная длина предложения: "},
            {"maxLenSentences", "Максимальная длина предложения: "},
            {"averageSentences", "Средняя длина предложения: "},

            {"amountStrings", "Количество строк: "},
            {"uniqueStrings", "Из них уникальных: "},
            {"minStrings", "Минимальная строка: "},
            {"maxStrings", "Максимальная строка: "},
            {"minLenStrings", "Минимальная длина строки: "},
            {"maxLenStrings", "Максимальная длина строки: "},
            {"averageStrings", "Средняя длина строки: "},

            {"amountWords", "Количество слов: "},
            {"uniqueWords", "Из них уникальных: "},
            {"minWords", "Минимальное слово: "},
            {"maxWords", "Максимальное слово: "},
            {"minLenWords", "Минимальная длина слова: "},
            {"maxLenWords", "Максимальная длина слова: "},
            {"averageWords", "Средняя длина слова: "},

            {"amountNumbers", "Количество чисел: "},
            {"uniqueNumbers", "Из них уникальных: "},
            {"minNumbers", "Минимальное число: "},
            {"maxNumbers", "Максимальное число: "},
            {"minLenNumbers", "Минимальная длина числа: "},
            {"maxLenNumbers", "Максимальная длина числа: "},
            {"averageNumbers", "Среднее арифметическое чисел: "},

            {"amountCurrencies", "Количество валют: "},
            {"uniqueCurrencies", "Из них уникальных: "},
            {"minCurrencies", "Минимальная валюта: "},
            {"maxCurrencies", "Максимальная валюта: "},
            {"minLenCurrencies", "Минимальная длина валюты: "},
            {"maxLenCurrencies", "Максимальная длина валюты: "},
            {"averageCurrencies", "Среднее валют: "},

            {"amountDates", "Количество дат: "},
            {"uniqueDates", "Из них уникальных: "},
            {"minDates", "Минимальная дата: "},
            {"maxDates", "Максимальная дата: "},
            {"minLenDates", "Минимальная длина даты: "},
            {"maxLenDates", "Максимальная длина даты: "},
            {"averageDates", "Среднее дат: "},

            {"SentencesStat", "Статистика предложений: "},
            {"StringsStat", "Статистика строк: "},
            {"WordsStat", "Статистика слов: "},
            {"NumbersStat", "Статистика чисел: "},
            {"CurrenciesStat", "Статистика валют: "},
            {"DatesStat", "Статистика дат: "}
    };
}
