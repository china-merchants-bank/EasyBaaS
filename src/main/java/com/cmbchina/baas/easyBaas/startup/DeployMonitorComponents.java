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

package com.cmbchina.baas.easyBaas.startup;

import cn.hutool.core.net.NetUtil;
import com.cmbchina.baas.easyBaas.service.internal.MonitorConfigService;
import com.cmbchina.baas.easyBaas.service.internal.MonitorDeployService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * @Title: DeployMonitorComponents
 * @Description: 部署监控组件（grafana、prometheus、alertmanager）
 * @Date 2021/7/22 9:58
 */
@Slf4j
@Component
public class DeployMonitorComponents implements ApplicationRunner {

    @Autowired
    private MonitorDeployService monitorDeployService;

    @Autowired
    private MonitorConfigService monitorConfigService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("启动时执行部署监控服务组件进程开始");
        try {
            deployMonitorComponents();
            log.info("启动时执行部署监控服务组件进程结束");
        } catch (Exception e) {
            log.warn("启动时执行部署监控服务组件进程出现异常[{}],请联系服务提供方处理", e.getMessage(), e);
        }
    }

    private void deployMonitorComponents() throws Exception {
        log.info("查询当前主机的系统,如果是windows就跳出，如果是linux就继续");
        String osName = System.getProperty("os.name");
        if (!StringUtils.isEmpty(osName) && (osName.contains("windows") || osName.contains("Windows") || osName.contains("Mac") || osName.contains("mac"))) {
            log.info("当前系统为[{}],不支持部署", osName);
            return;
        }
        //获取本地IP
        String ip = NetUtil.getLocalhost().getHostAddress();
        log.info("部署alertmanager");
        monitorDeployService.deployAlertManager(ip, 9093);
        log.info("部署prometheus并调用容器中脚本配置alertmanager地址与端口");
        monitorDeployService.deployPrometheus(ip, 9090);
        log.info("在prometheus中配置alertmanager的地址，如果exporter存在同时配置exporter");
        monitorConfigService.configConfigInPrometheus(ip);
        log.info("部署grafana并调用容器中脚本配置prometheus地址与端口");
        monitorDeployService.deployGrafana(ip, 3000);
        log.info("在grafana中配置prometheus数据源的地址");
        monitorConfigService.configPrometheusDataSourceInGrafana(ip);
    }

}
