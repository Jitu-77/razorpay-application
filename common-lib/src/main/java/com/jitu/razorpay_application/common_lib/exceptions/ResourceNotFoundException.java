package com.jitu.razorpay_application.common_lib.exceptions;

import lombok.Getter;

@Getter
public class ResourceNotFoundException extends RuntimeException{
    private final String resouceName;
    private final Object identifier;

    public ResourceNotFoundException(String resourceName, Object identifier ){
        super(resourceName + "not found" + identifier);
        this.resouceName = resourceName;
        this.identifier = identifier;
    }
}
