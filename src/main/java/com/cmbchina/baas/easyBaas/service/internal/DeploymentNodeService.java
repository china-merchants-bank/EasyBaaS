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

import com.cmbchina.baas.easyBaas.exception.exceptions.LoginShellException;
import com.cmbchina.baas.easyBaas.exception.exceptions.NodeNameException;
import com.cmbchina.baas.easyBaas.response.Response;
import javax.servlet.http.HttpSession;
import org.springframework.web.multipart.MultipartFile;


public interface DeploymentNodeService {

    Response deploymentNode(MultipartFile nodeConfigFile, String privateKey, String address, String nodeAddress, String nodeName,
                            String username, String password, HttpSession session) throws NodeNameException, LoginShellException, Exception;

}
