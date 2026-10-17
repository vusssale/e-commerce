package driver;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import utils.ConfigReader;

import java.time.Duration;

/**
 * WebDriver-in yaradılması və bağlanması bir yerdə idarə olunur.
 * ThreadLocal istifadə olunur, buna görə paralel işləmə (parallel execution) də mümkündür.
 * Brauzer drayverini Selenium Manager özü yükləyir.
 */
public final class DriverManager {

    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();

    private DriverManager() {
    }

    public static WebDriver getDriver() {
        return DRIVER.get();
    }

    public static void initDriver() {
        initDriver(ConfigReader.browser(), ConfigReader.headless());
    }

    public static void initDriver(String browser, boolean headless) {
        WebDriver webDriver = switch (browser.toLowerCase()) {
            case "firefox" -> {
                FirefoxOptions options = new FirefoxOptions();
                if (headless) {
                    options.addArguments("-headless");
                }
                yield new FirefoxDriver(options);
            }
            case "edge" -> {
                EdgeOptions options = new EdgeOptions();
                if (headless) {
                    options.addArguments("--headless=new");
                }
                yield new EdgeDriver(options);
            }
            default -> {
                ChromeOptions options = new ChromeOptions();
                if (headless) {
                    options.addArguments("--headless=new");
                }
                yield new ChromeDriver(options);
            }
        };

        if (headless) {
            webDriver.manage().window().setSize(new Dimension(1920, 1080));
        } else {
            webDriver.manage().window().maximize();
        }
        webDriver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
        DRIVER.set(webDriver);
    }

    public static void quitDriver() {
        WebDriver webDriver = DRIVER.get();
        if (webDriver != null) {
            webDriver.quit();
            DRIVER.remove();
        }
    }
}
