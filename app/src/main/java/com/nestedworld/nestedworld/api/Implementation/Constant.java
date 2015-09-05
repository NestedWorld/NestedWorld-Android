package com.nestedworld.nestedworld.api.Implementation;

import com.nestedworld.nestedworld.BuildConfig;

public class Constant {
    public final static String BASE_URL = BuildConfig.DEBUG ? "" : "";
    public final static String API_VERSION = "v0";

    public static final String USER_SIGN_UP = "/users/sign_up";
    public static final String USER_SIGN_IN = "/users/sign_in";
    public static final String USER_SIGN_OUT = "/users/sign_out";
    public static final String RESET_PASSWORD_URL = "/users/password";
}
