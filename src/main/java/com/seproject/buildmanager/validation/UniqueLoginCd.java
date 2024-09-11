package com.seproject.buildmanager.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

/**
 * 
 */
@Constraint(validatedBy = UniqueLoginCdValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueLoginCd {
  String message() default "This login ID is already taken.";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
