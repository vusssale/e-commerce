package utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * config.properties faylını oxuyur.
 * System property (-Dkey=value) həmişə fayldakı dəyərdən üstündür.
 */
public final class ConfigReader {

    private static final Properties PROPS = load();

    private ConfigReader() {
    }

    private static Properties load() {
        Properties props = new Properties();
        try (InputStream is = ConfigReader.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (is == null) {
                throw new IllegalStateException("config.properties tapılmadı (src/main/resources)");
            }
            props.load(is);
        } catch (IOException e) {
            throw new IllegalStateException("config.properties oxunmadı", e);
        }
        return props;
    }

    public static String get(String key) {
        String fromSystem = System.getProperty(key);
        if (fromSystem != null && !fromSystem.isBlank()) {
            return fromSystem.trim();
        }
        String fromFile = PROPS.getProperty(key);
        if (fromFile == null) {
            throw new IllegalStateException("Konfiqurasiya açarı tapılmadı: " + key);
        }
        return fromFile.trim();
    }

    public static String baseUrl() {
        String url = get("base.url");
        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }

    public static String browser() {
        return get("browser");
    }

    public static boolean headless() {
        return Boolean.parseBoolean(get("headless"));
    }

    public static int timeoutSeconds() {
        return Integer.parseInt(get("timeout.seconds"));
    }

    public static int retryCount() {
        return Integer.parseInt(get("retry.count"));
    }
}
