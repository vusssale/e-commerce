package ui;

import api.ApiClient;
import base.BaseTest;
import driver.DriverManager;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import pages.CartPage;
import pages.HomePage;
import pages.LoginPage;
import utils.TestUser;

/** UI testləri üçün ortaq setup/teardown. Brauzer məntiqi yalnız burada və DriverManager-dədir. */
public abstract class BaseUiTest extends BaseTest {

    protected final ApiClient api = new ApiClient();

    protected WebDriver driver;
    protected HomePage homePage;
    protected LoginPage loginPage;
    protected CartPage cartPage;

    /** Hər test sinfi üçün API ilə yeni istifadəçi yaradılır. */
    protected TestUser user;

    @BeforeClass(alwaysRun = true)
    public void createUserViaApi() {
        user = api.createUser();
    }

    @BeforeMethod(alwaysRun = true)
    public void setUpBrowser() {
        DriverManager.initDriver();
        driver = DriverManager.getDriver();

        // Page Object-lər əvvəl yaradılır ki, açılış uğursuz olsa belə null qalmasınlar
        homePage = new HomePage(driver);
        loginPage = new LoginPage(driver);
        cartPage = new CartPage(driver);

        homePage.open();
    }

    @AfterMethod(alwaysRun = true)
    public void tearDownBrowser() {
        DriverManager.quitDriver();
    }
}
