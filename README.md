# Automation Framework — OWASP Juice Shop

Java, Selenium WebDriver, Rest Assured və TestNG ilə yazılmış kiçik test avtomatlaşdırma framework-ü.
Test edilən tətbiq: **OWASP Juice Shop** (lokal, `http://localhost:3000`). Frontend (Angular) backend ilə REST API vasitəsilə məlumat mübadiləsi edir.

## Texnologiyalar

| Alət | Versiya |
|------|---------|
| Java | 17+ (Rest Assured 6 tələbi) |
| Maven | 3.9+ |
| Selenium WebDriver | 4.51.0 |
| Rest Assured | 6.0.1 |
| TestNG | 7.11.0 |
| Jackson (JSON serializasiya) | 2.18.2 |

## Əvvəlcədən lazım olanlar

1. Juice Shop lokalda işləməlidir (`npm start` və ya Docker). Yoxlama: brauzerdə `http://localhost:3000` açılmalıdır.
2. Java 17+ və Maven quraşdırılıb.
3. Chrome (və ya Firefox / Edge) quraşdırılıb. Drayveri Selenium Manager özü yükləyir.

## Testləri işə salmaq

```bash
# Bütün testlər (testng.xml)
mvn clean test

# Yalnız smoke testlər
mvn clean test -DsuiteXmlFile=testng-smoke.xml

# Yalnız regression testlər
mvn clean test -DsuiteXmlFile=testng-regression.xml

# Parametrlər: brauzer, headless rejim, başqa ünvan
mvn clean test -Dbrowser=firefox -Dheadless=true -Dbase.url=http://localhost:3000
```

Nəticələr: `target/surefire-reports/`. UI test uğursuz olarsa, ekran görüntüsü `target/screenshots/` qovluğuna düşür.

## Layihənin strukturu

```
automation-framework
├── pom.xml
├── testng.xml                  # bütün testlər
├── testng-smoke.xml            # smoke qrupu
├── testng-regression.xml       # regression qrupu
├── README.md
├── .gitignore
└── src
    ├── main
    │   ├── java
    │   │   ├── driver/   DriverManager        (WebDriver yaradılması / bağlanması, ThreadLocal)
    │   │   ├── api/      ApiClient            (Juice Shop REST API client-i)
    │   │   ├── pages/    BasePage, LoginPage, HomePage, ProductPage, CartPage   (Page Object Model)
    │   │   └── utils/    ConfigReader, TestDataGenerator, TestUser, ParseUtils
    │   └── resources/    config.properties
    └── test
        └── java
            ├── base/       BaseTest             (@BeforeSuite: tətbiq işləyirmi? / @AfterSuite)
            ├── ui/         BaseUiTest, AuthenticatedUiTest, LoginTests, ProductTests, CartTests, UiApiIntegrationTests
            ├── api/        ProductApiTests, UserApiTests, BasketApiTests
            └── listeners/  TestListener (screenshot), RetryAnalyzer, RetryListener
```

## Test siyahısı

### UI testləri (Selenium) — 14 test metodu

| Sinif | Test | Qrup |
|-------|------|------|
| LoginTests | validLoginTest | smoke, regression |
| LoginTests | invalidUsernameLoginTest (negative) | regression |
| LoginTests | invalidPasswordLoginTest (negative) | regression |
| LoginTests | emptyFieldsLoginTest (negative) | regression |
| ProductTests | searchShowsMatchingProducts (@DataProvider: apple, orange, banana) | regression |
| ProductTests | productDetailsAreDisplayed | regression |
| ProductTests | productPriceMatchesApi (UI qiyməti = API qiyməti) | regression |
| CartTests | addProductToCartTest | smoke, regression |
| CartTests | productAppearsInCartTest | smoke, regression |
| CartTests | cartQuantityTest | regression |
| CartTests | cartTotalTest | regression |
| CartTests | removeProductFromCartTest | regression |
| UiApiIntegrationTests | userCreatedViaApiCanLoginInUiTest | smoke, regression |
| UiApiIntegrationTests | productAddedViaApiIsVisibleInUiCartTest | regression |

### API testləri (Rest Assured) — 10 test

| Metod | Endpoint | Test |
|-------|----------|------|
| GET | /api/Products | getAllProductsTest (status, header, response time, body) |
| GET | /api/Products/1 | getProductByIdTest |
| GET | /rest/products/search?q=apple | searchProductsTest |
| GET | /rest/basket/{id} | getBasketTest |
| POST | /api/Users | registerUserTest |
| POST | /rest/user/login | loginWithValidCredentialsTest, loginWithInvalidPasswordTest (401) |
| POST | /api/BasketItems | addItemToBasketTest |
| PUT | /api/BasketItems/{id} | updateBasketItemQuantityTest |
| DELETE | /api/BasketItems/{id} | deleteBasketItemTest |

### UI + API inteqrasiyası

1. **API → UI login:** API ilə istifadəçi yaradılır, eyni məlumatlarla UI-da login edilir.
2. **API → UI səbət:** API ilə məhsul səbətə əlavə edilir, UI-da login olunub səbətdə məhsulun və sayın görünməsi yoxlanılır.

## Əsas dizayn qərarları

- **Page Object Model:** locator-lar yalnız `pages` paketindədir, testlər `loginPage.login(...)` kimi çağırışlardan ibarətdir.
- **DriverManager:** brauzer yaratma/bağlama məntiqi test siniflərində təkrarlanmır. `ThreadLocal` sayəsində paralel işə hazırdır.
- **Test məlumatı:** hər test sinfi API ilə yeni istifadəçi yaradır, parol/token kodda saxlanmır. Məhsulun adı və qiyməti API-dən oxunur.
- **Təmiz başlanğıc:** login tələb edən UI testlərindən əvvəl səbət API ilə təmizlənir.
- **Bonus:** `@DataProvider`, retry mexanizmi (`retry.count`), screenshot listener-i, `config.properties` + `-D` parametrləri ilə mühit idarəsi, ThreadLocal driver.

## Qeydlər

- Juice Shop veb-səhifə versiyaları arasında kiçik fərqlər ola bilər. Test element tapa bilmirsə, locator-ları `pages` paketindəki sabitlərdə yoxlayın (brauzerdə F12).
- Juice Shop qəsdən zəif qurulmuş tətbiqdir, yalnız lokal və test məqsədli istifadə edin.
