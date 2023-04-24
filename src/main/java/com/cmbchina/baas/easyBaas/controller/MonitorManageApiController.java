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

import com.cmbchina.baas.easyBaas.constant.ConstantsContainer;
import com.cmbchina.baas.easyBaas.exception.ErrorCodes;
import com.cmbchina.baas.easyBaas.request.AlertConfigAlerterRequest;
import com.cmbchina.baas.easyBaas.request.AlertConfigSMTPRequest;
import com.cmbchina.baas.easyBaas.request.MonitorComponentRequest;
import com.cmbchina.baas.easyBaas.request.UpdateGrafanaConfigUrlRequest;
import com.cmbchina.baas.easyBaas.response.Response;
import com.cmbchina.baas.easyBaas.service.internal.MonitorAlertManageService;
import com.cmbchina.baas.easyBaas.service.internal.MonitorManageService;
import com.cmbchina.baas.easyBaas.service.internal.MonitorUpdateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

/**
 * @Title: MonitorManageApiController
 * @Description: 监控管理接口实现类
 * @Date 2021/7/27 9:04
 */
@Slf4j
@RestController
public class MonitorManageApiController implements MonitorManageApi {

    @Autowired
    private MonitorManageService monitorManageService;

    @Autowired
    private MonitorAlertManageService monitorAlertManageService;

    @Autowired
    private MonitorUpdateService monitorUpdateService;

    @Override
    public Response queryComponentList(HttpSession session) {
        try {
            return monitorManageService.queryComponentList(session);
        } catch (Exception e) {
            log.error("{}获取监控组件列表异常：{}", session.getId(), e.getMessage());
            return Response.builder().code(String.valueOf(ErrorCodes.QUERY_MONITOR_COMPONENT_FAILED_ERROR.getCode())).
                    msg(ErrorCodes.QUERY_MONITOR_COMPONENT_FAILED_ERROR.getMessage()).data(null).build();
        }
    }

    @Override
    public Response startOrStopComponent(@Valid MonitorComponentRequest monitorComponentRequest, HttpSession session) {
        try {
            return monitorManageService.startOrStopComponent(monitorComponentRequest, session);
        } catch (Exception e) {
            log.error("{}启停监控组件异常：{}", session.getId(), e.getMessage());
            return Response.builder().code(String.valueOf(ErrorCodes.START_OR_STOP_MONITOR_COMPONENT_FAILED_ERROR.getCode())).
                    msg(ErrorCodes.START_OR_STOP_MONITOR_COMPONENT_FAILED_ERROR.getMessage()).data(null).build();
        }
    }

    @Override
    public Response addAlertSendEmails(AlertConfigSMTPRequest alertConfigSMTPRequest, HttpSession session, HttpServletRequest request) {
        try {
            return monitorAlertManageService.addAlertSendEmails(alertConfigSMTPRequest, session, request);
        } catch (Exception e) {
            log.error("{}配置smtp服务异常：{}", session.getId(), e.getMessage());
            return Response.builder().code(String.valueOf(ErrorCodes.CONFIG_SMTP_FAILED_ERROR.getCode())).
                    msg(ErrorCodes.CONFIG_SMTP_FAILED_ERROR.getMessage()).data(null).build();
        }
    }

    @Override
    public Response addAlertEmails(AlertConfigAlerterRequest alertConfigAlerterRequest, HttpSession session) {
        try {
            return monitorAlertManageService.addAlertEmails(alertConfigAlerterRequest, session);
        } catch (Exception e) {
            log.error("{}添加告警接收人异常：{}", session.getId(), e.getMessage());
            return Response.builder().code(String.valueOf(ErrorCodes.ADD_ALERTER_FAILED_ERROR.getCode())).
                    msg(ErrorCodes.ADD_ALERTER_FAILED_ERROR.getMessage() + ConstantsContainer.IP_PORT_SEP + e.getMessage()).data(null).build();
        }
    }

    @Override
    public Response updateAlertEmails(AlertConfigAlerterRequest alertConfigAlerterRequest, HttpSession session) {
        try {
            return monitorAlertManageService.updateAlertEmails(alertConfigAlerterRequest, session);
        } catch (Exception e) {
            log.error("{}更新告警接收人信息异常：{}", session.getId(), e.getMessage());
            return Response.builder().code(String.valueOf(ErrorCodes.UPDATE_ALERTER_FAILED_ERROR.getCode())).
                    msg(ErrorCodes.UPDATE_ALERTER_FAILED_ERROR.getMessage() + ConstantsContainer.IP_PORT_SEP + e.getMessage()).data(null).build();
        }
    }

    @Override
    public Response deleteAlertEmails(String alarmEmail, HttpSession session) {
        try {
            return monitorAlertManageService.deleteAlertEmails(alarmEmail, session);
        } catch (Exception e) {
            log.error("{}删除告警接收人信息异常：{}", session.getId(), e.getMessage());
            return Response.builder().code(String.valueOf(ErrorCodes.DELETE_ALERTER_FAILED_ERROR.getCode())).
                    msg(ErrorCodes.DELETE_ALERTER_FAILED_ERROR.getMessage() + ConstantsContainer.IP_PORT_SEP + e.getMessage()).data(null).build();
        }
    }

    @Override
    public Response queryAllAlertEmails(HttpSession session) {
        try {
            return monitorAlertManageService.queryAllAlertEmails(session);
        } catch (Exception e) {
            log.error("{}查询告警接收人列表异常：{}", session.getId(), e.getMessage());
            return Response.builder().code(String.valueOf(ErrorCodes.QUERY_ALERTER_FAILED_ERROR.getCode())).
                    msg(ErrorCodes.QUERY_ALERTER_FAILED_ERROR.getMessage() + ConstantsContainer.IP_PORT_SEP + e.getMessage()).data(null).build();
        }
    }

    @Override
    public Response updateGrafanaConfigUrl(@Valid UpdateGrafanaConfigUrlRequest updateGrafanaConfigUrlRequest, HttpSession session) {
        try {
            return monitorUpdateService.updateGrafanaInfoUrl(updateGrafanaConfigUrlRequest, session);
        } catch (Exception e) {
            log.error("{}更新grafana配置失败：{}", session.getId(), e.getMessage());
            return Response.builder().code(String.valueOf(ErrorCodes.UPDATE_GRAFANA_CONFIG_ERROR.getCode())).
                    msg(ErrorCodes.UPDATE_GRAFANA_CONFIG_ERROR.getMessage() + ConstantsContainer.IP_PORT_SEP + e.getMessage()).data(null).build();
        }
    }

}
