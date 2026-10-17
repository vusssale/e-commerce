package base;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import utils.ConfigReader;

/** Bütün testlər üçün ortaq suite səviyyəli hazırlıq. */
public abstract class BaseTest {

    @BeforeSuite(alwaysRun = true)
    public void checkApplicationIsRunning() {
        String url = ConfigReader.baseUrl();
        try {
            Response response = RestAssured.given().baseUri(url).get("/rest/admin/application-version");
            if (response.statusCode() != 200) {
                throw new IllegalStateException("Juice Shop gözlənilməz status qaytardı: " + response.statusCode());
            }
            System.out.println("Juice Shop işləyir: " + url + " -> " + response.asString());
        } catch (IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalStateException(
                    "Juice Shop-a qoşulmaq mümkün olmadı: " + url + ". Əvvəlcə tətbiqi işə salın (npm start).", e);
        }
    }

    @AfterSuite(alwaysRun = true)
    public void suiteFinished() {
        System.out.println("Test suite tamamlandı.");
    }
}
