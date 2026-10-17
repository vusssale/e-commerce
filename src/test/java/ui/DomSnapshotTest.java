package ui;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.testng.annotations.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;

/**
 * Köməkçi alət: testng.xml-ə daxil DEYİL, adi testlərlə işləmir.
 * Juice Shop-un əsas səhifələrinin real DOM-unu və ekran görüntüsünü yığır, locator-ları yoxlamaq üçündür.
 *
 *   mvn clean test -Dtest=DomSnapshotTest -Dbrowser=chrome -Dheadless=false
 *
 * Nəticə: target/dom-snapshots/ (index.txt + .html + .png)
 */
public class DomSnapshotTest extends BaseUiTest {

    private static final Path DIR = Path.of("target", "dom-snapshots");

    @FunctionalInterface
    private interface Step {
        void run() throws Exception;
    }

    @Test(description = "Real DOM snapshot-ları yığır")
    public void captureDomSnapshots() throws Exception {
        Files.createDirectories(DIR);
        Files.deleteIfExists(DIR.resolve("index.txt"));

        snapshot("01_home", () -> { });

        snapshot("02_login_page", () -> loginPage.open());

        snapshot("03_login_wrong_password", () -> {
            loginPage.login(user.email(), "YanlisParol!999");
            pause(1500);
        });

        snapshot("04_after_login", () -> {
            loginPage.open();
            loginPage.loginSuccessfully(user.email(), user.password());
            pause(1500);
        });

        snapshot("05_account_menu_logged_in", () -> {
            driver.findElement(By.id("navbarAccount")).click();
            pause(1000);
        });
        driver.findElement(By.tagName("body")).sendKeys(Keys.ESCAPE);

        snapshot("06_product_dialog", () -> {
            driver.get(baseUrl() + "/#/search");
            pause(1500);
            driver.findElement(By.cssSelector(
                    "app-product section[aria-label='Click for more information about the product']")).click();
            pause(1500);
        });
        driver.findElement(By.tagName("body")).sendKeys(Keys.ESCAPE);
        pause(500);

        snapshot("07_after_add_to_basket", () -> {
            driver.get(baseUrl() + "/#/search");
            pause(1500);
            driver.findElement(By.cssSelector("app-product button[aria-label='Add to Basket']")).click();
            pause(600);
        });
        pause(2500);

        snapshot("08_basket_page", () -> {
            driver.get(baseUrl() + "/#/basket");
            pause(2500);
        });

        System.out.println("DOM snapshot-lar hazırdır: " + DIR.toAbsolutePath());
    }

    private String baseUrl() {
        return utils.ConfigReader.baseUrl();
    }

    private void snapshot(String name, Step step) throws Exception {
        String error = "";
        try {
            step.run();
        } catch (Exception e) {
            error = e.getClass().getSimpleName() + ": " + firstLine(e.getMessage());
            System.err.println("[" + name + "] addım uğursuz oldu: " + error);
        }
        Files.writeString(DIR.resolve(name + ".html"), driver.getPageSource());
        File png = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        Files.copy(png.toPath(), DIR.resolve(name + ".png"), StandardCopyOption.REPLACE_EXISTING);
        Files.writeString(DIR.resolve("index.txt"),
                name + " | url=" + driver.getCurrentUrl() + " | error=" + error + System.lineSeparator(),
                StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }

    private static String firstLine(String text) {
        if (text == null) {
            return "";
        }
        int idx = text.indexOf('\n');
        return idx < 0 ? text : text.substring(0, idx);
    }

    private static void pause(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
