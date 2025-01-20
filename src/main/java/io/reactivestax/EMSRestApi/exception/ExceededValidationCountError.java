package io.reactivestax.EMSRestApi.exception;

public class ExceededValidationCountError extends RuntimeException {
    public ExceededValidationCountError(String message) {
        super(message);
    }
}
