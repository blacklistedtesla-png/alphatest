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
import org.testng.asserts.SoftAssert;

/**
 * Positive login test scenarios.
 * Validates successful authentication with correct credentials.
 *
 * Assertion strategy:
 * - Soft assertions (SoftAssert) are used where a method contains multiple independent
 *   checks. All checks execute even if earlier ones fail, giving a complete picture
 *   of test results in a single run. Failures are reported together via assertAll().
 * - Hard assertions (Assert) are kept where checks have sequential dependencies
 *   (e.g., getSuccessText() would throw if the screen isn't displayed) or where
 *   there is only a single assertion (soft provides no benefit).
 */
public class LoginPositiveTest extends BaseTest {

    private LoginPage loginPage;
    private MainPage mainPage;

    private static final String VALID_USERNAME = "Login";
    private static final String VALID_PASSWORD = "Password";

    @BeforeMethod
    @Override
    public void setUp() {
        super.setUp();
        loginPage = new LoginPage(driver);
        mainPage = new MainPage(driver);
    }

    @Test
    @Severity(SeverityLevel.BLOCKER)
    @Description("Test successful login with valid credentials (Login/Password)")
    public void testSuccessfulLogin() {
        loginPage.login(VALID_USERNAME, VALID_PASSWORD);

        Assert.assertTrue(mainPage.isSuccessScreenDisplayed(15),
                "Main screen should be displayed after successful login");

        String successText = mainPage.getSuccessText();
        Assert.assertTrue(successText.contains("Alfa-Test"),
                "Success screen should contain 'Alfa-Test' text");
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Description("Test successful login using XPath locators")
    public void testSuccessfulLoginViaXPath() {
        loginPage.loginViaXPath(VALID_USERNAME, VALID_PASSWORD);

        Assert.assertTrue(mainPage.isSuccessScreenDisplayed(15),
                "Main screen should be displayed after successful login via XPath");
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify valid login format using regex")
    public void testValidLoginFormatWithRegex() {
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertTrue(RegexHelper.isValidLoginFormat(VALID_USERNAME),
                "Valid username 'Login' should match the login format regex");
        softAssert.assertTrue(RegexHelper.isWithinMaxLength(VALID_USERNAME, 50),
                "Valid username should be within max length of 50 characters");
        softAssert.assertAll();
    }
}
