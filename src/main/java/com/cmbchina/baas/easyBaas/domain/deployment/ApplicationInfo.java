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

package com.cmbchina.baas.easyBaas.domain.deployment;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Map;

/**
 * @Title: ApplicationInfo
 * @Description: 应用基础信息
 * @Date 2021/6/24 9:45
 */
@Data
@Builder
@EqualsAndHashCode
@ToString
public class ApplicationInfo {
    private String applicationName;
    private String containerId;

    private String[] command;
    private String status;

    private String imageName;
    private String imageId;

    private String workDir;

    private Long createdTime;

    // 定义bindMounts为key:宿主机文件路径，value:容器内文件路径
    private Map<String, String> bindMounts;
    private Map<String, String> env;
    // 定义bindPorts为key:应用内部端口，value:外部端口
    private Map<Integer, Integer> bindPorts;
    // 定义额外的host配置 key为host,value为ip
    private Map<String, String> extraHosts;

    // 定义额外的pidMode
    private String pidMode;
    // 定义额外的networkMode
    private String networkMode;
    // 定义privileged
    private boolean privileged;
}
