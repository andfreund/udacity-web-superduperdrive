package com.udacity.jwdnd.course1.cloudstorage.data;

public class TestUser {
    private static final String DEFAULT_FIRST_NAME = "Homer";
    private static final String DEFAULT_LAST_NAME = "Simpson";
    private static final String DEFAULT_USERNAME = "simpsonh";
    private static final String DEFAULT_PASSWORD = "supersafepassword1234";

    private final String firstName;
    private final String lastName;
    private final String username;
    private final String password;

    public TestUser(String firstName, String lastName, String username, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
    }

    public TestUser(String username) {
        this(DEFAULT_FIRST_NAME, DEFAULT_LAST_NAME, username, DEFAULT_PASSWORD);
    }

    public TestUser() {
        this(DEFAULT_FIRST_NAME, DEFAULT_LAST_NAME, DEFAULT_USERNAME, DEFAULT_PASSWORD);
    }

    public String firstName() {
        return firstName;
    }

    public String lastName() {
        return lastName;
    }

    public String username() {
        return username;
    }

    public String password() {
        return password;
    }
}
