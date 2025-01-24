package io.reactivestax.EMSRestApi.exception;

public class ExceededValidationException extends RuntimeException {
    public ExceededValidationException(String message) {
        super(message);
    }
}
