package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import utils.ParseUtils;

import java.util.List;

/** Səbət səhifəsi (#/basket). */
public class CartPage extends BasePage {

    private static final By BASKET_COMPONENT = By.tagName("app-purchase-basket");
    private static final By ROWS = By.tagName("mat-row");
    private static final By TOTAL_PRICE = By.id("price");

    public CartPage(WebDriver driver) {
        super(driver);
    }

    public CartPage open() {
        driver.get(baseUrl + "/#/basket");
        wait.until(ExpectedConditions.urlContains("/basket"));
        wait.until(ExpectedConditions.presenceOfElementLocated(BASKET_COMPONENT));
        return this;
    }

    /** Məhsul səbətdə görünürmü (yüklənməsini gözləyir). */
    public boolean isProductInCart(String productName) {
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(rowByName(productName)));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    public int getQuantity(String productName) {
        WebElement row = waitVisible(rowByName(productName));
        String text = row.findElement(By.cssSelector(".mat-column-quantity")).getText();
        return ParseUtils.parseInt(text);
    }

    /** "Total Price" sahəsindəki məbləğ. Səbətdə məhsul varsa, 0-dan böyük dəyər gözləyir. */
    public double getTotalPrice() {
        try {
            wait.until(d -> ParseUtils.parsePriceOrZero(d.findElement(TOTAL_PRICE).getText()) > 0);
        } catch (TimeoutException ignored) {
            // boş səbətdə total 0 qalır
        }
        return ParseUtils.parsePriceOrZero(driver.findElement(TOTAL_PRICE).getText());
    }

    /** Məhsulun sətrindəki sonuncu düyməni (silmə düyməsi) sıxır və sətrin yox olmasını gözləyir. */
    public void removeProduct(String productName) {
        WebElement row = waitVisible(rowByName(productName));
        List<WebElement> buttons = row.findElements(By.tagName("button"));
        wait.until(ExpectedConditions.elementToBeClickable(buttons.get(buttons.size() - 1))).click();
        wait.until(d -> d.findElements(rowByName(productName)).isEmpty());
    }

    public boolean isEmpty() {
        try {
            wait.until(d -> d.findElements(ROWS).isEmpty());
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    private By rowByName(String productName) {
        return By.xpath("//mat-row[contains(normalize-space(.), '" + productName + "')]");
    }
}
