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

package com.cmbchina.baas.easyBaas.config;

import com.cmbchina.baas.easyBaas.interceptor.ResponseInterceptor;
import com.cmbchina.baas.easyBaas.interceptor.TokenInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.List;

@Configuration
public class IntercertorConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        List<String> patterns = Arrays.asList(new String[]{"/easyBaas/admin/getVerCode", "/easyBaas/admin/login", "/", "/error"
                , "index", "/easyBaas/admin/resetPassword"});

        registry.addInterceptor(new TokenInterceptor()).addPathPatterns("/easyBaas/**")
                .excludePathPatterns(patterns);
        registry.addInterceptor(new ResponseInterceptor()).addPathPatterns("/easyBaas/**")
                .excludePathPatterns(patterns);

    }
}