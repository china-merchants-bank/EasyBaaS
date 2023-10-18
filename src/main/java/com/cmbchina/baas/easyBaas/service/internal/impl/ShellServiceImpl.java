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

import com.alibaba.fastjson.JSONObject;
import com.cmbchina.baas.easyBaas.constant.ConstantsContainer;
import com.cmbchina.baas.easyBaas.domain.deployment.ImageInfo;
import com.cmbchina.baas.easyBaas.exception.exceptions.DecryptException;
import com.cmbchina.baas.easyBaas.model.WebConnectionInfo;
import com.cmbchina.baas.easyBaas.response.JsonRpcResponse;
import com.cmbchina.baas.easyBaas.util.connection.SSHConnection;
import com.cmbchina.baas.easyBaas.util.secret.EncryptTools;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Vector;

@Service
@Slf4j
public class ShellServiceImpl {

    @Autowired
    SSHConnection sshConnection;
    @Autowired
    DeploymentNodeServiceImpl deploymentNodeServiceImpl;
    @Autowired
    DeploymentNodeFileServiceImpl deploymentNodeFileService;
    @Autowired
    CitaNodeApiServiceImpl citaNodeApiService;
    @Autowired
    UpgradeNodeCheckImpl upgradeNodeCheckImpl;
    @Value("${linux.key}")
    String linuxKey;

    public void setFaclFromUserClientForUser(String userName, String nodeAddress, HttpSession session) throws Exception {
        log.info("用户{},登录服务器进行目录赋权", session.getId());
        WebConnectionInfo webConnectionInfo = WebConnectionInfo.builder().userName(ConstantsContainer.SHELL_USER_NAME)
                .password(EncryptTools.decrypt(linuxKey))
                .port(22).host(nodeAddress).channelType(ConstantsContainer.CHANNEL_TYPE_EXEC).build();
        webConnectionInfo.setChannelType(ConstantsContainer.CHANNEL_TYPE_EXEC);
        ChannelExec channel = (ChannelExec) sshConnection.open(webConnectionInfo, session.getId());
        channel.setCommand("setfacl -m u:" + userName + ":rwx /home/userClient\n");
        channel.connect();
        InputStream inputStream = channel.getInputStream();
        inputStream.close();
        sshConnection.close(session.getId());
    }

    public WebConnectionInfo getNetworkPathForUserClient(WebConnectionInfo webConnectionInfo, String username, String password, HttpSession session, String filePath, Map<String, Object> map)
            throws DecryptException, NoSuchAlgorithmException, Exception {
        log.info("用户{},登录服务器并复制数据", session.getId());
        setFaclFromUserClientForUser(webConnectionInfo.getUserName(), webConnectionInfo.getHost(), session);
        String netWorkName = filePath.substring(filePath.lastIndexOf(File.separator) + 1);
        map.put("netWorkName", netWorkName);
        try {
            webConnectionInfo.setChannelType(ConstantsContainer.CHANNEL_TYPE_EXEC);
            ChannelExec channel = (ChannelExec) sshConnection.open(webConnectionInfo, session.getId());
            /*channel.setCommand("cp -r " + filePath + " /home/userClient && chown -R userClient:userClient /home/userClient/" + netWorkName);*/
            channel.setCommand("chown -R userClient:userClient /home/userClient/" + netWorkName);
            channel.connect();
            InputStream inputStream = channel.getInputStream();
            inputStream.close();
            sshConnection.close(session.getId());
        } catch (JSchException | IOException e) {
            log.error("登录服务器失败或读取配置文件异常{}", e);
            throw new JSchException("登录服务器失败或赋权异常");
        }
        webConnectionInfo = deploymentNodeServiceImpl.getWebConnectionInfo(username, password, webConnectionInfo.getHost(), session);
        return webConnectionInfo;
    }

    public void getConfigFile(WebConnectionInfo webConnectionInfo, HttpSession session, Map<String, Object> map, String type, String path) throws JSchException {
        try {
            webConnectionInfo.setChannelType(ConstantsContainer.CHANNEL_TYPE_SFTP);
            ChannelSftp channel = (ChannelSftp) sshConnection.open(webConnectionInfo, session.getId());
            channel.connect();
            channel.cd(path);
            Vector v = channel.ls("*");
            String nodeFolder = v.get(0).toString().substring(v.get(0).toString().lastIndexOf(" ") + 1);
            map.put("nodeId", nodeFolder);
            channel.cd(path + File.separator + nodeFolder);
            map.put("nodeFolder", path + File.separator + nodeFolder);
            channel.get("jsonrpc.toml", ConstantsContainer.USER_HOME_PATH + File.separator + (String) map.get("netWorkName")
                    + type + ConstantsContainer.CONNECTION_SEP + ConstantsContainer.JSONRPC_TOML_FILE);
            channel.get("network.toml", ConstantsContainer.USER_HOME_PATH + File.separator + (String) map.get("netWorkName")
                    + type + ConstantsContainer.CONNECTION_SEP + ConstantsContainer.NETWORK_TOML_FILE);
            channel.get("address", ConstantsContainer.USER_HOME_PATH + File.separator + (String) map.get("netWorkName") + type + ConstantsContainer.CONNECTION_SEP + ConstantsContainer.ADDRESS_FILE);
            sshConnection.close(session.getId());
        } catch (JSchException | SftpException e) {
            log.error("登录服务器失败或读取配置文件异常{}", e);
            throw new JSchException("登录服务器失败或读取配置文件异常");
        }
    }

    /**
     * 使用jsonrpc获取链的版本
     */
    public void getCITAChainVersionByRpc(WebConnectionInfo webConnectionInfo, Map<String, Object> map, HttpSession session) throws JSchException {
        try {
            log.info("{}开始获取CITA版本", session.getId());
            webConnectionInfo.setChannelType(ConstantsContainer.CHANNEL_TYPE_EXEC);
            ChannelExec channel = (ChannelExec) sshConnection.open(webConnectionInfo, session.getId());
            channel.setCommand(ConstantsContainer.GET_CITA_VERSION_COMMAND);
            channel.connect();
            InputStream inputStream = channel.getInputStream();
            String result = IOUtil.toString(inputStream, "UTF-8");
            JsonRpcResponse jsonRpcResponse = JSONObject.parseObject(result, JsonRpcResponse.class);
            log.info("{}获取CITA版本为{}", session.getId(), jsonRpcResponse.getResult().getSoftwareVersion());
            map.put("version", jsonRpcResponse.getResult().getSoftwareVersion());
            inputStream.close();
            sshConnection.close(session.getId());
            log.info("{}获取CITA版本结束", session.getId());
        } catch (JSchException | IOException | NullPointerException e) {
            log.error("登录服务器失败或获取CITA版本失败{}", e);
            throw new JSchException("登录服务器失败或获取CITA版本失败");
        }
    }

    /**
     * 根据容器获取版本
     */
    public void getCITAChainVersionByCon(String nodeAddress, String nodeName, Map<String, Object> map, HttpSession session) throws Exception {
        ImageInfo imageInfo = upgradeNodeCheckImpl.getCITAImage(nodeAddress, nodeName);
        if (null != imageInfo) {
            log.info("{}获取CITA版本为{}", session.getId(), imageInfo.getTag());
            map.put("version", imageInfo.getTag());
        } else {
            throw new Exception("获取容器对应镜像失败");
        }
    }


}
