# Test Coverage & Optimization Recommendations

## Current Coverage Analysis

### What's Covered (19 tests across 4 classes)

| Category | Tests | Coverage |
|----------|-------|----------|
| **Positive login** | 3 | Valid credentials, XPath variant, regex format validation |
| **Negative login** | 5 | Wrong password, wrong username, both wrong, special chars, whitespace |
| **Field validation** | 6 | Empty fields, max length exceeded, empty single field, exact max length |
| **UI verification** | 5 | Element presence, title text, XPath discovery, password masking, button state |

Note: This is currently a local/offline authentication app — all auth logic runs on-device.
Network tests are included as "what if" scenarios for future server-side migration.

---

## Gap Analysis — Grouped by Test Type

### Security Tests
| # | Severity | Gap | Suggested Test |
|---|----------|-----|----------------|
| 1 | Blocker | **SQL injection in login fields** | Enter `' OR 1=1 --` as username, verify error (not success) |
| 2 | Blocker | **XSS in login fields** | Enter `<script>alert(1)</script>`, verify sanitized display |
| 3 | Normal | **Path traversal in fields** | Enter `../../etc/passwd` → verify no file system access |

### State & Lifecycle Tests
| # | Severity | Gap | Suggested Test |
|---|----------|-----|----------------|
| 4 | Blocker | **Password visibility toggle** | Tap toggle → verify visible → tap again → verify masked |
| 5 | Blocker | **Logout / session flow** | Login → main → logout → verify returned to login |
| 6 | Critical | **App backgrounding during login** | Fill fields → background → resume → verify state preserved |
| 7 | Critical | **App killed during login** | Fill fields → force stop → relaunch → verify clean state |
| 8 | Normal | **Rapid login/logout cycles** | Login → logout × 10 → verify app stable, no OOM |

### Configuration Change Tests (Rotation, Locale)
| # | Severity | Gap | Suggested Test |
|---|----------|-----|----------------|
| 9 | Critical | **Screen rotation during login** | Enter credentials → rotate → verify fields retain values |
| 10 | Critical | **Rotation on success screen** | Login → main → rotate → verify success text preserved |
| 11 | Normal | **System locale change** | Switch device locale → relaunch → verify UI adapts |

### Navigation Tests
| # | Severity | Gap | Suggested Test |
|---|----------|-----|----------------|
| 12 | Critical | **Back button from main screen** | Login → main → press back → verify expected behavior |
| 13 | Critical | **Double-tap login button** | Tap login rapidly 3x → verify single auth attempt |
| 14 | Normal | **Home button during login** | Fill fields → press home → return → verify state |

### Input & Accessibility Tests
| # | Severity | Gap | Suggested Test |
|---|----------|-----|----------------|
| 15 | Normal | **Unicode/emoji in fields** | Enter emoji/CJK characters → verify handled without crash |
| 16 | Normal | **Copy-paste into fields** | Paste text from clipboard → verify accepted |
| 17 | Normal | **Keyboard dismiss behavior** | Tap outside field → verify keyboard dismissed |
| 18 | Normal | **Field focus order** | Tap username → next → verify focus moves to password |

### Stress & Performance Tests
| # | Severity | Gap | Suggested Test |
|---|----------|-----|----------------|
| 19 | Minor | **Very long strings (1000+ chars)** | Paste 1000+ chars → verify no crash or ANR |
| 20 | Minor | **Login response time** | Measure tap-to-result time → assert < threshold |

### What If: Network Tests (Future — If App Migrates to Server Auth)

These tests are not applicable to the current offline app, but should be added if
authentication is moved to a server-side API in the future.

| # | Severity | Gap | Suggested Test |
|---|----------|-----|----------------|
| N1 | Blocker | **Network timeout during login** | Simulate slow network → verify timeout message |
| N2 | Blocker | **No connectivity** | Disable network → attempt login → verify graceful error |
| N3 | Critical | **Server error (500)** | Mock 500 response → verify error message (not crash) |
| N4 | Critical | **Intermittent connectivity** | Toggle network during login → verify retry or clean error |
| N5 | Normal | **Slow network response** | Throttle to 2G → verify login completes or shows timeout |

---

## Testing Optimizations

### Code-Level Optimizations

**1. Page Object Method Chaining (Low Effort)**
Current page objects return `void` — methods can't be chained. Adding `return this`
enables fluent syntax that's more readable and reduces line count:
```java
// Before:
loginPage.enterUsername("Login");
loginPage.enterPassword("Password");
loginPage.clickLoginButton();

// After (with method chaining):
loginPage.enterUsername("Login").enterPassword("Password").clickLoginButton();
```
The Selenide copies already demonstrate this pattern.

**2. Base Page Class for Shared Wait Logic (Medium Effort)**
Both `LoginPage` and `MainPage` have duplicate wait logic (timeout handling, implicit wait
toggling). Extract into a `BasePage` class:
```java
public abstract class BasePage {
    protected final AppiumDriver driver;
    protected BasePage(AppiumDriver driver) { ... }
    protected WebElement waitForElement(By locator, int timeout) { ... }
    protected boolean isElementPresent(By locator, int timeout) { ... }
}
```

**3. Custom TestNG Assertions for Allure Integration (Medium Effort)**
Wrap assertions to auto-attach element screenshots on failure:
```java
public class AllureAssert {
    public static void assertVisible(WebElement element, String name) {
        try {
            Assert.assertTrue(element.isDisplayed(), name + " should be visible");
        } catch (AssertionError e) {
            Allure.addAttachment("Failed: " + name, takeScreenshot());
            throw e;
        }
    }
}
```

### Execution Optimizations

**4. Shared App Session Between Tests (High Impact)**
Currently each `@BeforeMethod` creates a new Appium session (full app restart). For tests
that start from the same screen, reuse the session and just clear state:
```java
@BeforeClass  // once per class, not per method
public void setUpSession() { driver = driverManager.createDriver(); }

@BeforeMethod
public void resetState() { driver.resetApp(); }  // faster than new session
```
This cuts ~3-5 seconds per test method (session creation overhead).

**5. Parallel Class Execution with Multiple Emulators (High Impact, High Effort)**
Run each test class on a separate emulator simultaneously:
```xml
<suite parallel="classes" thread-count="4">
```
Requires 4 emulators running + 4 Appium server ports. Cuts total time from ~3min to ~45s.

**6. Test Ordering — Fast Tests First (Low Effort)**
Configure TestNG to run fast tests (UI checks, regex validation) before slow tests
(login flows with async waits). Gives faster feedback on basic failures:
```java
@Test(priority = 1)  // runs first
public void testLoginScreenElementsPresence() { ... }

@Test(priority = 10)  // runs later
public void testSuccessfulLogin() { ... }
```

### Framework Optimizations

**7. Screenshot-on-Failure via TestNG Listener (Low Effort)**
Add a global listener that captures screenshots automatically:
```java
public class ScreenshotOnFailureListener implements ITestListener {
    @Override
    public void onTestFailure(ITestResult result) {
        // Capture and attach to Allure report
    }
}
```
Currently Allure captures some info, but explicit screenshots of the app state are more
useful for debugging mobile UI failures.

**8. Retry Analyzer for Infrastructure Flakiness (Low Effort)**
Add a retry analyzer specifically for `SessionNotCreatedException`:
```java
public class AppiumRetryAnalyzer implements IRetryAnalyzer {
    private int count = 0;
    @Override
    public boolean retry(ITestResult result) {
        if (count < 2 && result.getThrowable() instanceof SessionNotCreatedException) {
            count++;
            return true;
        }
        return false;
    }
}
```

**9. Allure Environment Info (Low Effort)**
Write environment details to Allure results for better report context:
```java
@BeforeSuite
public void writeAllureEnvironment() {
    // Write platform, device, Appium version to allure-results/environment.properties
}
```

### After Test Failure — Debugging & Recovery

**10. Automatic Screenshot on Failure (Critical)**
Capture the exact app state when a test fails — the most valuable debugging artifact:
```java
public class ScreenshotListener implements ITestListener {
    @Override
    public void onTestFailure(ITestResult result) {
        AppiumDriver driver = ((BaseTest) result.getInstance()).driver;
        if (driver != null) {
            byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            Allure.addAttachment("Failure screenshot", "image/png",
                new ByteArrayInputStream(screenshot), ".png");
        }
    }
}
```

**11. Page Source Dump on Failure (Normal)**
Save the XML element tree alongside screenshots — useful when elements are present but
have unexpected attributes:
```java
@Override
public void onTestFailure(ITestResult result) {
    String pageSource = driver.getPageSource();
    Allure.addAttachment("Page source", "application/xml", pageSource);
}
```

**12. Appium Server Log Collection (Normal)**
After failures, capture the Appium server log to diagnose driver-level issues:
```bash
# Appium server log location (default)
cat ~/.appium/logs/appium.log | tail -100
```
Can be automated in `@AfterMethod` when a test fails.

**13. Video Recording of Test Execution (Nice to Have)**
Appium supports video recording via capabilities — enables visual replay of failures:
```java
options.setCapability("recordVideo", true);
// After test: driver.stopRecordingScreen() → attach to Allure
```

**14. Failure Categorization in Allure (Normal)**
Tag failures by type so reports group them meaningfully:
- `@Issue("ALFA-123")` — links to bug tracker
- `@TmsLink("TC-456")` — links to test management system
- Custom categories in `allure-results/categories.json`:
```json
[
  {"name": "Element not found", "matchedStatuses": ["broken"],
   "messageRegex": ".*NoSuchElement.*"},
  {"name": "Appium connection", "matchedStatuses": ["broken"],
   "messageRegex": ".*SessionNotCreated.*"}
]
```

---

## Performance Optimization

### 1. Fix Appium Connection Flakiness (Blocker)

**Current problem:** Every test run shows 4 `SessionNotCreatedException` failures on the first
attempt per class, then passes on retry. This adds ~4-8 seconds of wasted time per run.

**Root cause:** Appium server isn't fully ready when the first test class connects.

**Solution — retry in driver manager:**
```java
private AppiumDriver createDriverWithRetry(String platform, String appiumUrl, String udid) {
    int maxRetries = 3;
    for (int i = 0; i < maxRetries; i++) {
        try {
            return "ios".equals(platform)
                ? createIOSDriver(appiumUrl, udid)
                : createAndroidDriver(appiumUrl, udid);
        } catch (SessionNotCreatedException e) {
            if (i == maxRetries - 1) throw e;
            try { Thread.sleep(2000); } catch (InterruptedException ignored) {}
        }
    }
    throw new RuntimeException("Unreachable");
}
```

### 2. Reduce Implicit Wait for Negative Tests (Critical)

**Current problem:** Tests checking "element should NOT be present" wait the full 10s implicit
wait before concluding. This makes negative tests slow.

**Current workaround** in `MainPage.isSuccessScreenDisplayed()` toggles implicit wait — this
is a known Selenium anti-pattern (implicit + explicit wait mixing).

**Better approach:** Use explicit waits everywhere and set implicit wait to 0:
```java
// In AppiumDriverManager.createDriver() — remove implicitlyWait entirely
// In page objects — use explicit WebDriverWait for every element interaction
```

### 3. Test Grouping with TestNG (Critical)

Add `@Test(groups = ...)` to enable selective execution:

```java
@Test(groups = {"smoke"})       // Critical path tests (login success, basic UI)
@Test(groups = {"regression"})  // Full suite
@Test(groups = {"security"})    // SQL injection, XSS, etc.
@Test(groups = {"ui"})          // UI verification only
```

Run specific groups:
```bash
mvn test -Dgroups=smoke              # Quick ~30s sanity check
mvn test -Dgroups=regression         # Full suite
mvn test -Dgroups="smoke,security"   # Combined groups
```

### 4. Parallel Test Execution (Minor — Future)

TestNG supports `parallel="methods"`, but Appium on a single device is sequential.
Worth revisiting when scaling to a device farm with multiple emulators.

---

## Test Data Management

### Current State
Credentials are hardcoded as constants in each test class — duplicated across 4 files.

### Recommendations

**1. Centralize test data** in a shared constants class:
```java
public class TestData {
    public static final String VALID_USERNAME = "Login";
    public static final String VALID_PASSWORD = "Password";
    public static final String WRONG_USERNAME = "WrongUser";
    public static final String WRONG_PASSWORD = "WrongPass";
    public static final String EXPECTED_ERROR = "Введены неверные данные";
    public static final int MAX_FIELD_LENGTH = 50;
    public static final int ERROR_WAIT_TIMEOUT = 10;
}
```

**2. Use TestNG DataProvider** for parameterized negative tests:
```java
@DataProvider(name = "invalidCredentials")
public Object[][] invalidCredentials() {
    return new Object[][] {
        {"WrongUser", "Password",  "Wrong username"},
        {"Login",     "WrongPass", "Wrong password"},
        {"WrongUser", "WrongPass", "Both wrong"},
    };
}

@Test(dataProvider = "invalidCredentials")
public void testLoginWithInvalidCredentials(String user, String pass, String desc) {
    loginPage.login(user, pass);
    String error = loginPage.waitForErrorText(10);
    Assert.assertFalse(error.isEmpty(), desc + ": error should appear");
}
```

This reduces 3 separate test methods into 1 parameterized method with 3 data rows.

---

## Flakiness Prevention

### Current Flakiness Sources
1. **Appium connection on first test** — solved by retry (see above)
2. **Async error text appearance** — mitigated by `waitForErrorText()`
3. **Implicit/explicit wait conflict** — present in `MainPage.isSuccessScreenDisplayed()`

### Best Practices
- Prefer explicit waits (`WebDriverWait`) over implicit waits
- Never mix implicit and explicit waits in the same session
- Add `@RetryAnalyzer` for known infrastructure flakiness (not test logic bugs)
- Take screenshots on failure (Allure already supports `@Attachment`)
- Log element state before assertions for debugging

---

## CI/CD Integration

### Recommended Pipeline Structure
```yaml
stages:
  - smoke:       # 3-5 critical tests, runs on every commit (~1 min)
      run: mvn test -Dgroups=smoke
  - regression:  # Full suite, runs on PR merge (~5 min)
      run: mvn test -Dplatform=android
  - cross-platform:  # Both platforms, runs nightly
      run: |
        mvn test -Dplatform=android
        mvn test -Dplatform=ios
```

### Allure Reporting
```bash
allure serve tests/allure-results
allure generate tests/allure-results-android tests/allure-results-ios -o combined-report
```

---

## Prioritized Action Items (by Severity)

### Blocker
| # | Action | Group | Effort |
|---|--------|-------|--------|
| 1 | Add retry logic for Appium connection in driver manager | Infrastructure | Low |
| 2 | Add password toggle interaction test | State & Lifecycle | Low |
| 3 | Add logout/session flow test | State & Lifecycle | Medium |
| 4 | Add security tests (SQL injection, XSS) | Security | Medium |

### Critical
| # | Action | Group | Effort |
|---|--------|-------|--------|
| 5 | Add test groups (smoke, regression, security) | Infrastructure | Low |
| 6 | Centralize test data into shared class | Infrastructure | Low |
| 7 | Replace implicit waits with explicit waits | Infrastructure | Medium |
| 8 | Add screen rotation tests | Configuration Change | Medium |
| 9 | Add back button / double-tap navigation tests | Navigation | Low |
| 10 | Add app backgrounding / kill lifecycle tests | State & Lifecycle | Medium |

### Normal
| # | Action | Group | Effort |
|---|--------|-------|--------|
| 11 | Add DataProvider for parameterized negative tests | Infrastructure | Medium |
| 12 | Add Unicode/emoji input tests | Input & Accessibility | Low |
| 13 | Add keyboard dismiss / field focus tests | Input & Accessibility | Low |
| 14 | Set up CI pipeline with test groups | Infrastructure | Medium |

### Minor
| # | Action | Group | Effort |
|---|--------|-------|--------|
| 15 | Add stress tests (long strings, rapid cycles) | Stress & Performance | Low |
| 16 | Add performance timing assertions | Stress & Performance | Medium |

### Future (When App Goes Online)
| # | Action | Group | Effort |
|---|--------|-------|--------|
| N1 | Add network timeout / no-connectivity tests | Network | Medium |
| N2 | Add server error (500) handling test | Network | Medium |
| N3 | Add slow/intermittent network tests | Network | High |
