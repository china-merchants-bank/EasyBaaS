package com.cmbchina.baas.easyBaas.validation;

import cn.hutool.core.util.StrUtil;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author IT007429
 * @description
 * @data 2022/06/23 10:28
 */
public class PathValidator implements ConstraintValidator<PathValid, Object> {


    @Override
    public boolean isValid(Object o, ConstraintValidatorContext constraintValidatorContext) {
        String path = (String) o;
        //为空的情况交给其它校验
        if (StrUtil.isEmpty(path)) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate("路径不能为空").addConstraintViolation();
            return false;
        }


        if (path.contains("../")) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate("路径格式不正确").addConstraintViolation();
            return false;
        }

        return true;
    }
}
