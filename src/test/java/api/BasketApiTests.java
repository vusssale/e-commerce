package api;

import base.BaseTest;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import utils.TestUser;

import static org.hamcrest.Matchers.*;

/** Autentifikasiya tələb edən GET / POST / PUT / DELETE testləri (səbət). */
public class BasketApiTests extends BaseTest {

    private final ApiClient api = new ApiClient();
    private ApiClient.AuthSession session;
    private ApiClient.ProductInfo product;

    @BeforeClass(alwaysRun = true)
    public void prepare() {
        TestUser user = api.createUser();
        session = api.loginAndGetSession(user);
        product = api.getProductInfo(1);
    }

    @BeforeMethod(alwaysRun = true)
    public void emptyBasket() {
        api.clearBasket(session);
    }

    @Test(groups = "regression", description = "GET /rest/basket/{id}")
    public void getBasketTest() {
        api.getBasket(session).then()
                .statusCode(200)
                .body("status", equalTo("success"))
                .body("data.id", equalTo(session.basketId()))
                .body("data.Products", hasSize(0));
    }

    @Test(groups = "regression", description = "POST /api/BasketItems")
    public void addItemToBasketTest() {
        api.addToBasket(session, product.id(), 1).then()
                .statusCode(anyOf(is(200), is(201)))
                .body("status", equalTo("success"))
                .body("data.ProductId", equalTo(product.id()))
                .body("data.quantity", equalTo(1));

        Assert.assertTrue(api.getBasketProductNames(session).contains(product.name()),
                "Məhsul səbətdə görünməlidir");
    }

    @Test(groups = "regression", description = "PUT /api/BasketItems/{id}")
    public void updateBasketItemQuantityTest() {
        int itemId = api.addToBasket(session, product.id(), 1).jsonPath().getInt("data.id");

        api.updateBasketItem(session, itemId, 3).then()
                .statusCode(200)
                .body("status", equalTo("success"))
                .body("data.quantity", equalTo(3));
    }

    @Test(groups = "regression", description = "DELETE /api/BasketItems/{id}")
    public void deleteBasketItemTest() {
        Response added = api.addToBasket(session, product.id(), 1);
        int itemId = added.jsonPath().getInt("data.id");

        api.deleteBasketItem(session, itemId).then()
                .statusCode(200);

        api.getBasket(session).then()
                .statusCode(200)
                .body("data.Products", hasSize(0));
    }
}
