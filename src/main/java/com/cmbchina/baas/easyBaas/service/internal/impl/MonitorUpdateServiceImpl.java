/**
 * Copyright (c) 2023 招商银行股份有限公司
 * EasyBaaS is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND,
 * EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT,
 * MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.cmbchina.baas.easyBaas.service.internal.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.cmbchina.baas.easyBaas.client.deployment.PlatformOperation;
import com.cmbchina.baas.easyBaas.config.ImageConfig;
import com.cmbchina.baas.easyBaas.constant.ConstantsContainer;
import com.cmbchina.baas.easyBaas.domain.deployment.ApplicationInfo;
import com.cmbchina.baas.easyBaas.exception.ErrorCodes;
import com.cmbchina.baas.easyBaas.mapper.MonitorInfoMapper;
import com.cmbchina.baas.easyBaas.model.MonitorInfo;
import com.cmbchina.baas.easyBaas.model.WebConnectionInfo;
import com.cmbchina.baas.easyBaas.request.UpdateGrafanaConfigUrlRequest;
import com.cmbchina.baas.easyBaas.response.Response;
import com.cmbchina.baas.easyBaas.service.internal.MonitorUpdateService;
import com.cmbchina.baas.easyBaas.util.connection.SSHConnection;
import com.jcraft.jsch.ChannelExec;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class MonitorUpdateServiceImpl implements MonitorUpdateService {

    @Autowired
    DeploymentNodeServiceImpl deploymentNodeServiceImpl;
    @Autowired
    MonitorInfoMapper monitorInfoMapper;
    @Autowired
    ImageConfig imageConfig;
    @Autowired
    SSHConnection sshConnection;

    @Override
    public Response updateGrafanaInfoUrl(UpdateGrafanaConfigUrlRequest updateGrafanaConfigUrlRequest, HttpSession session) throws Exception {

        log.info("{}开始更新grafana配置", session.getId());
        List<MonitorInfo> grafana = monitorInfoMapper.selectByName(imageConfig.getGrafanaAlias());
        if (CollectionUtil.isEmpty(grafana)) {
            throw new Exception("找不到grafana组件");
        }
        String grafanaIp = grafana.get(0).getHost().split(ConstantsContainer.IP_PORT_SEP)[0];
        log.info("{}grafana在机器{}上", session.getId(), grafanaIp);
        WebConnectionInfo webConnectionInfo = deploymentNodeServiceImpl.getWebConnectionInfo(null, null, grafanaIp, session);
        String containerId = getGrafanaContainerId(grafanaIp, session);
        log.info("{}容器id为{}", session.getId(), containerId);
        invokeShell(webConnectionInfo, containerId, updateGrafanaConfigUrlRequest.getPrometheusUrl(), session);
        log.info("{}更新grafana配置完成", session.getId());
        return Response.builder().code(String.valueOf(ErrorCodes.OK.getCode())).msg(ErrorCodes.OK.getMessage()).build();
    }

    private String getGrafanaContainerId(String address, HttpSession session) throws Exception {
        log.info("{}开始查询容器id", session.getId());
        List<ApplicationInfo> applicationInfoList = PlatformOperation.getDockerJavaPlatformOperation(address).listApplications();
        Optional<ApplicationInfo> optional = applicationInfoList.stream().
                filter(applicationInfo -> applicationInfo.getApplicationName().contains(File.separator + imageConfig.getGrafanaAlias())).findFirst();
        if (!optional.isPresent()) {
            throw new Exception("找不到grafana组件");
        }
        ApplicationInfo applicationInfo = optional.get();
        log.info("{}成功获取容器", session.getId());
        return applicationInfo.getContainerId();
    }

    private void invokeShell(WebConnectionInfo webConnectionInfo, String containerId, String newIp, HttpSession session) throws Exception {
        InputStream inputStream = null;
        try {
            log.info("{}开始执行脚本", session.getId());
            ChannelExec channel = (ChannelExec) sshConnection.open(webConnectionInfo, session.getId());
            channel.setCommand("sh /home/userClient/shell/UpdateGrafanaConfigUrl.sh " + containerId + " " + newIp);
            channel.connect();
            inputStream = channel.getInputStream();
            String result = IOUtil.toString(inputStream, "UTF-8");
            if (!result.contains(containerId)) {
                throw new Exception("脚本执行失败");
            }
            log.info("{}成功修改并重启容器{}", session.getId(), containerId);
            inputStream.close();
            sshConnection.close(session.getId());
        } catch (Exception e) {
            if (null != inputStream) {
                inputStream.close();
            }
            log.error("登录服务器失败或执行脚本失败,[{}]", e);
            throw new Exception("登录服务器失败或执行脚本失败");
        }
    }
}
