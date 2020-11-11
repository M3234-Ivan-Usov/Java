package ru.ifmo.rain.usov.i18n;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import static org.junit.Assert.*;

import java.util.Locale;

public class Tests {
    private String testInput;
    private TextStatistics tester;
    private Locale in;
    private Locale out;

    @BeforeClass
    public static void start() {
        System.out.println("---Start testing---");
    }

    @AfterClass
    public static void end() {
        System.out.println("---End testing---");
    }

    @Test
    public void test01_numeric() {
        testInput = "Check for dates: 22/07/2019 and 07/11/1846 or 22/07/2019\n" +
                "Check for currency: $100 or $123.00 or $1,002,556.32, $123, $375\n" +
                "Check for numbers: integer 57, negative -23, double 1024,234 and negative double -653,11\n" +
                "non unique -23 and non unique 1024,234\n" +
                "So, there must be 3 ( 2 ) dates, 5 ( 4 ) currencies and 12 ( 10 ) numbers";
        in = new Locale("en", "US");
        out = new Locale("ru");
        tester = new TextStatistics(testInput, in, out);
        tester.launch();
        Params dates = tester.summary.get("Dates");
        Params currencies = tester.summary.get("Currencies");
        Params numbers = tester.summary.get("Numbers");
        assertEquals(3, dates.amount);
        assertEquals(2, dates.uniqueAmount);
        assertEquals(5, currencies.amount);
        assertEquals(4, currencies.uniqueAmount);
        assertEquals(12, numbers.amount);
        assertEquals(10, numbers.uniqueAmount);
    }

    @Test
    public void test02_german() {
        testInput = "Boeing hat die Produktion der einem weltweiten Flugverbot unterliegenden 737 MAX " +
                "wieder aufgenommen. Die Herstellung des Passagierflugzeugs in der Fabrik in Renton im " +
                "US-Bundesstaat Washington sei auf \"niedrigem\" Niveau wieder angelaufen, teilte der " +
                "krisengeschüttelte Konzern am Mittwoch mit. Die Produktion der 737 MAX war seit Januar " +
                "ausgesetzt gewesen.\n" +
                "Nach zwei Abstürzen von Maschinen dieses Typs in Indonesien und Äthiopien mit insgesamt 346 " +
                "Todesopfern darf die 737 MAX seit März vergangenen Jahres weltweit nicht mehr fliegen. Wann sie " +
                "wieder starten kann, ist völlig unklar.\n" +
                "Ermittler gehen davon aus, dass die Abstürze durch ein Softwareproblem in einem Stabilisierungssystem " +
                "verursacht worden waren, das bei einem drohenden Strömungsabriss die Flugzeugnase nach unten drückt. " +
                "Die Sicherheitsprobleme der 737 MAX stürzten Boeing in eine tiefe Krise, die dann zuletzt durch den " +
                "weltweiten Einbruch des Flugverkehrs als Folge der Coronavirus-Pandemie weiter verschärft wurde.\n" +
                "Boeing verzeichnete im ersten Quartal 2020 einen Verlust von 641 Millionen Dollar ( 582 Millionen " +
                "Euro ). Der Umsatz schrumpfte um 26 Prozent im Vergleich zum Vorjahreszeitraum auf 16,91 Milliarden " +
                "Dollar. Der Konzern will zehn Prozent seiner weltweit rund 160.000 Stellen abbauen.";
        in = new Locale("de");
        out = new Locale("en");
        tester = new TextStatistics(testInput, in, out);
        tester.launch();
        assertEquals(10, tester.summary.get("Sentences").amount);
        assertEquals(372, tester.summary.get("Words").amount);
        assertEquals(140, tester.summary.get("Words").uniqueAmount);
        assertEquals(11, tester.summary.get("Numbers").amount);
        assertEquals(8, tester.summary.get("Numbers").uniqueAmount);
    }

    public static void main(String[] args) {
        final Result result = new JUnitCore().run(Tests.class);
        if (result.wasSuccessful()) {
            System.out.println("---Success---");
        } else {
            for (Failure fail : result.getFailures()) {
                System.out.println(String.format("%s :: %s", fail.getDescription().getMethodName(), fail.getMessage()));
            }
        }
        System.exit(0);
    }

}
