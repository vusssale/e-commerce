package listeners;

import driver.DriverManager;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/** UI testi uğursuz olanda ekran görüntüsünü target/screenshots qovluğuna saxlayır. */
public class TestListener implements ITestListener {

    @Override
    public void onTestFailure(ITestResult result) {
        WebDriver webDriver = DriverManager.getDriver();
        if (webDriver == null) {
            return; // API testləri
        }
        try {
            File source = ((TakesScreenshot) webDriver).getScreenshotAs(OutputType.FILE);
            Path dir = Path.of("target", "screenshots");
            Files.createDirectories(dir);
            String name = result.getTestClass().getRealClass().getSimpleName()
                    + "_" + result.getMethod().getMethodName() + ".png";
            Files.copy(source.toPath(), dir.resolve(name), StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            System.err.println("Screenshot saxlanmadı: " + e.getMessage());
        }
    }
}
