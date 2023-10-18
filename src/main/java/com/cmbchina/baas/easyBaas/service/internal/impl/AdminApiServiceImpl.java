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
 
package com.cmbchina.baas.easyBaas.service.internal.impl;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.CircleCaptcha;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.crypto.digest.Digester;
import cn.hutool.jwt.JWT;
import com.cmbchina.baas.easyBaas.constant.ConstantsContainer;
import com.cmbchina.baas.easyBaas.exception.ErrorCodes;
import com.cmbchina.baas.easyBaas.mapper.UserMapper;
import com.cmbchina.baas.easyBaas.model.User;
import com.cmbchina.baas.easyBaas.request.ChangePasswordRequest;
import com.cmbchina.baas.easyBaas.request.LoginRequest;
import com.cmbchina.baas.easyBaas.response.Response;
import com.cmbchina.baas.easyBaas.service.internal.AdminApiService;
import javax.servlet.ServletOutputStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class AdminApiServiceImpl implements AdminApiService {

    @Autowired
    UserMapper userMapper;

    @Value("${aes.key}")
    private String algorithmCode;

    @Override
    public void getVerCode(String type, HttpServletResponse response, HttpSession session) throws IOException {
        try {
            CircleCaptcha circleCaptcha = CaptchaUtil.createCircleCaptcha(80, 40, 4, 20);
            String verCode = circleCaptcha.getCode();
            log.info("{}请求生成验证码为：{}", session.getId(), verCode);
            if (type.equalsIgnoreCase(ConstantsContainer.LOGIN_VER_CODE_SIGN)) {
                session.setAttribute(session.getId() + ":" + ConstantsContainer.LOGIN_VER_CODE_SIGN, verCode);
            } else {
                session.setAttribute(session.getId() + ":" + ConstantsContainer.RESET_VER_CODE_SIGN, verCode);
            }
            try (ServletOutputStream outputStream = response.getOutputStream()) {
                response.setContentType("application/octet-stream");
                circleCaptcha.write(outputStream);
            }
        } catch (IOException e) {
            log.error("用户{},请求生成验证码异常{}", session.getId(), e);
            throw new IOException("生成验证码出现异常");
        }
    }

    @Override
    public Response login(LoginRequest loginRequest, HttpSession session) {
        String sessionId = session.getId();
        //1.校验验证码
        Response response = validateVerifyCode(session, sessionId, ConstantsContainer.LOGIN_VER_CODE_SIGN, loginRequest.getVerifyCode());
        if (null != response) {
            return response;
        }
        //2.查询数据库校验密码
        User user = userMapper.selectByUserNameAndPassWord(new User(loginRequest.getUserName(),
                new Digester(DigestAlgorithm.SHA256).digestHex(loginRequest.getPassword()), ConstantsContainer.USER_TYPE_CLIENT));
        if (null == user) {
            log.info("{}登录，账号或密码不正确", sessionId);
            session.removeAttribute(sessionId + ":" + ConstantsContainer.LOGIN_VER_CODE_SIGN);
            return Response.builder().code(String.valueOf(ErrorCodes.USER_LOGIN_ERROR.getCode())).msg(ErrorCodes.USER_LOGIN_ERROR.getMessage()).data(null).build();
        }
        String token = getToken(sessionId, loginRequest.getUserName(), session);

        Map<String, Object> returnMap = new HashMap<>(1);
        returnMap.put("token", token);
        returnMap.put("algorithmCode", algorithmCode);
        //清理与记录信息
        session.removeAttribute(sessionId + ":" + ConstantsContainer.LOGIN_VER_CODE_SIGN);
        session.setAttribute(sessionId + ":" + ConstantsContainer.USER, new User(user.getUserName()));
        return Response.builder().code(String.valueOf(ErrorCodes.OK.getCode())).msg(ErrorCodes.OK.getMessage()).data(returnMap).build();
    }

    @Override
    public Response logout(HttpSession session) {
        String sessionId = session.getId();
        log.info("{}进行退出操作", sessionId);
        session.removeAttribute(sessionId + ":" + ConstantsContainer.USER);
        session.removeAttribute(sessionId + ":" + ConstantsContainer.TOKEN);
        session.removeAttribute(sessionId + ":" + ConstantsContainer.TOKEN_TIME);
        return Response.builder().code(String.valueOf(ErrorCodes.OK.getCode())).msg(ErrorCodes.OK.getMessage()).data(null).build();
    }

    @Override
    public Response changePassword(ChangePasswordRequest changePasswordRequest, HttpSession session) {
        String sessionId = session.getId();
        log.info("{}进行修改密码操作", sessionId);
        //session中获取用户信息
        User oldUser = (User) session.getAttribute(sessionId + ":" + ConstantsContainer.USER);
        //1.校验老密码是否一致
        User dbUser = userMapper.selectByUserNameAndPassWord(new User(oldUser.getUserName(),
                new Digester(DigestAlgorithm.SHA256).digestHex(changePasswordRequest.getOldPassword()), ConstantsContainer.USER_TYPE_CLIENT));
        if (null == dbUser) {
            log.info("{}修改密码，旧密码不正确", sessionId);
            return Response.builder().code(String.valueOf(ErrorCodes.OLD_PASSWORD_ERROR.getCode())).msg(ErrorCodes.OLD_PASSWORD_ERROR.getMessage()).data(null).build();
        }
        if (changePasswordRequest.getNewPassword().equals(changePasswordRequest.getOldPassword())) {
            return Response.builder().code(String.valueOf(ErrorCodes.PASSWORD_EQUALS_ERROR.getCode())).msg(ErrorCodes.PASSWORD_EQUALS_ERROR.getMessage()).data(null).build();
        }
        //2.修改新密码
        dbUser.setPassword(new Digester(DigestAlgorithm.SHA256).digestHex(changePasswordRequest.getNewPassword()));
        dbUser.setUpdateTime(String.valueOf(System.currentTimeMillis()));
        userMapper.updateByPrimaryKeySelective(dbUser);
        return Response.builder().code(String.valueOf(ErrorCodes.OK.getCode())).msg(ErrorCodes.OK.getMessage()).data(null).build();
    }

    //生成token
    private String getToken(String sessionId, String userName, HttpSession session) {
        LocalDateTime localDateTime = LocalDateTime.now();
        LocalDateTime nextDateTime = localDateTime.minusDays(-1);
        Date expiresTime = Date.from(nextDateTime.atZone(ZoneId.systemDefault()).toInstant());
        session.setAttribute(sessionId + ":" + ConstantsContainer.TOKEN_TIME, expiresTime);
        String token = JWT.create().setPayload("sessionId", sessionId).setPayload("userName", userName)
                .setExpiresAt(expiresTime).setPayload("date", new Date()).setKey(ConstantsContainer.TOKEN_KEY).sign();
        session.setAttribute(sessionId + ":" + ConstantsContainer.TOKEN, token);
        return token;
    }

    private Response validateVerifyCode(HttpSession session, String sessionId, String sign, String requestVerifyCode) {
        String verCode = (String) session.getAttribute(sessionId + ":" + sign);
        if (StrUtil.isBlank(verCode)) {
            log.info("{}后台获取不到验证码", sessionId);
            session.removeAttribute(sessionId + ":" + sign);
            return Response.builder().code(String.valueOf(ErrorCodes.GET_VERCODE_ERROR.getCode())).msg(ErrorCodes.GET_VERCODE_ERROR.getMessage()).data(null).build();
        }
        log.info("{}传递的验证码为:{},后台记录的验证码为{}", sessionId, requestVerifyCode, verCode);
        if (!verCode.equalsIgnoreCase(requestVerifyCode)) {
            log.info("{}验证码不一致", sessionId);
            session.removeAttribute(sessionId + ":" + sign);
            return Response.builder().code(String.valueOf(ErrorCodes.VALIDATE_VERCODE_ERROR.getCode())).msg(ErrorCodes.VALIDATE_VERCODE_ERROR.getMessage()).data(null).build();
        }
        return null;
    }
}