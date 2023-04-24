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
 
package com.cmbchina.baas.easyBaas.config;

import com.cmbchina.baas.easyBaas.constant.ConstantsContainer;
import com.cmbchina.baas.easyBaas.mapper.NodeInfoMapper;
import com.cmbchina.baas.easyBaas.mapper.TableMapper;
import com.cmbchina.baas.easyBaas.model.NodeInfo;
import com.cmbchina.baas.easyBaas.model.WebConnectionInfo;
import com.cmbchina.baas.easyBaas.service.internal.impl.DeploymentNodeFileServiceImpl;
import com.cmbchina.baas.easyBaas.service.internal.impl.DeploymentNodeServiceImpl;
import com.cmbchina.baas.easyBaas.util.connection.SSHConnection;
import com.cmbchina.baas.easyBaas.util.secret.EncryptTools;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.util.List;
import java.util.UUID;
import java.util.Vector;

@Slf4j
@Configuration
public class StartupConfig {

    @Autowired
    TableMapper tableMapper;
    @Autowired
    DeploymentNodeServiceImpl deploymentNodeService;
    @Autowired
    NodeInfoMapper nodeInfoMapper;
    @Autowired
    DeploymentNodeFileServiceImpl deploymentNodeFileService;
    @Autowired
    SSHConnection sshConnection;
    @Value("${aes.key}")
    private String algorithmCode;


    @PostConstruct
    public void startUp() {

        String result = tableMapper.queryNodeInfoTable();
        if ("1".equals(result)) {
            try {
                //证明已经有表了，需要进行表修改
                tableMapper.alterTableNodeInfoAddColumnNetworkPath();
                tableMapper.alterTableNodeInfoAddColumnNodeFolder();
                //数据补充
                List<NodeInfo> list = nodeInfoMapper.selectAllNodeInfo();
                if (list.size() > 0) {
                    //证明有数据，是以前部署过的节点
                    updateNodeInfo(list);
                }
            } catch (Exception e) {
                log.warn("扩容字段异常 [{}]", e.getMessage());
            }
        } else {
            try {
                tableMapper.createAlerterTable();
                tableMapper.createMonitorInfoTable();
                tableMapper.createNodeInfoTable();
                tableMapper.createUserTable();
                tableMapper.createSMTPInfoTable();
                tableMapper.insertUserTableWithClientUser();
                //tableMapper.insertUserTableWithSheelUser();
            } catch (Exception e) {
                log.warn("init sqlite db error [{}]", e.getMessage());
            }
        }

        EncryptTools.algorithmCode = algorithmCode;
    }


    private void updateNodeInfo(List<NodeInfo> list) {
        for (int i = 0; i < list.size(); i++) {
            WebConnectionInfo webConnectionInfo = deploymentNodeService.getWebConnectionInfo(null, null,
                    list.get(i).getHost().split(ConstantsContainer.IP_PORT_SEP)[0], null);
            String networkPath = deploymentNodeFileService.getMaxSpacePath(webConnectionInfo, null);
            networkPath = networkPath + File.separator + list.get(i).getNetworkName();
            String nodeFolder = "";
            try {
                nodeFolder = getConfigFile(webConnectionInfo, null, networkPath);
            } catch (JSchException e) {
                log.error("初始化目录失败{}", e.getMessage());
                return;
            }
            list.get(i).setNetworkPath(networkPath);
            list.get(i).setNodeFolder(nodeFolder);
            nodeInfoMapper.updateByPrimaryKey(list.get(i));
        }
    }

    private String getConfigFile(WebConnectionInfo webConnectionInfo, HttpSession session, String path) throws JSchException {
        String nodeFolder = null;
        String id = UUID.randomUUID().toString().replaceAll("-", "");
        try {
            webConnectionInfo.setChannelType(ConstantsContainer.CHANNEL_TYPE_SFTP);
            ChannelSftp channel = (ChannelSftp) sshConnection.open(webConnectionInfo, session == null ? id : session.getId());
            channel.connect();
            channel.cd(path);
            Vector v = channel.ls("*");
            nodeFolder = v.get(0).toString().substring(v.get(0).toString().lastIndexOf(" ") + 1);
            channel.cd(path + File.separator + nodeFolder);
            nodeFolder = channel.pwd();
            sshConnection.close(session == null ? id : session.getId());
        } catch (JSchException | SftpException e) {
            log.error("登录服务器失败或读取配置文件异常{}", e);
            throw new JSchException("登录服务器失败或读取配置文件异常");
        }
        return nodeFolder;
    }
}
