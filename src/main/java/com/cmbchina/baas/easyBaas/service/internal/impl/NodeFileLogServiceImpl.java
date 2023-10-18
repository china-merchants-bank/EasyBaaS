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
import com.cmbchina.baas.easyBaas.constant.ConstantsContainer;
import com.cmbchina.baas.easyBaas.domain.deployment.ApplicationInfo;
import com.cmbchina.baas.easyBaas.exception.ErrorCodes;
import com.cmbchina.baas.easyBaas.exception.exceptions.DecryptException;
import com.cmbchina.baas.easyBaas.exception.exceptions.FindContainerException;
import com.cmbchina.baas.easyBaas.mapper.NodeInfoMapper;
import com.cmbchina.baas.easyBaas.model.NodeInfo;
import com.cmbchina.baas.easyBaas.model.WebConnectionInfo;
import com.cmbchina.baas.easyBaas.response.Response;
import com.cmbchina.baas.easyBaas.service.internal.NodeFileLogService;
import com.cmbchina.baas.easyBaas.util.connection.SSHConnection;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@Service
@Slf4j
public class NodeFileLogServiceImpl implements NodeFileLogService {

    @Autowired
    DeploymentNodeServiceImpl deploymentNodeService;
    @Autowired
    NodeInfoMapper nodeInfoMapper;
    @Autowired
    SSHConnection sshConnection;
    @Autowired
    CitaNodeApiServiceImpl citaNodeApiService;
    @Autowired
    ReStartCitaNodeServiceImpl reStartCitaNodeService;

    @Override
    public Response getNodeLogFileSize(String nodeName, String username, String password, HttpSession session) throws Exception {
        NodeInfo nodeInfo = nodeInfoMapper.selectByNodeName(nodeName);
        if (null == nodeInfo) {
            return Response.builder().code(String.valueOf(ErrorCodes.NODE_NOT_EXIST.getCode())).msg(ErrorCodes.NODE_NOT_EXIST.getMessage()).data(null).build();
        }
        log.info("用户{}找到节点信息{}", session.getId(), nodeName);
        WebConnectionInfo webConnectionInfo = deploymentNodeService.getWebConnectionInfo(username, password, nodeInfo.getHost().split(ConstantsContainer.IP_PORT_SEP)[0], session);
        String result = getNodeLogFileSizeByShell(webConnectionInfo, nodeInfo, session);
        if (null == result) {
            return Response.builder().code(ErrorCodes.GET_LOG_SIZE_FAILED_ERROR.getCode()).msg(ErrorCodes.GET_LOG_SIZE_FAILED_ERROR.getMessage()).build();
        }
        return Response.builder().code(ErrorCodes.OK.getCode()).msg(ErrorCodes.OK.getMessage()).data(result).build();
    }

    @Override
    public Response clearNodeLogFile(String nodeName, String username, String password, String line, HttpSession session) throws Exception {
        NodeInfo nodeInfo = nodeInfoMapper.selectByNodeName(nodeName);
        if (null == nodeInfo) {
            return Response.builder().code(String.valueOf(ErrorCodes.NODE_NOT_EXIST.getCode())).msg(ErrorCodes.NODE_NOT_EXIST.getMessage()).data(null).build();
        }
        log.info("用户{}找到节点信息{}", session.getId(), nodeName);
        //WebConnectionInfo webConnectionInfo = deploymentNodeService.getWebConnectionInfo(username, password, nodeInfo.getHost().split(ConstantsContainer.IP_PORT_SEP)[0], session);
        //cpClearShellTToLogFile(webConnectionInfo, nodeInfo, session);
        PlatformOperation platformOperation = PlatformOperation.getDockerJavaPlatformOperation(nodeInfo.getHost().split(ConstantsContainer.IP_PORT_SEP)[0]);
        List<ApplicationInfo> applicationInfos = platformOperation.listApplications();
        ApplicationInfo runningApplicationInfo = reStartCitaNodeService.getApplicationInfo(applicationInfos, nodeName);
        if (null == runningApplicationInfo) {
            log.error("用户{}找不到对应容器{}", session.getId(), nodeName);
            throw new FindContainerException("找不到对应容器");
        }
        String path = nodeInfo.getNetworkName() + File.separator + nodeInfo.getNodeFolder().substring(nodeInfo.getNodeFolder().lastIndexOf("/"));
        if ("0".equals(line)) {
            platformOperation.execApplication(runningApplicationInfo, new String[]{"sh", ConstantsContainer.CONTAINER_INTERNAL_PATH + "/ClearCITANodeLogs.sh", path});
            return Response.builder().code(ErrorCodes.OK.getCode()).msg(ErrorCodes.OK.getMessage()).data("日志清理成功").build();
        } else {
            platformOperation.execApplication(runningApplicationInfo, new String[]{"sh", ConstantsContainer.CONTAINER_INTERNAL_PATH + "/BackupCITANodeLogs.sh", line, path});
            platformOperation.execApplication(runningApplicationInfo, new String[]{"sh", ConstantsContainer.CONTAINER_INTERNAL_PATH + "/ClearCITANodeLogs.sh", path});
            String resultPath = "日志备份并清理成功，备份内容位于" + nodeInfo.getNodeFolder() + "/logs文件夹下名字带有" + line + "的日志文件中";
            return Response.builder().code(ErrorCodes.OK.getCode()).msg(ErrorCodes.OK.getMessage()).data(resultPath).build();
        }


    }

    public String getNodeLogFileSizeByShell(WebConnectionInfo webConnectionInfo, NodeInfo nodeInfo, HttpSession session)
            throws DecryptException, NoSuchAlgorithmException, Exception {
        log.info("用户{},查询节点{}logs文件夹日志大小", session.getId(), nodeInfo.getNodeName());
        String nodeFolder = nodeInfo.getNodeFolder();
        String result = null;
        try {
            webConnectionInfo.setChannelType(ConstantsContainer.CHANNEL_TYPE_EXEC);
            ChannelExec channel = (ChannelExec) sshConnection.open(webConnectionInfo, session.getId());
            //df -h /home/userClient/lw65-uat-thinclient2/4/logs | grep "G" | awk '{print $3}' | cut -d 'G' -f1-1
            channel.setCommand("cd " + nodeFolder + "/logs ;du -sh");
            channel.connect();
            try (InputStream inputStream = channel.getInputStream()) {
                result = IOUtil.toString(inputStream, "UTF-8");
            }
            sshConnection.close(session.getId());
        } catch (JSchException | IOException e) {
            log.error("登录服务器失败或查询日志文件夹大小异常,[{}]", e);
            throw new JSchException("登录服务器失败或查询日志文件夹大小异常");
        }
        return result.substring(0, result.length() - 3);
    }

    public void cpClearShellTToLogFile(WebConnectionInfo webConnectionInfo, NodeInfo nodeInfo, HttpSession session)
            throws DecryptException, NoSuchAlgorithmException, Exception {
        log.info("用户{},复制删除节点{}logs文件日志脚本", session.getId(), nodeInfo.getNodeName());
        try {
            webConnectionInfo.setChannelType(ConstantsContainer.CHANNEL_TYPE_SFTP);
            ChannelSftp channel = (ChannelSftp) sshConnection.open(webConnectionInfo, session.getId());
            //String command = getCommand(line,nodeInfo);
            //channel.setCommand("cp /home/userClient/BackupCITANodeLogs.sh /home/userClient/ClearCITANodeLogs.sh  " + nodeInfo.getNodeFolder());
            channel.connect();
            channel.put("/home/userClient/BackupCITANodeLogs.sh", nodeInfo.getNodeFolder() + File.separator + "BackupCITANodeLogs.sh");
            channel.put("/home/userClient/ClearCITANodeLogs.sh", nodeInfo.getNodeFolder() + File.separator + "ClearCITANodeLogs.sh");
            sshConnection.close(session.getId());
            log.info("用户{},删除节点{}logs文件日志完成", session.getId(), nodeInfo.getNodeName());
        } catch (JSchException e) {
            log.error("登录服务器失败或推送清理脚本异常,[{}]", e);
            throw new JSchException("登录服务器失败或推送清理脚本异常");
        }
    }

    private static String getCommand(String line, NodeInfo nodeInfo) {
        return "tail -" + line + " " + nodeInfo.getNodeFolder() + "/logs/cita-auth.log >> " + " " + nodeInfo.getNodeFolder() + "/logs/cita-auth.log.save;"
                + "mv " + nodeInfo.getNodeFolder() + "/logs/cita-auth.log.save " + nodeInfo.getNodeFolder() + "/logs/cita-auth.log;"
                + "tail -" + line + " " + nodeInfo.getNodeFolder() + "/logs/cita-bft.log >> " + " " + nodeInfo.getNodeFolder() + "/logs/cita-bft.log.save;"
                + "mv " + nodeInfo.getNodeFolder() + "/logs/cita-bft.log.save " + nodeInfo.getNodeFolder() + "/logs/cita-bft.log;"
                + "tail -" + line + " " + nodeInfo.getNodeFolder() + "/logs/cita-chain.log >> " + " " + nodeInfo.getNodeFolder() + "/logs/cita-chain.log.save;"
                + "mv " + nodeInfo.getNodeFolder() + "/logs/cita-chain.log.save " + nodeInfo.getNodeFolder() + "/logs/cita-chain.log;"
                + "tail -" + line + " " + nodeInfo.getNodeFolder() + "/logs/cita-executor.log >> " + " " + nodeInfo.getNodeFolder() + "/logs/cita-executor.log.save;"
                + "mv " + nodeInfo.getNodeFolder() + "/logs/cita-executor.log.save " + nodeInfo.getNodeFolder() + "/logs/cita-executor.log;"
                + "tail -" + line + " " + nodeInfo.getNodeFolder() + "/logs/cita-forever.log >> " + " " + nodeInfo.getNodeFolder() + "/logs/cita-forever.log.save;"
                + "mv " + nodeInfo.getNodeFolder() + "/logs/cita-forever.log.save " + nodeInfo.getNodeFolder() + "/logs/cita-forever.log;"
                + "tail -" + line + " " + nodeInfo.getNodeFolder() + "/logs/cita-jsonrpc.log >> " + " " + nodeInfo.getNodeFolder() + "/logs/cita-jsonrpc.log.save;"
                + "mv " + nodeInfo.getNodeFolder() + "/logs/cita-jsonrpc.log.save " + nodeInfo.getNodeFolder() + "/logs/cita-jsonrpc.log;"
                + "tail -" + line + " " + nodeInfo.getNodeFolder() + "/logs/cita-network.log >> " + " " + nodeInfo.getNodeFolder() + "/logs/cita-network.log.save;"
                + "mv " + nodeInfo.getNodeFolder() + "/logs/cita-network.log.save " + nodeInfo.getNodeFolder() + "/logs/cita-network.log;";
    }
}
