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
 
package com.cmbchina.baas.easyBaas.service.internal;

import com.cmbchina.baas.easyBaas.request.AlertConfigAlerterRequest;
import com.cmbchina.baas.easyBaas.request.AlertConfigSMTPRequest;
import com.cmbchina.baas.easyBaas.response.Response;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public interface MonitorAlertManageService {
    Response addAlertSendEmails(AlertConfigSMTPRequest alertConfigSMTPRequest, HttpSession session, HttpServletRequest request) throws Exception;

    Response addAlertEmails(AlertConfigAlerterRequest alertConfigAlerterRequest, HttpSession session) throws Exception;

    Response updateAlertEmails(AlertConfigAlerterRequest alertConfigAlerterRequest, HttpSession session) throws Exception;

    Response deleteAlertEmails(String alarmAddress, HttpSession session) throws Exception;

    Response queryAllAlertEmails(HttpSession session);
}
