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
import com.cmbchina.baas.easyBaas.client.deployment.PlatformOperation;
import com.cmbchina.baas.easyBaas.constant.CitaNodeStatusEnum;
import com.cmbchina.baas.easyBaas.constant.ConstantsContainer;
import com.cmbchina.baas.easyBaas.domain.deployment.ApplicationInfo;
import com.cmbchina.baas.easyBaas.domain.deployment.ImageInfo;
import com.cmbchina.baas.easyBaas.exception.ErrorCodes;
import com.cmbchina.baas.easyBaas.mapper.NodeInfoMapper;
import com.cmbchina.baas.easyBaas.model.NodeInfo;
import com.cmbchina.baas.easyBaas.model.NodeShow;
import com.cmbchina.baas.easyBaas.response.Response;
import com.cmbchina.baas.easyBaas.service.internal.CitaNodeListService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


@Service
@Slf4j
public class CitaNodeListServiceImpl implements CitaNodeListService {

    @Autowired
    NodeInfoMapper nodeInfoMapper;
    @Autowired
    UpgradeNodeCheckImpl upgradeNodeCheck;


    @Override
    public Response nodeLists(HttpSession session) throws Exception {
        //1.查库获取所有节点列表
        List<NodeInfo> list = nodeInfoMapper.selectAllNodeInfoByStatus();
        //2.核对信息
        checkChainVersion(list, session);
        //3.获取每个节点状态
        List<NodeShow> showList = new ArrayList<>();
        list.forEach(item -> {
            NodeShow node = new NodeShow();
            node.setNodeName(item.getNodeName());
            node.setNodeAddress(item.getHost().split(ConstantsContainer.IP_PORT_SEP)[0]);
            node.setNodeVersion(item.getVersion());
            node.setNodeStatus(getNodeStatus(item, session));
            showList.add(node);
        });
        return Response.builder().code(String.valueOf(ErrorCodes.OK.getCode())).msg(ErrorCodes.OK.getMessage()).data(showList).build();
    }

    /**
     * 获取节点状态
     *
     * @param nodeInfo
     * @param session
     * @return
     */
    private String getNodeStatus(NodeInfo nodeInfo, HttpSession session) {

        //1.判断容器是否停止
        if (getNodeStatusByContainerStatus(nodeInfo, session)) {
            try {
                return getRunNodeProcess(nodeInfo, session);
            } catch (Exception e) {
                log.error("用户{},获取{}节点运行状态失败", session.getId(), nodeInfo.getNodeName());
                return CitaNodeStatusEnum.STOP.toString();
            }
        } else {
            //如果不是true证明容器没有运行，直接返回
            return CitaNodeStatusEnum.STOP.toString();
        }
    }

    /**
     * 判断容器是否运行中
     *
     * @param nodeInfo
     * @param session
     * @return
     */
    private boolean getNodeStatusByContainerStatus(NodeInfo nodeInfo, HttpSession session) {
        List<ApplicationInfo> applicationInfos = PlatformOperation.getDockerJavaPlatformOperation(nodeInfo.getHost().split(ConstantsContainer.IP_PORT_SEP)[0]).listApplications();
        ApplicationInfo applicationInfo = null;
        for (int i = 0; i < applicationInfos.size(); i++) {
            applicationInfo = applicationInfos.get(i);
            if (applicationInfo.getApplicationName().equals(File.separator + nodeInfo.getNodeName())) {
                log.info("用户{}获取到名字为{}的容器,容器状态为:{}", session.getId(), nodeInfo.getNodeName(), applicationInfo.getStatus());
                if (applicationInfo.getStatus().contains(ConstantsContainer.CONTAINER_STATUS)) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;
    }

    /**
     * 获取容器运行的cita进程
     *
     * @param nodeInfo
     * @return
     * @throws Exception
     */
    private String getRunNodeProcess(NodeInfo nodeInfo, HttpSession session) throws Exception {
        log.info("用户{}，获取cita容器中进程", session.getId());
        List<ApplicationInfo> applicationInfos = PlatformOperation.getDockerJavaPlatformOperation(nodeInfo.getHost().split(ConstantsContainer.IP_PORT_SEP)[0]).listApplications();
        Optional<ApplicationInfo> optionalApplicationInfo = applicationInfos.stream().filter(applicationInfo -> {
            return applicationInfo.getApplicationName().equals(File.separator + nodeInfo.getNodeName());
        }).findFirst();
        if (!optionalApplicationInfo.isPresent()) {
            throw new Exception("未能查询到cita节点容器");
        }
        ApplicationInfo applicationInfo = optionalApplicationInfo.get();
        String[] cmd = {"ps", "-e"};
        String result = PlatformOperation.getDockerJavaPlatformOperation(nodeInfo.getHost().split(ConstantsContainer.IP_PORT_SEP)[0]).execApplication(applicationInfo, cmd);
        List<String> citaProcess = Arrays.asList(result.split("\r\n"));
        List<String> citaList = Arrays.asList(new String[]{ConstantsContainer.CITA_FOREVER, ConstantsContainer.CITA_AUTH, ConstantsContainer.CITA_CHAIN, ConstantsContainer.CITA_JSONRPC
                , ConstantsContainer.CITA_EXECTOR, ConstantsContainer.CITA_NETWORK, ConstantsContainer.CITA_BFT});
        return getRunNodeStatusByProcess(citaProcess, citaList, session);
    }

    /**
     * cita进程判断
     *
     * @param citaProcess
     * @return
     */
    private String getRunNodeStatusByProcess(List<String> citaProcess, List<String> citaList, HttpSession session) {
        log.info("用户{}，开始筛选cita容器中进程状态", session.getId());
        List<String> result = new ArrayList<>();
        for (int i = 0; i < citaProcess.size(); i++) {
            for (int i1 = 0; i1 < citaList.size(); i1++) {
                log.info("用户{}，进程筛选，{}中是否有{}，", session.getId(), citaProcess.get(i), citaList.get(i1));
                if (citaProcess.get(i).contains(citaList.get(i1))) {
                    result.add(citaList.get(i1));
                }
            }
        }
        log.info("用户{}，筛选进程结果{}", session.getId(), JSONObject.toJSON(result));
        return processResult(result, session);
    }

    private String processResult(List<String> result, HttpSession session) {
        log.info("用户{}，筛选结果长度{}", session.getId(), result.size());
        //正常是7个进程
        if (result.size() == 7) {
            return CitaNodeStatusEnum.RUN.toString();
            //6个进程没有bft则警告
        } else if (result.size() == 6 && !result.contains(ConstantsContainer.CITA_BFT)) {
            return CitaNodeStatusEnum.WARN.toString();
        } else {
            return CitaNodeStatusEnum.STOP.toString();
        }
    }

    /**
     * 检查节点版本
     *
     * @param list
     * @param session
     * @return
     */
    private void checkChainVersion(List<NodeInfo> list, HttpSession session) throws Exception {
        for (NodeInfo info : list) {
            ImageInfo imageInfo = upgradeNodeCheck.getCITAImage(info.getHost().split(ConstantsContainer.IP_PORT_SEP)[0], info.getNodeName());
            if (null != imageInfo) {
                if (!imageInfo.getTag().equals(info.getVersion())) {
                    info.setVersion(imageInfo.getTag());
                    nodeInfoMapper.updateByPrimaryKey(info);
                }
            } else {
                throw new Exception("节点对应镜像获取异常");
            }
        }
    }

}
