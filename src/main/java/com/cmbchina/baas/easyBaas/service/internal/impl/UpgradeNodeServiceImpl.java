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
import com.cmbchina.baas.easyBaas.config.UpgradeConfig;
import com.cmbchina.baas.easyBaas.config.UpgradeHeightConfig;
import com.cmbchina.baas.easyBaas.constant.ConstantsContainer;
import com.cmbchina.baas.easyBaas.domain.deployment.ApplicationInfo;
import com.cmbchina.baas.easyBaas.domain.deployment.ImageInfo;
import com.cmbchina.baas.easyBaas.exception.ErrorCodes;
import com.cmbchina.baas.easyBaas.exception.exceptions.BlockHeightException;
import com.cmbchina.baas.easyBaas.exception.exceptions.ChainVerionIsNewException;
import com.cmbchina.baas.easyBaas.exception.exceptions.CreateApplicationException;
import com.cmbchina.baas.easyBaas.exception.exceptions.DecryptException;
import com.cmbchina.baas.easyBaas.exception.exceptions.NodeNotExistException;
import com.cmbchina.baas.easyBaas.exception.exceptions.NodeVersionException;
import com.cmbchina.baas.easyBaas.mapper.NodeInfoMapper;
import com.cmbchina.baas.easyBaas.model.NetworkToml;
import com.cmbchina.baas.easyBaas.model.NodeInfo;
import com.cmbchina.baas.easyBaas.model.WebConnectionInfo;
import com.cmbchina.baas.easyBaas.response.Response;
import com.cmbchina.baas.easyBaas.service.internal.CitaNodeApiService;
import com.cmbchina.baas.easyBaas.service.internal.UpgradeNodeService;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//sw9327@cmbchina.com 节点升级实现类 2021/09/06 16:57
@Service
@Slf4j
public class UpgradeNodeServiceImpl implements UpgradeNodeService {
    @Autowired
    NodeInfoMapper nodeInfoMapper;
    @Autowired
    ImageConfig imageConfig;
    @Autowired
    UpgradeConfig upgradeConfig;
    @Autowired
    DeploymentNodeServiceImpl deploymentNodeServiceImpl;
    @Autowired
    ShellServiceImpl shellService;
    @Autowired
    BringIntoNodeServiceImpl bringIntoNodeService;
    @Autowired
    DeploymentNodeFileServiceImpl deploymentNodeFileService;
    @Autowired
    BringIntoNodeServiceImpl bringIntoNodeServiceImpl;
    @Autowired
    CitaNodeApiServiceImpl citaNodeApiServiceImpl;
    @Autowired
    ErrorOperationClearServiceImpl errorOperationService;
    @Autowired
    CitaNodeApiService citaNodeApiService;
    @Autowired
    UpgradeHeightConfig upgradeHeightConfig;
    @Autowired
    UpgradeNodeCheckImpl upgradeNodeCheckImpl;

    @Override
    public Response updateNode(String nodeName, String username, String password, HttpSession session) throws BlockHeightException, CreateApplicationException,
            DecryptException, JSchException, Exception {
        Map<String, Object> map = new HashMap<>();
        NodeInfo nodeInfo = checkNode(nodeName, map);
        //2.webconnection对象
        WebConnectionInfo webConnectionInfo = deploymentNodeServiceImpl.getWebConnectionInfo(username, password, nodeInfo.getHost().split(ConstantsContainer.IP_PORT_SEP)[0], session);
        //条件检测
        checkUpgradeConditions(nodeInfo, nodeName, session);
        //3.将需要的文件读取到本地
        shellService.getConfigFile(webConnectionInfo, session, map, ConstantsContainer.UPDATE_TYPE, nodeInfo.getNetworkPath());
        bringIntoNodeService.readConfigFile(webConnectionInfo, session, map, ConstantsContainer.UPDATE_TYPE, ConstantsContainer.USER_HOME_PATH);
        //4和5.命令和升级
        String[] command = deploymentNodeServiceImpl.getContainerStartCommand((String) map.get("nodeFolder"), session);
        map.put("imageInfo", ImageInfo.builder().repository(upgradeConfig.getCITANodeImageRepository()).tag(upgradeConfig.getCITANodeImageTag())
                .imageID(upgradeConfig.getCITANodeImageID()).imageName(upgradeConfig.getCITANodeImageName()).build());
        //停止并重命名老的应用
        stopAndRenameApplication(nodeInfo, session);
        //创建新应用
        try {
            bringIntoNodeServiceImpl.startContainerAndStart(command, nodeInfo.getHost().split(ConstantsContainer.IP_PORT_SEP)[0],
                    nodeName, map, (NetworkToml) map.get("networkToml"), webConnectionInfo, session, nodeInfo.getNetworkPath());
        } catch (Exception e) {
            log.error("用户{}创建新应用失败{}", session.getId(), e.getMessage());
            //进行回退，删除文件
            rollbackNode(nodeInfo, session);
            errorOperationService.deleteNetworkConfigFiles((String) map.get("netWorkName"), nodeInfo.getAddress(), username,
                    password, session.getId(), false, ConstantsContainer.UPDATE_TYPE, ConstantsContainer.USER_HOME_PATH);
            return Response.builder().code(ErrorCodes.UPGRADE_NODE_FAIL.getCode()).msg(ErrorCodes.UPGRADE_NODE_FAIL.getMessage()).build();
        }
        bringIntoNodeServiceImpl.updateNodeInfo(map);
        try {
            errorOperationService.deleteNetworkConfigFiles((String) map.get("netWorkName"), nodeInfo.getAddress(), username,
                    password, session.getId(), false, ConstantsContainer.UPDATE_TYPE, ConstantsContainer.USER_HOME_PATH);
        } catch (Exception e) {
            log.error("临时文件删除失败:[{}]", e);
        }
        return Response.builder().code(ErrorCodes.OK.getCode()).msg(ErrorCodes.OK.getMessage() + ",请视情况清理原始数据").build();
    }

    /**
     * 检查节点是否存在，版本是否为可升级版本
     *
     * @param nodeName
     * @return
     */
    private NodeInfo checkNode(String nodeName, Map<String, Object> map) throws NodeNotExistException, NodeVersionException {
        //1.查库获取升级节点的信息，判断
        NodeInfo nodeInfo = nodeInfoMapper.selectByNodeName(nodeName);
        if (null == nodeInfo) {
            throw new NodeNotExistException("网络[{" + nodeName + "}]不存在");
        }
        map.put("nodeInfo", nodeInfo);
        map.put("netWorkName", nodeInfo.getNetworkName());
        return nodeInfo;
    }

    /**
     * 停止并删除节点
     *
     * @param nodeInfo
     * @param session
     * @throws JSchException
     * @throws SftpException
     */
    private void stopAndRenameApplication(NodeInfo nodeInfo, HttpSession session) throws Exception {
        try {
            log.info("用户{}开始停止节点{}", session.getId(), nodeInfo.getNodeName());
            PlatformOperation platformOperation = PlatformOperation.getDockerJavaPlatformOperation(nodeInfo.getHost().split(ConstantsContainer.IP_PORT_SEP)[0]);
            List<ApplicationInfo> applicationInfos = platformOperation.listApplications();
            Object flag = citaNodeApiServiceImpl.getNodeStatusThenQueryInfoOrOperateNode(nodeInfo, applicationInfos, ConstantsContainer.NODE_OPERATE_STOP, session);
            boolean flagBool = (flag == null ? false : (boolean) flag);
            log.info("用户{}停止节点{}完成，停止结果{},开始修改命名", session.getId(), nodeInfo.getNodeName(), flagBool);
            boolean deleteResult = platformOperation.rename(ApplicationInfo.builder().applicationName(File.separator + nodeInfo.getNodeName()).build(), "UpGradeBak" + nodeInfo.getNodeName());
            log.info("用户{}修改节点命名{}结果为{}", session.getId(), nodeInfo.getNodeName(), deleteResult);
        } catch (Exception e) {
            log.error("停止并重命名节点异常{}", e);
            throw new Exception("停止并重命名节点异常");
        }
    }

    /**
     * 回滚操作
     *
     * @param nodeInfo
     * @param session
     * @throws JSchException
     * @throws SftpException
     */
    private void rollbackNode(NodeInfo nodeInfo, HttpSession session) throws JSchException, SftpException {
        //1.判断新的容器是否存在，存在就删除
        try {
            PlatformOperation.getDockerJavaPlatformOperation(nodeInfo.getHost().split(ConstantsContainer.IP_PORT_SEP)[0])
                    .deleteApplication(ApplicationInfo.builder().applicationName(File.separator + nodeInfo.getNodeName()).build());
        } catch (Exception e) {
            log.error("用户{}删除创建的新容器失败", session.getId());
        }
        PlatformOperation platformOperation = PlatformOperation.getDockerJavaPlatformOperation(nodeInfo.getHost().split(ConstantsContainer.IP_PORT_SEP)[0]);
        boolean renameResult = platformOperation.rename(ApplicationInfo.builder().applicationName(File.separator + "UpGradeBak" + nodeInfo.getNodeName()).build(), nodeInfo.getNodeName());
        log.info("用户{}修改节点命名{}结果为{}", session.getId(), nodeInfo.getNodeName(), renameResult);
        platformOperation = PlatformOperation.getDockerJavaPlatformOperation(nodeInfo.getHost().split(ConstantsContainer.IP_PORT_SEP)[0]);
        List<ApplicationInfo> applicationInfos = platformOperation.listApplications();
        Object flag = citaNodeApiServiceImpl.getNodeStatusThenQueryInfoOrOperateNode(nodeInfo, applicationInfos, ConstantsContainer.NODE_OPERATE_START, session);
        log.info("用户{}启动节点完成，启动结果{}", session.getId(), flag);
    }

    private void checkUpgradeConditions(NodeInfo nodeInfo, String nodeName, HttpSession session) throws ChainVerionIsNewException, BlockHeightException, Exception {
        ImageInfo imageInfo = upgradeNodeCheckImpl.getCITAImage(nodeInfo.getHost().split(ConstantsContainer.IP_PORT_SEP)[0], nodeName);
        if (null != imageInfo) {
            log.info("容器运行版本id[{}],配置文件id[{}]", imageInfo.getImageID(), ConstantsContainer.CON_ID_SHA + upgradeConfig.getCITANodeImageID());
            if (imageInfo.getImageID().startsWith(ConstantsContainer.CON_ID_SHA + upgradeConfig.getCITANodeImageID())) {
                throw new ChainVerionIsNewException(ErrorCodes.CHAIN_VERSION_IS_NEWEST.getMessage());
            }
            log.info("是否检查高度:[{}]", upgradeHeightConfig.isSign());
            /*if (upgradeHeightConfig.isSign()) {
                log.info("需要升级的网络为{}", nodeInfo.getNetworkName());
                upgradeNodeCheckImpl.checkUpgradeHeight(nodeName, session, nodeInfo);
            }*/
        } else {
            throw new Exception("获取容器对应镜像失败");
        }
    }
}