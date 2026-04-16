package com.alpha.MoveBuddy.exception;

public class MobileAlreadyRegisteredException extends RuntimeException {

    public MobileAlreadyRegisteredException() {
        super("This mobile number is already registered");
    }

    public MobileAlreadyRegisteredException(String message) {
        super(message);
    }
}
