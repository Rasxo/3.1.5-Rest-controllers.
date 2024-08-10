package ru.kata.spring.boot_security.demo.util;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String exceptionMessage) {
        super(exceptionMessage);
    }
}
