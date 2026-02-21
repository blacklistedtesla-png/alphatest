package com.alfabank.qapp.base;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

/**
 * Manages Appium AndroidDriver lifecycle.
 * Configures capabilities and provides driver instance.
 *
 * Device selection:
 *   - By default connects to the first available device/emulator.
 *   - To target a specific device, pass its serial via system property:
 *       mvn test -Dudid=emulator-5554
 *       mvn test -Dudid=RFCR81AQ8MW
 *   - Or via environment variable APPIUM_UDID.
 *   - Get available device serials with: adb devices
 */
public class AppiumDriverManager {

    private static final String APPIUM_SERVER_URL = "http://127.0.0.1:4723";
    private static final String APP_PACKAGE = "com.alfabank.qapp";
    private static final String APP_ACTIVITY = ".presentation.MainActivity";
    private static final Duration IMPLICIT_WAIT = Duration.ofSeconds(10);

    private AndroidDriver driver;

    public AndroidDriver createDriver() {
        UiAutomator2Options options = new UiAutomator2Options()
                .setPlatformName("Android")
                .setAutomationName("UiAutomator2")
                .setAppPackage(APP_PACKAGE)
                .setAppActivity(APP_ACTIVITY)
                .setNoReset(false);

        String udid = resolveUdid();
        if (udid != null && !udid.isEmpty()) {
            options.setUdid(udid);
            options.setDeviceName(udid);
        }

        try {
            driver = new AndroidDriver(new URL(APPIUM_SERVER_URL), options);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Invalid Appium server URL: " + APPIUM_SERVER_URL, e);
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

    public AndroidDriver getDriver() {
        return driver;
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
