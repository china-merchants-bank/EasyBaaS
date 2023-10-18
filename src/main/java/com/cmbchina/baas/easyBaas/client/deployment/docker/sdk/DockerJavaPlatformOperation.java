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

import com.cmbchina.baas.easyBaas.client.deployment.docker.DockerPlatformOperation;
import com.cmbchina.baas.easyBaas.domain.deployment.ApplicationInfo;
import com.cmbchina.baas.easyBaas.domain.deployment.ImageInfo;
import com.cmbchina.baas.easyBaas.util.file.LocalFileOperation;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.ExecCreateCmdResponse;
import com.github.dockerjava.api.command.LoadImageCmd;
import com.github.dockerjava.api.command.RenameContainerCmd;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.ContainerMount;
import com.github.dockerjava.api.model.ContainerPort;
import com.github.dockerjava.api.model.Image;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class DockerJavaPlatformOperation implements DockerPlatformOperation {

    public static final String OPERATION_TYPE = "docker-java";

    private DockerClient dockerClient;

    public DockerJavaPlatformOperation(DockerClient dockerClient) {
        this.dockerClient = dockerClient;
    }

    @Override
    public boolean loadImage(String srcImageFile) {
        try (FileInputStream fileInputStream = new LocalFileOperation().readFile(srcImageFile);
             LoadImageCmd loadImageCmd = dockerClient.loadImageCmd(fileInputStream)) {
            loadImageCmd.exec();
        } catch (Exception e) {
            log.info("导入镜像失败, 失败原因为[{}]", e.getMessage(), e);
            return false;
        }
        return true;
    }

    @Override
    public boolean tagImage(ImageInfo imageInfo) {
        try {
            dockerClient.tagImageCmd(imageInfo.getImageID(), imageInfo.getRepository(), imageInfo.getTag()).exec();
        } catch (Exception e) {
            log.error("镜像打标签失败，失败原因为[{}]", e.getMessage());
            return false;
        }
        return true;
    }

    @Override
    public boolean deleteImage(ImageInfo imageInfo) {
        try {
            dockerClient.removeImageCmd(imageInfo.getImageID()).exec();
        } catch (Exception e) {
            log.error("移除镜像失败，失败原因为[{}]", e.getMessage());
            return false;
        }
        return true;
    }

    @Override
    public List<ImageInfo> listImages() {
        List<ImageInfo> imageInfos = new ArrayList<>();
        try {
            List<Image> images = dockerClient.listImagesCmd().withShowAll(true).exec();
            images.forEach(image -> {
                ImageInfo imageInfo = ImageInfo.builder().imageID(image.getId()).repository(image.getRepoTags()[0].split(":")[0]).tag(image.getRepoTags()[0].split(":")[1]).build();
                imageInfos.add(imageInfo);
            });
        } catch (Exception e) {
            log.error("查询镜像列表失败，失败原因为[{}]", e.getMessage());
        }
        return imageInfos;
    }

    @Override
    public boolean createApplication(ImageInfo imageInfo, ApplicationInfo applicationInfo) {
        return DockerJavaCreateContainerOperation.createContainer(imageInfo, applicationInfo, dockerClient);
    }

    @Override
    public boolean stopApplication(ApplicationInfo applicationInfo) {
        String containerId = queryContainerId(applicationInfo.getApplicationName());
        if (StringUtils.isEmpty(containerId)) {
            return false;
        }
        try {
            dockerClient.stopContainerCmd(containerId).exec();
        } catch (Exception e) {
            log.error("停止应用失败，失败原因为[{}]", e.getMessage());
            return false;
        }
        return true;
    }

    @Override
    public boolean startApplication(ApplicationInfo applicationInfo) {
        log.info("开始启动应用");
        String containerId = queryContainerId(applicationInfo.getApplicationName());
        if (StringUtils.isEmpty(containerId)) {
            return false;
        }
        try {
            dockerClient.startContainerCmd(containerId).exec();
            log.info("启动应用完成");
        } catch (Exception e) {
            log.error("启动应用失败，失败原因为[{}]", e.getMessage());
            return false;
        }
        return true;
    }

    @Override
    public List<ApplicationInfo> listApplications() {
        List<ApplicationInfo> applicationInfos = new ArrayList<>();
        try {
            for (Container container : dockerClient.listContainersCmd().withShowAll(true).exec()) {
                applicationInfos.add(ApplicationInfo.builder().applicationName(container.getNames()[0]).containerId(container.getId())
                        .command(container.getCommand().split(" ")).status(container.getStatus()).createdTime(container.getCreated()).imageName(container.getImage())
                        .imageId(container.getImageId()).bindMounts(getBindMounts(container)).bindPorts(getBindPorts(container)).build());
            }
        } catch (Exception e) {
            log.error("查询应用列表失败，失败原因为[{}]", e.getMessage());
        }
        return applicationInfos;
    }

    @Override
    public boolean deleteApplication(ApplicationInfo applicationInfo) {
        String containerId = queryContainerId(applicationInfo.getApplicationName());
        if (StringUtils.isEmpty(containerId)) {
            return false;
        }
        try {
            dockerClient.removeContainerCmd(containerId).exec();
        } catch (Exception e) {
            log.error("删除应用失败，失败原因为[{}]", e.getMessage());
            return false;
        }
        return true;
    }

    @Override
    public String execApplication(ApplicationInfo applicationInfo, String[] execCmd) throws Exception {
        ExecCreateCmdResponse execCreateCmdResponse = null;
        try {
            execCreateCmdResponse = dockerClient.execCreateCmd(applicationInfo.getContainerId()).withAttachStdout(true)
                    .withTty(true).withPrivileged(true).withAttachStderr(true).withAttachStdin(true)
                    .withCmd(execCmd).exec();
        } catch (Exception e) {
            throw new Exception("容器操作失败");
        }
        DockerJavaExecStartResultCallback callback = new DockerJavaExecStartResultCallback();
        try {
            dockerClient.execStartCmd(execCreateCmdResponse.getId()).withDetach(false).withTty(true).exec(callback).awaitCompletion();
        } catch (Exception e) {
            log.warn("执行过程被异常打断:[{}]", e.getMessage());
        }
        return callback.toString();
    }

    @Override
    public boolean rename(ApplicationInfo applicationInfo, String newName) {
        String containerId = queryContainerId(applicationInfo.getApplicationName());
        if (StringUtils.isEmpty(containerId)) {
            return false;
        }
        try {
            RenameContainerCmd renameContainerCmd = dockerClient.renameContainerCmd(containerId);
            renameContainerCmd.withName(newName);
            renameContainerCmd.exec();
        } catch (Exception e) {
            log.error("容器重命名错误:{}", e);
            return false;
        }
        return true;
    }

    @Override
    public boolean reStartApplication(ApplicationInfo applicationInfo) {
        log.info("开始重启应用");
        String containerId = queryContainerId(applicationInfo.getApplicationName());
        if (StringUtils.isEmpty(containerId)) {
            return false;
        }
        try {
            dockerClient.restartContainerCmd(containerId).exec();
            log.info("启动重启完成");
        } catch (Exception e) {
            log.error("启动重启失败，失败原因为[{}]", e.getMessage());
            return false;
        }
        return true;
    }

    private String queryContainerId(String applicationName) {
        for (ApplicationInfo applicationInfo : listApplications()) {
            if (applicationInfo.getApplicationName().contains(applicationName)) {
                log.info("ContainerId为：{}", applicationInfo.getContainerId());
                return applicationInfo.getContainerId();
            }
        }
        return null;
    }

    private Map<Integer, Integer> getBindPorts(Container container) {
        Map<Integer, Integer> bindPort = new HashMap<>();
        ContainerPort[] ports = container.getPorts();
        if (null == ports || 0 == ports.length) {
            return bindPort;
        }
        Arrays.asList(ports).forEach(containerPort -> {
            bindPort.put(containerPort.getPrivatePort(), containerPort.getPublicPort());
        });
        return bindPort;
    }

    private Map<String, String> getBindMounts(Container container) {
        Map<String, String> bindMount = new HashMap<>();
        List<ContainerMount> mounts = container.getMounts();
        if (CollectionUtils.isEmpty(mounts)) {
            return bindMount;
        }
        mounts.forEach(containerMount -> {
            bindMount.put(containerMount.getSource(), containerMount.getDestination());
        });
        return bindMount;
    }
}