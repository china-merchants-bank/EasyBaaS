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

import com.cmbchina.baas.easyBaas.client.deployment.PlatformOperation;
import com.cmbchina.baas.easyBaas.constant.ConstantsContainer;
import com.cmbchina.baas.easyBaas.domain.deployment.ApplicationInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.util.List;

@Service
@Slf4j
public class BringNodeServiceImpl {

    /**
     * 纳管外部已有节点，停止以前通过bin方式启动的容器，或根据传参停止名字
     *
     * @param nodeAddress
     * @param nodeName
     * @param session
     */
    public ApplicationInfo stopDockerConAndRename(String nodeAddress, String nodeName, HttpSession session) throws Exception {
        try {
            log.info("{}用户外部纳管前停止容器", session.getId());
            PlatformOperation platformOperation = PlatformOperation.getDockerJavaPlatformOperation(nodeAddress);
            List<ApplicationInfo> applicationInfos = platformOperation.listApplications();
            ApplicationInfo applicationInfo = null;
            for (ApplicationInfo info : applicationInfos) {
                if (info.getApplicationName().equals(File.separator + nodeName)
                        || info.getApplicationName().startsWith(File.separator + ConstantsContainer.BIN_START_CITA_NAME_PREFIX)) {
                    applicationInfo = info;
                }
            }
            if (null == applicationInfo) {
                log.error("找不到容器");
                throw new Exception("容器操作失败");
            }
            log.info("{}用户需要操作的容器为{},id为{}", session.getId(), applicationInfo.getApplicationName(), applicationInfo.getContainerId());
            platformOperation.stopApplication(applicationInfo);
            platformOperation.rename(applicationInfo, applicationInfo.getApplicationName() + "Bak");
            applicationInfo.setApplicationName(applicationInfo.getApplicationName() + "Bak");
            return applicationInfo;
        } catch (Exception e) {
            throw new Exception("容器操作失败");
        }
    }

    /**
     * 纳管外部已有节点，异常情况恢复原容器
     *
     * @param nodeAddress
     * @param applicationInfo
     * @param session
     */
    public void renameCon(ApplicationInfo applicationInfo, String nodeAddress, HttpSession session) throws Exception {
        try {
            log.info("{}用户外部纳管异常恢复容器", session.getId());
            PlatformOperation platformOperation = PlatformOperation.getDockerJavaPlatformOperation(nodeAddress);
            platformOperation.rename(applicationInfo, applicationInfo.getApplicationName().replaceAll("Bak", ""));
        } catch (Exception e) {
            throw new Exception("容器操作失败");
        }
    }
}
