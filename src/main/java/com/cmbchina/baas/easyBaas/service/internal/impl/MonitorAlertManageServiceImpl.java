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
 
package com.cmbchina.baas.easyBaas.service.internal.impl;

import com.cmbchina.baas.easyBaas.client.deployment.PlatformOperation;
import com.cmbchina.baas.easyBaas.config.ImageConfig;
import com.cmbchina.baas.easyBaas.constant.ConstantsContainer;
import com.cmbchina.baas.easyBaas.domain.deployment.ApplicationInfo;
import com.cmbchina.baas.easyBaas.exception.ErrorCodes;
import com.cmbchina.baas.easyBaas.mapper.AlerterMapper;
import com.cmbchina.baas.easyBaas.mapper.MonitorInfoMapper;
import com.cmbchina.baas.easyBaas.mapper.SMTPInfoMapper;
import com.cmbchina.baas.easyBaas.model.Alerter;
import com.cmbchina.baas.easyBaas.model.MonitorInfo;
import com.cmbchina.baas.easyBaas.model.SMTPInfo;
import com.cmbchina.baas.easyBaas.request.AlertConfigAlerterRequest;
import com.cmbchina.baas.easyBaas.request.AlertConfigSMTPRequest;
import com.cmbchina.baas.easyBaas.response.AlerterResponse;
import com.cmbchina.baas.easyBaas.response.Response;
import com.cmbchina.baas.easyBaas.service.internal.MonitorAlertManageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MonitorAlertManageServiceImpl implements MonitorAlertManageService {
    @Autowired
    private SMTPInfoMapper smtpInfoMapper;
    @Autowired
    private AlerterMapper alerterMapper;
    @Autowired
    private ImageConfig imageConfig;
    @Autowired
    private MonitorInfoMapper monitorInfoMapper;

    @Override
    public Response addAlertSendEmails(AlertConfigSMTPRequest smtpRequest, HttpSession session, HttpServletRequest request) throws Exception {
        smtpRequest.setSmtpAddress(smtpRequest.getSmtpAddress().contains(":") ? smtpRequest.getSmtpAddress() : smtpRequest.getSmtpAddress() + ":465");
        log.info("配置smtp服务开始");
        List<SMTPInfo> smtpInfoList = smtpInfoMapper.selectAll();
        if (CollectionUtils.isEmpty(smtpInfoList)) {
            log.info("当前smtp信息为空，需要新增");
            SMTPInfo smtpInfo = SMTPInfo.builder().email(smtpRequest.getAlarmUsername()).authCode(smtpRequest.getAlarmPassword()).smtpHost(smtpRequest.getSmtpAddress())
                    .createTime(String.valueOf(System.currentTimeMillis())).updateTime(String.valueOf(System.currentTimeMillis())).build();
            smtpInfoMapper.insert(smtpInfo);
        } else {
            log.info("当前smtp信息不为空，需要修改");
            SMTPInfo smtpInfo = smtpInfoList.get(0);
            smtpInfo.setEmail(smtpRequest.getAlarmUsername());
            smtpInfo.setAuthCode(smtpRequest.getAlarmPassword());
            smtpInfo.setSmtpHost(smtpRequest.getSmtpAddress());
            smtpInfo.setUpdateTime(String.valueOf(System.currentTimeMillis()));
            smtpInfoMapper.updateByPrimaryKey(smtpInfo);
        }
        configAlertManagerConfig();
        return Response.builder().code(ErrorCodes.OK.getCode()).msg(ErrorCodes.OK.getMessage()).build();
    }

    @Override
    public Response addAlertEmails(AlertConfigAlerterRequest alertConfigAlerterRequest, HttpSession session) throws Exception {
        log.info("开始进行被告警通知人的设置");
        if (smtpInfoMapper.selectAll().size() <= 0) {
            return Response.builder().code(ErrorCodes.SMTP_ISNULL_FAILED_ERROR.getCode()).msg(ErrorCodes.SMTP_ISNULL_FAILED_ERROR.getMessage()).build();
        }
        Alerter alerter = alerterMapper.selectByEmail(alertConfigAlerterRequest.getAlertEmail());
        if (null != alerter) {
            throw new Exception("被告警通知邮箱已被配置，请查证后处理");
        }
        Alerter alerter1 = Alerter.builder().name(alertConfigAlerterRequest.getAlertName()).email(alertConfigAlerterRequest.getAlertEmail())
                .createTime(String.valueOf(System.currentTimeMillis())).updateTime(String.valueOf(System.currentTimeMillis())).build();
        alerterMapper.insert(alerter1);
        try {
            configAlertManagerConfig();
        } catch (Exception e) {
            alerter = alerterMapper.selectByEmail(alertConfigAlerterRequest.getAlertEmail());
            alerterMapper.deleteByPrimaryKey(alerter.getId());
            log.error("修改容器告警人配置信息异常{}", e.getMessage());
            throw new Exception("修改容器告警人配置信息异常");
        }
        return Response.builder().code(ErrorCodes.OK.getCode()).msg(ErrorCodes.OK.getMessage()).build();
    }

    @Override
    public Response updateAlertEmails(AlertConfigAlerterRequest alertConfigAlerterRequest, HttpSession session) throws Exception {
        log.info("开始修改被告警通知人的邮箱设置");
        Alerter alerter = alerterMapper.selectByName(alertConfigAlerterRequest.getAlertName());
        Alerter oldAlerter = alerter;
        if (null == alerter) {
            throw new Exception("被告警通知人未被配置，请查证后处理");
        }
        Alerter alerter1 = alerterMapper.selectByEmail(alertConfigAlerterRequest.getAlertEmail());
        if (null != alerter1) {
            throw new Exception("被告警通知邮箱已被配置，请查证后处理");
        }
        alerter.setEmail(alertConfigAlerterRequest.getAlertEmail());
        alerter.setUpdateTime(String.valueOf(System.currentTimeMillis()));
        alerterMapper.updateByPrimaryKey(alerter);
        try {
            configAlertManagerConfig();
        } catch (Exception e) {
            alerterMapper.updateByPrimaryKey(oldAlerter);
            log.error("修改容器告警人配置信息异常{}", e.getMessage());
            throw new Exception("修改容器告警人配置信息异常");
        }
        return Response.builder().code(ErrorCodes.OK.getCode()).msg(ErrorCodes.OK.getMessage()).build();
    }

    @Override
    public Response deleteAlertEmails(String alarmAddress, HttpSession session) throws Exception {
        log.info("开始删除被告警通知人的邮箱设置");
        Alerter alerter = alerterMapper.selectByEmail(alarmAddress);
        if (null == alerter) {
            throw new Exception("被告警通知邮箱未被配置，请查证后处理");
        }
        alerterMapper.deleteByPrimaryKey(alerter.getId());
        try {
            configAlertManagerConfig();
        } catch (Exception e) {
            alerterMapper.insert(Alerter.builder().email(alerter.getEmail()).name(alerter.getName()).createTime(alerter.getCreateTime()).updateTime(alerter.getUpdateTime()).build());
            log.error("修改容器告警人配置信息异常{}", e.getMessage());
            throw new Exception("修改容器告警人配置信息异常");
        }
        return Response.builder().code(ErrorCodes.OK.getCode()).msg(ErrorCodes.OK.getMessage()).build();
    }

    @Override
    public Response queryAllAlertEmails(HttpSession session) {
        log.info("查询当前所有已配置告警邮箱信息");
        List<AlerterResponse> list = new ArrayList<>();
        for (Alerter alerter : alerterMapper.selectAll()) {
            AlerterResponse alerterResponse = AlerterResponse.builder().alertName(alerter.getName()).alertEmail(alerter.getEmail()).build();
            list.add(alerterResponse);
        }
        return Response.builder().code(ErrorCodes.OK.getCode()).data(list).msg(ErrorCodes.OK.getMessage()).build();
    }

    private void configAlertManagerConfig() throws Exception {
        List<MonitorInfo> monitorInfos = monitorInfoMapper.selectByName(imageConfig.getAlertmanagerAlias());
        if (CollectionUtils.isEmpty(monitorInfos)) {
            return;
        }
        String ip = monitorInfos.get(0).getHost().split(ConstantsContainer.IP_PORT_SEP)[0];
        List<ApplicationInfo> applicationInfos = PlatformOperation.getDockerJavaPlatformOperation(ip).listApplications();
        Optional<ApplicationInfo> optionalApplicationInfo = applicationInfos.stream().filter(applicationInfo -> {
            return applicationInfo.getApplicationName().contains(imageConfig.getAlertmanagerAlias()); }).findFirst();
        if (optionalApplicationInfo.isPresent()) {
            ApplicationInfo applicationInfo = optionalApplicationInfo.get();
            String content = prepareAlertManagerConfigFileContent();
            log.info("调用alertmanager的容器exec中的脚本将文件内容写入到配置中并重启alertmanager");
            PlatformOperation.getDockerJavaPlatformOperation(ip).execApplication(applicationInfo, new String[]{"sh", "writeFile.sh", content});
            PlatformOperation.getDockerJavaPlatformOperation(ip).stopApplication(applicationInfo);
            PlatformOperation.getDockerJavaPlatformOperation(ip).startApplication(applicationInfo);
        }
    }

    private String prepareAlertManagerConfigFileContent() {
        log.info("使用模板创建alertmanager的配置文件内容");
        Yaml yaml = new Yaml(new SafeConstructor(new LoaderOptions()));
        Map<String, Object> map = (LinkedHashMap<String, Object>) yaml.load(this.getClass().getClassLoader().getResourceAsStream("monitor/alertmanager.yml"));
        List<SMTPInfo> smtpInfoList = smtpInfoMapper.selectAll();
        if (!CollectionUtils.isEmpty(smtpInfoList)) {
            configSMTPInAlertManagerConfigFile(map, smtpInfoList.get(0));
        }
        List<Alerter> alerterList = alerterMapper.selectAll();
        if (!CollectionUtils.isEmpty(alerterList)) {
            configAlertersInAlertManagerConfigFile(map, alerterList);
        }
        return yaml.dump(map);
    }

    private void configSMTPInAlertManagerConfigFile(Map<String, Object> map, SMTPInfo smtpInfo) {
        LinkedHashMap globalMap = (LinkedHashMap) map.get("global");
        globalMap.put("smtp_from", smtpInfo.getEmail());
        globalMap.put("smtp_auth_username", smtpInfo.getEmail());
        globalMap.put("smtp_auth_password", smtpInfo.getAuthCode());
        globalMap.put("smtp_smarthost", smtpInfo.getSmtpHost());
    }

    private void configAlertersInAlertManagerConfigFile(Map<String, Object> map, List<Alerter> alerterList) {
        List<String> emailList = alerterList.stream().map(Alerter::getEmail).collect(Collectors.toList());
        String emailString = StringUtils.join(emailList, ", ");
        ArrayList receivers = (ArrayList) map.get("receivers");
        LinkedHashMap linkedHashMap = (LinkedHashMap) receivers.get(0);
        ArrayList emailConfigs = (ArrayList) linkedHashMap.get("email_configs");
        LinkedHashMap hashMap = (LinkedHashMap) emailConfigs.get(0);
        hashMap.put("to", emailString);
    }
}