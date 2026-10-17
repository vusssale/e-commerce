package listeners;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;
import utils.ConfigReader;

/** Uğursuz testi config-dəki "retry.count" qədər təkrar işlədir (flaky testlər üçün). */
public class RetryAnalyzer implements IRetryAnalyzer {

    private int attempt = 0;

    @Override
    public boolean retry(ITestResult result) {
        if (attempt < ConfigReader.retryCount()) {
            attempt++;
            return true;
        }
        return false;
    }
}
