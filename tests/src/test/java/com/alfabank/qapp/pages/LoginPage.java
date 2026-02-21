package com.alfabank.qapp.pages;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Page Object for the login screen of Alfa-Test application.
 * Demonstartes both XPath and CSS-style (UiAutomator/resource-id) locators.
 */
public class LoginPage {

    private final AndroidDriver driver;

    // --- Locators using @AndroidFindBy (resource-id based, CSS-style) ---

    @AndroidFindBy(id = "com.alfabank.qapp:id/tvTitle")
    private WebElement titleText;

    @AndroidFindBy(id = "com.alfabank.qapp:id/etUsername")
    private WebElement usernameField;

    @AndroidFindBy(id = "com.alfabank.qapp:id/etPassword")
    private WebElement passwordField;

    @AndroidFindBy(id = "com.alfabank.qapp:id/btnConfirm")
    private WebElement loginButton;

    @AndroidFindBy(id = "com.alfabank.qapp:id/tvError")
    private WebElement errorText;

    // --- XPath locators (used in specific methods to demonstrate XPath usage) ---

    private static final By TITLE_XPATH = By.xpath(
            "//android.widget.TextView[@resource-id='com.alfabank.qapp:id/tvTitle']");

    private static final By USERNAME_XPATH = By.xpath(
            "//android.widget.EditText[@resource-id='com.alfabank.qapp:id/etUsername']");

    private static final By PASSWORD_XPATH = By.xpath(
            "//android.widget.EditText[@resource-id='com.alfabank.qapp:id/etPassword']");

    private static final By LOGIN_BUTTON_XPATH = By.xpath(
            "//android.widget.Button[@resource-id='com.alfabank.qapp:id/btnConfirm']");

    private static final By ERROR_TEXT_XPATH = By.xpath(
            "//android.widget.TextView[@resource-id='com.alfabank.qapp:id/tvError']");

    private static final By PASSWORD_TOGGLE_XPATH = By.xpath(
            "//android.widget.ImageButton[contains(@content-desc, 'password') or contains(@content-desc, 'пароль')]");

    public LoginPage(AndroidDriver driver) {
        this.driver = driver;
        PageFactory.initElements(new AppiumFieldDecorator(driver, Duration.ofSeconds(10)), this);
    }

    // --- Actions using CSS-style locators (resource-id via @AndroidFindBy) ---

    public void enterUsername(String username) {
        usernameField.clear();
        usernameField.sendKeys(username);
    }

    public void enterPassword(String password) {
        passwordField.clear();
        passwordField.sendKeys(password);
    }

    public void clickLoginButton() {
        loginButton.click();
    }

    public void login(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickLoginButton();
    }

    // --- Getters using CSS-style locators ---

    public String getTitleText() {
        return titleText.getText();
    }

    public String getErrorText() {
        return errorText.getText();
    }

    /**
     * Waits for error text to become non-empty (async operation in the app).
     */
    public String waitForErrorText(int timeoutSeconds) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
            wait.until(d -> {
                String text = errorText.getText();
                return text != null && !text.isEmpty();
            });
            return errorText.getText();
        } catch (Exception e) {
            return errorText.getText();
        }
    }

    public boolean isLoginButtonDisplayed() {
        return loginButton.isDisplayed();
    }

    public boolean isLoginButtonEnabled() {
        return loginButton.isEnabled();
    }

    public boolean isUsernameFieldDisplayed() {
        return usernameField.isDisplayed();
    }

    public boolean isPasswordFieldDisplayed() {
        return passwordField.isDisplayed();
    }

    public boolean isTitleDisplayed() {
        return titleText.isDisplayed();
    }

    public String getUsernameFieldText() {
        return usernameField.getText();
    }

    public String getPasswordFieldText() {
        return passwordField.getText();
    }

    // --- Methods using XPath locators (to demonstrate XPath usage) ---

    public WebElement findTitleByXPath() {
        return driver.findElement(TITLE_XPATH);
    }

    public WebElement findUsernameFieldByXPath() {
        return driver.findElement(USERNAME_XPATH);
    }

    public WebElement findPasswordFieldByXPath() {
        return driver.findElement(PASSWORD_XPATH);
    }

    public WebElement findLoginButtonByXPath() {
        return driver.findElement(LOGIN_BUTTON_XPATH);
    }

    public WebElement findErrorTextByXPath() {
        return driver.findElement(ERROR_TEXT_XPATH);
    }

    /**
     * Uses XPath to enter credentails — demonstrates XPath-based interacton.
     */
    public void loginViaXPath(String username, String password) {
        WebElement userField = findUsernameFieldByXPath();
        userField.clear();
        userField.sendKeys(username);

        WebElement passField = findPasswordFieldByXPath();
        passField.clear();
        passField.sendKeys(password);

        findLoginButtonByXPath().click();
    }

    /**
     * Returns the password field's 'password' attribute to check masking.
     */
    public boolean isPasswordMasked() {
        String inputType = passwordField.getAttribute("password");
        return "true".equals(inputType);
    }
}
