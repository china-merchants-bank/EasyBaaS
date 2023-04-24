package com.cmbchina.baas.easyBaas.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author IT007429
 * @description List内容是否重复校验
 * @data 2022/06/23 10:28
 */
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PathValidator.class)
public @interface PathValid {

    String message();
    
    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
