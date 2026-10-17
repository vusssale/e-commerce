package utils;

import java.util.UUID;

/** Hər test işləməsi üçün unikal test məlumatı yaradır. */
public final class TestDataGenerator {

    private static final String DEFAULT_PASSWORD = "Passw0rd!123";

    private TestDataGenerator() {
    }

    public static TestUser newUser() {
        String id = UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        return new TestUser("qa_" + id + "@juice-sh.op", DEFAULT_PASSWORD);
    }

    public static String randomEmail() {
        return newUser().email();
    }
}
