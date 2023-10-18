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
 * @Description: 部署状态枚举
 * @Date 2021/6/25 16:43
 */
public enum DeployStatusEnum {
    /**
     * 已部署
     */
    DEPLOYED("1"),
    /**
     * 未部署
     */
    UNDEPLOYED("0");

    private String code;

    DeployStatusEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
