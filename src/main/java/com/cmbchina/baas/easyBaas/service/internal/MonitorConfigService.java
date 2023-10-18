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
 
package com.cmbchina.baas.easyBaas.service.internal;

import com.cmbchina.baas.easyBaas.response.Response;

/**
 * @Title: MonitorConfigService
 * @Description: 监控配置服务
 * @Date 2021/7/23 11:17
 */
public interface MonitorConfigService {
    Response configConfigInPrometheus(String ip) throws Exception;

    Response configPrometheusDataSourceInGrafana(String ip) throws Exception;
}
