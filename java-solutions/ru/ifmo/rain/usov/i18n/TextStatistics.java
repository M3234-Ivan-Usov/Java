package ru.ifmo.rain.usov.i18n;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.*;
import java.util.*;
import java.util.stream.Collectors;

public class TextStatistics {

    private static List<Locale> available = Arrays.stream(Locale.getAvailableLocales()).
            collect(Collectors.toCollection(LinkedList::new));

    private String text;
    private ResourceBundle dictionary;
    private Locale locale;

    private List<TreeMap<Double, String>> numericInfo;
    private NumberFormat numberIterator;
    private NumberFormat currencyIterator;
    private DateFormat dateIterator;

    private StringBuilder report;
    Map<String, Params> summary;

    TextStatistics(String text, Locale in, Locale out) throws IllegalArgumentException {
        switch (out.getLanguage()) {
            case "en":
                dictionary = ResourceBundle.getBundle(Dictionary_EN.class.getCanonicalName());
                break;
            case "ru":
                dictionary = ResourceBundle.getBundle(Dictionary_RU.class.getCanonicalName());
                break;
            default:
                throw new IllegalArgumentException("Use only 'en' or 'ru' for report");
        }

        this.text = text;
        report = new StringBuilder();
        locale = in;

        summary = new LinkedHashMap<>(6);
        summary.put("Sentences", new Params());
        summary.put("Strings", new Params());
        summary.put("Words", new Params());
        summary.put("Numbers", new Params());
        summary.put("Currencies", new Params());
        summary.put("Dates", new Params(false));

        numericInfo = new LinkedList<>();
        numericInfo.add(new TreeMap<>());
        numberIterator = NumberFormat.getNumberInstance(locale);
        numericInfo.add(new TreeMap<>());
        currencyIterator = NumberFormat.getCurrencyInstance(locale);
        numericInfo.add(new TreeMap<>());
        dateIterator = DateFormat.getDateInstance(DateFormat.SHORT, locale);
    }

    public void launch() {
        parseText("Sentences", BreakIterator.getSentenceInstance(locale));
        parseText("Strings", BreakIterator.getLineInstance(locale));
        parseText("Words", BreakIterator.getWordInstance(locale));
        for (int i = 0; i < 3; i++) {
            String param = "";
            switch (i) {
                case 0:
                    param = "Numbers";
                    break;
                case 1:
                    param = "Currencies";
                    break;
                case 2:
                    param = "Dates";
                    break;
            }
            if (!(summary.get(param).amount == 0)) {
                summary.get(param).uniqueAmount = numericInfo.get(i).size();

                summary.get(param).minValue = numericInfo.get(i).get(numericInfo.get(i).firstKey());
                summary.get(param).maxValue = numericInfo.get(i).get(numericInfo.get(i).lastKey());

                summary.get(param).minLengthValue = numericInfo.get(i).values().
                        stream().min(Comparator.comparingInt(String::length)).get();
                summary.get(param).minLength = summary.get(param).minLengthValue.length();

                summary.get(param).maxLengthValue = numericInfo.get(i).values().
                        stream().max(Comparator.comparingInt(String::length)).get();
                summary.get(param).maxLength = summary.get(param).maxLengthValue.length();
            }
        }
    }

    private void parseText(String category, BreakIterator iterator) {
        iterator.setText(text);
        Params currentCategory = summary.get(category);
        NavigableSet<String> contentComparator = new TreeSet<>(Collator.getInstance(locale));
        NavigableMap<Integer, String> lengthComparator = new TreeMap<>();
        int begin = iterator.first();
        int end = iterator.next();
        while (end != BreakIterator.DONE) {
            String current = text.substring(begin, end);
            if (category.equals("Strings")) {
                try {
                    Date check = dateIterator.parse(current);
                    numericInfo.get(2).put((double) check.getTime(), check.toString());
                    summary.get("Dates").amount++;
                    begin = end;
                    end = iterator.next();
                    continue;
                } catch (ParseException ignored) {
                }
                try {
                    Number check = currencyIterator.parse(current);
                    numericInfo.get(1).put(check.doubleValue(), check.toString());
                    summary.get("Currencies").amount++;
                    summary.get("Currencies").sum += check.doubleValue();
                    begin = end;
                    end = iterator.next();
                    continue;
                } catch (ParseException ignored) {
                }
                try {
                    Number check = numberIterator.parse(current);
                    numericInfo.get(0).put(check.doubleValue(), check.toString());
                    summary.get("Numbers").amount++;
                    summary.get("Numbers").sum += check.doubleValue();
                    begin = end;
                    end = iterator.next();
                    continue;
                } catch (ParseException ignored) {
                }
            }
            summary.get(category).amount++;
            summary.get(category).sum += current.length();
            contentComparator.add(current);
            lengthComparator.put(current.length(), current);
            begin = end;
            end = iterator.next();
        }
        currentCategory.uniqueAmount = contentComparator.size();
        currentCategory.minValue = contentComparator.first();
        currentCategory.maxValue = contentComparator.last();
        currentCategory.minLength = lengthComparator.firstKey();
        currentCategory.minLengthValue = lengthComparator.get(lengthComparator.firstKey());
        currentCategory.maxLength = lengthComparator.lastKey();
        currentCategory.maxLengthValue = lengthComparator.get(lengthComparator.lastKey());
    }

    public String createReport(String fileName) {
        getHeader(fileName);
        getSummary();
        summary.forEach(this::print);
        getTail();
        return report.toString();
    }

    private void getHeader(String fileName) {
        report.append("<html>\n");
        report.append("<head>\n");
        report.append("<meta charset=\"utf-8\">\n");
        report.append("<title>").append(get("pageTitle")).append("</title>\n");
        report.append("<body>\n");
        report.append("<h1>").append(get("file")).append(fileName).append("</h1>\n");
    }

    private void getSummary() {
        report.append("<p><h3>").append(get("summary")).append("</h3><br>\n");
        for (String category : summary.keySet()) {
            report.append(get("amount" + category)).append(summary.get(category).amount).append("<br>\n");
        }
        report.append("</p>\n");

    }

    private void print(String category, Params statistic) {
        report.append("<p><h4>").append(get(category + "Stat")).append("</h4><br>");
        report.append(get("amount" + category)).append(statistic.amount).append("<br>\n");
        report.append(get("unique" + category)).append(statistic.uniqueAmount).append("<br>\n");
        report.append(get("min" + category)).append(statistic.minValue).append("<br>\n");
        report.append(get("max" + category)).append(statistic.maxValue).append("<br>\n");
        report.append(get("minLen" + category)).append(statistic.minLength).
                append(" (").append(statistic.minLengthValue).append(")<br>\n");
        report.append(get("maxLen" + category)).append(statistic.maxLength).
                append(" (").append(statistic.maxLengthValue).append(")<br>\n");
        if (statistic.hasAverage) {
            report.append(get("average" + category)).append(statistic.getAverage()).append("<br>\n");
        }
        report.append("</p>\n");
    }

    private void getTail() {
        report.append("</body>\n");
        report.append("</html>");
    }

    private String get(String x) {
        return dictionary.getString(x);
    }

    public static Locale getLocale(String[] args) {
        switch (args.length) {
            case 4:
                return new Locale(args[0]);
            case 5:
                return new Locale(args[0], args[1]);
            case 6:
                return new Locale(args[0], args[1], args[2]);
            default:
                return null;
        }
    }

    public static void main(String[] args) {
        if (args.length < 4) {
            System.out.println("Incorrect args");
            return;
        }
        Locale inLocale = getLocale(args);
        Locale outLocale = new Locale(args[args.length - 3]);
        if (!((available.contains(inLocale) && (available.contains(outLocale))))) {
            System.out.println("Locale is not available");
            return;
        }
        try {
            Path path = Path.of(args[args.length - 2]);
            String inputText = Files.readString(path);
            TextStatistics analyser = new TextStatistics(inputText, inLocale, outLocale);
            analyser.launch();
            BufferedWriter report = Files.newBufferedWriter(Path.of(args[args.length - 1]));
            report.write(analyser.createReport(path.getFileName().toString()));
            report.close();
        } catch (IOException e) {
            System.out.println("I/O Error: " + e.getMessage());
        }
    }
}
