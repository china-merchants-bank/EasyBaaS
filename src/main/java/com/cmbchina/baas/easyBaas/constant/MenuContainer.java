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
 
package com.cmbchina.baas.easyBaas.constant;

/**
 * @description 枚举池：记录需要用到的枚举内容
 * @data 2021/06/24 9:44
 */
public enum MenuContainer {
    /**
     * 部署准备
     */
    DEPLOYMENT_OF_PREPAREDNESS("000", "部署准备"),
    /**
     * 节点管理
     */
    NODE_MANANEMENT("001", "节点管理"),
    /**
     * 监控服务
     */
    MONITOR_SERVER("002", "监控服务");
    /**
     * 描述信息
     */
    private final String sign;

    /**
     * 描述内容
     */
    private final String message;

    MenuContainer(final String sign, final String message) {
        this.sign = sign;
        this.message = message;
    }

    public String getSign() {
        return sign;
    }

    public String getMessage() {
        return message;
    }
}
