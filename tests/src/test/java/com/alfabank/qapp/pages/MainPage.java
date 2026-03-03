package com.alfabank.qapp.pages;

import com.alfabank.qapp.base.AppiumDriverManager;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Page Object for the main screen shown after successful login.
 * Supports both Android and iOS platforms.
 */
public class MainPage {

    private final AppiumDriver driver;

    // XPath locator for the success message ("Вход в Alfa-Test выполнен")
    // Android uses @text attribute; iOS uses @label
    private static final By SUCCESS_TEXT_XPATH = AppiumDriverManager.isIOS()
            ? By.xpath("//XCUIElementTypeStaticText[contains(@label, 'выполнен')]")
            : By.xpath("//android.widget.TextView[contains(@text, 'выполнен')]");

    public MainPage(AppiumDriver driver) {
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
     * Checks if the success screen is displayed within a given timeout.
     */
    public boolean isSuccessScreenDisplayed() {
        return isSuccessScreenDisplayed(5);
    }

    /**
     * Checks if the success screen is displayed within a given timeout (seconds).
     * Uses explicit wait to avoid blocking on implicit wait for negative tests.
     */
    public boolean isSuccessScreenDisplayed(int timeoutSeconds) {
        try {
            // Temporarily reduce implicit wait so explicit wait controls timing
            driver.manage().timeouts().implicitlyWait(Duration.ofMillis(500));
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
            wait.until(ExpectedConditions.presenceOfElementLocated(SUCCESS_TEXT_XPATH));
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        }
    }
}
