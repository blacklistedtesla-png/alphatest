package com.alfabank.qapp.base;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

/**
 * Manages Appium AndroidDriver lifecycle.
 * Configures capabilities and provides driver instance.
 */
public class AppiumDriverManager {

    private static final String APPIUM_SERVER_URL = "http://127.0.0.1:4723";
    private static final String APP_PACKAGE = "com.alfabank.qapp";
    private static final String APP_ACTIVITY = ".presentation.MainActivity";
    private static final Duration IMPLICIT_WAIT = Duration.ofSeconds(10);

    private AndroidDriver driver;

    public AndroidDriver createDriver() {
        UiAutomator2Options options = new UiAutomator2Options()
                .setDeviceName("emulator-5554")
                .setPlatformName("Android")
                .setAutomationName("UiAutomator2")
                .setAppPackage(APP_PACKAGE)
                .setAppActivity(APP_ACTIVITY)
                .setNoReset(false);

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
}
