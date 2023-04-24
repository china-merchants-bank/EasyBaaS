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

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.cmbchina.baas.easyBaas.client.deployment.PlatformOperation;
import com.cmbchina.baas.easyBaas.config.ImageConfig;
import com.cmbchina.baas.easyBaas.config.UpgradeConfig;
import com.cmbchina.baas.easyBaas.constant.ConstantsContainer;
import com.cmbchina.baas.easyBaas.domain.deployment.ApplicationInfo;
import com.cmbchina.baas.easyBaas.domain.deployment.ImageInfo;
import com.cmbchina.baas.easyBaas.exception.ErrorCodes;
import com.cmbchina.baas.easyBaas.exception.exceptions.CreateApplicationException;
import com.cmbchina.baas.easyBaas.exception.exceptions.NoLoginShellUserException;
import com.cmbchina.baas.easyBaas.mapper.NodeInfoMapper;
import com.cmbchina.baas.easyBaas.mapper.UserMapper;
import com.cmbchina.baas.easyBaas.model.JsonRpcToml;
import com.cmbchina.baas.easyBaas.model.NetworkToml;
import com.cmbchina.baas.easyBaas.model.NodeInfo;
import com.cmbchina.baas.easyBaas.model.WebConnectionInfo;
import com.cmbchina.baas.easyBaas.response.Response;
import com.cmbchina.baas.easyBaas.service.internal.BringIntoNodeService;
import com.cmbchina.baas.easyBaas.util.connection.SSHConnection;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import com.moandjiezana.toml.Toml;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BringIntoNodeServiceImpl implements BringIntoNodeService {
    @Autowired
    SSHConnection sshConnection;
    @Autowired
    DeploymentNodeServiceImpl deploymentNodeServiceImpl;
    @Autowired
    DeploymentNodeFileServiceImpl deploymentNodeFileService;
    @Autowired
    ImageConfig imageConfig;
    @Autowired
    ErrorOperationClearServiceImpl nodeErrorOperationService;
    @Autowired
    UserMapper userMapper;
    @Autowired
    ShellServiceImpl shellService;
    @Autowired
    UpgradeConfig upgradeConfig;
    @Autowired
    NodeInfoMapper nodeInfoMapper;
    @Autowired
    BringNodeServiceImpl bringNodeService;

    @Override
    public Response managinExternalExistsNode(String nodeAddress, String nodeName, String filePath, String username, String password, HttpSession session)
            throws JSchException, NoLoginShellUserException, Exception {
        checkShellUser(nodeName, username, password, nodeAddress);
        //1.shell登录对象,2.用户名密码校验
        WebConnectionInfo webConnectionInfo = deploymentNodeServiceImpl.getWebConnectionInfo(username, password, nodeAddress, session);
        Map<String, Object> map = new HashMap<>();
        shellService.getCITAChainVersionByRpc(webConnectionInfo, map, session);
        ApplicationInfo originalCon = bringNodeService.stopDockerConAndRename(nodeAddress, nodeName, session);
        //4.读取相应配置
        try {
            webConnectionInfo = shellService.getNetworkPathForUserClient(webConnectionInfo, username, password, session, filePath, map);
            shellService.getConfigFile(webConnectionInfo, session, map, ConstantsContainer.EXTERNAL_TYPE, filePath);
            readConfigFile(webConnectionInfo, session, map, ConstantsContainer.EXTERNAL_TYPE, ConstantsContainer.USER_HOME_PATH);
            deplomentNode(map, nodeAddress, nodeName, webConnectionInfo, session, filePath);
        } catch (JSchException | SftpException e) {
            deleteCon(nodeAddress, nodeName, session);
            bringNodeService.renameCon(originalCon, nodeAddress, session);
            nodeErrorOperationService.deleteNetworkConfigFiles((String) map.get("netWorkName"), nodeAddress, username, password,
                    session.getId(), true, ConstantsContainer.EXTERNAL_TYPE, ConstantsContainer.USER_HOME_PATH);
            throw new Exception(e);
        }
        String monitorPath = filePath.substring(0, filePath.lastIndexOf(File.separator));
        try {
            deploymentNodeServiceImpl.deplomentMonitor(nodeAddress, nodeName, (String) map.get("netWorkName"), (String) map.get("nodeId"), session, monitorPath);
        } catch (Exception e) {
            log.error("监控组件部署异常{}", e.getMessage());
        }
        return deleteFilesAndReture(map, session, nodeAddress, username, password, ConstantsContainer.USER_HOME_PATH);
    }

    private void deleteCon(String host, String nodeName, HttpSession session) {
        try {
            PlatformOperation.getDockerJavaPlatformOperation(host)
                    .deleteApplication(ApplicationInfo.builder().applicationName(File.separator + nodeName).build());
        } catch (Exception e) {
            log.error("用户{}删除创建的新容器失败", session.getId());
        }
    }

    private void checkShellUser(String nodeName, String userName, String password, String nodeAddress) throws Exception {
        if (StrUtil.isBlank(userName) && StrUtil.isBlank(password)) {
            if (null == userMapper.selectByTypeAndAddress(ConstantsContainer.USER_TYPE_SHELL, nodeAddress)) {
                throw new NoLoginShellUserException(ErrorCodes.NO_LOGIN_SHELL_USER_ERROR.getMessage());
            }
        }
        List<NodeInfo> nodeInfos = nodeInfoMapper.selectByHostAddress(nodeAddress);
        if (CollectionUtil.isNotEmpty(nodeInfos)) {
            throw new Exception("机器已部署节点");
        }
        deploymentNodeServiceImpl.check(nodeName, null, nodeAddress);
    }

    public void readConfigFile(WebConnectionInfo webConnectionInfo, HttpSession session, Map<String, Object> map, String type, String path) throws NoLoginShellUserException {
        log.info("用户{},获取读取配置文件内容", session.getId());
        try {
            File networkFile = new File(path + File.separator, (String) map.get("netWorkName") + type + ConstantsContainer.CONNECTION_SEP + ConstantsContainer.NETWORK_TOML_FILE);
            Toml networkTomlFile = new Toml().read(networkFile);
            NetworkToml networkToml = networkTomlFile.to(NetworkToml.class);
            File jsonRpcFile = new File(path + File.separator, (String) map.get("netWorkName") + type + ConstantsContainer.CONNECTION_SEP + ConstantsContainer.JSONRPC_TOML_FILE);
            Toml jsonRpcFileTomFile = new Toml().read(jsonRpcFile);
            JsonRpcToml jsonRpcToml = jsonRpcFileTomFile.to(JsonRpcToml.class);
            map.put("networkToml", networkToml);
            map.put("jsonRpcToml", jsonRpcToml);
            try (FileReader fileReader = new FileReader(path + File.separator + (String) map.get("netWorkName") + type + ConstantsContainer.CONNECTION_SEP + ConstantsContainer.ADDRESS_FILE);
                 BufferedReader bufferedReader = new BufferedReader(fileReader);) {
                String line = null;
                while (null != (line = bufferedReader.readLine())) {
                    map.put("address", line.replaceAll("\r\n", ""));
                }
            }
        } catch (IOException e) {
            log.info("用户{},获取读取配置文件内容异常{}", session.getId(), e);
            throw new NoLoginShellUserException("登录服务器失败或读取配置文件异常");
        }
    }

    private void deplomentNode(Map<String, Object> map, String nodeAddress, String nodeName, WebConnectionInfo webConnectionInfo,
                               HttpSession session, String filePath) throws JSchException, SftpException, Exception {
        log.info("用户{},进行部署", session.getId());
        String[] command = deploymentNodeServiceImpl.getContainerStartCommand((String) map.get("nodeFolder"), session);
        log.info("命令：" + Arrays.asList(command).stream().filter(string -> !string.isEmpty()).collect(Collectors.joining(" ")));
        ImageInfo imageInfo = null;
        log.info("{}用户节点版本为{}", session.getId(), ((String) map.get("version")));
        if (ConstantsContainer.VERSION_140.equals(((String) map.get("version")))) {
            imageInfo = ImageInfo.builder().repository(imageConfig.getCITANodeImageRepository())
                    .tag(imageConfig.getCITANodeImageTag()).imageID(imageConfig.getCITANodeImageID()).imageName(imageConfig.getCITANodeImageName()).build();
        } else {
            imageInfo = ImageInfo.builder().repository(upgradeConfig.getCITANodeImageRepository())
                    .tag(upgradeConfig.getCITANodeImageTag()).imageID(upgradeConfig.getCITANodeImageID()).imageName(upgradeConfig.getCITANodeImageName()).build();
        }
        map.put("imageInfo", imageInfo);
        log.info("id:{},repository:{},tag:{},name:{}", imageInfo.getImageID(), imageInfo.getRepository(), imageInfo.getTag(), imageInfo.getImageName());
        startContainerAndStart(command, nodeAddress, nodeName, map, (NetworkToml) map.get("networkToml"), webConnectionInfo, session, filePath);
        deploymentNodeServiceImpl.insertNodeInfo(webConnectionInfo, nodeName, (String) map.get("address"), nodeAddress,
                (NetworkToml) map.get("networkToml"), (String) map.get("netWorkName"), filePath, (String) map.get("nodeFolder"), session);
    }

    public void startContainerAndStart(String[] command, String nodeAddress, String nodeName, Map<String, Object> map, NetworkToml networkToml,
                                       WebConnectionInfo webConnectionInfo, HttpSession session, String filePath) throws JSchException, CreateApplicationException {
        PlatformOperation platformOperation = PlatformOperation.getDockerJavaPlatformOperation(nodeAddress);

        Map<String, String> bindMounts = new HashMap<>(1);
        bindMounts.put(filePath, ConstantsContainer.CONTAINER_INTERNAL_PATH + File.separator + (String) map.get("netWorkName"));
        Map<Integer, Integer> bindPorts = new HashMap<>(4);
        String port = networkToml.getPort() == null ? networkToml.getTls_port() : networkToml.getPort();
        bindPorts.put(Integer.valueOf(port), Integer.valueOf(port));
        bindPorts.put(Integer.valueOf(((JsonRpcToml) map.get("jsonRpcToml")).getHttp_config().getListen_port()),
                Integer.valueOf(((JsonRpcToml) map.get("jsonRpcToml")).getHttp_config().getListen_port()));
        bindPorts.put(15672, 15672);
        bindPorts.put(5672, 5672);
        Map<String, String> env = new HashMap<>(3);
        env.put("RPC_HTTP_PORT", ((JsonRpcToml) map.get("jsonRpcToml")).getHttp_config().getListen_port());
        env.put("RABBITMQ_LOG_BASE", ConstantsContainer.CONTAINER_INTERNAL_MQLOG_PATH);
        env.put("HOME", ConstantsContainer.CONTAINER_INTERNAL_PATH);
        ApplicationInfo applicationInfo = ApplicationInfo.builder().applicationName(nodeName).command(command).imageName(((ImageInfo) map.get("imageInfo")).getImageName())
                .workDir(ConstantsContainer.CONTAINER_INTERNAL_PATH).bindMounts(bindMounts).bindPorts(bindPorts).env(env).build();
        deploymentNodeServiceImpl.createAndStartApplication(platformOperation, (ImageInfo) map.get("imageInfo"), applicationInfo);
    }

    private Response deleteFilesAndReture(Map<String, Object> map, HttpSession session, String nodeAddress, String username, String password, String path) {
        try {
            nodeErrorOperationService.deleteNetworkConfigFiles((String) map.get("netWorkName"), nodeAddress, username, password, session.getId(), false, ConstantsContainer.EXTERNAL_TYPE, path);
            log.info("用户{},进行临时文件删除成功", session.getId());
        } catch (Exception e) {
            log.error("用户{},进行临时文件删除异常", session.getId());
        }
        return Response.builder().code(ErrorCodes.OK.getCode()).msg(ErrorCodes.OK.getMessage() + ",请视情况清理原始数据").build();
    }

    public void updateNodeInfo(Map<String, Object> map) {
        NodeInfo nodeInfo = (NodeInfo) map.get("nodeInfo");
        nodeInfo.setVersion(upgradeConfig.getCITANodeImageTag());
        nodeInfoMapper.updateByPrimaryKey(nodeInfo);
    }
}