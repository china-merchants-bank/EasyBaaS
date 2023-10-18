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
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import static com.cmbchina.baas.easyBaas.constant.ConstantsContainer.ID_PATH_SEP;

/**
 * @Title: ImagePathConfig
 * @Description: 镜像文件地址配置读取
 * @Date 2021/6/25 16:15
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "image")
public class ImageConfig {
    private String citaNode;
    private String grafana;
    private String prometheus;
    private String alertmanager;
    private String citaExporter;
    private String nodeExporter;
    private String processExporter;
    private String cadvisor;
    private String rabbitMQExporter;

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

    public String getAlertmanagerAlias() {
        return alertmanager.split(ID_PATH_SEP)[0];
    }

    public String getAlertManagerImageTag() {
        return alertmanager.split(ID_PATH_SEP)[3];
    }

    public String getAlertManagerImageId() {
        return alertmanager.split(ID_PATH_SEP)[1];
    }

    public String getAlertManagerRepo() {
        return alertmanager.split(ID_PATH_SEP)[2];
    }

    public String getGrafanaAlias() {
        return grafana.split(ID_PATH_SEP)[0];
    }

    public String getGrafanaRepo() {
        return grafana.split(ID_PATH_SEP)[2];
    }

    public String getGrafanaImageId() {
        return grafana.split(ID_PATH_SEP)[1];
    }

    public String getGrafanaImageTag() {
        return grafana.split(ID_PATH_SEP)[3];
    }

    public String getPrometheusAlias() {
        return prometheus.split(ID_PATH_SEP)[0];
    }

    public String getPrometheusImageId() {
        return prometheus.split(ID_PATH_SEP)[1];
    }

    public String getPrometheusRepo() {
        return prometheus.split(ID_PATH_SEP)[2];
    }

    public String getPrometheusImageTag() {
        return prometheus.split(ID_PATH_SEP)[3];
    }

    public String getCITAExporterAlias() {
        return citaExporter.split(ID_PATH_SEP)[0];
    }

    public String getRabbitMQExporterAlias() {
        return rabbitMQExporter.split(ID_PATH_SEP)[0];
    }

    public String getRabbitMQExporterImageId() {
        return rabbitMQExporter.split(ID_PATH_SEP)[1];
    }

    public String getRabbitMQExporterRepo() {
        return rabbitMQExporter.split(ID_PATH_SEP)[2];
    }

    public String getRabbitMQExporterImageTag() {
        return rabbitMQExporter.split(ID_PATH_SEP)[3];
    }

    public String getCadvisorAlias() {
        return cadvisor.split(ID_PATH_SEP)[0];
    }

    public String getCadvisorImageId() {
        return cadvisor.split(ID_PATH_SEP)[1];
    }

    public String getCadvisorRepo() {
        return cadvisor.split(ID_PATH_SEP)[2];
    }

    public String getCadvisorImageTag() {
        return cadvisor.split(ID_PATH_SEP)[3];
    }

    public String getCitaExporterAlias() {
        return citaExporter.split(ID_PATH_SEP)[0];
    }

    public String getCitaExporterImageId() {
        return citaExporter.split(ID_PATH_SEP)[1];
    }

    public String getCitaExporterRepo() {
        return citaExporter.split(ID_PATH_SEP)[2];
    }

    public String getCitaExporterImageTag() {
        return citaExporter.split(ID_PATH_SEP)[3];
    }

    public String getNodeExporterAlias() {
        return nodeExporter.split(ID_PATH_SEP)[0];
    }

    public String getNodeExporterImageId() {
        return nodeExporter.split(ID_PATH_SEP)[1];
    }

    public String getNodeExporterRepo() {
        return nodeExporter.split(ID_PATH_SEP)[2];
    }

    public String getNodeExporterImageTag() {
        return nodeExporter.split(ID_PATH_SEP)[3];
    }

    public String getProExporterAlias() {
        return processExporter.split(ID_PATH_SEP)[0];
    }

    public String getProExporterImageId() {
        return processExporter.split(ID_PATH_SEP)[1];
    }

    public String getProExporterRepo() {
        return processExporter.split(ID_PATH_SEP)[2];
    }

    public String getProExporterImageTag() {
        return processExporter.split(ID_PATH_SEP)[3];
    }
}
