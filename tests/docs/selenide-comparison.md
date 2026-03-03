# Selenide vs Raw Appium — Side-by-Side Comparison

## Overview

This project contains two parallel implementations of the same 19 test cases:
- **Original** (`tests.LoginPositiveTest`, etc.) — raw Appium + TestNG assertions
- **Selenide** (`tests.selenide.LoginPositiveSelenideTest`, etc.) — Selenide-Appium + Selenide conditions

Both test the same login functionality of the Alfa-Test Android application.

## Pattern-by-Pattern Comparison

### 1. Driver Setup

**Original (BaseTest + AppiumDriverManager):**
```java
// BaseTest.java
protected AppiumDriver driver;
private final AppiumDriverManager driverManager = new AppiumDriverManager();

@BeforeMethod
public void setUp() {
    driver = driverManager.createDriver();  // manual driver creation
}

@AfterMethod
public void tearDown() {
    driverManager.quitDriver();  // manual cleanup
}
```

**Selenide (BaseSelenideTest + WebDriverProvider):**
```java
// BaseSelenideTest.java
@Listeners({SoftAsserts.class})
public abstract class BaseSelenideTest {
    @BeforeMethod
    public void setUp() {
        Configuration.browser = SelenideAppiumDriverProvider.class.getName();
        SelenideAppium.launchApp();  // Selenide handles lifecycle
    }

    @AfterMethod
    public void tearDown() {
        SelenideAppium.terminateApp("com.alfabank.qapp");
    }
}
```

**Verdict:** Selenide moves driver creation into a `WebDriverProvider` class, which is called automatically. The test code is slightly cleaner, but there's a similar total amount of setup code — it's just organized differently.

---

### 2. Page Object Pattern

**Original (LoginPage):**
```java
public class LoginPage {
    private final AppiumDriver driver;  // requires driver reference

    @AndroidFindBy(id = "com.alfabank.qapp:id/etUsername")
    @iOSXCUITFindBy(accessibility = "etUsername")
    private WebElement usernameField;

    public LoginPage(AppiumDriver driver) {
        this.driver = driver;
        PageFactory.initElements(new AppiumFieldDecorator(driver), this);  // eager init
    }
}
```

**Selenide (LoginSelenidePage):**
```java
public class LoginSelenidePage {
    // No constructor, no driver field
    private final SelenideElement usernameField =
        $(AppiumBy.accessibilityId("etUsername"));  // lazy resolution

    // Page object methods return SelenideElement for fluent assertions
    public SelenideElement username() { return usernameField; }
}
```

**Verdict:** Selenide page objects are significantly lighter — no constructor, no driver passing, no PageFactory initialization. Elements are resolved lazily on first access.

---

### 3. Assertions

**Original (hard + soft assertions):**
```java
// Hard assertion — test stops immediately on failure
Assert.assertTrue(loginPage.isTitleDisplayed(), "Title should be visible");

// Soft assertion — continues execution, reports all failures at end
SoftAssert softAssert = new SoftAssert();
softAssert.assertTrue(loginPage.isTitleDisplayed(), "Title should be visible");
softAssert.assertTrue(loginPage.isUsernameFieldDisplayed(), "Username should be visible");
softAssert.assertAll();  // must remember to call this!
```

**Selenide (fluent conditions):**
```java
// Selenide assertion — readable, auto-waiting, soft by default (@Listeners)
loginPage.title().shouldBe(visible);
loginPage.username().shouldBe(visible);
// No assertAll() needed — Selenide collects failures automatically
```

**Verdict:** Selenide assertions are more readable and include automatic waiting. Soft assertions are configuration-level (`AssertionMode.SOFT`) rather than per-method boilerplate. However, non-UI checks (regex, business logic) still need TestNG `Assert`.

---

### 4. Waiting

**Original (manual waits):**
```java
// Custom wait method in LoginPage
public String waitForErrorText(int timeoutSeconds) {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
    wait.until(d -> {
        String text = errorText.getText();
        return text != null && !text.isEmpty();
    });
    return errorText.getText();
}

// In MainPage — toggling implicit wait for explicit wait precision
driver.manage().timeouts().implicitlyWait(Duration.ofMillis(500));
WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
wait.until(ExpectedConditions.presenceOfElementLocated(SUCCESS_TEXT_XPATH));
driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
```

**Selenide (built-in smart waiting):**
```java
// Just assert — Selenide waits automatically up to Configuration.timeout
loginPage.error().shouldBe(visible);
mainPage.successText().shouldHave(text("Alfa-Test"));

// Custom timeout when needed
mainPage.successText().shouldBe(visible, Duration.ofSeconds(15));
```

**Verdict:** Selenide eliminates manual wait boilerplate entirely. No `WebDriverWait`, no implicit wait toggling, no custom wait methods. This is arguably the biggest advantage.

---

### 5. Negative Checks (Element Should NOT Be Present)

**Original:**
```java
// Custom method with implicit wait toggling
public boolean isSuccessScreenDisplayed(int timeoutSeconds) {
    try {
        driver.manage().timeouts().implicitlyWait(Duration.ofMillis(500));
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        wait.until(ExpectedConditions.presenceOfElementLocated(SUCCESS_TEXT_XPATH));
        return true;
    } catch (Exception e) {
        return false;
    } finally {
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    }
}

// In test:
Assert.assertFalse(mainPage.isSuccessScreenDisplayed(3), "Should NOT be displayed");
```

**Selenide:**
```java
mainPage.successText().shouldNotBe(visible, Duration.ofSeconds(3));
```

**Verdict:** One line vs 15 lines. Selenide handles the wait-then-check-absence pattern natively.

---

## Summary Table

| Aspect | Original (Appium + TestNG) | Selenide-Appium |
|--------|---------------------------|-----------------|
| Lines of code | More (manual waits, PageFactory) | Less (~30-40% reduction) |
| Readability | Good with named methods | Better — fluent API reads like English |
| Waiting | Manual (error-prone) | Automatic (configuration-based) |
| Soft assertions | Per-method SoftAssert + assertAll() | Global config + @Listeners |
| Page objects | Constructor + driver + PageFactory | No constructor, lazy elements |
| Non-UI checks | Assert.assertTrue/assertFalse | Same — still need TestNG Assert |
| Learning curve | Standard Selenium/Appium | Selenide-specific API |
| Debug visibility | Direct WebElement access | Abstraced behind SelenideElement |
| Community/docs | Very large (Appium standard) | Growing, but smaller |

## When to Use Which

**Use raw Appium when:**
- Your team is already fluent in Selenium/Appium patterns
- You need fine-grained control over driver capabilities and session management
- You're integrating with custom frameworks that expect WebDriver directly
- The project requires maximum portability across automation tools

**Use Selenide when:**
- Readability and reduced boilerplate are priorities
- You want automatic waiting without manual WebDriverWait everywhere
- Soft assertions should be the default behavior
- Your team values concise test code and faster test writing

## File Mapping

| Original | Selenide Copy |
|----------|--------------|
| `base/BaseTest.java` | `tests/selenide/BaseSelenideTest.java` |
| `base/AppiumDriverManager.java` | `tests/selenide/SelenideAppiumDriverProvider.java` |
| `pages/LoginPage.java` | `tests/selenide/pages/LoginSelenidePage.java` |
| `pages/MainPage.java` | `tests/selenide/pages/MainSelenidePage.java` |
| `tests/LoginPositiveTest.java` | `tests/selenide/LoginPositiveSelenideTest.java` |
| `tests/LoginNegativeTest.java` | `tests/selenide/LoginNegativeSelenideTest.java` |
| `tests/LoginFieldValidationTest.java` | `tests/selenide/LoginFieldValidationSelenideTest.java` |
| `tests/LoginUITest.java` | `tests/selenide/LoginUISelenideTest.java` |
