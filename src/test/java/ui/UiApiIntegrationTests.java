package ui;

import api.ApiClient;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import utils.TestUser;

/** API-dən alınan məlumat UI testində istifadə olunur. */
public class UiApiIntegrationTests extends BaseUiTest {

    @Test(groups = {"smoke", "regression"}, description = "API ilə yaradılan istifadəçi UI-da login edir")
    public void userCreatedViaApiCanLoginInUiTest() {
        // API: yeni istifadəçi
        TestUser newUser = api.createUser();
        Assert.assertEquals(api.login(newUser.email(), newUser.password()).statusCode(), 200,
                "API login işləməlidir");

        // UI: həmin məlumatlarla login
        loginPage.open();
        loginPage.loginSuccessfully(newUser.email(), newUser.password());

        Assert.assertTrue(homePage.isUserLoggedIn(), "API ilə yaradılan istifadəçi UI-da login ola bilməlidir");
    }

    @Test(groups = "regression", description = "API ilə səbətə əlavə olunan məhsul UI-da görünür")
    public void productAddedViaApiIsVisibleInUiCartTest() {
        // API: istifadəçi + səbətə 2 ədəd məhsul
        TestUser newUser = api.createUser();
        ApiClient.AuthSession session = api.loginAndGetSession(newUser);
        ApiClient.ProductInfo product = api.getProductInfo(1);
        Response added = api.addToBasket(session, product.id(), 2);
        Assert.assertTrue(added.statusCode() == 200 || added.statusCode() == 201,
                "Məhsul API ilə əlavə olunmalı idi, status=" + added.statusCode());

        // UI: login və səbətə baxış
        loginPage.open();
        loginPage.loginSuccessfully(newUser.email(), newUser.password());
        cartPage.open();

        Assert.assertTrue(cartPage.isProductInCart(product.name()), "API ilə əlavə olunan məhsul UI səbətində olmalıdır");
        Assert.assertEquals(cartPage.getQuantity(product.name()), 2, "Say da API-dəki kimi olmalıdır");
    }
}
