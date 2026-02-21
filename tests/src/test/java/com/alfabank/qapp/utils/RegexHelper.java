package com.alfabank.qapp.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class demonstrating regex usage for test validation.
 * Provides helper metods for validating field inputs and error mesages.
 */
public class RegexHelper {

    /**
     * Validates that a string contains only allowed characters for login field.
     * Allowed: alphanumeric, underscores, dots, hyphens.
     */
    public static boolean isValidLoginFormat(String login) {
        Pattern pattern = Pattern.compile("^[a-zA-Z0-9._-]+$");
        Matcher matcher = pattern.matcher(login);
        return matcher.matches();
    }

    /**
     * Checks if the error message matches the expected pattern for invalid credentials.
     * Expected: "Введены неверные данные"
     */
    public static boolean isInvalidCredentialsError(String errorText) {
        Pattern pattern = Pattern.compile(".*неверн.*данн.*", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
        Matcher matcher = pattern.matcher(errorText);
        return matcher.matches();
    }

    /**
     * Validates that a string does not exceed the maximum allowed length.
     */
    public static boolean isWithinMaxLength(String input, int maxLength) {
        Pattern pattern = Pattern.compile("^.{0," + maxLength + "}$", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(input);
        return matcher.matches();
    }

    /**
     * Checks if a string contains only whitespace characters.
     */
    public static boolean isWhitespaceOnly(String input) {
        Pattern pattern = Pattern.compile("^\\s+$");
        Matcher matcher = pattern.matcher(input);
        return matcher.matches();
    }

    /**
     * Extracts digits from a string using regex.
     */
    public static String extractDigits(String input) {
        return input.replaceAll("[^0-9]", "");
    }

    /**
     * Checks if a string contains special characters.
     */
    public static boolean containsSpecialCharacters(String input) {
        Pattern pattern = Pattern.compile("[^a-zA-Z0-9\\s]");
        Matcher matcher = pattern.matcher(input);
        return matcher.find();
    }
}
