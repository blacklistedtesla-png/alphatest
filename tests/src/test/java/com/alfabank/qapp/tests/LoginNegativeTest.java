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
 * Negative login test scenarios.
 * Validates error handling for invalid credentials.
 */
public class LoginNegativeTest extends BaseTest {

    private LoginPage loginPage;
    private MainPage mainPage;

    private static final String VALID_USERNAME = "Login";
    private static final String VALID_PASSWORD = "Password";
    private static final String WRONG_USERNAME = "WrongUser";
    private static final String WRONG_PASSWORD = "WrongPass";
    private static final String EXPECTED_ERROR = "Введены неверные данные";
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
    @Description("Test login with wrong password — should show error message")
    public void testLoginWithWrongPassword() {
        loginPage.login(VALID_USERNAME, WRONG_PASSWORD);

        String errorText = loginPage.waitForErrorText(ERROR_WAIT_TIMEOUT);
        Assert.assertFalse(errorText.isEmpty(),
                "Error message should be displayed for wrong password");
        Assert.assertTrue(RegexHelper.isInvalidCredentialsError(errorText),
                "Error message should match expected pattern: " + errorText);
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Description("Test login with wrong username — should show error message")
    public void testLoginWithWrongUsername() {
        loginPage.login(WRONG_USERNAME, VALID_PASSWORD);

        String errorText = loginPage.waitForErrorText(ERROR_WAIT_TIMEOUT);
        Assert.assertFalse(errorText.isEmpty(),
                "Error message should be displayed for wrong username");
        Assert.assertEquals(errorText, EXPECTED_ERROR,
                "Error text should be: " + EXPECTED_ERROR);
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @Description("Test login with both wrong username and password")
    public void testLoginWithBothWrongCredentials() {
        loginPage.login(WRONG_USERNAME, WRONG_PASSWORD);

        String errorText = loginPage.waitForErrorText(ERROR_WAIT_TIMEOUT);
        Assert.assertFalse(errorText.isEmpty(),
                "Error message should be displayed for completely wrong credentials");

        Assert.assertFalse(mainPage.isSuccessScreenDisplayed(),
                "Main screen should NOT be displayed after failed login");
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @Description("Test login with special characters in credentials")
    public void testLoginWithSpecialCharacters() {
        String specialLogin = "user@#$%";
        String specialPassword = "p@ss!word&";

        Assert.assertTrue(RegexHelper.containsSpecialCharacters(specialLogin),
                "Test input should contain special characters");

        loginPage.login(specialLogin, specialPassword);

        String errorText = loginPage.waitForErrorText(ERROR_WAIT_TIMEOUT);
        Assert.assertFalse(errorText.isEmpty(),
                "Error message should be displayed for special character credentials");
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @Description("Test login with whitespace-only input")
    public void testLoginWithWhitespaceOnly() {
        String whitespaceInput = "   ";

        Assert.assertTrue(RegexHelper.isWhitespaceOnly(whitespaceInput),
                "Test input should be whitespace only (regex validation)");

        loginPage.login(whitespaceInput, whitespaceInput);

        Assert.assertFalse(mainPage.isSuccessScreenDisplayed(),
                "Main screen should NOT be displayed for whitespace-only input");
    }
}
