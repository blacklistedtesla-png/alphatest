package com.alfabank.qapp.tests.selenide;

import com.codeborne.selenide.AssertionMode;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.appium.SelenideAppium;
import com.codeborne.selenide.testng.SoftAsserts;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;

/**
 * Base class for Selenide-Appium tests.
 *
 * Comparison with the original BaseTest:
 * ┌─────────────────────┬──────────────────────────┬──────────────────────────────┐
 * │ Aspect              │ Original (BaseTest)      │ Selenide (BaseSelenideTest)  │
 * ├─────────────────────┼──────────────────────────┼──────────────────────────────┤
 * │ Driver management   │ Manual via               │ SelenideAppium.launchApp()   │
 * │                     │ AppiumDriverManager      │ manages lifecycle            │
 * │ Soft assertions     │ Manual SoftAssert per    │ Automatic via @Listeners +   │
 * │                     │ method + assertAll()     │ AssertionMode.SOFT           │
 * │ Timeouts            │ implicitlyWait(10s)      │ Configuration.timeout        │
 * │ Platform selection  │ -Dplatform system prop   │ Same, via WebDriverProvider  │
 * └─────────────────────┴──────────────────────────┴──────────────────────────────┘
 *
 * The @Listeners(SoftAsserts.class) annotation enables automatic soft assertion
 * collection for all Selenide shouldBe/shouldHave calls. No manual SoftAssert
 * instantiation or assertAll() is needed — Selenide reports all failures at test end.
 */
@Listeners({SoftAsserts.class})
public abstract class BaseSelenideTest {

    private static final String APP_PACKAGE = "com.alfabank.qapp";

    @BeforeMethod
    public void setUp() {
        Configuration.timeout = 10_000;
        Configuration.assertionMode = AssertionMode.SOFT;

        // Use the custom WebDriverProvider that creates the correct driver per platform.
        // IMPORTANT: Do NOT set Configuration.remote here — it overrides the WebDriverProvider
        // and causes Selenide to use RemoteDriverFactory with generic browser capabilities
        // instead of our Appium-specific capabilities. The provider handles the URL internally.
        Configuration.browser = SelenideAppiumDriverProvider.class.getName();

        SelenideAppium.launchApp();
    }

    @AfterMethod
    public void tearDown() {
        // Close the driver entirely between tests to ensure a clean app state.
        // terminateApp() + launchApp() reuses the session but doesn't reliably
        // restart the app — it can leave us on the home screen instead.
        Selenide.closeWebDriver();
    }
}
