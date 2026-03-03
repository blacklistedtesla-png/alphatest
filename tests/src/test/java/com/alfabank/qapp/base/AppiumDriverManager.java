package com.alfabank.qapp.base;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.options.XCUITestOptions;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

/**
 * Manages Appium driver lifecycle for Android and iOS platforms.
 * Configures capabilities and provides driver instance.
 *
 * Platform selection:
 *   - Default platform is Android.
 *   - To select iOS: mvn test -Dplatform=ios
 *
 * Appium server URL:
 *   - Default: http://127.0.0.1:4723
 *   - Override: mvn test -Dappium.url=http://127.0.0.1:4724
 *
 * Device selection:
 *   - By default connects to the first available device/emulator.
 *   - To target a specific device, pass its serial via system property:
 *       mvn test -Dudid=emulator-5554
 *       mvn test -Dudid=RFCR81AQ8MW
 *   - Or via environment variable APPIUM_UDID.
 *   - Get available device serials with: adb devices (Android) or xcrun simctl list (iOS)
 */
public class AppiumDriverManager {

    private static final String DEFAULT_APPIUM_URL = "http://127.0.0.1:4723";
    private static final String APP_PACKAGE = "com.alfabank.qapp";
    private static final String APP_ACTIVITY = ".presentation.MainActivity";
    private static final String IOS_BUNDLE_ID = "com.alfabank.qapp"; // adjust when iOS build is available
    private static final Duration IMPLICIT_WAIT = Duration.ofSeconds(10);

    private AppiumDriver driver;

    public AppiumDriver createDriver() {
        String platform = resolvePlatform();
        String appiumUrl = System.getProperty("appium.url", DEFAULT_APPIUM_URL);
        String udid = resolveUdid();

        if ("ios".equals(platform)) {
            driver = createIOSDriver(appiumUrl, udid);
        } else {
            driver = createAndroidDriver(appiumUrl, udid);
        }

        driver.manage().timeouts().implicitlyWait(IMPLICIT_WAIT);
        return driver;
    }

    public void quitDriver() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }

    public AppiumDriver getDriver() {
        return driver;
    }

    /**
     * Returns true if the current test run targets iOS.
     * Used by page objects for platform-specific locator branching.
     */
    public static boolean isIOS() {
        return "ios".equalsIgnoreCase(System.getProperty("platform", "android"));
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

    private String resolvePlatform() {
        return System.getProperty("platform", "android").toLowerCase();
    }

    /**
     * Resolves device UDID from system property (-Dudid=...) or environment variable (APPIUM_UDID).
     * Returns null if neither is set — Appium will pick the first available device.
     */
    private String resolveUdid() {
        String udid = System.getProperty("udid");
        if (udid != null && !udid.isEmpty()) {
            return udid;
        }
        return System.getenv("APPIUM_UDID");
    }
}
