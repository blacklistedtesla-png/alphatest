package com.alfabank.qapp.tests.selenide.pages;

import com.codeborne.selenide.SelenideElement;
import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;

import static com.codeborne.selenide.appium.SelenideAppium.$;

/**
 * Selenide-Appium page object for the login screen.
 *
 * Comparison with the original LoginPage:
 * ┌─────────────────────┬──────────────────────────┬──────────────────────────────┐
 * │ Aspect              │ Original (LoginPage)     │ Selenide (LoginSelenidePage) │
 * ├─────────────────────┼──────────────────────────┼──────────────────────────────┤
 * │ Driver dependency   │ Passed via constructor   │ None — Selenide manages it   │
 * │ Element type        │ WebElement               │ SelenideElement (lazy)       │
 * │ Element init        │ PageFactory.initElements  │ Lazy — resolved on first use│
 * │ Waits               │ Manual WebDriverWait     │ Built-in (Configuration.     │
 * │                     │                          │ timeout)                     │
 * │ Locators            │ @AndroidFindBy +         │ AppiumBy.id (Android) —      │
 * │                     │ @iOSXCUITFindBy          │ same resource IDs            │
 * └─────────────────────┴──────────────────────────┴──────────────────────────────┘
 *
 * Key advantage: no constructor parameters means page objects can be instantiated
 * anywhere without passing the driver. Selenide resolves elements lazily against
 * the current global driver, which reduces boilerplate significantly.
 */
public class LoginSelenidePage {

    // Selenide elements using Android resource IDs.
    // Note: accessibilityId() won't work here because the app doesn't set content-desc
    // attributes. We use AppiumBy.id() which maps to @AndroidFindBy(id = ...) in the original.
    private final SelenideElement titleText = $(AppiumBy.id("com.alfabank.qapp:id/tvTitle"));
    private final SelenideElement usernameField = $(AppiumBy.id("com.alfabank.qapp:id/etUsername"));
    private final SelenideElement passwordField = $(AppiumBy.id("com.alfabank.qapp:id/etPassword"));
    private final SelenideElement loginButton = $(AppiumBy.id("com.alfabank.qapp:id/btnConfirm"));
    private final SelenideElement errorText = $(AppiumBy.id("com.alfabank.qapp:id/tvError"));

    // XPath locator (for demonstrating XPath usage, same as original)
    private final SelenideElement titleByXPath = $(By.xpath(
            "//android.widget.TextView[@resource-id='com.alfabank.qapp:id/tvTitle']"));

    public LoginSelenidePage enterUsername(String username) {
        usernameField.clear();
        usernameField.sendKeys(username);
        return this;
    }

    public LoginSelenidePage enterPassword(String password) {
        passwordField.clear();
        passwordField.sendKeys(password);
        return this;
    }

    public LoginSelenidePage clickLoginButton() {
        loginButton.click();
        return this;
    }

    public LoginSelenidePage login(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickLoginButton();
        return this;
    }

    // --- Selenide-style element accessors for fluent assertions ---
    // Usage: loginPage.title().shouldBe(visible)
    public SelenideElement title() { return titleText; }
    public SelenideElement username() { return usernameField; }
    public SelenideElement password() { return passwordField; }
    public SelenideElement loginBtn() { return loginButton; }
    public SelenideElement error() { return errorText; }
    public SelenideElement titleByXPath() { return titleByXPath; }

    // Text getters for regex-based checks that still need raw strings
    public String getTitleText() { return titleText.getText(); }
    public String getErrorText() { return errorText.getText(); }

    public boolean isPasswordMasked() {
        String attr = passwordField.getAttribute("password");
        return "true".equals(attr);
    }
}
