/**
 *    Copyright (c) 2023 招商银行股份有限公司
 *    EasyBaaS is licensed under Mulan PSL v2.
 *    You can use this software according to the terms and conditions of the Mulan PSL v2.
 *    You may obtain a copy of Mulan PSL v2 at:
 *                http://license.coscl.org.cn/MulanPSL2
 *    THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND,
 *    EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT,
 *    MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 *    See the Mulan PSL v2 for more details.
 */
 
package com.cmbchina.baas.easyBaas.validation;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @Title: EnumValidator
 * @Description: 枚举校验器
 * @Date 2021/7/27 10:38
 */
@Slf4j
public class EnumValidator implements ConstraintValidator<EnumValid, String> {

    private EnumValid annotation;

    @Override
    public void initialize(EnumValid constraintAnnotation) {
        annotation = constraintAnnotation;
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        Class<?> cls = annotation.target();
        boolean ignoreEmpty = annotation.ignoreEmpty();
        if (ignoreEmpty) {
            return true;
        }
        return validCheck(cls, value);
    }

    private boolean validCheck(Class<?> cls, String value) {
        if (cls.isEnum() && StringUtils.isNotEmpty(value)) {
            Object[] objects = cls.getEnumConstants();
            try {
                Method method = cls.getMethod("name");
                for (Object obj : objects) {
                    Object code = method.invoke(obj);
                    if (value.equals(code.toString())) {
                        return true;
                    }
                }
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                log.warn("EnumValidator call isValid() method exception.");
                return false;
            }
        }
        return true;
    }
}
