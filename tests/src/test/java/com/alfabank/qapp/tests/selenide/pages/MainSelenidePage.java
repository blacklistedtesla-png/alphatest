package com.alfabank.qapp.tests.selenide.pages;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.appium.SelenideAppium.$;

/**
 * Selenide-Appium page object for the main/success screen.
 *
 * Comparison with the original MainPage:
 * - Original uses manual WebDriverWait + implicit wait toggling for isSuccessScreenDisplayed().
 * - Selenide version uses shouldBe(visible) which handles waiting automatically.
 * - No driver field or constructor — Selenide manages driver globally.
 */
public class MainSelenidePage {

    private static final By SUCCESS_XPATH = By.xpath(
            "//android.widget.TextView[contains(@text, 'выполнен')]");

    public SelenideElement successText() {
        return $(SUCCESS_XPATH);
    }

    public String getSuccessText() {
        return $(SUCCESS_XPATH).getText();
    }
}
