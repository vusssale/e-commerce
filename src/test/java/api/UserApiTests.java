package api;

import base.BaseTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import utils.TestDataGenerator;
import utils.TestUser;

import static org.hamcrest.Matchers.*;

/** POST testləri: qeydiyyat və login. */
public class UserApiTests extends BaseTest {

    private final ApiClient api = new ApiClient();
    private TestUser existingUser;

    @BeforeClass(alwaysRun = true)
    public void createUser() throws InterruptedException {
        existingUser = api.createUser();
    }

    @Test(groups = "regression", description = "POST /api/Users")
    public void registerUserTest() throws InterruptedException {
        TestUser newUser = TestDataGenerator.newUser();

        api.register(newUser.email(), newUser.password()).then()
                .statusCode(201)
                .body("status", equalTo("success"))
                .body("data.email", equalTo(newUser.email()))
                .body("data.id", notNullValue());
    }

    @Test(groups = {"smoke", "regression"}, description = "POST /rest/user/login (valid)")
    public void loginWithValidCredentialsTest() throws InterruptedException {
        api.login(existingUser.email(), existingUser.password()).then()
                .statusCode(200)
                .time(lessThan(5000L))
                .body("authentication.token", not(emptyOrNullString()))
                .body("authentication.umail", equalTo(existingUser.email()))
                .body("authentication.bid", notNullValue());
    }

    @Test(groups = "regression", description = "POST /rest/user/login (yanlış parol, negative)")
    public void loginWithInvalidPasswordTest() throws InterruptedException {
        api.login(existingUser.email(), "YanlisParol!999").then()
                .statusCode(401);
    }
}
