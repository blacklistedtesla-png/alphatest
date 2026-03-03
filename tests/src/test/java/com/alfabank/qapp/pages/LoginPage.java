package com.alfabank.qapp.pages;

import com.alfabank.qapp.base.AppiumDriverManager;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Page Object for the login screen of Alfa-Test application.
 * Supports both Android and iOS platforms with dual locator annotations.
 * Demonstrates both XPath and CSS-style (resource-id / accessibility) locators.
 */
public class LoginPage {

    private final AppiumDriver driver;

    // Platform-dependent element class names
    private static final boolean IS_IOS = AppiumDriverManager.isIOS();

    // --- Locators using @AndroidFindBy + @iOSXCUITFindBy (auto-selected by AppiumFieldDecorator) ---

    @AndroidFindBy(id = "com.alfabank.qapp:id/tvTitle")
    @iOSXCUITFindBy(accessibility = "tvTitle")
    private WebElement titleText;

    @AndroidFindBy(id = "com.alfabank.qapp:id/etUsername")
    @iOSXCUITFindBy(accessibility = "etUsername")
    private WebElement usernameField;

    @AndroidFindBy(id = "com.alfabank.qapp:id/etPassword")
    @iOSXCUITFindBy(accessibility = "etPassword")
    private WebElement passwordField;

    @AndroidFindBy(id = "com.alfabank.qapp:id/btnConfirm")
    @iOSXCUITFindBy(accessibility = "btnConfirm")
    private WebElement loginButton;

    @AndroidFindBy(id = "com.alfabank.qapp:id/tvError")
    @iOSXCUITFindBy(accessibility = "tvError")
    private WebElement errorText;

    // --- XPath locators (platform-aware) ---

    private static final By TITLE_XPATH = buildXPath(tv(), "tvTitle");
    private static final By USERNAME_XPATH = buildXPath(et(), "etUsername");
    private static final By PASSWORD_XPATH = buildXPath(et(), "etPassword");
    private static final By LOGIN_BUTTON_XPATH = buildXPath(btn(), "btnConfirm");
    private static final By ERROR_TEXT_XPATH = buildXPath(tv(), "tvError");

    private static final By PASSWORD_TOGGLE_XPATH = IS_IOS
            ? By.xpath("//XCUIElementTypeButton[contains(@name, 'password') or contains(@name, 'пароль')]")
            : By.xpath("//android.widget.ImageButton[contains(@content-desc, 'password') or contains(@content-desc, 'пароль')]");

    public LoginPage(AppiumDriver driver) {
        this.driver = driver;
        PageFactory.initElements(new AppiumFieldDecorator(driver, Duration.ofSeconds(10)), this);
    }

    // --- Actions using CSS-style locators (resource-id / accessibility via annotations) ---

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
     * Uses XPath to enter credentials — demonstrates XPath-based interaction.
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
     * Returns whether the password field is masked.
     * Android: checks the 'password' attribute.
     * iOS: checks if the element is a XCUIElementTypeSecureTextField.
     */
    public boolean isPasswordMasked() {
        if (IS_IOS) {
            String type = passwordField.getAttribute("type");
            return "XCUIElementTypeSecureTextField".equals(type);
        }
        String inputType = passwordField.getAttribute("password");
        return "true".equals(inputType);
    }

    // --- Platform-aware XPath helpers ---

    private static String tv() {
        return IS_IOS ? "XCUIElementTypeStaticText" : "android.widget.TextView";
    }

    private static String et() {
        return IS_IOS ? "XCUIElementTypeTextField" : "android.widget.EditText";
    }

    private static String btn() {
        return IS_IOS ? "XCUIElementTypeButton" : "android.widget.Button";
    }

    /**
     * Builds a platform-aware XPath By locator.
     * Android uses @resource-id with full package path; iOS uses @name with short ID.
     */
    private static By buildXPath(String elementType, String shortId) {
        if (IS_IOS) {
            return By.xpath("//" + elementType + "[@name='" + shortId + "']");
        }
        return By.xpath("//" + elementType + "[@resource-id='com.alfabank.qapp:id/" + shortId + "']");
    }
}
