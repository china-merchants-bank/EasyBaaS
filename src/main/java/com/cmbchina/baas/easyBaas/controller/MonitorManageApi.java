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

import com.cmbchina.baas.easyBaas.request.AlertConfigAlerterRequest;
import com.cmbchina.baas.easyBaas.request.AlertConfigSMTPRequest;
import com.cmbchina.baas.easyBaas.request.MonitorComponentRequest;
import com.cmbchina.baas.easyBaas.request.UpdateGrafanaConfigUrlRequest;
import com.cmbchina.baas.easyBaas.response.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

/**
 * @Title: MonitorManageApi
 * @Description: 监控服务管理api
 * @Date 2021/7/27 8:57
 */
@Validated
@Api(value = "monitor")
@RequestMapping(value = "/easyBaas")
public interface MonitorManageApi {

    @ApiOperation(value = "查询监控组件列表", nickname = "list", notes = "查询监控组件列表", response = Response.class, tags = {"监控管理"})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "查询成功", response = Response.class),
            @ApiResponse(code = 500, message = "查询失败", response = Response.class)})
    @RequestMapping(value = "/monitor/component/list",
            produces = {"application/json"},
            method = RequestMethod.GET)
    Response queryComponentList(HttpSession session);

    @ApiOperation(value = "启停监控组件", nickname = "startOrStop", notes = "启停监控组件", response = Response.class, tags = {"监控管理"})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "启停成功", response = Response.class),
            @ApiResponse(code = 500, message = "启停失败", response = Response.class)})
    @RequestMapping(value = "/monitor/component/startOrStop",
            produces = {"application/json"},
            consumes = {"application/json"},
            method = RequestMethod.PUT)
    Response startOrStopComponent(@ApiParam(value = "监控组件启停请求体") @Valid @RequestBody MonitorComponentRequest monitorComponentRequest, HttpSession session);

    @ApiOperation(value = "配置邮箱告警SMTP服务", nickname = "addAlertSendEmails", notes = "配置邮箱告警SMTP服务", response = Response.class, tags = {"监控管理"})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "配置成功", response = Response.class),
            @ApiResponse(code = 500, message = "配置失败", response = Response.class)})
    @RequestMapping(value = "/monitor/alertRule/addAlertSendEmails",
            produces = {"application/json"},
            consumes = {"application/json"},
            method = RequestMethod.POST)
    Response addAlertSendEmails(@ApiParam(value = "配置邮箱告警SMTP服务请求体") @Valid @RequestBody AlertConfigSMTPRequest alertConfigSMTPRequest,
                                HttpSession session, HttpServletRequest request);

    @ApiOperation(value = "配置接收告警人", nickname = "addAlertEmails", notes = "配置接收告警人", response = Response.class, tags = {"监控管理"})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "配置成功", response = Response.class),
            @ApiResponse(code = 500, message = "配置失败", response = Response.class)})
    @RequestMapping(value = "/monitor/alertRule/addAlertEmails",
            produces = {"application/json"},
            consumes = {"application/json"},
            method = RequestMethod.POST)
    Response addAlertEmails(@ApiParam(value = "配置接收告警人请求体") @Valid @RequestBody AlertConfigAlerterRequest alertConfigAlerterRequest, HttpSession session);

    @ApiOperation(value = "修改接收告警人配置", nickname = "updateAlertEmails", notes = "修改接收告警人配置", response = Response.class, tags = {"监控管理"})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "修改成功", response = Response.class),
            @ApiResponse(code = 500, message = "修改失败", response = Response.class)})
    @RequestMapping(value = "/monitor/alertRule/updateAlertEmails",
            produces = {"application/json"},
            consumes = {"application/json"},
            method = RequestMethod.PUT)
    Response updateAlertEmails(@ApiParam(value = "修改接收告警人配置请求体") @Valid @RequestBody AlertConfigAlerterRequest alertConfigAlerterRequest, HttpSession session);

    @ApiOperation(value = "删除接收告警人", nickname = "deleteAlertEmails", notes = "删除接收告警人", response = Response.class, tags = {"监控管理"})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "删除成功", response = Response.class),
            @ApiResponse(code = 500, message = "删除失败", response = Response.class)})
    @RequestMapping(value = "/monitor/alertRule/deleteAlertEmails",
            produces = {"application/json"},
            method = RequestMethod.DELETE)
    Response deleteAlertEmails(@ApiParam(value = "删除接收告警人email地址") @RequestParam(value = "alertEmail") String alertEmail, HttpSession session);

    @ApiOperation(value = "查询接收告警人列表", nickname = "queryAllAlertEmails", notes = "查询接收告警人列表", response = Response.class, tags = {"监控管理"})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "查询成功", response = Response.class),
            @ApiResponse(code = 500, message = "查询失败", response = Response.class)})
    @RequestMapping(value = "/monitor/alertRule/queryAllAlertEmails",
            produces = {"application/json"},
            method = RequestMethod.GET)
    Response queryAllAlertEmails(HttpSession session);

    @ApiOperation(value = "修改grafana配置地址", nickname = "updateGrafanaConfigUrl", notes = "修改grafana配置地址", response = Response.class, tags = {"监控管理"})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "修改成功", response = Response.class),
            @ApiResponse(code = 500, message = "修改失败", response = Response.class)})
    @RequestMapping(value = "/monitor/component/updateGrafanaConfig",
            produces = {"application/json"},
            method = RequestMethod.POST)
    Response updateGrafanaConfigUrl(@ApiParam(value = "修改grafana配置地址服务请求体") @Valid @RequestBody UpdateGrafanaConfigUrlRequest updateGrafanaConfigUrlRequest, HttpSession session);

}
