# iOS Cross-Platform Support — Implementation Notes

## Problem
The test framework was tightly coupled to Android: `AndroidDriver`, `UiAutomator2Options`,
and Android-specific XPath class names (`android.widget.TextView`, etc.) were hardcoded
throughout the driver layer, base test, and page objects. Adding iOS support required
changes at every layer.

## Decisions Made

### 1. AppiumDriver as the Common Type
**Changed:** `AndroidDriver` → `AppiumDriver` in BaseTest, LoginPage, MainPage constructors.

**Why:** `AppiumDriver` is the superclass of both `AndroidDriver` and `IOSDriver`. By typing
shared code to the superclass, page objects and tests become platform-agnostic. The actual
driver instance (Android or iOS) is created by `AppiumDriverManager` and cast implicitly.

### 2. Platform Selection via System Property
**Added:** `-Dplatform=android|ios` (default: `android`)

**Why:** System properties are the standard Maven mechanism for parameterizing test runs.
This keeps backward compatibility — running `mvn test` without `-Dplatform` defaults to
Android, so existing CI pipelines don't break.

### 3. Dual Locator Annotations
**Added:** `@iOSXCUITFindBy(accessibility = "...")` alongside each `@AndroidFindBy`

**Why:** Appium's `AppiumFieldDecorator` automatically selects the correct annotation based
on the driver type at runtime. This means the same page object works for both platforms
without any if/else branching — the framework handles it internally.

**Note:** The iOS accessibility IDs are placeholders (`tvTitle`, `etUsername`, etc.) and must
be verified against the actual iOS app build using Appium Inspector.

### 4. Platform-Aware XPath Helpers
**Added:** `buildXPath()`, `tv()`, `et()`, `btn()` helper methods in LoginPage.

**Why:** Android and iOS use completely different class names in their element hierarchies:
- Android: `android.widget.TextView`, `android.widget.EditText`, `android.widget.Button`
- iOS: `XCUIElementTypeStaticText`, `XCUIElementTypeTextField`, `XCUIElementTypeButton`

Additionally, Android uses `@resource-id` while iOS uses `@name` for element identification
in XPath. The `buildXPath()` helper centralizes this branching so each locator is a
one-liner instead of a verbose conditional.

### 5. Configurable Appium Server URL
**Added:** `-Dappium.url=http://...` (default: `http://127.0.0.1:4723`)

**Why:** This enables two execution models:
- **Sequential:** Same Appium server, run Android then iOS
- **Parallel:** Two Appium servers on different ports, both platforms simultaneously

### 6. Static `isIOS()` Helper
**Added:** `AppiumDriverManager.isIOS()` static method.

**Why:** Page objects need to branch XPath locators at class-load time (static fields).
Reading the system property via a static method is the simplest approach. This works
correctly for the sequential execution model (separate JVM per `mvn test` invocation).

## Files Changed
| File | Change |
|------|--------|
| `AppiumDriverManager.java` | Platform factory, configurable URL, `isIOS()`, `createIOSDriver()` |
| `BaseTest.java` | `AndroidDriver` → `AppiumDriver` (field + import) |
| `LoginPage.java` | Dual annotations, platform-aware XPaths, iOS `isPasswordMasked()` |
| `MainPage.java` | `AppiumDriver` type, platform-aware XPath |

## Execution Examples

```bash
# Default (backward-compatible, Android)
mvn test

# Sequential: Android then iOS
mvn test -Dplatform=android && mvn test -Dplatform=ios

# Parallel: two Appium servers
appium -p 4723 &    # Terminal 1
appium -p 4724 &    # Terminal 2
mvn test -Dplatform=android -Dappium.url=http://127.0.0.1:4723 &
mvn test -Dplatform=ios -Dappium.url=http://127.0.0.1:4724 &
wait

# Target specific device
mvn test -Dplatform=ios -Dudid=iPhone-15-simulator-UDID
```

## CI (GitHub Actions)
```yaml
jobs:
  test-android:
    runs-on: ubuntu-latest
    steps:
      - run: appium &
      - run: mvn test -Dplatform=android
  test-ios:
    runs-on: macos-latest
    steps:
      - run: appium &
      - run: mvn test -Dplatform=ios
```
Both jobs run in parallel automatically. Allure results can be merged with
`allure generate results-android/ results-ios/`.
