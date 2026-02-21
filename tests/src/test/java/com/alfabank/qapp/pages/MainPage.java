package com.alfabank.qapp.pages;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

import java.time.Duration;

/**
 * Page Object for the main screen shown after successfull login.
 */
public class MainPage {

    private final AndroidDriver driver;

    // XPath locator for the success message
    private static final By SUCCESS_TEXT_XPATH = By.xpath(
            "//android.widget.TextView[contains(@text, 'Alfa-Test')]");

    public MainPage(AndroidDriver driver) {
        this.driver = driver;
        PageFactory.initElements(new AppiumFieldDecorator(driver, Duration.ofSeconds(10)), this);
    }

    /**
     * Returns the success message text using XPath locator.
     */
    public String getSuccessText() {
        WebElement successElement = driver.findElement(SUCCESS_TEXT_XPATH);
        return successElement.getText();
    }

    /**
     * Checks if the success screen is displayed.
     */
    public boolean isSuccessScreenDisplayed() {
        try {
            return driver.findElement(SUCCESS_TEXT_XPATH).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}
