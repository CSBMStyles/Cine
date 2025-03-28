package com.unicine.util.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.Pattern;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE })
@Retention( RetentionPolicy.RUNTIME )
@Constraint( validatedBy = MultiPatternValidator.class )
/*
 * Esta anotación permite realizar multiples @Pattern
 */ 
public @interface MultiPattern {

    Pattern[] value();

    String message() default "Valor no válido";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}