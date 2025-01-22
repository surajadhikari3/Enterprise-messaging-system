package io.reactivestax.EMSRestApi.exception;

public class ExceededGenerationCountException extends RuntimeException {
    public ExceededGenerationCountException(String message) {
        super(message);
    }
}
