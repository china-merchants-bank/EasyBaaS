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
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import javax.servlet.http.HttpSession;


/**
 * @Title: MonitorDeployService
 * @Description: 监控服务部署服务
 * @Date 2021/7/22 14:56
 */
public interface MonitorDeployService {
    Response deployPrometheus(String ip, int port);

    Response deployGrafana(String ip, int port);

    Response deployAlertManager(String ip, int port);

    boolean deployNodeExporter(String ip, int port, String imageName, HttpSession session);

    boolean deployCITAExporter(String ip, int port, String imageName, String chainName, String nodeId, HttpSession session, String path)
            throws SftpException, JSchException, Exception;

    boolean deployProcessExporter(String ip, int port, String imageName, HttpSession session);

    boolean deployCadvisor(String ip, int port, String imageName, HttpSession session);
}
