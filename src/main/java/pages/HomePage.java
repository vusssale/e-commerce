package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.Keys;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Ana səhifə / məhsul siyahısı (Juice Shop "search" səhifəsi).
 *
 * Locator-lar Juice Shop-un Angular frontend-inə görə yazılıb. Tətbiqin versiyası fərqlidirsə
 * və test element tapa bilmirsə, brauzerdə F12 ilə elementi yoxlayıb aşağıdakı sabitləri düzəldin.
 */
public class HomePage extends BasePage {

    private static final By PRODUCT_TILE = By.cssSelector("mat-grid-tile");
    private static final By PRODUCT_NAME = By.cssSelector("mat-grid-tile .item-name");
    private static final By ACCOUNT_BUTTON = By.id("navbarAccount");
    private static final By LOGOUT_BUTTON = By.id("navbarLogoutButton");
    private static final By SNACKBAR = By.cssSelector("simple-snack-bar, .mat-mdc-snack-bar-container");
    private static final By WELCOME_CLOSE = By.cssSelector("button[aria-label='Close Welcome Banner']");
    private static final By COOKIE_DISMISS = By.cssSelector("a[aria-label='dismiss cookie message']");

    public HomePage(WebDriver driver) {
        super(driver);
    }

    /**
     * Tətbiqi açır. "Welcome" pəncərəsi və cookie bildirişi testləri bloklamasın deyə
     * müvafiq cookie-lər əvvəlcədən qoyulur.
     */
    public HomePage open() {
        driver.get(baseUrl);
        try {
            driver.manage().addCookie(new Cookie("welcomebanner_status", "dismiss"));
            driver.manage().addCookie(new Cookie("cookieconsent_status", "dismiss"));
        } catch (WebDriverException ignored) {
            // cookie qoyulmasa, aşağıdakı dismissOverlays() pəncərələri əl ilə bağlayacaq
        }
        driver.navigate().refresh();
        waitVisible(PRODUCT_TILE);
        dismissOverlays();
        return this;
    }

    /** Axtarışı URL vasitəsilə edir və səhifəni yeniləyir (köhnə nəticələr qarışmasın deyə). */
    public HomePage searchFor(String query) {
        String encoded = URLEncoder.encode(query, StandardCharsets.UTF_8).replace("+", "%20");
        driver.get(baseUrl + "/#/search?q=" + encoded);
        driver.navigate().refresh();
        waitVisible(PRODUCT_TILE);
        dismissOverlays();
        return this;
    }

    public List<String> getProductNames() {
        waitVisible(PRODUCT_NAME);
        return driver.findElements(PRODUCT_NAME).stream()
                .map(e -> e.getText().trim())
                .filter(text -> !text.isEmpty())
                .toList();
    }

    public String getProductPriceText(String productName) {
        WebElement tile = waitVisible(tileByName(productName));
        return tile.findElement(By.cssSelector(".item-price")).getText().trim();
    }

    /** Məhsulun "detallar" pəncərəsini açır. */
    public ProductPage openProductDetails(String productName) {
        WebElement tile = waitVisible(tileByName(productName));
        List<WebElement> infoButtons = tile.findElements(
                By.cssSelector("button[aria-label='Click for more information about the product']"));
        if (!infoButtons.isEmpty()) {
            wait.until(ExpectedConditions.elementToBeClickable(infoButtons.get(0))).click();
        } else {
            tile.findElement(By.cssSelector("img")).click();
        }
        return new ProductPage(driver);
    }

    /**
     * Məhsulu səbətə əlavə edir (yalnız login olmuş istifadəçi üçün düymə görünür).
     *
     * @return ekranda çıxan təsdiq mesajı (məsələn "Placed Apple Juice (1000ml) into basket.")
     */
    public String addToBasket(String productName) {
        WebElement tile = waitVisible(tileByName(productName));
        WebElement button = tile.findElement(By.cssSelector("button[aria-label='Add to Basket']"));
        wait.until(ExpectedConditions.elementToBeClickable(button)).click();

        String message = waitVisible(SNACKBAR).getText().trim();
        // növbəti əlavə etmədən əvvəl bildirişin yox olmasını gözləyirik
        wait.until(ExpectedConditions.invisibilityOfElementLocated(SNACKBAR));
        return message;
    }

    /** Hesab menyusunu açıb "Logout" düyməsinin olub-olmadığına baxır. */
    public boolean isUserLoggedIn() {
        click(ACCOUNT_BUTTON);
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(LOGOUT_BUTTON));
            return true;
        } catch (TimeoutException e) {
            return false;
        } finally {
            driver.findElement(By.tagName("body")).sendKeys(Keys.ESCAPE);
        }
    }

    private By tileByName(String productName) {
        return By.xpath("//mat-grid-tile[.//div[contains(@class,'item-name') "
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
