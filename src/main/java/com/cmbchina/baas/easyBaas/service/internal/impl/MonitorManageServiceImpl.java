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
import com.cmbchina.baas.easyBaas.constant.ConstantsContainer;
import com.cmbchina.baas.easyBaas.constant.DeployStatusEnum;
import com.cmbchina.baas.easyBaas.constant.MonitorComponentOperationEnum;
import com.cmbchina.baas.easyBaas.domain.deployment.ApplicationInfo;
import com.cmbchina.baas.easyBaas.exception.ErrorCodes;
import com.cmbchina.baas.easyBaas.mapper.MonitorInfoMapper;
import com.cmbchina.baas.easyBaas.model.MonitorInfo;
import com.cmbchina.baas.easyBaas.request.MonitorComponentRequest;
import com.cmbchina.baas.easyBaas.response.MonitorComponentItemResponse;
import com.cmbchina.baas.easyBaas.response.Response;
import com.cmbchina.baas.easyBaas.service.internal.MonitorManageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * @Title: MonitorManageServiceImpl
 * @Description: 监控管理服务实现类
 * @Date 2021/7/27 9:10
 */
@Slf4j
@Service
public class MonitorManageServiceImpl implements MonitorManageService {

    private static final String START = "started";

    private static final String STOP = "stopped";

    private static final String UP = "Up";

    private static final String DELETE = "deleted";
    @Autowired
    private MonitorInfoMapper monitorInfoMapper;

    @Override
    public Response queryComponentList(HttpSession session) {
        List<MonitorComponentItemResponse> responseList = new ArrayList<>();
        log.info("查询当前部署的所有的监控组件的信息");
        List<MonitorInfo> monitorInfoList = monitorInfoMapper.selectByDeployStatus(DeployStatusEnum.DEPLOYED.getCode());
        if (CollectionUtils.isEmpty(monitorInfoList)) {
            return Response.builder().code(ErrorCodes.OK.getCode()).msg(ErrorCodes.OK.getMessage()).data(responseList).build();
        }
        log.info("将监控组件信息依据ip进行分组处理");
        Map<String, Set<String>> ipNameMap = getIPNameMap(monitorInfoList);
        for (Map.Entry<String, Set<String>> ipName : ipNameMap.entrySet()) {
            String ip = ipName.getKey();
            Set<String> names = ipName.getValue();
            responseList.addAll(getMonitorComponentItemResponseListByIPAndName(ip, names));
        }
        responseList = replaceMonitorName(responseList);
        return Response.builder().code(ErrorCodes.OK.getCode()).msg(ErrorCodes.OK.getMessage()).data(responseList).build();
    }

    @Override
    public Response startOrStopComponent(MonitorComponentRequest monitorComponentRequest, HttpSession session) {
        log.info("在指定的地址查询该组件的containerId和启停的情况");
        MonitorComponentRequest monitorComponent = replaceMonitorName(monitorComponentRequest);
        List<ApplicationInfo> applicationInfoList = PlatformOperation.getDockerJavaPlatformOperation(monitorComponent.getAddress()).listApplications();
        Optional<ApplicationInfo> optional = applicationInfoList.stream().filter(applicationInfo -> applicationInfo.getApplicationName().contains(monitorComponentRequest.getName())).findFirst();
        if (!optional.isPresent()) {
            return Response.builder().code(ErrorCodes.START_OR_STOP_MONITOR_COMPONENT_FAILED_ERROR.getCode()).msg("组件应用不存在").build();
        }
        ApplicationInfo applicationInfo = optional.get();
        String type = monitorComponent.getType();
        if (type.equals(MonitorComponentOperationEnum.START.toString())) {
            return startComponent(applicationInfo, monitorComponent.getAddress());
        }
        return stopComponent(applicationInfo, monitorComponent.getAddress());
    }

    private Response startComponent(ApplicationInfo applicationInfo, String address) {
        if (applicationInfo.getStatus().contains(UP)) {
            return Response.builder().code(ErrorCodes.OK.getCode()).msg(ErrorCodes.OK.getMessage()).build();
        }
        if (PlatformOperation.getDockerJavaPlatformOperation(address).startApplication(applicationInfo)) {
            return Response.builder().code(ErrorCodes.OK.getCode()).msg(ErrorCodes.OK.getMessage()).build();
        }
        return Response.builder().code(ErrorCodes.START_OR_STOP_MONITOR_COMPONENT_FAILED_ERROR.getCode()).msg(ErrorCodes.START_OR_STOP_MONITOR_COMPONENT_FAILED_ERROR.getMessage()).build();
    }

    private Response stopComponent(ApplicationInfo applicationInfo, String address) {
        if (!applicationInfo.getStatus().contains(UP)) {
            return Response.builder().code(ErrorCodes.OK.getCode()).msg(ErrorCodes.OK.getMessage()).build();
        }
        if (PlatformOperation.getDockerJavaPlatformOperation(address).stopApplication(applicationInfo)) {
            return Response.builder().code(ErrorCodes.OK.getCode()).msg(ErrorCodes.OK.getMessage()).build();
        }
        return Response.builder().code(ErrorCodes.START_OR_STOP_MONITOR_COMPONENT_FAILED_ERROR.getCode()).msg(ErrorCodes.START_OR_STOP_MONITOR_COMPONENT_FAILED_ERROR.getMessage()).build();
    }

    private List<MonitorComponentItemResponse> getMonitorComponentItemResponseListByIPAndName(String ip, Set<String> names) {
        List<MonitorComponentItemResponse> responseList = new ArrayList<>();
        List<ApplicationInfo> applicationInfoList = PlatformOperation.getDockerJavaPlatformOperation(ip).listApplications();
        log.info("查询ip下的所有应用并根据名称查询各应用的状态");
        for (String name : names) {
            Optional<ApplicationInfo> optionalApplicationInfo = applicationInfoList.stream().filter(applicationInfo -> applicationInfo.getApplicationName().contains(name)).findFirst();
            if (!optionalApplicationInfo.isPresent()) {
                responseList.add(MonitorComponentItemResponse.builder().name(name).address(ip).status(DELETE).build());
                continue;
            }
            ApplicationInfo applicationInfo = optionalApplicationInfo.get();
            responseList.add(MonitorComponentItemResponse.builder().name(name).sign(false).address(ip).status(applicationInfo.getStatus().contains(UP) ? START : STOP).build());
        }
        return responseList;
    }

    private Map<String, Set<String>> getIPNameMap(List<MonitorInfo> monitorInfoList) {
        Map<String, Set<String>> result = new HashMap<>();
        for (MonitorInfo monitorInfo : monitorInfoList) {
            String ip = monitorInfo.getHost().split(ConstantsContainer.IP_PORT_SEP)[0];
            if (!result.containsKey(ip)) {
                Set<String> set = new HashSet<>();
                result.put(ip, set);
            }
            result.get(ip).add(monitorInfo.getName());
        }
        return result;
    }

    private List<MonitorComponentItemResponse> replaceMonitorName(List<MonitorComponentItemResponse> responseList) {
        for (int i = 0; i < responseList.size(); i++) {
            if ("grafana".equals(responseList.get(i).getName())) {
                responseList.get(i).setName("监控展示组件");
                responseList.get(i).setSign(true);
            }
            if ("prometheus".equals(responseList.get(i).getName())) {
                responseList.get(i).setName("数据收集组件");
            }
            if ("alertmanager".equals(responseList.get(i).getName())) {
                responseList.get(i).setName("监控告警组件");
            }
            replaceMonitorName(responseList.get(i));
        }
        return responseList;
    }

    private void replaceMonitorName(MonitorComponentItemResponse monitorComponentItemResponse) {
        if (monitorComponentItemResponse.getName().contains("cadvisor")) {
            monitorComponentItemResponse.setName(monitorComponentItemResponse.getName().split("_")[1] + "_" + "容器资源组件");
        }
        if (monitorComponentItemResponse.getName().contains("process-exporter")) {
            monitorComponentItemResponse.setName(monitorComponentItemResponse.getName().split("_")[1] + "_" + "进程监控组件");
        }
        if (monitorComponentItemResponse.getName().contains("node-exporter")) {
            monitorComponentItemResponse.setName(monitorComponentItemResponse.getName().split("_")[1] + "_" + "节点监控组件");
        }
        if (monitorComponentItemResponse.getName().contains("rabbitmq-exporter")) {
            monitorComponentItemResponse.setName(monitorComponentItemResponse.getName().split("_")[1] + "_" + "队列监控组件");
        }
        if (monitorComponentItemResponse.getName().contains("cita-exporter")) {
            monitorComponentItemResponse.setName(monitorComponentItemResponse.getName().split("_")[1] + "_" + "CITA监控组件");
        }
    }

    private MonitorComponentRequest replaceMonitorName(MonitorComponentRequest monitorComponentRequest) {
        if ("监控展示组件".equals(monitorComponentRequest.getName())) {
            monitorComponentRequest.setName("grafana");
        }
        if ("数据收集组件".equals(monitorComponentRequest.getName())) {
            monitorComponentRequest.setName("prometheus");
        }
        if ("监控告警组件".equals(monitorComponentRequest.getName())) {
            monitorComponentRequest.setName("alertmanager");
        }
        replaceMonitorNameByNode(monitorComponentRequest);
        return monitorComponentRequest;
    }

    private void replaceMonitorNameByNode(MonitorComponentRequest monitorComponentRequest) {
        if (monitorComponentRequest.getName().contains("容器资源组件")) {
            monitorComponentRequest.setName("cadvisor" + "_" + monitorComponentRequest.getName().split("_")[0]);
        }
        if (monitorComponentRequest.getName().contains("进程监控组件")) {
            monitorComponentRequest.setName("process-exporter" + "_" + monitorComponentRequest.getName().split("_")[0]);
        }
        if (monitorComponentRequest.getName().contains("节点监控组件")) {
            monitorComponentRequest.setName("node-exporter" + "_" + monitorComponentRequest.getName().split("_")[0]);
        }
        if (monitorComponentRequest.getName().contains("队列监控组件")) {
            monitorComponentRequest.setName("rabbitmq-exporter" + "_" + monitorComponentRequest.getName().split("_")[0]);
        }
        if (monitorComponentRequest.getName().contains("CITA监控组件")) {
            monitorComponentRequest.setName("cita-exporter" + "_" + monitorComponentRequest.getName().split("_")[0]);
        }
    }
}
