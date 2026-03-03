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
 * Selenide-Appium version of LoginNegativeTest.
 *
 * Key differences from the original:
 * - Error text validation uses shouldBe(visible) + shouldHave(text(...))
 *   instead of waitForErrorText() + Assert.assertFalse(errorText.isEmpty()).
 *   Selenide's built-in waiting replaces manual WebDriverWait.
 * - "Screen should NOT be displayed" uses shouldNotBe(visible) with a short timeout
 *   instead of Assert.assertFalse(mainPage.isSuccessScreenDisplayed()).
 * - matchText() accepts regex directly — integrates regex validation into Selenide assertions.
 */
public class LoginNegativeSelenideTest extends BaseSelenideTest {

    private LoginSelenidePage loginPage;
    private MainSelenidePage mainPage;

    private static final String VALID_USERNAME = "Login";
    private static final String VALID_PASSWORD = "Password";
    private static final String WRONG_USERNAME = "WrongUser";
    private static final String WRONG_PASSWORD = "WrongPass";
    private static final String EXPECTED_ERROR = "Введены неверные данные";

    @BeforeMethod
    @Override
    public void setUp() {
        super.setUp();
        loginPage = new LoginSelenidePage();
        mainPage = new MainSelenidePage();
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Description("Selenide: Test login with wrong password")
    public void testLoginWithWrongPassword() {
        loginPage.login(VALID_USERNAME, WRONG_PASSWORD);

        // shouldBe(visible) waits for the error element to appear, replacing waitForErrorText()
        loginPage.error().shouldBe(visible);
        loginPage.error().shouldHave(matchText(".*неверн.*данн.*"));
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Description("Selenide: Test login with wrong username")
    public void testLoginWithWrongUsername() {
        loginPage.login(WRONG_USERNAME, VALID_PASSWORD);

        loginPage.error().shouldBe(visible);
        loginPage.error().shouldHave(exactText(EXPECTED_ERROR));
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @Description("Selenide: Test login with both wrong credentials")
    public void testLoginWithBothWrongCredentials() {
        loginPage.login(WRONG_USERNAME, WRONG_PASSWORD);

        loginPage.error().shouldBe(visible);
        // Short timeout for negative check — avoids waiting full 10s for an absent element
        mainPage.successText().shouldNotBe(visible, Duration.ofSeconds(3));
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @Description("Selenide: Test login with special characters")
    public void testLoginWithSpecialCharacters() {
        String specialLogin = "user@#$%";
        String specialPassword = "p@ss!word&";

        // Pure Java regex check — Selenide assertions don't apply to non-UI values
        Assert.assertTrue(RegexHelper.containsSpecialCharacters(specialLogin),
                "Test input should contain special characters");

        loginPage.login(specialLogin, specialPassword);
        loginPage.error().shouldBe(visible);
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @Description("Selenide: Test login with whitespace-only input")
    public void testLoginWithWhitespaceOnly() {
        String whitespaceInput = "   ";

        Assert.assertTrue(RegexHelper.isWhitespaceOnly(whitespaceInput),
                "Test input should be whitespace only (regex validation)");

        loginPage.login(whitespaceInput, whitespaceInput);
        mainPage.successText().shouldNotBe(visible, Duration.ofSeconds(3));
    }
}
