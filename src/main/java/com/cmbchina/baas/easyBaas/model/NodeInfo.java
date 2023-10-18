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
 
package com.cmbchina.baas.easyBaas.model;

import lombok.Builder;

@Builder
public class NodeInfo {
    private Integer id;

    private String nodeName;

    private String address;

    private String host;

    private String deployStatus;

    private String version;

    private String networkName;

    private String networkPath;

    private String nodeFolder;

    private String createTime;

    private String updateTime;

    public NodeInfo() {
    }

    public NodeInfo(Integer id, String nodeName, String address, String host, String deployStatus, String version, String networkName, String networkPath, String createTime, String updateTime) {
        this.id = id;
        this.nodeName = nodeName;
        this.address = address;
        this.host = host;
        this.deployStatus = deployStatus;
        this.version = version;
        this.networkName = networkName;
        this.networkPath = networkPath;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    public NodeInfo(Integer id, String nodeName, String address, String host, String deployStatus, String version, String networkName,
                    String networkPath, String nodeFolder, String createTime, String updateTime) {
        this.id = id;
        this.nodeName = nodeName;
        this.address = address;
        this.host = host;
        this.deployStatus = deployStatus;
        this.version = version;
        this.networkName = networkName;
        this.networkPath = networkPath;
        this.nodeFolder = nodeFolder;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getDeployStatus() {
        return deployStatus;
    }

    public void setDeployStatus(String deployStatus) {
        this.deployStatus = deployStatus;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getNetworkName() {
        return networkName;
    }

    public void setNetworkName(String networkName) {
        this.networkName = networkName;
    }

    public String getNetworkPath() {
        return networkPath;
    }

    public void setNetworkPath(String networkPath) {
        this.networkPath = networkPath;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getNodeFolder() {
        return nodeFolder;
    }

    public void setNodeFolder(String nodeFolder) {
        this.nodeFolder = nodeFolder;
    }
}