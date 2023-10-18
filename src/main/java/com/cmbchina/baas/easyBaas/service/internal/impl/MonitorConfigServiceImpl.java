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
import com.cmbchina.baas.easyBaas.domain.deployment.ApplicationInfo;
import com.cmbchina.baas.easyBaas.exception.ErrorCodes;
import com.cmbchina.baas.easyBaas.mapper.MonitorInfoMapper;
import com.cmbchina.baas.easyBaas.model.MonitorInfo;
import com.cmbchina.baas.easyBaas.response.Response;
import com.cmbchina.baas.easyBaas.service.internal.MonitorConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @Title: MonitorConfigServiceImpl
 * @Description: 监控配置服务实现类
 * @Date 2021/7/23 11:19
 */
@Slf4j
@Service
public class MonitorConfigServiceImpl implements MonitorConfigService {

    @Autowired
    private ImageConfig imageConfig;

    @Autowired
    private MonitorInfoMapper monitorInfoMapper;

    @Override
    public Response configConfigInPrometheus(String ip) throws Exception {
        log.info("查询prometheus的containerid");
        List<ApplicationInfo> applicationInfos = PlatformOperation.getDockerJavaPlatformOperation(ip).listApplications();
        Optional<ApplicationInfo> optionalApplicationInfo = applicationInfos.stream().filter(applicationInfo -> {
            return applicationInfo.getApplicationName().contains(imageConfig.getPrometheusAlias());
        }).findFirst();
        if (!optionalApplicationInfo.isPresent()) {
            throw new Exception("未能查询到prometheus容器");
        }
        ApplicationInfo applicationInfo = optionalApplicationInfo.get();
        log.info("使用模板创建prometheus的配置文件内容");
        String content = preparePrometheusConfigFileContent();
        log.info("调用prometheus的容器exec中的脚本将文件内容写入到配置中");
        PlatformOperation.getDockerJavaPlatformOperation(ip).execApplication(applicationInfo, new String[]{"sh", "writeFile.sh", content});
        log.info("重启prometheus容器");
        PlatformOperation.getDockerJavaPlatformOperation(ip).stopApplication(applicationInfo);
        PlatformOperation.getDockerJavaPlatformOperation(ip).startApplication(applicationInfo);
        return Response.builder().code(ErrorCodes.OK.getCode()).msg(ErrorCodes.OK.getMessage()).build();
    }

    @Override
    public Response configPrometheusDataSourceInGrafana(String ip) throws Exception {
        log.info("查询grafana的containerid");
        List<ApplicationInfo> applicationInfos = PlatformOperation.getDockerJavaPlatformOperation(ip).listApplications();
        Optional<ApplicationInfo> optionalApplicationInfo = applicationInfos.stream().filter(applicationInfo -> {
            return applicationInfo.getApplicationName().contains(imageConfig.getGrafanaAlias());
        }).findFirst();
        if (!optionalApplicationInfo.isPresent()) {
            throw new Exception("未能查询到grafana容器");
        }
        ApplicationInfo applicationInfo = optionalApplicationInfo.get();
        log.info("使用模板创建grafana的配置文件");
        String content = prepareGrafanaConfigFileContent();
        log.info("调用grafana的容器exec中的脚本将文件内容写入到配置中");
        PlatformOperation.getDockerJavaPlatformOperation(ip).execApplication(applicationInfo, new String[]{"sh", "/var/lib/grafana/writeFile.sh", content});
        log.info("重启grafana容器");
        PlatformOperation.getDockerJavaPlatformOperation(ip).stopApplication(applicationInfo);
        PlatformOperation.getDockerJavaPlatformOperation(ip).startApplication(applicationInfo);
        return Response.builder().code(ErrorCodes.OK.getCode()).msg(ErrorCodes.OK.getMessage()).build();
    }

    private String prepareGrafanaConfigFileContent() {
        Yaml yaml = new Yaml(new SafeConstructor(new LoaderOptions()));
        Map<String, Object> map = (LinkedHashMap<String, Object>) yaml.load(this.getClass().getClassLoader().getResourceAsStream("monitor/datasources.yaml"));
        log.info("查询prometheus的地址，若地址不存在则跳过此步骤");
        List<MonitorInfo> monitorInfos = monitorInfoMapper.selectByName(imageConfig.getPrometheusAlias());
        if (CollectionUtils.isEmpty(monitorInfos)) {
            return yaml.dump(map);
        }
        ((Map) ((List) map.get("datasources")).get(0)).put("url", "http://" + monitorInfos.get(0).getHost());
        return yaml.dump(map);
    }

    private String preparePrometheusConfigFileContent() {
        Yaml yaml = new Yaml(new SafeConstructor(new LoaderOptions()));
        Map<String, Object> map = (LinkedHashMap<String, Object>) yaml.load(this.getClass().getClassLoader().getResourceAsStream("monitor/prometheus.yml"));
        log.info("查询alertmanager的地址，若地址不存在则将alerting从map中删除");
        List<MonitorInfo> monitorInfos = monitorInfoMapper.selectByName(imageConfig.getAlertmanagerAlias());
        if (CollectionUtils.isEmpty(monitorInfos)) {
            map.remove("alerting");
        }
        String host = monitorInfos.get(0).getHost();
        ((List) ((Map) ((List) ((Map) ((List) ((Map) map.get("alerting")).get("alertmanagers")).get(0)).get("static_configs")).get(0)).get("targets")).add(host);
        log.info("查询所有exporter的地址，若存在则添加进入配置之中");
        configCITAExporterInPrometheusConfig(map);
        configNodeExporterInPrometheusConfig(map);
        configProcessExporterInPrometheusConfig(map);
        configRabbitMQExporterInPrometheusConfig(map);
        configCadvisorInPrometheusConfig(map);
        return yaml.dump(map);
    }

    private void configCadvisorInPrometheusConfig(Map<String, Object> map) {
        List<MonitorInfo> monitorInfos = monitorInfoMapper.selectByNameLike(imageConfig.getCadvisorAlias());
        if (CollectionUtils.isEmpty(monitorInfos)) {
            return;
        }
        LinkedHashMap linkedHashMap = getMapByNameAndList((List<LinkedHashMap>) map.get("scrape_configs"), "cadvisor");
        ArrayList arrayList = (ArrayList) ((Map) ((ArrayList) linkedHashMap.get("static_configs")).get(0)).get("targets");
        monitorInfos.forEach(monitorInfo -> {
            arrayList.add(monitorInfo.getHost());
        });
    }

    private void configRabbitMQExporterInPrometheusConfig(Map<String, Object> map) {
        List<MonitorInfo> monitorInfos = monitorInfoMapper.selectByNameLike(imageConfig.getRabbitMQExporterAlias());
        if (CollectionUtils.isEmpty(monitorInfos)) {
            return;
        }
        LinkedHashMap linkedHashMap = getMapByNameAndList((List<LinkedHashMap>) map.get("scrape_configs"), "rabbitmq-exporter");
        ArrayList arrayList = (ArrayList) ((Map) ((ArrayList) linkedHashMap.get("static_configs")).get(0)).get("targets");
        monitorInfos.forEach(monitorInfo -> {
            arrayList.add(monitorInfo.getHost());
        });
    }

    private void configProcessExporterInPrometheusConfig(Map<String, Object> map) {
        List<MonitorInfo> monitorInfos = monitorInfoMapper.selectByNameLike(imageConfig.getProExporterAlias());
        if (CollectionUtils.isEmpty(monitorInfos)) {
            return;
        }
        LinkedHashMap linkedHashMap = getMapByNameAndList((List<LinkedHashMap>) map.get("scrape_configs"), "process-exporter");
        ArrayList arrayList = (ArrayList) ((Map) ((ArrayList) linkedHashMap.get("static_configs")).get(0)).get("targets");
        monitorInfos.forEach(monitorInfo -> {
            arrayList.add(monitorInfo.getHost());
        });
    }

    private void configNodeExporterInPrometheusConfig(Map<String, Object> map) {
        List<MonitorInfo> monitorInfos = monitorInfoMapper.selectByNameLike(imageConfig.getNodeExporterAlias());
        if (CollectionUtils.isEmpty(monitorInfos)) {
            return;
        }
        LinkedHashMap linkedHashMap = getMapByNameAndList((List<LinkedHashMap>) map.get("scrape_configs"), "node-exporter");
        ArrayList arrayList = (ArrayList) ((Map) ((ArrayList) linkedHashMap.get("static_configs")).get(0)).get("targets");
        monitorInfos.forEach(monitorInfo -> {
            arrayList.add(monitorInfo.getHost());
        });
    }

    private void configCITAExporterInPrometheusConfig(Map<String, Object> map) {
        List<MonitorInfo> monitorInfos = monitorInfoMapper.selectByNameLike(imageConfig.getCITAExporterAlias());
        if (CollectionUtils.isEmpty(monitorInfos)) {
            return;
        }
        LinkedHashMap linkedHashMap = getMapByNameAndList((List<LinkedHashMap>) map.get("scrape_configs"), "cita-exporter");
        ArrayList arrayList = (ArrayList) ((Map) ((ArrayList) linkedHashMap.get("static_configs")).get(0)).get("targets");
        monitorInfos.forEach(monitorInfo -> {
            arrayList.add(monitorInfo.getHost());
        });
    }

    private LinkedHashMap getMapByNameAndList(List<LinkedHashMap> mapList, String name) {
        for (LinkedHashMap map : mapList) {
            if (map.get("job_name").equals(name)) {
                return map;
            }
        }
        return new LinkedHashMap();
    }
}
