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
 
package com.cmbchina.baas.easyBaas.request;

import com.cmbchina.baas.easyBaas.constant.MonitorComponentOperationEnum;
import com.cmbchina.baas.easyBaas.validation.EnumValid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * @Title: MonitorComponentRequest
 * @Description: 监控组件启停请求体
 * @Date 2021/7/27 10:20
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MonitorComponentRequest {
    @NotBlank(message = "启停请求体中组件名称不能为空")
    private String name;

    @NotBlank(message = "启停请求体中组件地址不能为空")
    private String address;

    @EnumValid(target = MonitorComponentOperationEnum.class, message = "操作类型必须为start或stop")
    private String type;
}
