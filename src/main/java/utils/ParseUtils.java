package utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** UI-dan gələn mətni (məsələn "1.99¤" və ya "Total Price: 3.98¤") ədədə çevirir. */
public final class ParseUtils {

    private static final Pattern NUMBER = Pattern.compile("\\d+(?:\\.\\d+)?");
    private static final Pattern INTEGER = Pattern.compile("\\d+");

    private ParseUtils() {
    }

    public static double parsePrice(String text) {
        Matcher m = NUMBER.matcher(text);
        if (!m.find()) {
            throw new IllegalArgumentException("Mətndə qiymət tapılmadı: '" + text + "'");
        }
        return Double.parseDouble(m.group());
    }

    public static double parsePriceOrZero(String text) {
        Matcher m = NUMBER.matcher(text);
        return m.find() ? Double.parseDouble(m.group()) : 0.0;
    }

    public static int parseInt(String text) {
        Matcher m = INTEGER.matcher(text);
        if (!m.find()) {
            throw new IllegalArgumentException("Mətndə tam ədəd tapılmadı: '" + text + "'");
        }
        return Integer.parseInt(m.group());
    }
}
