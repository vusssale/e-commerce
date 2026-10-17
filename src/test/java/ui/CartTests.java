package ui;

import api.ApiClient;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class CartTests extends AuthenticatedUiTest {

    private ApiClient.ProductInfo product;

    @BeforeClass(alwaysRun = true)
    public void loadProductFromApi() {
        product = api.getProductInfo(1);
    }

    private void addProductViaUi(int times) {
        homePage.searchFor(product.name());
        for (int i = 0; i < times; i++) {
            homePage.addToBasket(product.name());
        }
    }

    @Test(groups = {"smoke", "regression"}, description = "Məhsulun səbətə əlavə edilməsi")
    public void addProductToCartTest() {
        homePage.searchFor(product.name());

        String message = homePage.addToBasket(product.name());

        Assert.assertTrue(message.toLowerCase().contains(product.name().toLowerCase()),
                "Təsdiq mesajında məhsulun adı olmalı idi, amma: '" + message + "'");
        Assert.assertTrue(api.getBasketProductNames(session).contains(product.name()),
                "Məhsul serverdəki səbətdə də olmalı idi");
    }

    @Test(groups = {"smoke", "regression"}, description = "Məhsul səbətdə görünür")
    public void productAppearsInCartTest() {
        addProductViaUi(1);

        cartPage.open();

        Assert.assertTrue(cartPage.isProductInCart(product.name()), "Məhsul səbətdə görünməli idi");
    }

    @Test(groups = "regression", description = "Məhsulun sayı (quantity)")
    public void cartQuantityTest() {
        addProductViaUi(2);

        cartPage.open();

        Assert.assertEquals(cartPage.getQuantity(product.name()), 2, "Eyni məhsul 2 dəfə əlavə olunub");
    }

    @Test(groups = "regression", description = "Səbətin ümumi qiyməti")
    public void cartTotalTest() {
        addProductViaUi(2);

        cartPage.open();
        Assert.assertTrue(cartPage.isProductInCart(product.name()));

        Assert.assertEquals(cartPage.getTotalPrice(), product.price() * 2, 0.01,
                "Ümumi məbləğ = qiymət x say olmalıdır");
    }

    @Test(groups = "regression", description = "Məhsulun səbətdən silinməsi")
    public void removeProductFromCartTest() {
        addProductViaUi(1);
        cartPage.open();
        Assert.assertTrue(cartPage.isProductInCart(product.name()), "Əvvəlcə məhsul səbətdə olmalıdır");

        cartPage.removeProduct(product.name());

        Assert.assertTrue(cartPage.isEmpty(), "Silindikdən sonra səbət boş olmalı idi");
        Assert.assertTrue(api.getBasketProductNames(session).isEmpty(), "Server tərəfdə də səbət boş olmalıdır");
    }
}
