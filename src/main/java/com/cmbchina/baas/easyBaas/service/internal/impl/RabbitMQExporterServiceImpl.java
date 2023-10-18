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
import com.cmbchina.baas.easyBaas.constant.ConstantsContainer;
import com.cmbchina.baas.easyBaas.constant.DeployStatusEnum;
import com.cmbchina.baas.easyBaas.domain.deployment.ApplicationInfo;
import com.cmbchina.baas.easyBaas.domain.deployment.ImageInfo;
import com.cmbchina.baas.easyBaas.mapper.MonitorInfoMapper;
import com.cmbchina.baas.easyBaas.model.MonitorInfo;
import com.cmbchina.baas.easyBaas.service.internal.RabbitMQExporterService;
import javax.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author sw9327@cmbchina.com
 * @description
 * @data 2021/07/26 20:16
 */

@Service
@Slf4j
public class RabbitMQExporterServiceImpl implements RabbitMQExporterService {

    @Autowired
    private ImageConfig imageConfig;

    @Autowired
    MonitorInfoMapper monitorInfoMapper;

    @Override
    public boolean deployRabbitMQExporter(String ip, int port, String imageName, HttpSession session) {
        ImageInfo imageInfo = ImageInfo.builder().repository(imageConfig.getRabbitMQExporterRepo()).
                tag(imageConfig.getRabbitMQExporterImageTag()).imageID(imageConfig.getRabbitMQExporterImageId()).build();
        Map<String, String> env = new HashMap<>();
        env.put("RABBIT_USER", "cita_monitor");
        env.put("RABBIT_PASSWORD", "cita_monitor");
        String alertManagerAlias = imageConfig.getRabbitMQExporterAlias() + ConstantsContainer.CONNECTION_SEP + imageName;
        ApplicationInfo applicationInfo = ApplicationInfo.builder().applicationName(alertManagerAlias).imageId(imageInfo.getImageID()).command(new String[]{})
                .workDir("").env(env).networkMode("host").imageName(imageInfo.getRepository() + ConstantsContainer.IP_PORT_SEP + imageInfo.getTag()).build();
        deploy(ip, port, imageInfo, applicationInfo, alertManagerAlias);
        log.info("用户{},{}部署完成", session.getId(), "rabbitMQ");
        return true;
    }

    public boolean deploy(String ip, int port, ImageInfo imageInfo, ApplicationInfo applicationInfo, String applicationAlias) {
        PlatformOperation platformOperation = PlatformOperation.getDockerJavaPlatformOperation(ip);
        if (platformOperation.createApplication(imageInfo, applicationInfo) && platformOperation.startApplication(applicationInfo)) {
            MonitorInfo monitorInfo = MonitorInfo.builder().name(applicationAlias).host(ip + ConstantsContainer.IP_PORT_SEP + port)
                    .deployStatus(DeployStatusEnum.DEPLOYED.getCode()).createTime(String.valueOf(System.currentTimeMillis())).updateTime(String.valueOf(System.currentTimeMillis())).build();
            monitorInfoMapper.insertSelective(monitorInfo);
            return true;
        }
        return false;
    }
}
