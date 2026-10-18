package ui;

import api.ApiClient;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pages.ProductPage;
import utils.ParseUtils;

import java.util.List;

public class ProductTests extends BaseUiTest {

    private ApiClient.ProductInfo product;

    @BeforeClass(alwaysRun = true)
    public void loadProductFromApi() throws InterruptedException {
        product = api.getProductInfo(1);
    }

    @DataProvider(name = "searchTerms")
    public Object[][] searchTerms() {
        return new Object[][]{{"apple"}, {"orange"}, {"banana"}};
    }

    @Test(groups = "regression", dataProvider = "searchTerms", description = "Məhsul axtarışı")
    public void searchShowsMatchingProducts(String term) throws InterruptedException {
        homePage.searchFor(term);

        List<String> names = homePage.getProductNames();
        Assert.assertFalse(names.isEmpty(), "'" + term + "' üçün nəticə tapılmalı idi");
        Assert.assertTrue(names.stream().anyMatch(n -> n.toLowerCase().contains(term)),
                "Nəticələrdən ən azı birinin adında '" + term + "' olmalı idi: " + names);
    }

    @Test(groups = "regression", description = "Məhsulun detallar pəncərəsi")
    public void productDetailsAreDisplayed() throws InterruptedException {
        homePage.searchFor(product.name());
        ProductPage productPage = homePage.openProductDetails(product.name());

        Assert.assertTrue(productPage.isDisplayed(), "Detallar pəncərəsi açılmalı idi");
        Assert.assertTrue(productPage.getContent().toLowerCase().contains(product.name().toLowerCase()),
                "Pəncərədə məhsulun adı olmalı idi: " + product.name());
    }

    @Test(groups = "regression", description = "UI-dakı qiymət API-dəki qiymətlə eynidir")
    public void productPriceMatchesApi() throws InterruptedException {
        homePage.searchFor(product.name());

        double uiPrice = ParseUtils.parsePrice(homePage.getProductPriceText(product.name()));

        Assert.assertEquals(uiPrice, product.price(), 0.001, "UI və API qiymətləri üst-üstə düşməlidir");
    }
}
