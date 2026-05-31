package com.fittrack.util;

import android.util.Patterns;

public class ValidationUtils {

    public static boolean isNotEmpty(String s) {
        return s != null && !s.trim().isEmpty();
    }

    public static boolean isPositiveInt(String s) {
        if (!isNotEmpty(s)) return false;
        try {
            return Integer.parseInt(s) > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isNonNegativeInt(String s) {
        if (!isNotEmpty(s)) return false;
        try {
            return Integer.parseInt(s) >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isPositiveDouble(String s) {
        if (!isNotEmpty(s)) return false;
        try {
            return Double.parseDouble(s) >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isValidEmail(String s) {
        return s != null && Patterns.EMAIL_ADDRESS.matcher(s).matches();
    }
}
