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

import cn.hutool.core.collection.CollUtil;
import com.cmbchina.baas.easyBaas.client.deployment.PlatformOperation;
import com.cmbchina.baas.easyBaas.config.ImageConfig;
import com.cmbchina.baas.easyBaas.config.UpgradeConfig;
import com.cmbchina.baas.easyBaas.constant.ConstantsContainer;
import com.cmbchina.baas.easyBaas.domain.deployment.ApplicationInfo;
import com.cmbchina.baas.easyBaas.domain.deployment.ImageInfo;
import com.cmbchina.baas.easyBaas.exception.ErrorCodes;
import com.cmbchina.baas.easyBaas.exception.exceptions.BlockHeightException;
import com.cmbchina.baas.easyBaas.exception.exceptions.CreateApplicationException;
import com.cmbchina.baas.easyBaas.exception.exceptions.FindContainerException;
import com.cmbchina.baas.easyBaas.exception.exceptions.OperatorContainerException;
import com.cmbchina.baas.easyBaas.mapper.NodeInfoMapper;
import com.cmbchina.baas.easyBaas.model.NodeInfo;
import com.cmbchina.baas.easyBaas.response.Response;
import com.cmbchina.baas.easyBaas.service.internal.CitaNodeApiService;
import com.cmbchina.baas.easyBaas.service.internal.NodeLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class NodeLogServiceImpl implements NodeLogService {

    @Autowired
    ReStartCitaNodeServiceImpl reStartCitaNodeService;
    @Autowired
    NodeInfoMapper nodeInfoMapper;
    @Autowired
    ImageConfig imageConfig;
    @Autowired
    UpgradeConfig upgradeConfig;
    @Autowired
    DeploymentNodeServiceImpl deploymentNodeService;
    @Autowired
    CitaNodeApiService citaNodeApiService;

    @Override
    public Response nodeTrace(String nodeName, boolean status, HttpSession session) {
        NodeInfo nodeInfo = nodeInfoMapper.selectByNodeName(nodeName);
        if (null == nodeInfo) {
            return Response.builder().code(String.valueOf(ErrorCodes.NODE_NOT_EXIST.getCode())).msg(ErrorCodes.NODE_NOT_EXIST.getMessage()).data(null).build();
        }
        log.info("用户{}找到节点信息{}", session.getId(), nodeName);
        PlatformOperation platformOperation = PlatformOperation.getDockerJavaPlatformOperation(nodeInfo.getHost().split(ConstantsContainer.IP_PORT_SEP)[0]);
        List<ApplicationInfo> applicationInfos = platformOperation.listApplications();
        ApplicationInfo runningApplicationInfo = reStartCitaNodeService.getApplicationInfo(applicationInfos, nodeName);
        if (null == runningApplicationInfo) {
            log.error("用户{}找不到对应容器{}", session.getId(), nodeName);
            throw new FindContainerException("找不到对应容器");
        }
        String prefix = "";
        if (status) {
            prefix = ConstantsContainer.TRACE_PREFIX_OPEN;
        } else {
            prefix = ConstantsContainer.TRACE_PREFIX_CLOSE;
        }
        return operatorContainer(session, nodeName, platformOperation, runningApplicationInfo, nodeInfo, status, prefix);
    }

    @Override
    public Response statusTrace(String nodeName, HttpSession session) throws Exception {
        Response response = citaNodeApiService.queryLog(nodeName, ConstantsContainer.CHAIN_TYPE, ConstantsContainer.TRACE_LOGS_NUM, session);
        List<String> chainLogs = (List<String>) response.getData();
        if (CollUtil.isEmpty(chainLogs)) {
            throw new BlockHeightException("查询状态失败");
        }
        for (int i = 0; i < chainLogs.size(); i++) {
            if (chainLogs.get(i).contains("TRACE")) {
                return Response.builder().code(ErrorCodes.OK.getCode()).msg(ErrorCodes.OK.getMessage()).data(true).build();
            }
        }
        return Response.builder().code(ErrorCodes.OK.getCode()).msg(ErrorCodes.OK.getMessage()).data(false).build();
    }

    private String[] getContainerStartCommandWithTrace(String netWorkPath, HttpSession session) {
        log.info("用户{},创建开启trace部署命令", session.getId());
        String[] pathNames = netWorkPath.split(File.separator);
        String nodePath = pathNames[pathNames.length - 2] + File.separator + pathNames[pathNames.length - 1];
        String[] command = {"sh", "-c", "cd " + ConstantsContainer.CONTAINER_INTERNAL_PATH + ";if [ -d \"" + nodePath + "/logs\" ];then sleep 10;fi;rm -rf "
                + nodePath + "/.cita-*;./bin/cita bebop setup " + nodePath + ";./bin/cita bebop start " + nodePath + " trace;trap \"./bin/cita bebop stop "
                + nodePath + " exit 143;\" TERM;while true; do sleep 2; done;"};
        return command;
    }

    private ApplicationInfo getCreateApplicationInfo(NodeInfo nodeInfo, ApplicationInfo runningApplicationInfo, HttpSession session, boolean flag) {
        String[] command = {};
        if (flag) {
            command = getContainerStartCommandWithTrace(nodeInfo.getNodeFolder(), session);
        } else {
            command = deploymentNodeService.getContainerStartCommand(nodeInfo.getNodeFolder(), session);
        }
        String imageName = runningApplicationInfo.getImageName();
        Map<String, String> bindMounts = runningApplicationInfo.getBindMounts();
        Map<Integer, Integer> bindPorts = runningApplicationInfo.getBindPorts();
        Map<String, String> env = new HashMap<>(3);
        env.put("RPC_HTTP_PORT", "1337");
        env.put("RABBITMQ_LOG_BASE", ConstantsContainer.CONTAINER_INTERNAL_MQLOG_PATH);
        env.put("HOME", ConstantsContainer.CONTAINER_INTERNAL_PATH);
        return ApplicationInfo.builder().applicationName(nodeInfo.getNodeName()).command(command).imageName(imageName)
                .workDir(ConstantsContainer.CONTAINER_INTERNAL_PATH).bindMounts(bindMounts).bindPorts(bindPorts).env(env).build();
    }

    /**
     * 容器操作
     *
     * @param session
     * @param nodeName
     * @param platformOperation
     * @param runningApplicationInfo
     * @param nodeInfo
     * @return
     */
    private Response operatorContainer(HttpSession session, String nodeName, PlatformOperation platformOperation, ApplicationInfo runningApplicationInfo
            , NodeInfo nodeInfo, boolean status, String prefix) {
        if (!platformOperation.stopApplication(runningApplicationInfo)) {
            log.error("用户{}停止容器失败{}", session.getId(), nodeName);
            throw new OperatorContainerException("停止容器失败");
        }
        if (!platformOperation.rename(runningApplicationInfo, prefix + nodeName)) {
            log.error("用户{}重命名容器失败{}", session.getId(), nodeName);
            throw new OperatorContainerException("重命名容器失败");
        }
        ImageInfo imageInfo = ImageInfo.builder().repository(upgradeConfig.getCITANodeImageRepository()).tag(upgradeConfig.getCITANodeImageTag()).imageID(upgradeConfig.getCITANodeImageID()).build();
        log.info("用户{}创建并操作trace日志的容器{}", session.getId(), nodeName);
        ApplicationInfo createApplication = getCreateApplicationInfo(nodeInfo, runningApplicationInfo, session, status);
        try {
            deploymentNodeService.createAndStartApplication(platformOperation, imageInfo, createApplication);
        } catch (CreateApplicationException e) {
            log.error("用户{}创建操作trace日志的容器{}失败", session.getId(), nodeName);
            rollbackContainer(platformOperation, runningApplicationInfo, createApplication, nodeInfo, session);
            throw new CreateApplicationException("重命名容器失败");
        }
        runningApplicationInfo.setApplicationName(prefix + nodeName);
        platformOperation.deleteApplication(runningApplicationInfo);
        return Response.builder().code(ErrorCodes.OK.getCode()).msg(ErrorCodes.OK.getMessage()).build();
    }

    private void rollbackContainer(PlatformOperation platformOperation, ApplicationInfo runningApplicationInfo, ApplicationInfo createApplication, NodeInfo nodeInfo, HttpSession session) {
        try {
            platformOperation.deleteApplication(createApplication);
            platformOperation.rename(runningApplicationInfo, nodeInfo.getNodeName());
            platformOperation.startApplication(runningApplicationInfo);
        } catch (Exception e) {
            log.error("用户{}trace回滚失败{}", session.getId(), nodeInfo.getNodeName());
        }

    }
}
