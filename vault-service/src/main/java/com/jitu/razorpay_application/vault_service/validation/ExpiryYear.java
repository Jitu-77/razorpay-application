package com.jitu.razorpay_application.vault_service.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * The annotated element must not be {@code null} and must contain at least one
 * non-whitespace character. Accepts {@code CharSequence}.
 *
 * @author Hardy Ferentschik
 * @since 2.0
 *
 * @see Character#isWhitespace(char)
 */
@Documented
@Constraint(validatedBy = { })
@Target({  FIELD, PARAMETER, TYPE_USE })
@Retention(RUNTIME)
public @interface ExpiryYear {
    String message() default "Expiry Year cant be past ";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
