package io.reactivestax.EMSRestApi.exception;

public class ExceededValidationCountException extends RuntimeException {
    public ExceededValidationCountException(String message) {
        super(message);
    }
}
