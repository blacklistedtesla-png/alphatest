package com.alfabank.qapp.base;

import io.appium.java_client.android.AndroidDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

/**
 * Base test class provding Appium driver setup and teardown.
 * All test classes should extend this class.
 */
public abstract class BaseTest {

    protected AndroidDriver driver;
    private final AppiumDriverManager driverManager = new AppiumDriverManager();

    @BeforeMethod
    public void setUp() {
        driver = driverManager.createDriver();
    }

    @AfterMethod
    public void tearDown() {
        driverManager.quitDriver();
    }
}
