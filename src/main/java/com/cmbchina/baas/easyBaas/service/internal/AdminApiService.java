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

import com.cmbchina.baas.easyBaas.request.ChangePasswordRequest;
import com.cmbchina.baas.easyBaas.request.LoginRequest;
import com.cmbchina.baas.easyBaas.response.Response;

import javax.servlet.http.HttpServletResponse;

import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * @description 账户操作逻辑
 * @data 2021/06/24 10:21
 */
public interface AdminApiService {

    /**
     * 验证按生成
     *
     * @param response
     * @param session
     */
    void getVerCode(String type, HttpServletResponse response, HttpSession session) throws IOException;

    /**
     * 用户登录
     *
     * @param loginRequest
     * @return
     */
    Response login(LoginRequest loginRequest, HttpSession session);

    /**
     * 用户退出
     *
     * @param session
     * @return
     */
    Response logout(HttpSession session);

    /**
     * 修改密码
     *
     * @param changePasswordRequest
     * @param session
     * @return
     */
    Response changePassword(ChangePasswordRequest changePasswordRequest, HttpSession session);

}
