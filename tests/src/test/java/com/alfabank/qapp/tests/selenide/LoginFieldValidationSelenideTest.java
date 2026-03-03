package com.alfabank.qapp.tests.selenide;

import com.alfabank.qapp.tests.selenide.pages.LoginSelenidePage;
import com.alfabank.qapp.tests.selenide.pages.MainSelenidePage;
import com.alfabank.qapp.utils.RegexHelper;
import io.qameta.allure.Description;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;

import static com.codeborne.selenide.Condition.*;

/**
 * Selenide-Appium version of LoginFieldValidationTest.
 *
 * Key differences from the original:
 * - shouldNotBe(visible) replaces Assert.assertFalse(isSuccessScreenDisplayed()).
 * - Selenide's built-in timeout handles the async wait that the original does with
 *   waitForErrorText() + isSuccessScreenDisplayed(3).
 * - Short timeout (3s) on shouldNotBe(visible) avoids full 10s wait for absent elements.
 */
public class LoginFieldValidationSelenideTest extends BaseSelenideTest {

    private LoginSelenidePage loginPage;
    private MainSelenidePage mainPage;
    private static final int MAX_FIELD_LENGTH = 50;

    @BeforeMethod
    @Override
    public void setUp() {
        super.setUp();
        loginPage = new LoginSelenidePage();
        mainPage = new MainSelenidePage();
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Description("Selenide: Test login with empty fields")
    public void testLoginWithEmptyFields() {
        loginPage.clickLoginButton();
        mainPage.successText().shouldNotBe(visible, Duration.ofSeconds(3));
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Description("Selenide: Test login with max-length username exceeded")
    public void testLoginWithMaxLengthUsername() {
        String longUsername = "A".repeat(MAX_FIELD_LENGTH + 1);

        Assert.assertFalse(RegexHelper.isWithinMaxLength(longUsername, MAX_FIELD_LENGTH),
                "String of 51 chars should exceed max length via regex check");

        loginPage.login(longUsername, "Password");
        mainPage.successText().shouldNotBe(visible, Duration.ofSeconds(3));
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Description("Selenide: Test login with max-length password exceeded")
    public void testLoginWithMaxLengthPassword() {
        String longPassword = "P".repeat(MAX_FIELD_LENGTH + 1);

        Assert.assertFalse(RegexHelper.isWithinMaxLength(longPassword, MAX_FIELD_LENGTH),
                "String of 51 chars should exceed max length via regex check");

        loginPage.login("Login", longPassword);
        mainPage.successText().shouldNotBe(visible, Duration.ofSeconds(3));
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @Description("Selenide: Test login with empty username")
    public void testLoginWithEmptyUsername() {
        loginPage.enterPassword("Password");
        loginPage.clickLoginButton();
        mainPage.successText().shouldNotBe(visible, Duration.ofSeconds(3));
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @Description("Selenide: Test login with empty password")
    public void testLoginWithEmptyPassword() {
        loginPage.enterUsername("Login");
        loginPage.clickLoginButton();
        mainPage.successText().shouldNotBe(visible, Duration.ofSeconds(3));
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @Description("Selenide: Test login with exactly max length")
    public void testLoginWithExactMaxLength() {
        String exactMaxUsername = "A".repeat(MAX_FIELD_LENGTH);

        Assert.assertTrue(RegexHelper.isWithinMaxLength(exactMaxUsername, MAX_FIELD_LENGTH),
                "String of exactly 50 chars should be within max length");

        loginPage.login(exactMaxUsername, "Password");
        mainPage.successText().shouldNotBe(visible, Duration.ofSeconds(3));
    }
}
