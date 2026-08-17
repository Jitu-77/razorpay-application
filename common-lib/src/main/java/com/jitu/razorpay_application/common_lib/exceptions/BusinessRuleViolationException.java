package com.jitu.razorpay_application.common_lib.exceptions;

public class BusinessRuleViolationException extends  RuntimeException{
    private final String errorCode;


    public BusinessRuleViolationException(String errorCode, String message ){
        super(message);
        this.errorCode = errorCode;
    }
}
