package com.alfabank.qapp.tests;

import com.alfabank.qapp.base.BaseTest;
import com.alfabank.qapp.pages.LoginPage;
import io.qameta.allure.Description;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

/**
 * UI element verification tests for the login screen.
 * Checks presence, visibility, and state of UI components.
 *
 * Assertion strategy:
 * - testLoginScreenElementsPresence uses SoftAssert because all 4 element visibility
 *   checks are independent — if the title is missing, we still want to know whether
 *   the input fields and button are visible.
 * - Other methods use hard Assert (single assertion or sequential dependency).
 */
public class LoginUITest extends BaseTest {

    private LoginPage loginPage;

    @BeforeMethod
    @Override
    public void setUp() {
        super.setUp();
        loginPage = new LoginPage(driver);
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify all login screen UI elements are visible")
    public void testLoginScreenElementsPresence() {
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertTrue(loginPage.isTitleDisplayed(),
                "Title 'Вход в Alfa-Test' should be visible");
        softAssert.assertTrue(loginPage.isUsernameFieldDisplayed(),
                "Username input field should be visible");
        softAssert.assertTrue(loginPage.isPasswordFieldDisplayed(),
                "Password input field should be visible");
        softAssert.assertTrue(loginPage.isLoginButtonDisplayed(),
                "Login button should be visible");
        softAssert.assertAll();
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify login screen title text")
    public void testLoginScreenTitle() {
        String title = loginPage.getTitleText();
        Assert.assertEquals(title, "Вход в Alfa-Test",
                "Title text should be 'Вход в Alfa-Test'");
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify title element is found via XPath locator")
    public void testTitleFoundByXPath() {
        Assert.assertNotNull(loginPage.findTitleByXPath(),
                "Title should be found via XPath locator");
        Assert.assertTrue(loginPage.findTitleByXPath().isDisplayed(),
                "Title found via XPath should be visible");
    }

    @Test
    @Severity(SeverityLevel.MINOR)
    @Description("Verify password field is masked by default")
    public void testPasswordFieldIsMasked() {
        loginPage.enterPassword("TestPassword");

        Assert.assertTrue(loginPage.isPasswordMasked(),
                "Password field should be masked (input type = password)");
    }

    @Test
    @Severity(SeverityLevel.MINOR)
    @Description("Verify login button is enabled")
    public void testLoginButtonEnabled() {
        Assert.assertTrue(loginPage.isLoginButtonEnabled(),
                "Login button should be enabled");
    }
}
