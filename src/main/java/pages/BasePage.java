package pages;

import constants.Constants;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.ConfigReader;

import java.time.Duration;

/** Bütün Page Object-lər üçün ortaq əməliyyatlar və explicit wait-lər. */
public abstract class BasePage {

    protected final WebDriver driver;
    protected final WebDriverWait wait;
    protected final String baseUrl = ConfigReader.baseUrl();

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.timeoutSeconds()));
    }

    protected WebElement waitVisible(By locator) throws InterruptedException {
        Thread.sleep(Constants.ActionTimeout);
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected void click(By locator) {
        wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
    }

    protected void type(By locator, String text) throws InterruptedException {
        WebElement element = waitVisible(locator);
        element.clear();
        element.sendKeys(text);
        Thread.sleep(Constants.ActionTimeout);
    }

    protected String getText(By locator) throws InterruptedException {
        Thread.sleep(Constants.ActionTimeout);
        return waitVisible(locator).getText().trim();
    }
}
