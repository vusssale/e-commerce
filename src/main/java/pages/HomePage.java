package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;

/**
 * Ana səhifə / məhsul siyahısı (Juice Shop "search" səhifəsi).
 *
 * Aşağıdakı locator-lar real DOM-dan (Juice Shop, Angular 22 versiyası) götürülüb:
 *   app-product > mat-card > article > section[aria-label="Click for more information about the product"]
 *   .name (məhsulun adı), .price (qiymət), button[aria-label="Add to Basket"], .warn-notification (səbət sayğacı)
 */
public class HomePage extends BasePage {

    private static final By PRODUCT_CARD = By.cssSelector("app-product");
    private static final By PRODUCT_NAME = By.cssSelector("app-product .name");
    private static final By BASKET_COUNTER = By.cssSelector(".warn-notification");
    private static final By WELCOME_CLOSE = By.cssSelector("button[aria-label='Close Welcome Banner']");
    private static final By COOKIE_DISMISS = By.cssSelector("a[aria-label='dismiss cookie message']");

    public HomePage(WebDriver driver) {
        super(driver);
    }

    /**
     * Tətbiqi açır. "Welcome" pəncərəsi və cookie bildirişi testləri bloklamasın deyə
     * müvafiq cookie-lər əvvəlcədən qoyulur.
     */
    public HomePage open() throws InterruptedException {
        driver.get(baseUrl);
        try {
            driver.manage().addCookie(new Cookie("welcomebanner_status", "dismiss"));
            driver.manage().addCookie(new Cookie("cookieconsent_status", "dismiss"));
        } catch (WebDriverException ignored) {
            // cookie qoyulmasa, aşağıdakı dismissOverlays() pəncərələri əl ilə bağlayacaq
        }
        driver.navigate().refresh();
        waitVisible(PRODUCT_CARD);
        dismissOverlays();
        return this;
    }

    /** Axtarışı URL vasitəsilə edir və səhifəni yeniləyir (köhnə nəticələr qarışmasın deyə). */
    public HomePage searchFor(String query) throws InterruptedException {
        String encoded = URLEncoder.encode(query, StandardCharsets.UTF_8).replace("+", "%20");
        driver.get(baseUrl + "/#/search?q=" + encoded);
        driver.navigate().refresh();
        waitVisible(PRODUCT_CARD);
        dismissOverlays();
        return this;
    }

    public List<String> getProductNames() throws InterruptedException {
        waitVisible(PRODUCT_NAME);
        return driver.findElements(PRODUCT_NAME).stream()
                .map(e -> e.getText().trim())
                .filter(text -> !text.isEmpty())
                .toList();
    }

    /** Məsələn "1.99¤". */
    public String getProductPriceText(String productName) throws InterruptedException {
        WebElement card = waitVisible(cardByName(productName));
        return card.findElement(By.cssSelector(".price")).getText().trim();
    }

    /** Məhsulun "detallar" pəncərəsini açır (kartın üst hissəsinə klik). */
    public ProductPage openProductDetails(String productName) throws InterruptedException {
        WebElement card = waitVisible(cardByName(productName));
        WebElement details = card.findElement(
                By.cssSelector("section[aria-label='Click for more information about the product']"));
        wait.until(ExpectedConditions.elementToBeClickable(details)).click();
        return new ProductPage(driver);
    }

    /** Məhsulu səbətə əlavə edir və səbət sayğacının yenilənməsini (ən çox 3 saniyə) gözləyir. */
    public void addToBasket(String productName) throws InterruptedException {
        WebElement card = waitVisible(cardByName(productName));
        int before = getBasketCount();
        WebElement button = card.findElement(By.cssSelector("button[aria-label='Add to Basket']"));
        wait.until(ExpectedConditions.elementToBeClickable(button)).click();
        try {
            new WebDriverWait(driver, Duration.ofSeconds(3)).until(d -> getBasketCount() != before);
        } catch (TimeoutException ignored) {
            // sayğac dəyişməyə bilər (eyni məhsulun sayı artanda), sorğunun bitməsi üçün 3 san. kifayətdir
        }
    }

    /** Yuxarı menyudakı "Your Basket" yanındakı rəqəm. */
    public int getBasketCount() {
        List<WebElement> counters = driver.findElements(BASKET_COUNTER);
        if (counters.isEmpty()) {
            return 0;
        }
        try {
            return Integer.parseInt(counters.get(0).getText().trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * İstifadəçi login olub? Tətbiqin özü login-dən sonra JWT token-i localStorage-də
     * və "token" cookie-sində saxlayır, ona görə onu yoxlayırıq.
     */
    public boolean isUserLoggedIn() {
        try {
            wait.until(d -> {
                Object token = ((JavascriptExecutor) d).executeScript("return window.localStorage.getItem('token');");
                return token != null || d.manage().getCookieNamed("token") != null;
            });
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    private By cardByName(String productName) {
        return By.xpath("//app-product[.//div[contains(concat(' ', normalize-space(@class), ' '), ' name ') "
                + "and contains(normalize-space(.), '" + productName + "')]]");
    }

    private void dismissOverlays() {
        for (By locator : List.of(WELCOME_CLOSE, COOKIE_DISMISS)) {
            List<WebElement> elements = driver.findElements(locator);
            if (!elements.isEmpty() && elements.get(0).isDisplayed()) {
                try {
                    elements.get(0).click();
                } catch (WebDriverException ignored) {
                    // artıq bağlanıb
                }
            }
        }
    }
}
