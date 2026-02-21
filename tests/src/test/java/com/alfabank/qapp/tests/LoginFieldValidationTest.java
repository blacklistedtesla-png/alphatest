package com.alfabank.qapp.tests;

import com.alfabank.qapp.base.BaseTest;
import com.alfabank.qapp.pages.LoginPage;
import com.alfabank.qapp.pages.MainPage;
import com.alfabank.qapp.utils.RegexHelper;
import io.qameta.allure.Description;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Input field validation test scenarios.
 * Validates empty fields and max length constraints.
 */
public class LoginFieldValidationTest extends BaseTest {

    private LoginPage loginPage;
    private MainPage mainPage;

    private static final int MAX_FIELD_LENGTH = 50;
    private static final int ERROR_WAIT_TIMEOUT = 10;

    @BeforeMethod
    @Override
    public void setUp() {
        super.setUp();
        loginPage = new LoginPage(driver);
        mainPage = new MainPage(driver);
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Description("Test login with empty username and password fields")
    public void testLoginWithEmptyFields() {
        loginPage.clickLoginButton();

        // Wait for the async login to process
        String errorText = loginPage.waitForErrorText(ERROR_WAIT_TIMEOUT);

        Assert.assertFalse(mainPage.isSuccessScreenDisplayed(3),
                "Main screen should NOT be displayed when fields are empty");
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Description("Test login with username exceeding maximum length (50 characters)")
    public void testLoginWithMaxLengthUsername() {
        String longUsername = "A".repeat(MAX_FIELD_LENGTH + 1);

        Assert.assertFalse(RegexHelper.isWithinMaxLength(longUsername, MAX_FIELD_LENGTH),
                "String of 51 chars should exceed max length via regex check");

        loginPage.login(longUsername, "Password");

        // Wait for the async validation
        String errorText = loginPage.waitForErrorText(ERROR_WAIT_TIMEOUT);

        Assert.assertFalse(mainPage.isSuccessScreenDisplayed(3),
                "Main screen should NOT be displayed when username exceeds max length");
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Description("Test login with password exceeding maximum length (50 characters)")
    public void testLoginWithMaxLengthPassword() {
        String longPassword = "P".repeat(MAX_FIELD_LENGTH + 1);

        Assert.assertFalse(RegexHelper.isWithinMaxLength(longPassword, MAX_FIELD_LENGTH),
                "String of 51 chars should exceed max length via regex check");

        loginPage.login("Login", longPassword);

        String errorText = loginPage.waitForErrorText(ERROR_WAIT_TIMEOUT);

        Assert.assertFalse(mainPage.isSuccessScreenDisplayed(3),
                "Main screen should NOT be displayed when password exceeds max length");
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @Description("Test login with empty username but valid password")
    public void testLoginWithEmptyUsername() {
        loginPage.enterPassword("Password");
        loginPage.clickLoginButton();

        String errorText = loginPage.waitForErrorText(ERROR_WAIT_TIMEOUT);

        Assert.assertFalse(mainPage.isSuccessScreenDisplayed(3),
                "Main screen should NOT be displayed when username is empty");
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @Description("Test login with valid username but empty password")
    public void testLoginWithEmptyPassword() {
        loginPage.enterUsername("Login");
        loginPage.clickLoginButton();

        String errorText = loginPage.waitForErrorText(ERROR_WAIT_TIMEOUT);

        Assert.assertFalse(mainPage.isSuccessScreenDisplayed(3),
                "Main screen should NOT be displayed when password is empty");
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @Description("Test that input at exactly max length (50 chars) is accepted")
    public void testLoginWithExactMaxLength() {
        String exactMaxUsername = "A".repeat(MAX_FIELD_LENGTH);

        Assert.assertTrue(RegexHelper.isWithinMaxLength(exactMaxUsername, MAX_FIELD_LENGTH),
                "String of exactly 50 chars should be within max length");

        loginPage.login(exactMaxUsername, "Password");

        // Wait for the async login
        String errorText = loginPage.waitForErrorText(ERROR_WAIT_TIMEOUT);

        Assert.assertFalse(mainPage.isSuccessScreenDisplayed(3),
                "Login with 50-char username should be processed (not rejected by length validation)");
    }
}
