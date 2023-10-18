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
import com.cmbchina.baas.easyBaas.config.ImageConfig;
import com.cmbchina.baas.easyBaas.constant.ConstantsContainer;
import com.cmbchina.baas.easyBaas.constant.DeployStatusEnum;
import com.cmbchina.baas.easyBaas.domain.deployment.ApplicationInfo;
import com.cmbchina.baas.easyBaas.domain.deployment.ImageInfo;
import com.cmbchina.baas.easyBaas.exception.ErrorCodes;
import com.cmbchina.baas.easyBaas.mapper.MonitorInfoMapper;
import com.cmbchina.baas.easyBaas.model.MonitorInfo;
import com.cmbchina.baas.easyBaas.response.Response;
import com.cmbchina.baas.easyBaas.service.internal.MonitorDeployService;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import javax.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Title: MonitorDeployServiceImpl
 * @Description: 监控组件部署实现类
 * @Date 2021/7/22 15:00
 */
@Slf4j
@Service
public class MonitorDeployServiceImpl implements MonitorDeployService {

    @Autowired
    private ImageConfig imageConfig;

    @Autowired
    private MonitorInfoMapper monitorInfoMapper;

    @Autowired
    DeploymentNodeFileServiceImpl deploymentNodeFileService;

    @Value("${prometheus.retention.days:3}")
    String prometheusRetentionDays;

    @Override
    public Response deployPrometheus(String ip, int port) {
        String prometheusAlias = imageConfig.getPrometheusAlias();
        List<MonitorInfo> monitorInfos = monitorInfoMapper.selectByName(prometheusAlias);
        if (!CollectionUtils.isEmpty(monitorInfos) && monitorInfos.get(0).getHost().equals(ip + ConstantsContainer.IP_PORT_SEP + port)) {
            return Response.builder().code(ErrorCodes.OK.getCode()).msg(ErrorCodes.OK.getMessage()).build();
        }
        monitorInfoMapper.deleteByName(prometheusAlias);
        Map<String, String> bindMounts = new HashMap<>();
        bindMounts.put("/home/userClient/files/prometheus/rules/cita.rules", "/etc/prometheus/cita.rules");
        bindMounts.put("/home/userClient/files/prometheus/rules/hardware.rules", "/etc/prometheus/hardware.rules");
        bindMounts.put("/home/userClient/files/prometheus/rules/instance.rules", "/etc/prometheus/instance.rules");
        bindMounts.put("/home/userClient/files/prometheus/rules/process.rules", "/etc/prometheus/process.rules");
        bindMounts.put("/home/userClient/files/prometheus/rules/rabbitmq.rules", "/etc/prometheus/rabbitmq.rules");
        bindMounts.put("/home/userClient/files/prometheus/writeFile.sh", "/prometheus/writeFile.sh");
        Map<Integer, Integer> bindPorts = new HashMap<>();
        bindPorts.put(port, 9090);
        String[] command = new String[]{"--storage.tsdb.retention.time=" + prometheusRetentionDays + "d", "--storage.tsdb.wal-compression",
                "--config.file=/etc/prometheus/prometheus.yml", "--storage.tsdb.path=/prometheus", "--web.enable-lifecycle"};
        ImageInfo imageInfo = ImageInfo.builder().repository(imageConfig.getPrometheusRepo()).imageID(imageConfig.getPrometheusImageId())
                .tag(imageConfig.getPrometheusImageTag()).build();
        ApplicationInfo applicationInfo =
                ApplicationInfo.builder().applicationName(prometheusAlias).imageId(imageInfo.getImageID()).bindMounts(bindMounts)
                        .imageName(imageInfo.getRepository() + ConstantsContainer.IP_PORT_SEP + imageInfo.getTag()).bindPorts(bindPorts)
                        .command(command).build();
        if (deploy(ip, port, imageInfo, applicationInfo, prometheusAlias)) {
            return Response.builder().code(ErrorCodes.OK.getCode()).msg(ErrorCodes.OK.getMessage()).build();
        }
        return Response.builder().code(ErrorCodes.PROMETHEUS_DEPLOYMENT_FAILED_ERROR.getCode())
                .msg(ErrorCodes.PROMETHEUS_DEPLOYMENT_FAILED_ERROR.getMessage()).build();
    }

    @Override
    public Response deployGrafana(String ip, int port) {
        String grafanaAlias = imageConfig.getGrafanaAlias();
        List<MonitorInfo> monitorInfos = monitorInfoMapper.selectByName(grafanaAlias);
        if (!CollectionUtils.isEmpty(monitorInfos) && monitorInfos.get(0).getHost().equals(ip + ConstantsContainer.IP_PORT_SEP + port)) {
            return Response.builder().code(ErrorCodes.OK.getCode()).msg(ErrorCodes.OK.getMessage()).build();
        }
        monitorInfoMapper.deleteByName(grafanaAlias);
        Map<Integer, Integer> bindPorts = new HashMap<>();
        bindPorts.put(port, 3000);
        Map<String, String> bindMounts = new HashMap<>();
        bindMounts.put("/home/userClient/files/grafana/defaults.ini", "/usr/share/grafana/conf/defaults.ini");
        bindMounts.put("/home/userClient/files/grafana/writeFile.sh", "/var/lib/grafana/writeFile.sh");
        bindMounts.put("/home/userClient/files/grafana/dashboards.yaml", "/etc/grafana/provisioning/dashboards/dashboards.yaml");
        bindMounts.put("/home/userClient/files/grafana/dashboards", "/var/lib/grafana/dashboards");
        ImageInfo imageInfo = ImageInfo.builder().repository(imageConfig.getGrafanaRepo()).imageID(imageConfig.getGrafanaImageId())
                .tag(imageConfig.getGrafanaImageTag()).build();
        ApplicationInfo applicationInfo =
                ApplicationInfo.builder().applicationName(grafanaAlias).imageId(imageInfo.getImageID()).bindMounts(bindMounts)
                        .imageName(imageInfo.getRepository() + ConstantsContainer.IP_PORT_SEP + imageInfo.getTag()).bindPorts(bindPorts).build();
        if (deploy(ip, port, imageInfo, applicationInfo, grafanaAlias)) {
            return Response.builder().code(ErrorCodes.OK.getCode()).msg(ErrorCodes.OK.getMessage()).build();
        }
        return Response.builder().code(ErrorCodes.GRAFANA_DEPLOYMENT_FAILED_ERROR.getCode())
                .msg(ErrorCodes.GRAFANA_DEPLOYMENT_FAILED_ERROR.getMessage()).build();
    }

    @Override
    public Response deployAlertManager(String ip, int port) {
        String alertManagerAlias = imageConfig.getAlertmanagerAlias();
        List<MonitorInfo> monitorInfos = monitorInfoMapper.selectByName(alertManagerAlias);
        if (!CollectionUtils.isEmpty(monitorInfos) && monitorInfos.get(0).getHost().equals(ip + ConstantsContainer.IP_PORT_SEP + port)) {
            return Response.builder().code(ErrorCodes.OK.getCode()).msg(ErrorCodes.OK.getMessage()).build();
        }
        monitorInfoMapper.deleteByName(alertManagerAlias);
        Map<String, String> bindMounts = new HashMap<>(1);
        bindMounts.put("/home/userClient/files/alertmanager/writeFile.sh", "/alertmanager/writeFile.sh");
        Map<Integer, Integer> bindPorts = new HashMap<>();
        bindPorts.put(port, 9093);
        ImageInfo imageInfo = ImageInfo.builder().repository(imageConfig.getAlertManagerRepo()).imageID(imageConfig.getAlertManagerImageId())
                .tag(imageConfig.getAlertManagerImageTag()).build();
        ApplicationInfo applicationInfo =
                ApplicationInfo.builder().applicationName(alertManagerAlias).imageId(imageInfo.getImageID()).bindMounts(bindMounts)
                        .imageName(imageInfo.getRepository() + ConstantsContainer.IP_PORT_SEP + imageInfo.getTag()).bindPorts(bindPorts).build();
        if (deploy(ip, port, imageInfo, applicationInfo, alertManagerAlias)) {
            return Response.builder().code(ErrorCodes.OK.getCode()).msg(ErrorCodes.OK.getMessage()).build();
        }
        return Response.builder().code(ErrorCodes.ALERTMANAGER_DEPLOYMENT_FAILED_ERROR.getCode())
                .msg(ErrorCodes.ALERTMANAGER_DEPLOYMENT_FAILED_ERROR.getMessage()).build();
    }

    @Override
    public boolean deployNodeExporter(String ip, int port, String imageName, HttpSession session) {
        ImageInfo imageInfo = ImageInfo.builder().repository(imageConfig.getNodeExporterRepo()).tag(imageConfig.getNodeExporterImageTag())
                .imageID(imageConfig.getNodeExporterImageId()).build();
        /*Map<Integer, Integer> bindPorts = new HashMap<>(1);
        bindPorts.put(ConstantsContainer.NODE_EXPORTER_PORT, ConstantsContainer.NODE_EXPORTER_PORT);*/
        Map<String, String> bindMounts = new HashMap<>();
        bindMounts.put("/", "/host:ro,rslave");
        String[] command = new String[]{"--path.rootfs=/host"};
        String alertManagerAlias = imageConfig.getNodeExporterAlias() + ConstantsContainer.CONNECTION_SEP + imageName;
        ApplicationInfo applicationInfo =
                ApplicationInfo.builder().applicationName(alertManagerAlias).imageId(imageInfo.getImageID()).bindMounts(bindMounts)
                        .workDir("").networkMode("host").pidMode("host")
                        .imageName(imageInfo.getRepository() + ConstantsContainer.IP_PORT_SEP + imageInfo.getTag())
                        .command(command).build();
        deploy(ip, port, imageInfo, applicationInfo, alertManagerAlias);
        log.info("用户{},{}部署完成", session.getId(), "node_exporter");
        return true;
    }

    @Override
    public boolean deployCITAExporter(String ip, int port, String imageName, String chainName,
                                      String nodeId, HttpSession session, String path) throws SftpException, JSchException, Exception {
        ImageInfo imageInfo = ImageInfo.builder().repository(imageConfig.getCitaExporterRepo()).tag(imageConfig.getCitaExporterImageTag())
                .imageID(imageConfig.getCitaExporterImageId()).build();
        Map<Integer, Integer> bindPorts = new HashMap<>(1);
        bindPorts.put(ConstantsContainer.CITA_EXPORTER_CON_PORT, ConstantsContainer.CITA_EXPORTER_LOCAL_PORT);
        Map<String, String> bindMounts = new HashMap<>();
        bindMounts.put("/etc/localtime", "/etc/localtime");
        bindMounts.put("/home/userClient/files/cita-exporter/writeFile.sh", "/config/writeFile.sh");
        bindMounts.put(path + File.separator + chainName + File.separator + nodeId
                , "/data/cita_secp256k1_sha3/" + chainName + File.separator + nodeId);
        Map<String, String> env = new HashMap<>();
        env.put("NODE_IP_PORT", ip + ConstantsContainer.IP_PORT_SEP + ConstantsContainer.CITA_PORT);
        env.put("NODE_DIR", "/data/cita_secp256k1_sha3/" + chainName + File.separator + nodeId);
        String alertManagerAlias = imageConfig.getCitaExporterAlias() + ConstantsContainer.CONNECTION_SEP + imageName;
        ApplicationInfo applicationInfo =
                ApplicationInfo.builder().applicationName(alertManagerAlias).imageId(imageInfo.getImageID()).bindMounts(bindMounts)
                        .workDir("").env(env).pidMode("host")
                        .imageName(imageInfo.getRepository() + ConstantsContainer.IP_PORT_SEP + imageInfo.getTag()).bindPorts(bindPorts).build();
        deploy(ip, port, imageInfo, applicationInfo, alertManagerAlias);
        log.info("用户{},{}部署完成", session.getId(), "cita_exporter");
        PlatformOperation.getDockerJavaPlatformOperation(ip).execApplication(applicationInfo, new String[]{"sh", "writeFile.sh"});
        log.info("用户{},进行cita-exporter脚本执行", session.getId());
        return true;
    }

    @Override
    public boolean deployProcessExporter(String ip, int port, String imageName, HttpSession session) {
        ImageInfo imageInfo = ImageInfo.builder().repository(imageConfig.getProExporterRepo()).tag(imageConfig.getProExporterImageTag())
                .imageID(imageConfig.getProExporterImageId()).build();
        Map<Integer, Integer> bindPorts = new HashMap<>(1);
        bindPorts.put(ConstantsContainer.PROCESS_EXPORTER_PORT, ConstantsContainer.PROCESS_EXPORTER_PORT);
        Map<String, String> bindMounts = new HashMap<>();
        bindMounts.put("/proc", "/host/proc");
        bindMounts.put("/home/userClient/files/process-exporter/config.yml", "/config/config.yml");
        String alertManagerAlias = imageConfig.getProExporterAlias() + ConstantsContainer.CONNECTION_SEP + imageName;
        String[] command = new String[]{"--procfs", "/host/proc", "-config.path", "/config/config.yml"};
        ApplicationInfo applicationInfo =
                ApplicationInfo.builder().applicationName(alertManagerAlias).imageId(imageInfo.getImageID()).command(new String[]{})
                        .bindMounts(bindMounts)
                        .workDir("").privileged(true).command(command)
                        .imageName(imageInfo.getRepository() + ConstantsContainer.IP_PORT_SEP + imageInfo.getTag()).bindPorts(bindPorts).build();
        deploy(ip, port, imageInfo, applicationInfo, alertManagerAlias);
        log.info("用户{},{}部署完成", session.getId(), "process_exporter");
        return true;
    }

    @Override
    public boolean deployCadvisor(String ip, int port, String imageName, HttpSession session) {
        ImageInfo imageInfo = ImageInfo.builder().repository(imageConfig.getCadvisorRepo()).tag(imageConfig.getCadvisorImageTag())
                .imageID(imageConfig.getCadvisorImageId()).build();
        Map<Integer, Integer> bindPorts = new HashMap<>(1);
        bindPorts.put(ConstantsContainer.CADVISOR_CON_PORT, ConstantsContainer.CADVISOR_PORT);
        Map<String, String> bindMounts = new HashMap<>();
        bindMounts.put("/", "/rootfs:ro");
        bindMounts.put("/var/run", "/var/run:rw");
        bindMounts.put("/sys", "/sys:ro");
        bindMounts.put("/var/lib/docker/", "/var/lib/docker:ro");
        String alertManagerAlias = imageConfig.getCadvisorAlias() + ConstantsContainer.CONNECTION_SEP + imageName;
        ApplicationInfo applicationInfo =
                ApplicationInfo.builder().applicationName(alertManagerAlias).imageId(imageInfo.getImageID()).command(new String[]{})
                        .bindMounts(bindMounts)
                        .workDir("").privileged(true).imageName(imageInfo.getRepository() + ConstantsContainer.IP_PORT_SEP + imageInfo.getTag())
                        .bindPorts(bindPorts).build();
        deploy(ip, port, imageInfo, applicationInfo, alertManagerAlias);
        log.info("用户{},{}部署完成", session.getId(), "cadvisor");
        return true;
    }

    public boolean deploy(String ip, int port, ImageInfo imageInfo, ApplicationInfo applicationInfo, String applicationAlias) {
        PlatformOperation platformOperation = PlatformOperation.getDockerJavaPlatformOperation(ip);
        if (platformOperation.createApplication(imageInfo, applicationInfo) && platformOperation.startApplication(applicationInfo)) {
            MonitorInfo monitorInfo = MonitorInfo.builder().name(applicationAlias).host(ip + ConstantsContainer.IP_PORT_SEP + port)
                    .deployStatus(DeployStatusEnum.DEPLOYED.getCode()).createTime(String.valueOf(System.currentTimeMillis()))
                    .updateTime(String.valueOf(System.currentTimeMillis())).build();
            monitorInfoMapper.insertSelective(monitorInfo);
            return true;
        }
        return false;
    }
}