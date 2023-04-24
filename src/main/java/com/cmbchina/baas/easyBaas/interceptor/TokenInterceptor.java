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

package com.cmbchina.baas.easyBaas.interceptor;

import cn.hutool.core.util.StrUtil;
import cn.hutool.jwt.JWT;
import com.alibaba.fastjson.JSON;
import com.cmbchina.baas.easyBaas.constant.ConstantsContainer;
import com.cmbchina.baas.easyBaas.exception.ErrorCodes;
import com.cmbchina.baas.easyBaas.model.User;
import com.cmbchina.baas.easyBaas.response.Response;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Date;

/**
 * @author sw9327@cmbchina.com
 * @description
 * @data 2021/08/17 8:38
 */
public class TokenInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (null != (User) request.getSession().getAttribute(request.getSession().getId() + ":" + ConstantsContainer.USER)) {
            if (!checkToken(request, request.getSession()) && !checkRequestPath(request)) {
                response.setContentType("application/json;charset=utf-8");
                response.getWriter().write(returnMessage(ErrorCodes.VALIDATE_TOKEN_FAILED_ERROR));
                return false;
            }
        } else {
            if (!checkRequestPath(request)) {
                response.setContentType("application/json;charset=utf-8");
                response.getWriter().write(returnMessage(ErrorCodes.USER_LOGIN_FAILED_ERROR));
                return false;
            }
        }
        return true;
    }

    private boolean checkToken(HttpServletRequest request, HttpSession session) {
        String headerToken = request.getHeader("Authorization");
        if (StrUtil.isBlank(headerToken)) {
            return false;
        }
        Date expirstAt = (Date) session.getAttribute(session.getId() + ":" + ConstantsContainer.TOKEN_TIME);
        if (JWT.of(headerToken).setKey(ConstantsContainer.TOKEN_KEY).verify()) {
            if (expirstAt.after(new Date()) && headerToken.equals(session.getAttribute(session.getId() + ":" + ConstantsContainer.TOKEN))) {
                return true;
            }
        }
        return false;
    }

    private boolean checkRequestPath(HttpServletRequest request) {
        String[] allowPath = {".css", ".html", ".js", ".jpg", ".png", ".svg", "/easyBaas/admin/getVerCode",
                "/easyBaas/admin/login", "/easyBaas/admin/resetPassword", "/index.html", "/login"};
        String requestUri = request.getRequestURI();
        for (String path : allowPath) {
            if (requestUri.contains(path) || "/".equals(requestUri)) {
                return true;
            }
        }
        return false;
    }

    private String returnMessage(ErrorCodes errorCodes) {
        return JSON.toJSONString(Response.builder().code(String.valueOf(errorCodes.getCode()))
                .msg(errorCodes.getMessage()).build());
    }
}
