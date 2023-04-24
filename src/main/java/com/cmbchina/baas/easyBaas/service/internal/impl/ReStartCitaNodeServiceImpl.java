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
import com.cmbchina.baas.easyBaas.exception.exceptions.FindContainerException;
import com.cmbchina.baas.easyBaas.mapper.NodeInfoMapper;
import com.cmbchina.baas.easyBaas.model.NodeInfo;
import com.cmbchina.baas.easyBaas.response.Response;
import com.cmbchina.baas.easyBaas.service.internal.ReStartCitaNodeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.util.List;

@Service
@Slf4j
public class ReStartCitaNodeServiceImpl implements ReStartCitaNodeService {

    @Autowired
    NodeInfoMapper nodeInfoMapper;

    @Override
    public Response reStartCita(String nodeName, HttpSession session) throws FindContainerException {
        NodeInfo nodeInfo = nodeInfoMapper.selectByNodeName(nodeName);
        if (null == nodeInfo) {
            return Response.builder().code(String.valueOf(ErrorCodes.NODE_NOT_EXIST.getCode())).msg(ErrorCodes.NODE_NOT_EXIST.getMessage()).data(null).build();
        }
        List<ApplicationInfo> applicationInfos = PlatformOperation.getDockerJavaPlatformOperation(nodeInfo.getHost().split(ConstantsContainer.IP_PORT_SEP)[0]).listApplications();
        ApplicationInfo applicationInfo = getApplicationInfo(applicationInfos, nodeName);
        if (null == applicationInfo) {
            throw new FindContainerException("找不到对应容器");
        }
        boolean flag = PlatformOperation.getDockerJavaPlatformOperation(nodeInfo.getHost().split(ConstantsContainer.IP_PORT_SEP)[0]).reStartApplication(applicationInfo);
        log.info("用户{}重启节点{}完成，重启结果{}", session.getId(), nodeName, flag);
        if (flag == true) {
            return Response.builder().code(String.valueOf(ErrorCodes.OK.getCode())).msg(ErrorCodes.OK.getMessage()).build();
        } else {
            return Response.builder().code(String.valueOf(ErrorCodes.NODE_RESTART_FAILED_ERROR.getCode())).msg(ErrorCodes.NODE_RESTART_FAILED_ERROR.getMessage()).data(null).build();
        }
    }

    public ApplicationInfo getApplicationInfo(List<ApplicationInfo> applicationInfos, String nodeName) {
        for (ApplicationInfo info : applicationInfos) {
            if (info.getApplicationName().equals(File.separator + nodeName)) {
                return info;
            }
        }
        return null;
    }
}
