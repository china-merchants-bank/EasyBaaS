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
import com.cmbchina.baas.easyBaas.config.UpgradeHeightConfig;
import com.cmbchina.baas.easyBaas.constant.ConstantsContainer;
import com.cmbchina.baas.easyBaas.domain.deployment.ApplicationInfo;
import com.cmbchina.baas.easyBaas.domain.deployment.ImageInfo;
import com.cmbchina.baas.easyBaas.exception.exceptions.BlockHeightException;
import com.cmbchina.baas.easyBaas.exception.exceptions.GetBlockHeightException;
import com.cmbchina.baas.easyBaas.model.NodeInfo;
import com.cmbchina.baas.easyBaas.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.util.List;
import java.util.Optional;

/**
 * @description 节点升级的各种条件检查
 * @data 2021/10/27 14:29
 */
@Service
@Slf4j
public class UpgradeNodeCheckImpl {

    @Autowired
    CitaNodeApiServiceImpl citaNodeApiService;
    @Autowired
    UpgradeHeightConfig upgradeHeightConfig;


    /**
     * 根据容器以及镜像以及运行状态获取容器
     */
    private ApplicationInfo getApplicationByNameAndStatusAndAddress(String nodeAddress, String nodeName) {
        List<ApplicationInfo> applicationInfos = PlatformOperation.getDockerJavaPlatformOperation(nodeAddress).listApplications();
        Optional<ApplicationInfo> optionalApplicationInfo = applicationInfos.stream().filter(applicationInfo -> {
            log.info("当前判断容器为：[{}]", applicationInfo);
            if (applicationInfo.getImageName().contains(ConstantsContainer.CITA_IMAGE_NAME)
                    && (applicationInfo.getApplicationName().equals(File.separator + nodeName) || applicationInfo.getApplicationName().startsWith(File.separator + "cita"))) {
                return true;
            }
            return false;
        }).findFirst();
        ApplicationInfo applicationInfo = null;
        if (optionalApplicationInfo.isPresent()) {
            applicationInfo = optionalApplicationInfo.get();
        }

        return applicationInfo;
    }

    /**
     * 获取容器对应CITA镜像
     */
    public ImageInfo getCITAImage(String nodeAddress, String nodeName) {
        String containerId = getCITAContainerId(nodeAddress, nodeName);
        List<ImageInfo> imageInfos = PlatformOperation.getDockerJavaPlatformOperation(nodeAddress).listImages();
        Optional<ImageInfo> optionalimageInfo = imageInfos.stream().filter(imageInfo -> {
            log.info("当前判断镜像为:[{}]", imageInfo);
            if (imageInfo.getImageID().equals(containerId)) {
                return true;
            }
            return false;
        }).findFirst();
        ImageInfo imageInfo = null;
        if (optionalimageInfo.isPresent()) {
            imageInfo = optionalimageInfo.get();
            return imageInfo;
        }
        return null;
    }

    /**
     * 获取容器对应ContainerId
     */
    public String getCITAContainerId(String nodeAddress, String nodeName) {
        ApplicationInfo applicationInfo = getApplicationByNameAndStatusAndAddress(nodeAddress, nodeName);
        if (null != applicationInfo) {
            return applicationInfo.getImageId();
        }
        log.info("--------------------------------------------------------------------------------------");
        return null;
    }

    /**
     * 检查升级条件:高度
     */
    public void checkUpgradeHeight(String nodeName, HttpSession session, NodeInfo nodeInfo) throws BlockHeightException, Exception {
        //调用接口获取日志
        Response response = citaNodeApiService.queryLog(nodeName, ConstantsContainer.CHAIN_TYPE, ConstantsContainer.LOGS_NUM, session);
        List<String> chainLogs = (List<String>) response.getData();
        if (CollUtil.isEmpty(chainLogs)) {
            throw new BlockHeightException("日志获取失败");
        }
        //取最后一行
        String chainLog = chainLogs.get(chainLogs.size() - 1);
        //2021-09-07 - 03:05:17 | core_chain::libchain - 1380  | INFO  - new chain status height 13893, hash 914325407e59fa3a6f826664fee499bdb6424ccb38a44e0f89a9322e55f265bc
        //先按照,分割，再按照空格分割，取最后一个就是块高
        String[] chainHeightLogs = (chainLog.split(",")[0]).split(" ");
        Integer height = 9999999;
        if ("opc-testnet".equals(nodeInfo.getNetworkName())) {
            height = upgradeHeightConfig.getUpgradeHeightTest();
        } else if ("opc-mainnet".equals(nodeInfo.getNetworkName())) {
            height = upgradeHeightConfig.getUpgradeHeightPrd();
        }
        log.info("升级检测高度为:{}", height);
        try {
            if (!(Integer.valueOf(chainHeightLogs[chainHeightLogs.length - 1]) > height)) {
                throw new BlockHeightException("当前高度为[" + (Integer.valueOf(chainHeightLogs[chainHeightLogs.length - 1])) + "]，可升级高度为[" + ConstantsContainer.CAN_UPGRADE_HEIGHT + "]");
            }
        } catch (BlockHeightException e) {
            throw new BlockHeightException("当前高度为[" + (Integer.valueOf(chainHeightLogs[chainHeightLogs.length - 1])) + "]，可升级高度为[" + ConstantsContainer.CAN_UPGRADE_HEIGHT + "]");
        } catch (NumberFormatException e) {
            throw new GetBlockHeightException("日志获取失败");
        }
    }

}
