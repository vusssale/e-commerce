package api;

import constants.Constants;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import utils.ConfigReader;
import utils.TestDataGenerator;
import utils.TestUser;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;

/**
 * Juice Shop REST API üçün kiçik client.
 * Həm API testlərində, həm də UI testlərinin hazırlıq/təmizlik addımlarında istifadə olunur.
 */
public class ApiClient {

    /** Login-dən sonra alınan token və istifadəçinin səbət (basket) id-si. */
    public record AuthSession(String token, int basketId, String email) {
    }

    /** API-dən oxunan məhsul məlumatı. */
    public record ProductInfo(int id, String name, double price) {
    }

    private static RequestSpecification spec() {
        return given()
                .baseUri(ConfigReader.baseUrl())
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON);
    }

    private static RequestSpecification auth(AuthSession session) throws InterruptedException {
        Thread.sleep(Constants.ActionTimeout);
        return spec().header("Authorization", "Bearer " + session.token());
    }

    // ---------------------------------------------------------------- users

    public Response register(String email, String password) throws InterruptedException {
        Thread.sleep(Constants.ActionTimeout);
        return spec()
                .body(Map.of(
                        "email", email,
                        "password", password,
                        "passwordRepeat", password))
                .post("/api/Users");
    }

    /** Yeni istifadəçi yaradır (201 gözlənilir) və məlumatlarını qaytarır. */
    public TestUser createUser() throws InterruptedException {
        Thread.sleep(Constants.ActionTimeout);
        TestUser user = TestDataGenerator.newUser();
        Response response = register(user.email(), user.password());
        if (response.statusCode() != 201) {
            throw new IllegalStateException("İstifadəçi yaradıla bilmədi. status="
                    + response.statusCode() + ", body=" + response.asString());
        }
        return user;
    }

    public Response login(String email, String password) throws InterruptedException {
        Thread.sleep(Constants.ActionTimeout);
        return spec()
                .body(Map.of("email", email, "password", password))
                .post("/rest/user/login");
    }

    public AuthSession loginAndGetSession(TestUser user) throws InterruptedException {
        Thread.sleep(Constants.ActionTimeout);
        Response response = login(user.email(), user.password());
        if (response.statusCode() != 200) {
            throw new IllegalStateException("Login uğursuz oldu. status="
                    + response.statusCode() + ", body=" + response.asString());
        }
        JsonPath json = response.jsonPath();
        return new AuthSession(
                json.getString("authentication.token"),
                json.getInt("authentication.bid"),
                json.getString("authentication.umail"));
    }

    // ------------------------------------------------------------- products

    public Response getProducts() throws InterruptedException {
        Thread.sleep(Constants.ActionTimeout);
        return spec().get("/api/Products");
    }

    public Response getProduct(int id) throws InterruptedException {
        Thread.sleep(Constants.ActionTimeout);
        return spec().get("/api/Products/{id}", id);
    }

    public Response searchProducts(String query) throws InterruptedException {
        Thread.sleep(Constants.ActionTimeout);
        return spec().queryParam("q", query).get("/rest/products/search");
    }

    public ProductInfo getProductInfo(int id) throws InterruptedException {
        Thread.sleep(Constants.ActionTimeout);
        Response response = getProduct(id);
        if (response.statusCode() != 200) {
            throw new IllegalStateException("Məhsul oxunmadı. id=" + id + ", status=" + response.statusCode());
        }
        JsonPath json = response.jsonPath();
        return new ProductInfo(json.getInt("data.id"), json.getString("data.name"), json.getDouble("data.price"));
    }

    // --------------------------------------------------------------- basket

    public Response getBasket(AuthSession session) throws InterruptedException {
        Thread.sleep(Constants.ActionTimeout);
        return auth(session).get("/rest/basket/{id}", session.basketId());
    }

    public Response addToBasket(AuthSession session, int productId, int quantity) throws InterruptedException {
        Thread.sleep(Constants.ActionTimeout);
        return auth(session)
                .body(Map.of(
                        "ProductId", productId,
                        "BasketId", session.basketId(),
                        "quantity", quantity))
                .post("/api/BasketItems");
    }

    public Response updateBasketItem(AuthSession session, int basketItemId, int quantity) throws InterruptedException {
        Thread.sleep(Constants.ActionTimeout);
        return auth(session)
                .body(Map.of("quantity", quantity))
                .put("/api/BasketItems/{id}", basketItemId);
    }

    public Response deleteBasketItem(AuthSession session, int basketItemId) throws InterruptedException {
        Thread.sleep(Constants.ActionTimeout);
        return auth(session).delete("/api/BasketItems/{id}", basketItemId);
    }

    public List<String> getBasketProductNames(AuthSession session) throws InterruptedException {
        Thread.sleep(Constants.ActionTimeout);
        List<String> names = getBasket(session).jsonPath().getList("data.Products.name");
        return names == null ? List.of() : names;
    }

    /** Səbətdəki bütün məhsulları silir (testlər təmiz vəziyyətdən başlasın deyə). */
    public void clearBasket(AuthSession session) throws InterruptedException {
        List<Integer> itemIds = getBasket(session).jsonPath().getList("data.Products.BasketItem.id");
        if (itemIds != null) {
            for (Integer id : itemIds) {
                deleteBasketItem(session, id);
            }
        }
    }
}
