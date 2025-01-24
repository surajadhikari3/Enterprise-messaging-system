package io.reactivestax.EMSRestApi.exception;

public class ExceededGenerationException extends RuntimeException {
    public ExceededGenerationException(String message) {
        super(message);
    }
}
