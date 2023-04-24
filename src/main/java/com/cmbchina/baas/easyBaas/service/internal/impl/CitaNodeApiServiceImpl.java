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

import cn.hutool.core.util.StrUtil;
import com.cmbchina.baas.easyBaas.client.deployment.PlatformOperation;
import com.cmbchina.baas.easyBaas.config.ImageConfig;
import com.cmbchina.baas.easyBaas.config.UpgradeConfig;
import com.cmbchina.baas.easyBaas.constant.ConstantsContainer;
import com.cmbchina.baas.easyBaas.constant.DeployStatusEnum;
import com.cmbchina.baas.easyBaas.domain.deployment.ApplicationInfo;
import com.cmbchina.baas.easyBaas.exception.ErrorCodes;
import com.cmbchina.baas.easyBaas.mapper.MonitorInfoMapper;
import com.cmbchina.baas.easyBaas.mapper.NodeInfoMapper;
import com.cmbchina.baas.easyBaas.mapper.UserMapper;
import com.cmbchina.baas.easyBaas.model.NodeInfo;
import com.cmbchina.baas.easyBaas.model.WebConnectionInfo;
import com.cmbchina.baas.easyBaas.response.QueryAvailableMachinesResponse;
import com.cmbchina.baas.easyBaas.response.Response;
import com.cmbchina.baas.easyBaas.service.internal.CitaNodeApiService;
import com.cmbchina.baas.easyBaas.util.connection.SSHConnection;
import com.cmbchina.baas.easyBaas.util.shell.ShellUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class CitaNodeApiServiceImpl implements CitaNodeApiService {
    @Autowired
    ImageConfig imageConfig;
    @Autowired
    UpgradeConfig upgradeConfig;
    @Autowired
    NodeInfoMapper nodeInfoMapper;
    @Autowired
    ErrorOperationClearServiceImpl deploymentNodeErrorOperationServicel;
    @Autowired
    MonitorInfoMapper monitorInfoMapper;
    @Autowired
    ErrorOperationClearServiceImpl errorOperationClearServiceImpl;
    @Autowired
    UserMapper userMapper;
    @Autowired
    SSHConnection sshConnection;
    @Value("${linux.key}")
    String linuxKey;

    @Override
    public Response queryLog(String nodeName, String logType, int num, HttpSession session) throws Exception {
        NodeInfo nodeInfo = nodeInfoMapper.selectByNodeName(nodeName);
        List<ApplicationInfo> applicationInfos =
                PlatformOperation.getDockerJavaPlatformOperation(nodeInfo.getHost().split(ConstantsContainer.IP_PORT_SEP)[0]).listApplications();
        Optional<ApplicationInfo> applicationInfo = applicationInfos.stream().filter(applicationInfo1 -> {
            return applicationInfo1.getApplicationName().contains(nodeName)
                    && (applicationInfo1.getImageId().contains(imageConfig.getCITANodeImageID()) || applicationInfo1.getImageId()
                    .contains(upgradeConfig.getCITANodeImageID()));
        }).findFirst();
        if (!applicationInfo.isPresent()) {
            log.info("找不到容器信息");
            return Response.builder().code(String.valueOf(ErrorCodes.QUERY_LOG_FAILED_ERROR.getCode()))
                    .msg(ErrorCodes.QUERY_LOG_FAILED_ERROR.getMessage()).build();
        }
        log.info("成功找到容器：{}", applicationInfo.get().getApplicationName());
        String[] cmd = new String[]{"ls", ConstantsContainer.CONTAINER_INTERNAL_PATH + "/" + nodeInfo.getNetworkName()};
        String dir = PlatformOperation.getDockerJavaPlatformOperation(nodeInfo.getHost().split(ConstantsContainer.IP_PORT_SEP)[0])
                .execApplication(applicationInfo.get(), cmd);
        cmd = new String[]{"tail", "-n", String.valueOf(num), ConstantsContainer.CONTAINER_INTERNAL_PATH + "/" + nodeInfo.getNetworkName() + "/"
                + dir.replace("\r\n", "") + "/" + "logs" + "/" + "cita-" + logType + ".log"};
        //log.info("查询容器日志命令为：{}", );
        String result = PlatformOperation.getDockerJavaPlatformOperation(nodeInfo.getHost().split(ConstantsContainer.IP_PORT_SEP)[0])
                .execApplication(applicationInfo.get(), cmd);
        return Response.builder().code(String.valueOf(ErrorCodes.OK.getCode())).msg(ErrorCodes.OK.getMessage())
                .data(Arrays.asList(result.split("\r\n"))).build();
    }

    @Override
    public Response startNodeInfo(String nodeName, HttpSession session) throws Exception {
        NodeInfo nodeInfo = nodeInfoMapper.selectByNodeName(nodeName);
        if (null == nodeInfo) {
            return Response.builder().code(String.valueOf(ErrorCodes.NODE_NOT_EXIST.getCode())).msg(ErrorCodes.NODE_NOT_EXIST.getMessage()).data(null)
                    .build();
        }
        WebConnectionInfo webConnectionInfo = ShellUtils.getWebConnectionInfo(null, null, nodeInfo.getHost().split(ConstantsContainer.IP_PORT_SEP)[0],
                userMapper, linuxKey);
        String command = "/home/userClient/shell/StartNode.sh" + " " + nodeName;
        String result = ShellUtils.invokeShell(webConnectionInfo, sshConnection, session, command);
        if (result.contains(ErrorCodes.OK.getCode())) {
            return Response.builder().code(ErrorCodes.OK.getCode()).msg(ErrorCodes.OK.getMessage()).build();
        } else {
            return Response.builder().code(String.valueOf(ErrorCodes.NODE_START_FAILED_ERROR.getCode()))
                    .msg(ErrorCodes.NODE_START_FAILED_ERROR.getMessage()).data(null).build();
        }
    }

    @Override
    public Response stopNodeInfo(String nodeName, HttpSession session) throws Exception {
        NodeInfo nodeInfo = nodeInfoMapper.selectByNodeName(nodeName);
        if (null == nodeInfo) {
            return Response.builder().code(String.valueOf(ErrorCodes.NODE_NOT_EXIST.getCode())).msg(ErrorCodes.NODE_NOT_EXIST.getMessage()).data(null)
                    .build();
        }
        WebConnectionInfo webConnectionInfo = ShellUtils.getWebConnectionInfo(null, null, nodeInfo.getHost().split(ConstantsContainer.IP_PORT_SEP)[0],
                userMapper, linuxKey);
        String command = "/home/userClient/shell/StopNode.sh" + " " + nodeName;
        String result = ShellUtils.invokeShell(webConnectionInfo, sshConnection, session, command);
        if (result.contains(ErrorCodes.OK.getCode())) {
            return Response.builder().code(ErrorCodes.OK.getCode()).msg(ErrorCodes.OK.getMessage()).build();
        } else {
            return Response.builder().code(String.valueOf(ErrorCodes.NODE_STOP_FAILED_ERROR.getCode()))
                    .msg(ErrorCodes.NODE_STOP_FAILED_ERROR.getMessage()).data(null).build();
        }
    }

    @Override
    public Response deleteNode(String nodeName, HttpSession session, String userName, String password) {
        NodeInfo nodeInfo = nodeInfoMapper.selectByNodeName(nodeName);
        if (null == nodeInfo) {
            return Response.builder().code(String.valueOf(ErrorCodes.NODE_NOT_EXIST.getCode())).msg(ErrorCodes.NODE_NOT_EXIST.getMessage()).data(null)
                    .build();
        }
        List<ApplicationInfo> applicationInfos =
                PlatformOperation.getDockerJavaPlatformOperation(nodeInfo.getHost().split(ConstantsContainer.IP_PORT_SEP)[0]).listApplications();
        Object flag = getNodeStatusThenQueryInfoOrOperateNode(nodeInfo, applicationInfos, ConstantsContainer.NODE_OPERATE_DELETE, session);
        boolean flagBool = (flag == null ? false : (boolean) flag);
        log.info("用户{}停止节点{}完成，停止结果{},开始删除节点", session.getId(), nodeName, flagBool);
        boolean deleteResult = deleteNodeInfo(nodeInfo, nodeName, session, userName, password);
        log.info("用户{}删除节点{}结果为{}", session.getId(), nodeName, deleteResult);
        errorOperationClearServiceImpl.deleteMonitors(nodeInfo, nodeName, session);
        if (deleteResult == true) {
            return Response.builder().code(String.valueOf(ErrorCodes.OK.getCode())).msg(ErrorCodes.OK.getMessage()).build();
        } else {
            return Response.builder().code(String.valueOf(ErrorCodes.NODE_DELETE_FAILED_ERROR.getCode()))
                    .msg(ErrorCodes.NODE_DELETE_FAILED_ERROR.getMessage()).data(null).build();
        }
    }

    private boolean deleteNodeInfo(NodeInfo nodeInfo, String nodeName, HttpSession session, String userName, String password) {
        nodeInfo.setAddress("");
        nodeInfo.setNodeName("");
        nodeInfo.setDeployStatus(DeployStatusEnum.UNDEPLOYED.getCode());
        nodeInfo.setHost(nodeInfo.getHost().split(ConstantsContainer.IP_PORT_SEP)[0] + ConstantsContainer.IP_PORT_SEP);
        try {
            if (PlatformOperation.getDockerJavaPlatformOperation(nodeInfo.getHost().split(ConstantsContainer.IP_PORT_SEP)[0])
                    .deleteApplication(ApplicationInfo.builder().applicationName(File.separator + nodeName).build())
                    && deploymentNodeErrorOperationServicel.deleteFile(nodeInfo.getNetworkName(),
                    nodeInfo.getHost().split(ConstantsContainer.IP_PORT_SEP)[0], userName, password, session.getId())
                    && nodeInfoMapper.updateByPrimaryKeySelective(nodeInfo) > 0) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            log.error("用户{}删除节点{}异常{}", session.getId(), nodeName, e.getMessage());
            return false;
        }
    }

    //获取节点列表信息
    private void getResponseInfoWithDockerInfosOrNodeInfo(NodeInfo nodeInfo, List<QueryAvailableMachinesResponse> responseList, HttpSession session) {
        if (StrUtil.isBlank(nodeInfo.getNodeName())) {
            responseList.add(QueryAvailableMachinesResponse.builder().citaNodeHost(nodeInfo.getHost()).build());
        } else {
            List<ApplicationInfo> applicationInfos =
                    PlatformOperation.getDockerJavaPlatformOperation(nodeInfo.getHost().split(ConstantsContainer.IP_PORT_SEP)[0]).listApplications();
            log.info("用户{}为容器{}获取到服务器容器信息,共有{}个", session.getId(), nodeInfo.getNodeName(), applicationInfos.size());
            if (applicationInfos.size() > 0) {
                QueryAvailableMachinesResponse queryAvailableMachinesResponse =
                        (QueryAvailableMachinesResponse) getNodeStatusThenQueryInfoOrOperateNode(nodeInfo, applicationInfos,
                                ConstantsContainer.NODE_STATUS, session);
                responseList.add(queryAvailableMachinesResponse);
            }
        }
    }

    //此方法有大部分会在查询节点列表，节点启停的功能中使用，因此增加了一个type参数处理不同场景的功能
    public Object getNodeStatusThenQueryInfoOrOperateNode(NodeInfo nodeInfo, List<ApplicationInfo> applicationInfos, String type,
                                                          HttpSession session) {
        ApplicationInfo applicationInfo = null;
        Object result = null;
        for (int i = 0; i < applicationInfos.size(); i++) {
            applicationInfo = applicationInfos.get(i);
            if (applicationInfo.getApplicationName().equals(File.separator + nodeInfo.getNodeName())) {
                log.info("用户{}获取到名字为{}的容器,容器状态为:{}", session.getId(), nodeInfo.getNodeName(), applicationInfo.getStatus());
                if (applicationInfo.getStatus().contains("Up")) {
                    result = getInfoOrStopNodeWithUpNode(type, applicationInfo, nodeInfo, session);
                } else {
                    result = getInfoOrStartNodeWithStopNode(type, applicationInfo, nodeInfo, session);
                }
            }
        }
        return result;
    }

    //启动的节点，构建查询信息或者进行停止
    private Object getInfoOrStopNodeWithUpNode(String type, ApplicationInfo applicationInfo, NodeInfo nodeInfo, HttpSession session) {
        log.info("用户{}此次为容器{}执行操作类型为{}", session.getId(), nodeInfo.getNodeName(), type);
        Object object = null;
        if (ConstantsContainer.NODE_STATUS.equals(type)) {
            object = QueryAvailableMachinesResponse.builder().citaNodeName(nodeInfo.getNodeName()).citaNodeHost(nodeInfo.getHost())
                    .runStatus("running").build();
        } else if (ConstantsContainer.NODE_OPERATE_STOP.equals(type) || ConstantsContainer.NODE_OPERATE_DELETE.equals(type)) {
            applicationInfo.setApplicationName(applicationInfo.getApplicationName());
            object = PlatformOperation.getDockerJavaPlatformOperation(nodeInfo.getHost().split(ConstantsContainer.IP_PORT_SEP)[0])
                    .stopApplication(applicationInfo);
        }
        return object;
    }

    //停止的节点，构建查询信息或者进行启动
    private Object getInfoOrStartNodeWithStopNode(String type, ApplicationInfo applicationInfo, NodeInfo nodeInfo, HttpSession session) {
        log.info("用户{}此次为容器{}执行操作类型为{}", session.getId(), nodeInfo.getNodeName(), type);
        Object object = null;
        if (ConstantsContainer.NODE_STATUS.equals(type)) {
            object = QueryAvailableMachinesResponse.builder().citaNodeName(nodeInfo.getNodeName()).citaNodeHost(nodeInfo.getHost())
                    .runStatus("stopping").build();
        } else if (ConstantsContainer.NODE_OPERATE_START.equals(type)) {
            object = PlatformOperation.getDockerJavaPlatformOperation(nodeInfo.getHost().split(ConstantsContainer.IP_PORT_SEP)[0])
                    .startApplication(applicationInfo);
        } else if (ConstantsContainer.NODE_OPERATE_DELETE.equals(type)) {
            object = true;
        }
        return object;
    }
}