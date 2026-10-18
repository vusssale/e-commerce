package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

/** Login səhifəsi (#/login). */
public class LoginPage extends BasePage {

    private static final By EMAIL = By.id("email");
    private static final By PASSWORD = By.id("password");
    private static final By LOGIN_BUTTON = By.id("loginButton");
    private static final By ERROR_MESSAGE = By.cssSelector(".error");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public LoginPage open() throws InterruptedException {
        driver.get(baseUrl + "/#/login");
        waitVisible(EMAIL);
        return this;
    }

    /** Məlumatları daxil edib "Log in" düyməsini sıxır (nəticəni gözləmir). */
    public void login(String email, String password) throws InterruptedException {
        type(EMAIL, email);
        type(PASSWORD, password);
        click(LOGIN_BUTTON);
    }

    /** Login edir və uğurlu olduqda səhifənin /login-dən çıxmasını gözləyir. */
    public void loginSuccessfully(String email, String password) throws InterruptedException {
        login(email, password);
        wait.until(ExpectedConditions.not(ExpectedConditions.urlContains("/login")));
    }

    public String getErrorMessage() throws InterruptedException {
        return getText(ERROR_MESSAGE);
    }

    public boolean isLoginButtonEnabled() throws InterruptedException {
        return waitVisible(LOGIN_BUTTON).isEnabled();
    }
}
