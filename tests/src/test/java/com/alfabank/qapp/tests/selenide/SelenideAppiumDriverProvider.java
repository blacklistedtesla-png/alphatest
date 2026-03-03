package com.alfabank.qapp.tests.selenide;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.options.XCUITestOptions;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Custom WebDriverProvider for Selenide-Appium.
 *
 * Selenide requires a WebDriverProvider to create the driver instance.
 * This class mirrors the logic from AppiumDriverManager but adapts it
 * to the Selenide driver lifecycle.
 *
 * Comparison with original AppiumDriverManager:
 * - Same platform detection (-Dplatform=android|ios)
 * - Same UDID resolution (-Dudid or APPIUM_UDID env var)
 * - Same Appium URL configurability (-Dappium.url)
 * - Difference: Selenide calls createDriver() automatically; no manual lifecycle needed.
 */
public class SelenideAppiumDriverProvider implements com.codeborne.selenide.WebDriverProvider {

    private static final String APP_PACKAGE = "com.alfabank.qapp";
    private static final String APP_ACTIVITY = ".presentation.MainActivity";
    private static final String IOS_BUNDLE_ID = "com.alfabank.qapp";

    @Override
    @CheckReturnValue
    @Nonnull
    public WebDriver createDriver(@Nonnull Capabilities capabilities) {
        String platform = System.getProperty("platform", "android").toLowerCase();
        String appiumUrl = System.getProperty("appium.url", "http://127.0.0.1:4723");
        String udid = resolveUdid();

        if ("ios".equals(platform)) {
            return createIOSDriver(appiumUrl, udid);
        }
        return createAndroidDriver(appiumUrl, udid);
    }

    private AndroidDriver createAndroidDriver(String appiumUrl, String udid) {
        UiAutomator2Options options = new UiAutomator2Options()
                .setPlatformName("Android")
                .setAutomationName("UiAutomator2")
                .setAppPackage(APP_PACKAGE)
                .setAppActivity(APP_ACTIVITY)
                .setNoReset(false);

        if (udid != null && !udid.isEmpty()) {
            options.setUdid(udid);
            options.setDeviceName(udid);
        }

        try {
            return new AndroidDriver(new URL(appiumUrl), options);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Invalid Appium server URL: " + appiumUrl, e);
        }
    }

    private IOSDriver createIOSDriver(String appiumUrl, String udid) {
        XCUITestOptions options = new XCUITestOptions()
                .setPlatformName("iOS")
                .setAutomationName("XCUITest")
                .setBundleId(IOS_BUNDLE_ID)
                .setNoReset(false);

        if (udid != null && !udid.isEmpty()) {
            options.setUdid(udid);
            options.setDeviceName(udid);
        }

        try {
            return new IOSDriver(new URL(appiumUrl), options);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Invalid Appium server URL: " + appiumUrl, e);
        }
    }

    private String resolveUdid() {
        String udid = System.getProperty("udid");
        if (udid != null && !udid.isEmpty()) {
            return udid;
        }
        return System.getenv("APPIUM_UDID");
    }
}
