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

package com.cmbchina.baas.easyBaas.client.deployment.docker;

import com.cmbchina.baas.easyBaas.client.deployment.docker.sdk.DockerClientFactory;
import com.cmbchina.baas.easyBaas.client.deployment.docker.sdk.DockerJavaPlatformOperation;
import com.github.dockerjava.api.DockerClient;

/**
 * @Description: docker操作类创建工厂
 * @Date 2021/6/24 9:57
 */
public class DockerPlatformOperationFactory {

    public static DockerJavaPlatformOperation getDockerPlatformOperation(String operationType, String ip, String port) {
        if (operationType.equals(DockerJavaPlatformOperation.OPERATION_TYPE)) {
            DockerClient dockerClient = DockerClientFactory.getDockerClient(ip, port);
            return new DockerJavaPlatformOperation(dockerClient);
        } else {
            return null;
        }
    }
}
