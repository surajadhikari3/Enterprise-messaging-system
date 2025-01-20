package io.reactivestax.EMSRestApi.exception;

public class ExceededGenerationCountError extends RuntimeException {
    public ExceededGenerationCountError(String message) {
        super(message);
    }
}
