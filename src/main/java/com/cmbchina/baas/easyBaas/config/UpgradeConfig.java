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
 
package com.cmbchina.baas.easyBaas.config;

import com.cmbchina.baas.easyBaas.constant.ConstantsContainer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import static com.cmbchina.baas.easyBaas.constant.ConstantsContainer.ID_PATH_SEP;

/**
 * @Description: 镜像文件地址配置读取
 * @Date 2021/6/25 16:15
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "upgrade")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpgradeConfig {
    private String citaNode;

    public String getCITANodeImageID() {
        return citaNode.split(ID_PATH_SEP)[1];
    }

    public String getCITANodeImageTag() {
        return citaNode.split(ID_PATH_SEP)[3];
    }

    public String getCITANodeImageRepository() {
        return citaNode.split(ID_PATH_SEP)[2];
    }

    public String getCITANodeImageName() {
        return getCITANodeImageRepository() + ConstantsContainer.IP_PORT_SEP + getCITANodeImageTag();
    }

}
