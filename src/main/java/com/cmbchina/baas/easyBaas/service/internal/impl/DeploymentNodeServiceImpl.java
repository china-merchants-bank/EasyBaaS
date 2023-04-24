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
import com.cmbchina.baas.easyBaas.constant.ConstantsContainer;
import com.cmbchina.baas.easyBaas.constant.DeployStatusEnum;
import com.cmbchina.baas.easyBaas.domain.deployment.ApplicationInfo;
import com.cmbchina.baas.easyBaas.domain.deployment.ImageInfo;
import com.cmbchina.baas.easyBaas.exception.ErrorCodes;
import com.cmbchina.baas.easyBaas.exception.exceptions.CreateApplicationException;
import com.cmbchina.baas.easyBaas.exception.exceptions.DecryptException;
import com.cmbchina.baas.easyBaas.exception.exceptions.LoginShellException;
import com.cmbchina.baas.easyBaas.exception.exceptions.NetworkTelnetException;
import com.cmbchina.baas.easyBaas.exception.exceptions.NodeAddressException;
import com.cmbchina.baas.easyBaas.exception.exceptions.NodeNameException;
import com.cmbchina.baas.easyBaas.exception.exceptions.PrivateKeyOrAddressException;
import com.cmbchina.baas.easyBaas.exception.exceptions.UnZipException;
import com.cmbchina.baas.easyBaas.mapper.MonitorInfoMapper;
import com.cmbchina.baas.easyBaas.mapper.NodeInfoMapper;
import com.cmbchina.baas.easyBaas.mapper.UserMapper;
import com.cmbchina.baas.easyBaas.model.MonitorInfo;
import com.cmbchina.baas.easyBaas.model.NetworkToml;
import com.cmbchina.baas.easyBaas.model.NodeInfo;
import com.cmbchina.baas.easyBaas.model.User;
import com.cmbchina.baas.easyBaas.model.WebConnectionInfo;
import com.cmbchina.baas.easyBaas.response.Response;
import com.cmbchina.baas.easyBaas.service.internal.DeploymentNodeService;
import com.cmbchina.baas.easyBaas.service.internal.MonitorConfigService;
import com.cmbchina.baas.easyBaas.service.internal.RabbitMQExporterService;
import com.cmbchina.baas.easyBaas.service.internal.MonitorDeployService;
import com.cmbchina.baas.easyBaas.util.connection.SSHConnection;
import com.cmbchina.baas.easyBaas.util.file.FileOperation;
import com.cmbchina.baas.easyBaas.util.file.LocalFileOperation;
import com.cmbchina.baas.easyBaas.util.file.RemoteFileOperation;
import com.cmbchina.baas.easyBaas.util.secret.EncryptTools;
import com.cmbchina.baas.easyBaas.util.socket.SocketUtil;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSchException;
import javax.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.SocketException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class DeploymentNodeServiceImpl implements DeploymentNodeService {

    private FileOperation localFileOperation = new LocalFileOperation();

    @Autowired
    NodeInfoMapper nodeInfoMapper;

    @Autowired
    SSHConnection sshConnection;

    @Autowired
    ImageConfig imageConfig;

    @Autowired
    DeploymentNodeFileServiceImpl deploymentNodeFileService;

    @Autowired
    ErrorOperationClearServiceImpl deploymentNodeErrorOperationService;

    @Autowired
    MonitorDeployService monitorDeployService;

    @Autowired
    UserMapper userMapper;

    @Autowired
    MonitorConfigService monitorConfigService;

    @Autowired
    RabbitMQExporterService rabbitMQExporterService;

    @Autowired
    MonitorInfoMapper monitorInfoMapper;

    @Autowired
    DeploymentNodeFileServiceImpl deploymentNodeFileServiceImpl;

    @Value("${linux.key}")
    String linuxKey;

    @Override
    public Response deploymentNode(MultipartFile nodeConfigFile, String privateKey, String address, String nodeAddress, String nodeName, String username, String password,
                                   HttpSession session) throws NodeNameException, NodeAddressException, PrivateKeyOrAddressException, LoginShellException, Exception {
        check(nodeName, privateKey, nodeAddress);
        checkPrivateKeyAndAddress(privateKey, address);
        WebConnectionInfo webConnectionInfo = getWebConnectionInfo(username, password, nodeAddress, session);
        checkSftpUser(webConnectionInfo, session);
        //1-6步
        Map<String, Object> map = operationBeforeUploadFile(nodeConfigFile, nodeAddress, webConnectionInfo, privateKey, address, session);
        // 7.上传文件到节点机器地址
        String putFilePath = null;
        try {
            putFilePath = deploymentNodeFileService.sftpPutFileToServer((File) map.get("packFilePath"), webConnectionInfo, session);
        } catch (JSchException e) {
            deploymentNodeErrorOperationService.deleteFolder();
            throw new LoginShellException("登录服务器异常");
        } catch (Exception e) {
            log.error("上传文件到节点机器异常{}", e);
            deploymentNodeErrorOperationService.deleteFolder();
            throw new Exception("上传文件到节点机器异常");
        }
        moveFileAndOperationContainer(webConnectionInfo, session, putFilePath, map, nodeAddress, nodeName, address);
        return Response.builder().code(String.valueOf(ErrorCodes.OK.getCode())).msg(ErrorCodes.OK.getMessage()).data(null).build();
    }

    // 8.移动文件到指定目录解压文件然后创建并启动
    private void moveFileAndOperationContainer(WebConnectionInfo webConnectionInfo, HttpSession session, String putFilePath,
                                               Map<String, Object> map, String nodeAddress, String nodeName, String address) throws LoginShellException, IOException, Exception {
        log.info("用户{},上传压缩包到远程服务器完成开始解压", session.getId());
        webConnectionInfo.setChannelType(ConstantsContainer.CHANNEL_TYPE_EXEC);
        RemoteFileOperation remoteFileOperation = new RemoteFileOperation(webConnectionInfo, session.getId(), sshConnection);
        try {
            remoteFileOperation.unTarGzFile(putFilePath, deploymentNodeFileService.getMaxSpacePath(webConnectionInfo, session));
        } catch (IOException e) {
            log.error("移动并解压文件异常{}", e);
            remoteFileOperation.deleteFolder(putFilePath);
            throw new IOException("移动并解压文件异常");
        }
        remoteFileOperation.deleteFolder(putFilePath);
        try {
            getCommandAndBuildContainer(map, nodeAddress, nodeName, webConnectionInfo, session, address);
        } catch (Exception e) {
            log.error("创建应用并启动应用异常{}", e);
            throw new Exception("创建应用并启动应用异常");
        }
        log.info("用户{},创建应用并启动完成", session.getId());
    }

    private void getCommandAndBuildContainer(Map<String, Object> map, String nodeAddress, String nodeName, WebConnectionInfo webConnectionInfo, HttpSession session,
                                             String address) throws Exception {
        // 10.拼装应用参数
        String[] startCommand = getContainerStartCommand(((File) map.get("nodeFolder")).getAbsolutePath(), session);
        // 11.创建应用并启动
        try {
            startContainerAndStart((String) map.get("netWork"), startCommand, nodeAddress, nodeName, (NetworkToml) map.get("networkToml"), webConnectionInfo, session);
        } catch (Exception e) {
            log.error("创建应用或启动应用异常{}", e);
            throw new Exception("创建应用或启动应用异常");
        }
        String networkPath = deploymentNodeFileService.getMaxSpacePath(webConnectionInfo, session) + File.separator + (String) map.get("networkName");
        String node = networkPath + (String) map.get("nodeIdNum");
        // 12.数据库更新状态
        insertNodeInfo(webConnectionInfo, nodeName, address, nodeAddress, (NetworkToml) map.get("networkToml"), (String) map.get("networkName"), networkPath, node, session);
        //13.部署5个监控
        try {
            deplomentMonitor(nodeAddress, nodeName, (String) map.get("netWork"), ((File) map.get("nodeFolder")).getName(),
                    session, deploymentNodeFileService.getMaxSpacePath(webConnectionInfo, session));
            log.info("用户{},监控组件全部部署完成", session.getId());
        } catch (Exception e) {
            log.error("监控组件部署异常{}", e.getMessage());
        }
    }

    private Map<String, Object> operationBeforeUploadFile(MultipartFile nodeConfigFile, String nodeAddress, WebConnectionInfo webConnectionInfo,
                                                          String privateKey, String address, HttpSession session) throws IOException, Exception {
        Map<String, Object> map = new HashMap<>();
        log.info("用户{},查询数据库完成", session.getId());
        // 2.接收文件
        String saveFilePath = ConstantsContainer.USER_HOME_PATH + ConstantsContainer.UPLOAD_FILE_PATH + ConstantsContainer.UPLOAD_ORIGINALFILE_PATH;
        String originalFilePath = deploymentNodeFileService.getUploadFile(saveFilePath, nodeConfigFile);
        map.put("networkName", deploymentNodeFileServiceImpl.getNetWorkName(originalFilePath));
        log.info("用户{},接收文件完成", session.getId());
        // 3.判断文件类型（zip或者tar.gz）文件解压缩
        try {
            deploymentNodeFileService.unZipOrTarGzFile(saveFilePath, originalFilePath);
        } catch (UnZipException e) {
            log.error("解压文化异常或文件类型不匹配{}", e);
            deploymentNodeErrorOperationService.deleteFolder();
            throw new UnZipException("解压文件异常或文件类型不匹配");
        }
        log.info("用户{},判断文件并解压完成", session.getId());
        operationAfterUploadFile(saveFilePath, nodeAddress, webConnectionInfo, session, privateKey, address, map);
        return map;
    }

    private void operationAfterUploadFile(String saveFilePath, String nodeAddress, WebConnectionInfo webConnectionInfo, HttpSession session, String privateKey,
                                          String address, Map<String, Object> map) throws NetworkTelnetException, LoginShellException, IOException, Exception {
        // 4.读取network.toml文件中其他节点的IP地址和端口，登录节点机器地址，检测连通性,记录路径：/home/thinclient/original 和 /home/thinclient/original/lw65-st-ly-0421/3
        File file = new File(saveFilePath);
        String netWork = file.listFiles()[0].getName();
        map.put("netWork", netWork);
        File nodeFolder = file.listFiles()[0].listFiles()[0];
        map.put("nodeFolder", nodeFolder);
        map.put("nodeIdNum", nodeFolder.getAbsolutePath().substring(nodeFolder.getAbsolutePath().lastIndexOf(File.separator)));
        NetworkToml networkToml = null;
        try {
            networkToml = deploymentNodeFileService.readFileContentAndTestNetworkConnectivity(nodeFolder, webConnectionInfo, session);
            map.put("networkToml", networkToml);
        } catch (Exception e) {
            log.error("获取配置并检查网络异常{}", e);
            deploymentNodeErrorOperationService.deleteFolder();
            throw new Exception("获取配置并检查网络异常");
        }
        log.info("用户{},读取文件并判断网络连通性完成", session.getId());
        replaceAddressAndPrivatekeyAndPacking(nodeFolder, privateKey, address, map, file, saveFilePath, session);
    }

    private void replaceAddressAndPrivatekeyAndPacking(File nodeFolder, String privateKey, String address, Map<String, Object> map, File file,
                                                       String saveFilePath, HttpSession session) throws IOException {
        // 5.根据文件路径找到对应的目录，将address和privatekey文件替换为用户填入的数据
        try {
            deploymentNodeFileService.replaceAddressAndPrivatekey(nodeFolder, privateKey, address);
        } catch (IOException e) {
            log.error("替换address或privatekey文件异常{}", e);
            deploymentNodeErrorOperationService.deleteFolder();
            throw new IOException("替换文件内容异常");
        }
        log.info("用户{},替换文件数据完成", session.getId());
        // 6.文件重新打包
        File packFilePath = null;
        try {
            packFilePath = deploymentNodeFileService.packageNewFile(file);
            map.put("packFilePath", packFilePath);
        } catch (IOException e) {
            log.error("打包文件异常{}", e);
            deploymentNodeErrorOperationService.deleteFolder();
            throw new IOException("打包文件异常");
        }
        log.info("用户{},重新打包完成", session.getId());
    }

    private NodeInfo checkNodeAddress(String nodeAddress, HttpSession session) throws Exception {
        if (StrUtil.isEmpty(nodeAddress)) {
            throw new Exception("节点信息不能为空");
        }
        log.info("用户{},部署节点地址为{}", session.getId(), nodeAddress);
        List<NodeInfo> unDeploymentNodes = nodeInfoMapper.selectDeploymentNodeByHostAddress(DeployStatusEnum.UNDEPLOYED.getCode(), nodeAddress);
        if (CollectionUtil.isEmpty(unDeploymentNodes)) {
            throw new Exception("查不到该ip");
        }
        return unDeploymentNodes.get(0);
    }

    public String[] getContainerStartCommand(String netWorkPath, HttpSession session) {
        log.info("用户{},创建部署命令", session.getId());
        String[] pathNames = netWorkPath.split(File.separator);
        String nodePath = pathNames[pathNames.length - 2] + File.separator + pathNames[pathNames.length - 1];
        String[] command = {"sh", "-c", "cd " + ConstantsContainer.CONTAINER_INTERNAL_PATH + ";if [ -d \"" + nodePath + "/logs\" ];then sleep 10;fi;rm -rf "
                + nodePath + "/.cita-*;./bin/cita bebop setup " + nodePath + ";./bin/cita bebop start " + nodePath + ";trap \"./bin/cita bebop stop "
                + nodePath + " exit 143;\" TERM;while true; do sleep 2; done;"};
        return command;
    }

    private void startContainerAndStart(String netWork, String[] command, String nodeAddress, String nodeName, NetworkToml networkToml,
                                        WebConnectionInfo webConnectionInfo, HttpSession session) throws Exception {
        PlatformOperation platformOperation = PlatformOperation.getDockerJavaPlatformOperation(nodeAddress);
        ImageInfo imageInfo = ImageInfo.builder().repository(imageConfig.getCITANodeImageRepository()).tag(imageConfig.getCITANodeImageTag()).imageID(imageConfig.getCITANodeImageID()).build();
        Map<String, String> bindMounts = new HashMap<>(1);
        bindMounts.put(deploymentNodeFileService.getMaxSpacePath(webConnectionInfo, session) + File.separator + netWork, ConstantsContainer.CONTAINER_INTERNAL_PATH + File.separator + netWork);
        Map<Integer, Integer> bindPorts = new HashMap<>(4);
        String port = networkToml.getPort() == null ? networkToml.getTls_port() : networkToml.getPort();
        bindPorts.put(Integer.valueOf(port), Integer.valueOf(port));
        bindPorts.put(1337, 1337);
        //bindPorts.put(15672, 15672);
        //bindPorts.put(5672, 5672);
        Map<String, String> env = new HashMap<>();
        env.put("RPC_HTTP_PORT", "1337");
        env.put("RABBITMQ_LOG_BASE", ConstantsContainer.CONTAINER_INTERNAL_MQLOG_PATH);
        env.put("HOME", ConstantsContainer.CONTAINER_INTERNAL_PATH);
        ApplicationInfo applicationInfo = ApplicationInfo.builder().applicationName(nodeName).command(command).imageName(imageConfig.getCITANodeImageName())
                .workDir(ConstantsContainer.CONTAINER_INTERNAL_PATH).bindMounts(bindMounts).bindPorts(bindPorts).env(env).build();
        createAndStartApplication(platformOperation, imageInfo, applicationInfo);
    }

    public void createAndStartApplication(PlatformOperation platformOperation, ImageInfo imageInfo, ApplicationInfo applicationInfo) throws CreateApplicationException {
        if (platformOperation.createApplication(imageInfo, applicationInfo)) {
            if (!platformOperation.startApplication(applicationInfo)) {
                log.error("启动应用异常");
                throw new CreateApplicationException("启动应用异常");
            }
        } else {
            log.error("创建应用异常");
            throw new CreateApplicationException("创建应用异常");
        }
    }

    /**
     * 更新节点表
     *
     * @param nodeInfo
     * @param nodeName
     * @param address
     * @param networkToml
     * @param networkName
     */
    public void insertNodeInfo(WebConnectionInfo webConnectionInfo, String nodeName, String address, String nodeAddress, NetworkToml networkToml,
                               String networkName, String networkPath, String nodeFolder, HttpSession session) throws Exception {
        try {
            String port = networkToml.getPort() == null ? networkToml.getTls_port() : networkToml.getPort();
            NodeInfo nodeInfo = NodeInfo.builder().nodeName(nodeName).address(address).deployStatus(DeployStatusEnum.DEPLOYED.getCode()).createTime(String.valueOf(System.currentTimeMillis()))
                    .updateTime(String.valueOf(System.currentTimeMillis())).host(nodeAddress + ConstantsContainer.IP_PORT_SEP + port).version(imageConfig.getCITANodeImageTag())
                    .networkName(networkName).networkPath(networkPath)
                    .nodeFolder(nodeFolder).build();
            nodeInfoMapper.insert(nodeInfo);
        } catch (Exception e) {
            log.error("更新数据异常[{}]", e);
            throw new Exception("更新数据异常");
        }
    }

    /**
     * 获取WebconnectionInfo对象
     *
     * @param userName
     * @param passwrod
     * @param httpSession
     * @return
     */
    public WebConnectionInfo getWebConnectionInfo(String userName, String passWord, String nodeAddress, HttpSession httpSession) throws DecryptException {
        String password = "";
        if (StrUtil.isBlank(userName) && StrUtil.isBlank(password)) {
            //证明需要查库，从数据库获取数据
            User user = userMapper.selectByTypeAndAddress(ConstantsContainer.USER_TYPE_SHELL, nodeAddress);
            if (null != user) {
                userName = user.getUserName();
                password = EncryptTools.decrypt(user.getPassword());
            } else {
                userMapper.insertSelective(new User(ConstantsContainer.SHELL_USER_NAME, linuxKey, ConstantsContainer.USER_TYPE_SHELL,
                        nodeAddress, String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis())));
                userName = ConstantsContainer.SHELL_USER_NAME;
                password = EncryptTools.decrypt(linuxKey);
            }
        } else {
            //前端使用AES加密后传递给后台，后台进行AES解密
            password = EncryptTools.decrypt(passWord);
            userMapper.updateByAddress(new User(userName, passWord, nodeAddress, String.valueOf(System.currentTimeMillis())));
        }
        return WebConnectionInfo.builder().userName(userName).password(password)
                .port(22).host(nodeAddress).channelType(ConstantsContainer.CHANNEL_TYPE_EXEC).build();
    }

    public void deplomentMonitor(String nodeAddress, String nodeName, String chainName, String nodeId, HttpSession session, String path) throws Exception {
        if (!SocketUtil.checkIPPortIsUsed(nodeAddress, ConstantsContainer.CADVISOR_PORT)) {
            throw new SocketException("cadvisor端口被占用");
        }
        monitorDeployService.deployCadvisor(nodeAddress, ConstantsContainer.CADVISOR_PORT, nodeName, session);
        if (!SocketUtil.checkIPPortIsUsed(nodeAddress, ConstantsContainer.PROCESS_EXPORTER_PORT)) {
            throw new SocketException("process_exporter端口被占用");
        }
        monitorDeployService.deployProcessExporter(nodeAddress, ConstantsContainer.PROCESS_EXPORTER_PORT, nodeName, session);
        if (!SocketUtil.checkIPPortIsUsed(nodeAddress, ConstantsContainer.NODE_EXPORTER_PORT)) {
            throw new SocketException("node_exporter端口被占用");
        }
        monitorDeployService.deployNodeExporter(nodeAddress, ConstantsContainer.NODE_EXPORTER_PORT, nodeName, session);
        deplomentCitaMonitor(nodeAddress, nodeName, chainName, nodeId, session, path);
    }

    private void deplomentCitaMonitor(String nodeAddress, String nodeName, String chainName, String nodeId, HttpSession session, String path)
            throws SocketException, Exception {
        if (!SocketUtil.checkIPPortIsUsed(nodeAddress, ConstantsContainer.CITA_EXPORTER_LOCAL_PORT)) {
            throw new SocketException("cita_exporter端口被占用");
        }
        monitorDeployService.deployCITAExporter(nodeAddress, ConstantsContainer.CITA_EXPORTER_LOCAL_PORT, nodeName, chainName, nodeId, session, path);
        if (!SocketUtil.checkIPPortIsUsed(nodeAddress, ConstantsContainer.RABBIT_MQ_PORT)) {
            throw new SocketException("rabbitMQ_exporter端口被占用");
        }
        rabbitMQExporterService.deployRabbitMQExporter(nodeAddress, ConstantsContainer.RABBIT_MQ_PORT, nodeName, session);
        updateConfigInPrometheus();
    }


    void updateConfigInPrometheus() throws Exception {
        List<MonitorInfo> monitorInfos = monitorInfoMapper.selectByName(imageConfig.getPrometheusAlias());
        monitorConfigService.configConfigInPrometheus(monitorInfos.get(0).getHost().split(ConstantsContainer.IP_PORT_SEP)[0]);
    }

    public void check(String nodeName, String privateKey, String address) {
        if (checkNodeName(nodeName)) {
            log.error("节点名称已存在");
            throw new NodeNameException(ErrorCodes.NODE_NAME_ERROR);
        }
        if (checkNodeNameStyle(nodeName)) {
            log.error("节点名称格式错误");
            throw new NodeNameException(ErrorCodes.NODE_NAME_ERROR);
        }
        if (checkNodeIp(address)) {
            log.error("节点地址已存在");
            throw new NodeAddressException("");
        }
    }

    private boolean checkNodeName(String nodeName) {
        if (null != nodeInfoMapper.selectByNodeName(nodeName)) {
            return true;
        } else {
            return false;
        }
    }

    private boolean checkNodeNameStyle(String nodeName) {
        String regex = "[0-9a-zA-Z]{1,16}";
        if (!nodeName.matches(regex)) {
            return true;
        } else {
            return false;
        }
    }

    private boolean checkNodeIp(String nodeAddress) {
        List<NodeInfo> nodeInfos = nodeInfoMapper.selectByHostAddress(nodeAddress);
        if (CollectionUtil.isNotEmpty(nodeInfos)) {
            return true;
        } else {
            return false;
        }
    }

    private void checkPrivateKeyAndAddress(String privateKey, String address) {
        if (!(privateKey.startsWith("0x") && address.startsWith("0x"))) {
            log.error("私钥或地址格式错误");
            throw new PrivateKeyOrAddressException(ErrorCodes.PRIVATEKEY_ADDRESS_ERROR);
        }
    }

    private void checkSftpUser(WebConnectionInfo webConnectionInfo, HttpSession session) throws JSchException {
        try {
            ChannelExec channel = (ChannelExec) sshConnection.open(webConnectionInfo, session.getId());
            channel.connect();
            sshConnection.close(session.getId());
        } catch (JSchException e) {
            log.error("检测能否登陆失败{}", e);
            throw new LoginShellException("服务器登录异常");
        }
        deploymentNodeFileServiceImpl.checkServerIsHaveTelnet(webConnectionInfo, session);
    }
}