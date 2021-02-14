package com.ironhack.bankapp.utils;

/** Regular expressions for name, username and password validation **/
public class RegExp {
    public static final String VALID_NAME = "[\\p{L} .'-]+";

    public static final String VALID_USERNAME = "[0-9a-zA-Z_.-]+";

    public static final String VALID_PASSWORD = "[0-9a-zA-Z_.-@#*$%^&+=]+";
}
