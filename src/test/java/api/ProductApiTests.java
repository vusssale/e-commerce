package api;

import base.BaseTest;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import static org.hamcrest.Matchers.*;

/** GET testləri. */
public class ProductApiTests extends BaseTest {

    private final ApiClient api = new ApiClient();

    @Test(groups = {"smoke", "regression"}, description = "GET /api/Products")
    public void getAllProductsTest() throws InterruptedException {
        api.getProducts().then()
                .statusCode(200)
                .header("Content-Type", containsString("application/json"))   // header validation
                .time(lessThan(5000L))                                          // response time validation
                .body("status", equalTo("success"))
                .body("data", hasSize(greaterThan(0)));
    }

    @Test(groups = "regression", description = "GET /api/Products/1")
    public void getProductByIdTest() throws InterruptedException {
        Response response = api.getProduct(1);

        response.then()
                .statusCode(200)
                .body("data.id", equalTo(1))
                .body("data.name", not(emptyOrNullString()));
        Assert.assertTrue(response.jsonPath().getDouble("data.price") > 0, "Qiymət 0-dan böyük olmalıdır");
    }

    @Test(groups = "regression", description = "GET /rest/products/search?q=apple")
    public void searchProductsTest() throws InterruptedException {
        api.searchProducts("apple").then()
                .statusCode(200)
                .body("data", hasSize(greaterThan(0)))
                .body("data.name", hasItem(containsString("Apple")));
    }
}
