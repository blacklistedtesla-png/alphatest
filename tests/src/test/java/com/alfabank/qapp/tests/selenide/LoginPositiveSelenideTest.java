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
 * Selenide-Appium version of LoginPositiveTest.
 *
 * Key differences from the original:
 * - Extends BaseSelenideTest (Selenide driver lifecycle) instead of BaseTest (raw Appium).
 * - Page objects are instantiated without passing a driver.
 * - Assertions use shouldBe(visible), shouldHave(text(...)) instead of Assert.assertTrue().
 * - Soft assertions are automatic via @Listeners(SoftAsserts.class) on the base class —
 *   no manual SoftAssert object or assertAll() needed.
 * - Built-in smart waiting replaces explicit WebDriverWait / isSuccessScreenDisplayed(timeout).
 */
public class LoginPositiveSelenideTest extends BaseSelenideTest {

    private LoginSelenidePage loginPage;
    private MainSelenidePage mainPage;

    private static final String VALID_USERNAME = "Login";
    private static final String VALID_PASSWORD = "Password";

    @BeforeMethod
    @Override
    public void setUp() {
        super.setUp();
        loginPage = new LoginSelenidePage();
        mainPage = new MainSelenidePage();
    }

    @Test
    @Severity(SeverityLevel.BLOCKER)
    @Description("Selenide: Test successful login with valid credentials")
    public void testSuccessfulLogin() {
        loginPage.login(VALID_USERNAME, VALID_PASSWORD);

        // Selenide waits automatically for the element to appear (up to Configuration.timeout).
        // Compare with original: Assert.assertTrue(mainPage.isSuccessScreenDisplayed(15), ...)
        mainPage.successText().shouldBe(visible, Duration.ofSeconds(15));
        mainPage.successText().shouldHave(text("Alfa-Test"));
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Description("Selenide: Test successful login using XPath locators")
    public void testSuccessfulLoginViaXPath() {
        loginPage.login(VALID_USERNAME, VALID_PASSWORD);
        mainPage.successText().shouldBe(visible, Duration.ofSeconds(15));
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @Description("Selenide: Verify valid login format using regex")
    public void testValidLoginFormatWithRegex() {
        // Regex checks are pure Java logic, not element interactions —
        // Selenide assertions (shouldBe/shouldHave) only work with UI elements,
        // so we still use TestNG Assert for non-UI validations.
        Assert.assertTrue(RegexHelper.isValidLoginFormat(VALID_USERNAME),
                "Valid username 'Login' should match the login format regex");
        Assert.assertTrue(RegexHelper.isWithinMaxLength(VALID_USERNAME, 50),
                "Valid username should be within max length of 50 characters");
    }
}
