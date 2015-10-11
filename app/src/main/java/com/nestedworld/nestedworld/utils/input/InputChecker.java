package com.nestedworld.nestedworld.utils.input;


import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InputChecker {
    public static boolean checkEmailFormat(final String email) {
        boolean isValid = false;

        final String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";

        final Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        final Matcher matcher = pattern.matcher(email);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    public static boolean checkPasswordFormat(final String password) {
        return !TextUtils.isEmpty(password) && password.length() >= 6;
    }
}
