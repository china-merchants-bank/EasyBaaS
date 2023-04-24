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
import com.cmbchina.baas.easyBaas.exception.exceptions.LoginShellException;
import com.cmbchina.baas.easyBaas.mapper.MonitorInfoMapper;
import com.cmbchina.baas.easyBaas.model.MonitorInfo;
import com.cmbchina.baas.easyBaas.model.NodeInfo;
import com.cmbchina.baas.easyBaas.model.WebConnectionInfo;
import com.cmbchina.baas.easyBaas.service.internal.MonitorConfigService;
import com.cmbchina.baas.easyBaas.util.connection.SSHConnection;
import com.cmbchina.baas.easyBaas.util.file.FileOperation;
import com.cmbchina.baas.easyBaas.util.file.LocalFileOperation;
import com.cmbchina.baas.easyBaas.util.file.RemoteFileOperation;
import com.cmbchina.baas.easyBaas.util.secret.EncryptTools;
import com.jcraft.jsch.JSchException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @description 清理数据情况
 * @data 2021/07/02 9:54
 */
@Service
@Slf4j
public class ErrorOperationClearServiceImpl {

    @Autowired
    SSHConnection sshConnection;

    @Autowired
    MonitorInfoMapper monitorInfoMapper;

    @Autowired
    MonitorConfigService monitorConfigService;

    @Autowired
    ImageConfig imageConfig;

/*    @Autowired
    DeploymentNodeServiceImpl deploymentNodeServiceImpl;*/

    private FileOperation fileOperation = new LocalFileOperation();

    private FileOperation remoteFileOperation = new RemoteFileOperation();

    protected void deleteFolder() {
        String filePath = ConstantsContainer.USER_HOME_PATH + ConstantsContainer.UPLOAD_FILE_PATH;
        try {
            fileOperation.deleteFile(filePath);
        } catch (JSchException e) {
            log.error("删除文件失败");
        }
    }

    protected boolean deleteFile(String networkName, String host, String userName, String password, String sessionId) throws LoginShellException, IOException {
        WebConnectionInfo webConnectionInfo = WebConnectionInfo.builder().userName(userName).password(EncryptTools.decrypt(password))
                .port(22).host(host).channelType(ConstantsContainer.CHANNEL_TYPE_SFTP).build();
        RemoteFileOperation remoteFileOperation = new RemoteFileOperation(webConnectionInfo, sessionId, sshConnection);

        Boolean flag = false;
        try {
            remoteFileOperation.deleteFolder(networkName);
            flag = true;
        } catch (LoginShellException e) {
            log.error("用户{}，删除文件失败{}", sessionId, e.getMessage());
        }
        return flag;
    }

    boolean deleteMonitors(NodeInfo nodeInfo, String nodeName, HttpSession session) {
        List<MonitorInfo> list = monitorInfoMapper.selectByNameLike(nodeName);
        PlatformOperation platformOperation = PlatformOperation.getDockerJavaPlatformOperation(nodeInfo.getHost().split(ConstantsContainer.IP_PORT_SEP)[0]);
        list.forEach(item -> {
            try {
                platformOperation.deleteApplication(ApplicationInfo.builder().applicationName(item.getName()).build());
            } catch (Exception e) {
                log.error("用户{}删除监控组件{}失败，异常{}", session.getId(), item.getName(), e.getMessage());
            }
        });
        try {
            List<MonitorInfo> monitorInfos = monitorInfoMapper.selectByName(imageConfig.getPrometheusAlias());
            monitorConfigService.configConfigInPrometheus(monitorInfos.get(0).getHost().split(ConstantsContainer.IP_PORT_SEP)[0]);
        } catch (Exception e) {
            log.error("用户{}删除监控更新监控数据异常，异常{}", session.getId(), e.getMessage());
        }
        return true;
    }

    void deleteNetworkConfigFiles(String networkName, String host, String userName, String password, String sessionId, boolean flag, String type, String path) throws Exception {
        log.info("开始清理临时文件");
        try {
            fileOperation.deleteFile(path + File.separator + networkName + type + ConstantsContainer.CONNECTION_SEP + ConstantsContainer.NETWORK_TOML_FILE);
        } catch (JSchException e) {
            log.error("删除文件{}失败{}", path + File.separator + networkName + type + ConstantsContainer.CONNECTION_SEP + ConstantsContainer.NETWORK_TOML_FILE, e);
        }
        try {
            fileOperation.deleteFile(path + File.separator + networkName + type + ConstantsContainer.CONNECTION_SEP + ConstantsContainer.JSONRPC_TOML_FILE);
        } catch (JSchException e) {
            log.error("删除文件{}失败{}", path + File.separator + networkName + type + ConstantsContainer.CONNECTION_SEP + ConstantsContainer.JSONRPC_TOML_FILE, e);
        }
        try {
            fileOperation.deleteFile(path + File.separator + networkName + type + ConstantsContainer.CONNECTION_SEP + ConstantsContainer.ADDRESS_FILE);
        } catch (JSchException e) {
            log.error("删除文件{}失败{}", path + File.separator + networkName + type + ConstantsContainer.CONNECTION_SEP + ConstantsContainer.ADDRESS_FILE, e);
        }
        if (flag) {
            deleteFile(ConstantsContainer.USER_HOME_PATH + File.separator + networkName, host, userName, password, sessionId);
        }
    }
}
