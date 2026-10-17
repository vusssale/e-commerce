package ui;

import api.ApiClient;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

/** Login tələb edən UI testləri üçün: səbət təmizlənir və UI vasitəsilə login edilir. */
public abstract class AuthenticatedUiTest extends BaseUiTest {

    protected ApiClient.AuthSession session;

    @BeforeClass(alwaysRun = true)
    public void loginViaApi() {
        session = api.loginAndGetSession(user);
    }

    @BeforeMethod(alwaysRun = true)
    public void loginViaUi() {
        api.clearBasket(session);
        loginPage.open();
        loginPage.loginSuccessfully(user.email(), user.password());
    }
}
