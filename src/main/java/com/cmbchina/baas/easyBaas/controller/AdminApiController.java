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

package com.cmbchina.baas.easyBaas.controller;

import com.alibaba.fastjson.JSON;
import com.cmbchina.baas.easyBaas.exception.ErrorCodes;
import com.cmbchina.baas.easyBaas.request.ChangePasswordRequest;
import com.cmbchina.baas.easyBaas.request.LoginRequest;
import com.cmbchina.baas.easyBaas.response.Response;
import com.cmbchina.baas.easyBaas.service.internal.AdminApiService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.IOException;
import java.io.PrintWriter;

@RestController
public class AdminApiController implements AdminApi {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminApiController.class);

    private final ObjectMapper objectMapper;

    private final HttpServletRequest request;

    @Autowired
    AdminApiService adminApiService;

    @Autowired
    public AdminApiController(ObjectMapper objectMapper, HttpServletRequest request) {
        this.objectMapper = objectMapper;
        this.request = request;
    }

    @Override
    public Response changePassword(@ApiParam(value = "修改密码请求体") @Valid @RequestBody ChangePasswordRequest changePasswordRequest, HttpSession session) {
        try {
            return adminApiService.changePassword(changePasswordRequest, session);
        } catch (Exception e) {
            LOGGER.error("{}请求修改密码出现错误：{}", session.getId(), e);
            return Response.builder().code(String.valueOf(ErrorCodes.UPDATE_PASSWD_FAILED_ERROR.getCode())).msg(ErrorCodes.UPDATE_PASSWD_FAILED_ERROR.getMessage()).data(null).build();
        }
    }

    @Override
    public Response login(@ApiParam(value = "登录请求体") @Valid @RequestBody LoginRequest loginRequest, HttpSession session) {
        try {
            return adminApiService.login(loginRequest, session);
        } catch (Exception e) {
            LOGGER.error("{}请求登录出现错误：{}", session.getId(), e.getMessage());
            return Response.builder().code(String.valueOf(ErrorCodes.LOGIN_FAILED_ERROR.getCode())).msg(ErrorCodes.LOGIN_FAILED_ERROR.getMessage()).data(null).build();
        }
    }

    @Override
    public Response logout(HttpSession session) {
        try {
            return adminApiService.logout(session);
        } catch (Exception e) {
            LOGGER.error("{}请求退出出现错误：{}", session.getId(), e.getMessage());
            return Response.builder().code(String.valueOf(ErrorCodes.LOGINOUT_FAILED_ERROR.getCode())).msg(ErrorCodes.LOGINOUT_FAILED_ERROR.getMessage()).data(null).build();
        }
    }


    @Override
    public void getVerCode(String type, HttpServletResponse response, HttpSession session) throws IOException {
        LOGGER.info("{}开始生成验证码", session.getId());
        try {
            adminApiService.getVerCode(type, response, session);
        } catch (IOException e) {
            LOGGER.error("{}请求生成验证码出现错误：{}", session.getId(), e.getMessage());
            response.setContentType("application/json;charset=utf-8");
            PrintWriter writer = response.getWriter();
            writer.write(JSON.toJSONString(Response.builder().code(String.valueOf(ErrorCodes.OUTPUTSTREAM_ERROR.getCode())).msg(ErrorCodes.OUTPUTSTREAM_ERROR.getMessage()).build()));
            writer.flush();
            writer.close();
            return;
        }
        LOGGER.info("{}验证码生成完成", session.getId());
    }
}
