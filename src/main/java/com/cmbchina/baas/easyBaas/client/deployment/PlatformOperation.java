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

package com.cmbchina.baas.easyBaas.client.deployment;

import com.cmbchina.baas.easyBaas.client.deployment.docker.DockerPlatformOperationFactory;
import com.cmbchina.baas.easyBaas.client.deployment.docker.sdk.DockerJavaPlatformOperation;
import com.cmbchina.baas.easyBaas.domain.deployment.ApplicationInfo;
import com.cmbchina.baas.easyBaas.domain.deployment.ImageInfo;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.cmbchina.baas.easyBaas.constant.ConstantsContainer.DOCKER_API_PORT;

/**
 * @Description: 各种平台操作类
 * @Date 2021/6/24 10:07
 */
public class PlatformOperation implements BasicPlatformOperation {

    private static final Map<String, PlatformOperation> DOCKER_PLATFORM_OPERATION_MAP = new ConcurrentHashMap<>();

    private BasicPlatformOperation basicPlatformOperation;

    @Override
    public boolean loadImage(String srcImageFile) {
        return basicPlatformOperation.loadImage(srcImageFile);
    }

    @Override
    public boolean tagImage(ImageInfo imageInfo) {
        return basicPlatformOperation.tagImage(imageInfo);
    }

    @Override
    public boolean deleteImage(ImageInfo imageInfo) {
        return basicPlatformOperation.deleteImage(imageInfo);
    }

    @Override
    public List<ImageInfo> listImages() {
        return basicPlatformOperation.listImages();
    }

    @Override
    public boolean createApplication(ImageInfo imageInfo, ApplicationInfo applicationInfo) {
        return basicPlatformOperation.createApplication(imageInfo, applicationInfo);
    }

    @Override
    public boolean stopApplication(ApplicationInfo applicationInfo) {
        return basicPlatformOperation.stopApplication(applicationInfo);
    }

    @Override
    public boolean startApplication(ApplicationInfo applicationInfo) {
        return basicPlatformOperation.startApplication(applicationInfo);
    }

    @Override
    public List<ApplicationInfo> listApplications() {
        return basicPlatformOperation.listApplications();
    }

    @Override
    public boolean deleteApplication(ApplicationInfo applicationInfo) {
        return basicPlatformOperation.deleteApplication(applicationInfo);
    }

    @Override
    public String execApplication(ApplicationInfo applicationInfo, String[] execCmd) throws Exception {
        return basicPlatformOperation.execApplication(applicationInfo, execCmd);
    }

    @Override
    public boolean rename(ApplicationInfo applicationInfo, String newName) {
        return basicPlatformOperation.rename(applicationInfo, newName);
    }

    @Override
    public boolean reStartApplication(ApplicationInfo applicationInfo) {
        return basicPlatformOperation.reStartApplication(applicationInfo);
    }


    public void setBasicPlatformOperation(BasicPlatformOperation basicPlatformOperation) {
        this.basicPlatformOperation = basicPlatformOperation;
    }

    public static synchronized PlatformOperation getDockerJavaPlatformOperation(String address) {
        if (null == DOCKER_PLATFORM_OPERATION_MAP.get(address)) {
            PlatformOperation platformOperation = new PlatformOperation();
            DockerJavaPlatformOperation dockerPlatformOperation = DockerPlatformOperationFactory.getDockerPlatformOperation(DockerJavaPlatformOperation.OPERATION_TYPE, address, DOCKER_API_PORT);
            platformOperation.setBasicPlatformOperation(dockerPlatformOperation);
            DOCKER_PLATFORM_OPERATION_MAP.put(address, platformOperation);
        }
        return DOCKER_PLATFORM_OPERATION_MAP.get(address);
    }
}
