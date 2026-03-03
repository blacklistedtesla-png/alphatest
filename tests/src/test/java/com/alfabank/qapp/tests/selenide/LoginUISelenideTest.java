package com.alfabank.qapp.tests.selenide;

import com.alfabank.qapp.tests.selenide.pages.LoginSelenidePage;
import io.qameta.allure.Description;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static com.codeborne.selenide.Condition.*;

/**
 * Selenide-Appium version of LoginUITest.
 *
 * Key differences from the original:
 * - Element visibility checks use shouldBe(visible) instead of Assert.assertTrue(isDisplayed()).
 *   This is more readable and includes automatic waiting.
 * - Text checks use shouldHave(exactText(...)) instead of Assert.assertEquals().
 * - isPasswordMasked() still uses getAttribute — Selenide does not have a built-in
 *   Condition for Android "password" attribute, so we fall back to TestNG Assert.
 * - shouldBe(enabled) replaces Assert.assertTrue(isEnabled()) — more expressive.
 */
public class LoginUISelenideTest extends BaseSelenideTest {

    private LoginSelenidePage loginPage;

    @BeforeMethod
    @Override
    public void setUp() {
        super.setUp();
        loginPage = new LoginSelenidePage();
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @Description("Selenide: Verify all login screen UI elements are visible")
    public void testLoginScreenElementsPresence() {
        // Compare with original: Assert.assertTrue(loginPage.isTitleDisplayed(), ...)
        // Selenide version is more concise and auto-waits for each element.
        // Soft assertions (via @Listeners) ensure all 4 checks run even if one fails.
        loginPage.title().shouldBe(visible);
        loginPage.username().shouldBe(visible);
        loginPage.password().shouldBe(visible);
        loginPage.loginBtn().shouldBe(visible);
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @Description("Selenide: Verify login screen title text")
    public void testLoginScreenTitle() {
        // Compare with original: Assert.assertEquals(title, "Вход в Alfa-Test", ...)
        loginPage.title().shouldHave(exactText("Вход в Alfa-Test"));
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @Description("Selenide: Verify title found via XPath")
    public void testTitleFoundByXPath() {
        // Compare with original: Assert.assertNotNull(...) + Assert.assertTrue(isDisplayed())
        // Selenide combines both checks — shouldBe(visible) implies the element exists.
        loginPage.titleByXPath().shouldBe(visible);
        loginPage.titleByXPath().shouldHave(exactText("Вход в Alfa-Test"));
    }

    @Test
    @Severity(SeverityLevel.MINOR)
    @Description("Selenide: Verify password field is masked")
    public void testPasswordFieldIsMasked() {
        loginPage.enterPassword("TestPassword");
        // No Selenide Condition for Android "password" attribute — use standard Assert
        Assert.assertTrue(loginPage.isPasswordMasked(),
                "Password field should be masked (input type = password)");
    }

    @Test
    @Severity(SeverityLevel.MINOR)
    @Description("Selenide: Verify login button is enabled")
    public void testLoginButtonEnabled() {
        // Compare with original: Assert.assertTrue(loginPage.isLoginButtonEnabled(), ...)
        loginPage.loginBtn().shouldBe(enabled);
    }
}
