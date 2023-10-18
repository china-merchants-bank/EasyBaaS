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

package com.cmbchina.baas.easyBaas.client.deployment.docker.sdk;

import com.cmbchina.baas.easyBaas.constant.ConstantsContainer;
import com.cmbchina.baas.easyBaas.domain.deployment.ApplicationInfo;
import com.cmbchina.baas.easyBaas.domain.deployment.ImageInfo;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.Mount;
import com.github.dockerjava.api.model.MountType;
import com.github.dockerjava.api.model.Ports;
import com.github.dockerjava.api.model.Volume;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Description: docker-java创建container容器操作专用实现类
 * @Date 2021/6/25 9:47
 */
@Slf4j
public class DockerJavaCreateContainerOperation {

    public static boolean createContainer(ImageInfo imageInfo, ApplicationInfo applicationInfo, DockerClient dockerClient) {
        try {
            CreateContainerResponse createContainerResponse = createCreateContainerCmd(imageInfo, applicationInfo, dockerClient).exec();
            applicationInfo.setContainerId(null != createContainerResponse.getId() ? createContainerResponse.getId() : "");
            String[] warnings = createContainerResponse.getWarnings();
            if (null != warnings && 0 != warnings.length) {
                for (String warning : warnings) {
                    log.warn("创建应用[{}]警告:[{}]", applicationInfo.getApplicationName(), warning);
                }
                return false;
            }
        } catch (Exception e) {
            log.error("创建应用容器失败，错误为[{}]", e.getMessage());
            return false;
        }
        return true;
    }

    private static CreateContainerCmd createCreateContainerCmd(ImageInfo imageInfo, ApplicationInfo applicationInfo, DockerClient dockerClient) {
        CreateContainerCmd createContainerCmd = dockerClient.createContainerCmd(getImageId(imageInfo, applicationInfo)).withName(applicationInfo.getApplicationName())
                .withImage(applicationInfo.getImageName()).withEnv(getEnv(applicationInfo)).withExposedPorts(getExposedPort(applicationInfo))
                .withHostConfig(getHostConfig(applicationInfo)).withVolumes(getVolumes(applicationInfo));
        if (null != applicationInfo.getCommand()) {
            createContainerCmd.withCmd(applicationInfo.getCommand());
        }
        if (null != applicationInfo.getWorkDir()) {
            createContainerCmd.withWorkingDir(applicationInfo.getWorkDir());
        }
        return createContainerCmd;
    }

    private static HostConfig getHostConfig(ApplicationInfo applicationInfo) {
        HostConfig hostConfig = HostConfig.newHostConfig().withExtraHosts(getExtraHosts(applicationInfo)).withMounts(getMountBinds(applicationInfo))
                .withPortBindings(getPortBindings(applicationInfo)).withPrivileged(applicationInfo.isPrivileged());
        if (!StringUtils.isEmpty(applicationInfo.getPidMode())) {
            hostConfig.withPidMode(applicationInfo.getPidMode());
        }
        if (!StringUtils.isEmpty(applicationInfo.getNetworkMode())) {
            hostConfig.withNetworkMode(applicationInfo.getNetworkMode());
        }
        return hostConfig;
    }

    private static String[] getExtraHosts(ApplicationInfo applicationInfo) {
        Map<String, String> extraHostsMap = applicationInfo.getExtraHosts();
        if (CollectionUtils.isEmpty(extraHostsMap)) {
            return new String[]{"localhost:0.0.0.0"};
        }
        String[] extraHosts = new String[extraHostsMap.entrySet().size()];
        int i = 0;
        for (Map.Entry<String, String> extraHostEntry : extraHostsMap.entrySet()) {
            String host = extraHostEntry.getKey();
            String ip = extraHostEntry.getValue();
            extraHosts[i] = host + ":" + ip;
            i++;
        }
        return extraHosts;
    }

    private static List<Volume> getVolumes(ApplicationInfo applicationInfo) {
        List<Volume> volumes = new ArrayList<>();
        Map<String, String> bindMounts = applicationInfo.getBindMounts();
        if (CollectionUtils.isEmpty(bindMounts)) {
            return volumes;
        }
        for (Map.Entry<String, String> bindMountEntry : bindMounts.entrySet()) {
            String destFile = bindMountEntry.getValue();
            Volume volume = new Volume(destFile);
            volumes.add(volume);
        }
        return volumes;
    }

    private static List<ExposedPort> getExposedPort(ApplicationInfo applicationInfo) {
        List<ExposedPort> exposedPortList = new ArrayList<>();
        Map<Integer, Integer> bindPorts = applicationInfo.getBindPorts();
        if (CollectionUtils.isEmpty(bindPorts)) {
            return exposedPortList;
        }
        for (Map.Entry<Integer, Integer> bindPortEntry : bindPorts.entrySet()) {
            Integer privatePort = bindPortEntry.getKey();
            ExposedPort exposedPort = new ExposedPort(privatePort);
            exposedPortList.add(exposedPort);
        }
        return exposedPortList;
    }

    private static Ports getPortBindings(ApplicationInfo applicationInfo) {
        Ports portsBinding = new Ports();
        Map<Integer, Integer> bindPorts = applicationInfo.getBindPorts();
        if (CollectionUtils.isEmpty(bindPorts)) {
            return portsBinding;
        }
        for (Map.Entry<Integer, Integer> bindPortEntry : bindPorts.entrySet()) {
            Integer privatePort = bindPortEntry.getKey();
            Integer publicPort = bindPortEntry.getValue();
            ExposedPort exposedPort = new ExposedPort(privatePort);
            portsBinding.bind(exposedPort, Ports.Binding.bindPort(publicPort));
        }
        return portsBinding;
    }

    private static List<Mount> getMountBinds(ApplicationInfo applicationInfo) {
        List<Mount> mounts = new ArrayList<>();
        Map<String, String> bindMounts = applicationInfo.getBindMounts();
        if (CollectionUtils.isEmpty(bindMounts)) {
            return mounts;
        }
        for (Map.Entry<String, String> bindMountEntry : bindMounts.entrySet()) {
            String srcFile = bindMountEntry.getKey();
            String destFile = bindMountEntry.getValue();
            String[] split = destFile.split(ConstantsContainer.IP_PORT_SEP);
            Mount mount = new Mount();
            if (2 == split.length) {
                mount.withSource(srcFile).withTarget(split[0]).withType(MountType.BIND).withReadOnly(!"rw".equalsIgnoreCase(split[1]));
            } else {
                mount.withSource(srcFile).withTarget(destFile).withType(MountType.BIND).withReadOnly(false);
            }
            mounts.add(mount);
        }
        return mounts;
    }

    private static List<String> getEnv(ApplicationInfo applicationInfo) {
        List<String> envList = new ArrayList<>();
        Map<String, String> envMap = applicationInfo.getEnv();
        if (CollectionUtils.isEmpty(envMap)) {
            return envList;
        }
        for (Map.Entry<String, String> envEntry : envMap.entrySet()) {
            String env = envEntry.getKey() + "=" + envEntry.getValue();
            envList.add(env);
        }
        return envList;
    }

    private static String getImageId(ImageInfo imageInfo, ApplicationInfo applicationInfo) {
        String imageID = imageInfo.getImageID();
        String imageId = applicationInfo.getImageId();
        if (StringUtils.isEmpty(imageID)) {
            return imageId;
        }
        return imageID;
    }
}
