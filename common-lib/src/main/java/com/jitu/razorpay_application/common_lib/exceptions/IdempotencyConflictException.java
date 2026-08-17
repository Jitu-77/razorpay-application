package com.jitu.razorpay_application.common_lib.exceptions;

public class IdempotencyConflictException extends RuntimeException {
    public IdempotencyConflictException(String message) {
        super(message);
    }
}
