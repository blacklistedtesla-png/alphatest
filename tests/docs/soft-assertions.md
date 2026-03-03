# Soft Assertions — Implementation Notes

## Problem
All 37 assertions across 4 test classes used hard assertions (`org.testng.Assert.*`).
When a test method has multiple independent checks, a hard assertion stops execution
at the first failure — remaining checks never run. This means a single test run may
hide multiple issues, requiring repeated runs to find them all.

## What Changed
Converted 10 test methods (with 2+ independent checks) from hard to soft assertions.
Kept 9 methods unchanged (single assertion or sequential dependency).

## Why Hard Assertions Were Used Originally
`org.testng.Assert` is the default assertion class in TestNG. It's simple, requires
no setup, and works well for the common case of one check per test. Hard assertions
are the natural starting point for any test suite.

## When Hard Assertions Are Correct
Hard assertions should be kept when checks have **sequential dependencies** — where
assertion #2 literally cannot execute if assertion #1 fails. Example:

```java
// CORRECT: keep hard assertions here
Assert.assertTrue(mainPage.isSuccessScreenDisplayed(15), ...);
String successText = mainPage.getSuccessText();  // throws NoSuchElement if screen not displayed
Assert.assertTrue(successText.contains("Alfa-Test"), ...);
```

If `isSuccessScreenDisplayed()` fails, calling `getSuccessText()` would throw a
`NoSuchElementException`, which would obscure the real failure. The hard assertion
correctly stops execution before the dependent call.

## When Soft Assertions Are Better
Soft assertions shine when checks are **independent** — each verifies a separate
condition that doesn't affect the others. Example:

```java
// BETTER: use soft assertions — all 4 checks are independent
SoftAssert softAssert = new SoftAssert();
softAssert.assertTrue(loginPage.isTitleDisplayed(), ...);
softAssert.assertTrue(loginPage.isUsernameFieldDisplayed(), ...);
softAssert.assertTrue(loginPage.isPasswordFieldDisplayed(), ...);
softAssert.assertTrue(loginPage.isLoginButtonDisplayed(), ...);
softAssert.assertAll();  // reports ALL failures at once
```

If the title is missing, you still learn whether the other 3 elements are visible.
With hard assertions, you'd need to fix the title issue and rerun to discover
additional problems.

## Decision Table

### LoginPositiveTest
| Method | Assertions | Type | Reason |
|--------|-----------|------|--------|
| `testSuccessfulLogin` | 2 | **Hard** | `getSuccessText()` depends on screen being displayed |
| `testSuccessfulLoginViaXPath` | 1 | Hard | Single assertion |
| `testValidLoginFormatWithRegex` | 2 | **Soft** | Two independent regex checks |

### LoginNegativeTest
| Method | Assertions | Type | Reason |
|--------|-----------|------|--------|
| `testLoginWithWrongPassword` | 2 | **Soft** | Error non-empty + pattern match are independent |
| `testLoginWithWrongUsername` | 2 | **Soft** | Error non-empty + exact text are independent |
| `testLoginWithBothWrongCredentials` | 2 | **Soft** | Error shown + no success screen are independent |
| `testLoginWithSpecialCharacters` | 2 | **Soft** | Regex precondition + error check are independent |
| `testLoginWithWhitespaceOnly` | 2 | **Soft** | Regex precondition + no success screen are independent |

### LoginFieldValidationTest
| Method | Assertions | Type | Reason |
|--------|-----------|------|--------|
| `testLoginWithEmptyFields` | 1 | Hard | Single assertion |
| `testLoginWithMaxLengthUsername` | 2 | **Soft** | Regex check + UI outcome are independent |
| `testLoginWithMaxLengthPassword` | 2 | **Soft** | Regex check + UI outcome are independent |
| `testLoginWithEmptyUsername` | 1 | Hard | Single assertion |
| `testLoginWithEmptyPassword` | 1 | Hard | Single assertion |
| `testLoginWithExactMaxLength` | 2 | **Soft** | Regex check + UI outcome are independent |

### LoginUITest
| Method | Assertions | Type | Reason |
|--------|-----------|------|--------|
| `testLoginScreenElementsPresence` | 4 | **Soft** | All 4 element checks are independent |
| `testLoginScreenTitle` | 1 | Hard | Single assertion |
| `testTitleFoundByXPath` | 2 | **Hard** | `isDisplayed()` depends on `assertNotNull` |
| `testPasswordFieldIsMasked` | 1 | Hard | Single assertion |
| `testLoginButtonEnabled` | 1 | Hard | Single assertion |

## Summary
- **10 methods** converted to `SoftAssert` (24 assertions now soft)
- **9 methods** kept with hard `Assert` (13 assertions remain hard)
- Total: 37 assertions across 19 test methods
