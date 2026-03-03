package com.alfabank.qapp.base;

import io.appium.java_client.AppiumDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

/**
 * Base test class providing Appium driver setup and teardown.
 * All test classes should extend this class.
 */
public abstract class BaseTest {

    protected AppiumDriver driver;
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
