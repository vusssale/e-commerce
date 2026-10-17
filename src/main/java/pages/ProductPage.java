package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;

/** Məhsulun detallar pəncərəsi (dialog). */
public class ProductPage extends BasePage {

    private static final By DIALOG = By.cssSelector("mat-dialog-container");

    public ProductPage(WebDriver driver) {
        super(driver);
    }

    public boolean isDisplayed() {
        try {
            waitVisible(DIALOG);
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    /** Pəncərənin bütün mətni (ad, təsvir və s.). */
    public String getContent() {
        wait.until(d -> !d.findElement(DIALOG).getText().isBlank());
        return driver.findElement(DIALOG).getText();
    }

    public void close() {
        driver.findElement(By.tagName("body")).sendKeys(Keys.ESCAPE);
    }
}
