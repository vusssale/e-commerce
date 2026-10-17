package ui;

import org.testng.Assert;
import org.testng.annotations.Test;
import utils.TestDataGenerator;

public class LoginTests extends BaseUiTest {

    @Test(groups = {"smoke", "regression"}, description = "Valid credentials ilə login")
    public void validLoginTest() {
        loginPage.open();
        loginPage.loginSuccessfully(user.email(), user.password());

        Assert.assertTrue(homePage.isUserLoggedIn(), "Login-dən sonra 'Logout' düyməsi görünməli idi");
    }

    @Test(groups = "regression", description = "Mövcud olmayan istifadəçi ilə login (negative)")
    public void invalidUsernameLoginTest() {
        loginPage.open();
        loginPage.login(TestDataGenerator.randomEmail(), user.password());

        Assert.assertTrue(loginPage.getErrorMessage().contains("Invalid email or password"),
                "Xəta mesajı göstərilməli idi");
        Assert.assertTrue(driver.getCurrentUrl().contains("/login"), "İstifadəçi login səhifəsində qalmalı idi");
    }

    @Test(groups = "regression", description = "Yanlış parol ilə login (negative)")
    public void invalidPasswordLoginTest() {
        loginPage.open();
        loginPage.login(user.email(), "YanlisParol!999");

        Assert.assertTrue(loginPage.getErrorMessage().contains("Invalid email or password"),
                "Xəta mesajı göstərilməli idi");
        Assert.assertTrue(driver.getCurrentUrl().contains("/login"), "İstifadəçi login səhifəsində qalmalı idi");
    }

    @Test(groups = "regression", description = "Boş sahələrlə login (negative)")
    public void emptyFieldsLoginTest() {
        loginPage.open();

        Assert.assertFalse(loginPage.isLoginButtonEnabled(),
                "Sahələr boş olanda 'Log in' düyməsi aktiv olmamalıdır");
    }
}
