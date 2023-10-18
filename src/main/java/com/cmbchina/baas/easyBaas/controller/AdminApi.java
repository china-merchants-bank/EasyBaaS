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

import com.cmbchina.baas.easyBaas.request.ChangePasswordRequest;
import com.cmbchina.baas.easyBaas.request.LoginRequest;
import com.cmbchina.baas.easyBaas.response.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import javax.servlet.http.HttpServletResponse;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.IOException;

@Validated
@Api(value = "admin")
@RequestMapping(value = "/easyBaas")
public interface AdminApi {

    @ApiOperation(value = "用户修改密码", nickname = "changePassword", notes = "输入用户名、旧密码、新密码以修改用户密码 ", response = Response.class, tags = {"用户管理"})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "修改密码成功，data为空", response = Response.class),
            @ApiResponse(code = 400, message = "修改密码失败，系统错误", response = Response.class),
            @ApiResponse(code = 401, message = "修改密码失败，旧密码错误", response = Response.class)})
    @RequestMapping(value = "/admin/changePassword",
            produces = {"application/json"},
            consumes = {"application/json"},
            method = RequestMethod.PUT)
    Response changePassword(@ApiParam(value = "修改密码请求体") @Valid @RequestBody ChangePasswordRequest changePasswordRequest, HttpSession session);


    @ApiOperation(value = "用户登录", nickname = "login", notes = "使用提供的用户名密码进行登录 ", response = Response.class, tags = {"用户管理"})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "登录响应，返回携带token", response = Response.class),
            @ApiResponse(code = 400, message = "登录失败，系统错误", response = Response.class),
            @ApiResponse(code = 401, message = "登录失败，鉴权错误", response = Response.class)})
    @RequestMapping(value = "/admin/login",
            produces = {"application/json"},
            consumes = {"application/json"},
            method = RequestMethod.POST)
    Response login(@ApiParam(value = "登录请求体") @Valid @RequestBody LoginRequest loginRequest, HttpSession session);


    @ApiOperation(value = "用户登出", nickname = "logout", notes = "用户登出 ", response = Response.class, tags = {"用户管理"})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "登出成功", response = Response.class),
            @ApiResponse(code = 400, message = "登出失败", response = Response.class)})
    @RequestMapping(value = "/admin/logout",
            produces = {"application/json"},
            method = RequestMethod.GET)
    Response logout(HttpSession session);


    @ApiOperation(value = "获取验证码", nickname = "getVerCode", notes = "用户登录以及重置获取验证码 ", response = Response.class, tags = {"用户管理"})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "获取验证码成功", response = Response.class),
            @ApiResponse(code = 400, message = "获取验证码失败，系统错误", response = Response.class)})
    @RequestMapping(value = "/admin/getVerCode",
            consumes = {"application/json"},
            produces = {"application/octet-stream"},
            method = RequestMethod.GET)
    void getVerCode(@NotNull String type, HttpServletResponse response, HttpSession session) throws IOException;
}
