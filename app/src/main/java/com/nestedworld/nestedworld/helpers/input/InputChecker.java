package com.nestedworld.nestedworld.helpers.input;


import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class InputChecker {

    private final static int SHORTEST_PASSWORD_LENGTH = 6;

    /*
    ** Constructor
     */
    private InputChecker() {
        //Empty constructor for avoiding this class to be construct
    }

    /*
    ** Public method
     */
    public static boolean checkEmailFormat(@NonNull final String email) {
        boolean isValid = false;

        final String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";

        final Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        final Matcher matcher = pattern.matcher(email);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    public static boolean checkPasswordFormat(@NonNull final String password) {
        return !TextUtils.isEmpty(password) && password.length() >= SHORTEST_PASSWORD_LENGTH;
    }
}
