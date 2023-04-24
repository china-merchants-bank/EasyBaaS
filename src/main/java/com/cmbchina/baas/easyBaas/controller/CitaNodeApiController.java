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
 
package com.cmbchina.baas.easyBaas.controller;

import com.cmbchina.baas.easyBaas.constant.ConstantsContainer;
import com.cmbchina.baas.easyBaas.exception.ErrorCodes;
import com.cmbchina.baas.easyBaas.exception.exceptions.LoginShellException;
import com.cmbchina.baas.easyBaas.exception.exceptions.NoLoginShellUserException;
import com.cmbchina.baas.easyBaas.exception.exceptions.NodeAddressException;
import com.cmbchina.baas.easyBaas.exception.exceptions.NodeNameException;
import com.cmbchina.baas.easyBaas.exception.exceptions.PrivateKeyOrAddressException;
import com.cmbchina.baas.easyBaas.request.BringNodeRequest;
import com.cmbchina.baas.easyBaas.request.UpgradeNodeRequest;
import com.cmbchina.baas.easyBaas.response.Response;
import com.cmbchina.baas.easyBaas.service.internal.BringIntoNodeService;
import com.cmbchina.baas.easyBaas.service.internal.CitaNodeApiService;
import com.cmbchina.baas.easyBaas.service.internal.CitaNodeListService;
import com.cmbchina.baas.easyBaas.service.internal.DeploymentNodeService;
import com.cmbchina.baas.easyBaas.service.internal.ReStartCitaNodeService;
import com.cmbchina.baas.easyBaas.service.internal.UpgradeNodeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.security.NoSuchAlgorithmException;

@RestController
@Slf4j
public class CitaNodeApiController implements CitaNodeApi {

    private final ObjectMapper objectMapper;

    private final HttpServletRequest request;

    @Autowired
    CitaNodeApiService citaNodeApiService;
    @Autowired
    CitaNodeListService citaNodeListService;
    @Autowired
    DeploymentNodeService deploymentNodeService;
    @Autowired
    BringIntoNodeService bringIntoNodeService;
    @Autowired
    UpgradeNodeService nodeUpdateService;
    @Autowired
    ReStartCitaNodeService reStartCitaNodeService;

    @Autowired
    public CitaNodeApiController(ObjectMapper objectMapper, HttpServletRequest request) {
        this.objectMapper = objectMapper;
        this.request = request;
    }

    @Override
    public Response deploymentNode(@ApiParam(value = "节点配置文件") @Valid @RequestPart(value = "nodeConfigFile", required = false) @NotNull MultipartFile nodeConfigFile,
                                   @ApiParam(value = "私钥") @RequestParam(value = "privateKey", required = false) @NotBlank @Length(max = 70) String privateKey,
                                   @ApiParam(value = "私钥对应地址") @RequestParam(value = "address", required = false) @NotBlank @Length(max = 50) String address,
                                   @ApiParam(value = "所需部署节点") @RequestParam(value = "nodeAddress", required = false) @NotBlank
                                   @Pattern(regexp = "^((2[0-4]\\d|25[0-5]|[01]?\\d\\d?)\\.){3}(2[0-4]\\d|25[0-5]|[01]?\\d\\d?)$") String nodeAddress,
                                   @ApiParam(value = "节点名称") @RequestParam(value = "nodeName", required = false) @NotBlank @Length(max = 16) String nodeName,
                                   @ApiParam(value = "服务器用户名") @RequestParam(value = "username", required = false) String username,
                                   @ApiParam(value = "服务器密码") @RequestParam(value = "password", required = false) String password,
                                   HttpSession session) throws NodeNameException, NodeAddressException, PrivateKeyOrAddressException,
            LoginShellException, Exception {
        Response response = null;
        try {
            log.info("用户{}开始部署节点", session.getId());
            response = deploymentNodeService.deploymentNode(nodeConfigFile, privateKey, address, nodeAddress, nodeName, username, password, session);
        } catch (NodeNameException e) {
            response = Response.builder().code(String.valueOf(ErrorCodes.NODE_NAME_ERROR.getCode())).msg(ErrorCodes.NODE_NAME_ERROR.getMessage()).data(null).build();
        } catch (NodeAddressException e) {
            response = Response.builder().code(String.valueOf(ErrorCodes.NODE_ADDRESS_ERROR.getCode())).msg(ErrorCodes.NODE_ADDRESS_ERROR.getMessage()).data(null).build();
        } catch (PrivateKeyOrAddressException e) {
            response = Response.builder().code(String.valueOf(ErrorCodes.PRIVATEKEY_ADDRESS_ERROR.getCode())).msg(ErrorCodes.PRIVATEKEY_ADDRESS_ERROR.getMessage()).data(null).build();
        } catch (LoginShellException | NoSuchAlgorithmException e) {
            log.error("远程登录服务器异常：{}", e);
            response = Response.builder().code(String.valueOf(ErrorCodes.SFTP_LOGIN_FAILED.getCode())).msg(ErrorCodes.SFTP_LOGIN_FAILED.getMessage()).data(null).build();
        } catch (Exception e) {
            log.info("用户{}部署节点异常{}", session.getId(), e);
            response = Response.builder().code(String.valueOf(ErrorCodes.NODE_DEPLOYMENT_FAILED_ERROR.getCode())).msg(ErrorCodes.NODE_DEPLOYMENT_FAILED_ERROR.getMessage()).data(null).build();
        }
        return response;
    }

    @Override
    public Response queryLog(@ApiParam(value = "节点名称") @RequestParam(value = "nodeName", required = true) @NotBlank String nodeName,
                             @ApiParam(value = "节点日志类型") @RequestParam(value = "type", required = true) @NotBlank String logType,
                             @ApiParam(value = "节点日志行数") @RequestParam(value = "num", required = false, defaultValue = "100") @NotNull int num,
                             HttpSession session) {
        try {
            log.info("用户{}开始获取节点日志", session.getId());
            return citaNodeApiService.queryLog(nodeName, logType, num, session);
        } catch (Exception e) {
            log.error("{}获取节点日志异常：{}", session.getId(), e.getMessage());
            return Response.builder().code(String.valueOf(ErrorCodes.QUERY_LOG_FAILED_ERROR.getCode())).
                    msg(ErrorCodes.QUERY_LOG_FAILED_ERROR.getMessage() + ConstantsContainer.IP_PORT_SEP + e.getMessage()).data(null).build();
        }
    }

    @Override
    public Response startNode(@ApiParam(value = "节点名称") @Valid @RequestParam(value = "nodeName", required = false) @NotBlank String nodeName, HttpSession session) {
        try {
            log.info("用户{}开始启动节点{}", session.getId(), nodeName);
            return citaNodeApiService.startNodeInfo(nodeName, session);
        } catch (Exception e) {
            log.error("{}启动节点{}出现异常：{}", session.getId(), nodeName, e.getMessage());
            return Response.builder().code(String.valueOf(ErrorCodes.NODE_START_FAILED_ERROR.getCode())).msg(ErrorCodes.NODE_START_FAILED_ERROR.getMessage()).data(null).build();
        }
    }

    @Override
    public Response stopNode(@ApiParam(value = "节点名称") @Valid @RequestParam(value = "nodeName", required = false) @NotBlank String nodeName, HttpSession session) {
        try {
            log.info("用户{}开始停止节点{}", session.getId(), nodeName);
            return citaNodeApiService.stopNodeInfo(nodeName, session);
        } catch (Exception e) {
            log.error("{}停止节点{}出现异常{}", session.getId(), nodeName, e.getMessage());
            return Response.builder().code(String.valueOf(ErrorCodes.NODE_STOP_FAILED_ERROR.getCode())).msg(ErrorCodes.NODE_STOP_FAILED_ERROR.getMessage()).data(null).build();
        }
    }

    @Override
    public Response deleteNode(@ApiParam(value = "节点删除") @Valid @RequestParam(value = "nodeName", required = false) @NotBlank String nodeName,
                               @ApiParam(value = "服务器用户名") @RequestParam(value = "username", required = false) @NotBlank String username,
                               @ApiParam(value = "服务器密码") @RequestParam(value = "password", required = false) @NotBlank String password,
                               HttpSession session) {
        try {
            log.info("用户{}开始删除节点{}", session.getId(), nodeName);
            return citaNodeApiService.deleteNode(nodeName, session, username, password);
        } catch (Exception e) {
            log.error("{}删除节点{}出现异常{}", session.getId(), nodeName, e.getMessage());
            return Response.builder().code(String.valueOf(ErrorCodes.NODE_DELETE_FAILED_ERROR.getCode())).msg(ErrorCodes.NODE_DELETE_FAILED_ERROR.getMessage()).data(null).build();
        }
    }

    @Override
    public Response nodeLists(HttpSession session) {
        try {
            return citaNodeListService.nodeLists(session);
        } catch (Exception e) {
            log.error("{}获取节点列表出现异常{}", session.getId(), e);
            return Response.builder().code(String.valueOf(ErrorCodes.UPGRADE_NODE_FAIL.getCode()))
                    .msg(ErrorCodes.GET_NODE_LIST_ERROR.getMessage() + ConstantsContainer.IP_PORT_SEP + e.getMessage()).build();
        }
    }

    @Override
    public Response bringIntoNode(@ApiParam(value = "外部节点纳管请求体") @Valid @RequestBody BringNodeRequest bringNodeRequest, HttpSession session) {
        try {
            return bringIntoNodeService.managinExternalExistsNode(bringNodeRequest.getNodeAddress(), bringNodeRequest.getNodeName(), bringNodeRequest.getFilePath(),
                    bringNodeRequest.getUsername(), bringNodeRequest.getPassword(), session);
        } catch (NoLoginShellUserException e) {
            log.error("{}纳入外部节点{}异常{}", session.getId(), bringNodeRequest.getNodeAddress(), e);
            return Response.builder().code(String.valueOf(ErrorCodes.NO_LOGIN_SHELL_USER_ERROR.getCode())).msg(ErrorCodes.NO_LOGIN_SHELL_USER_ERROR.getMessage()).build();
        } catch (Exception e) {
            log.error("{}纳入外部节点{}异常{}", session.getId(), bringNodeRequest.getNodeAddress(), e);
            return Response.builder().code(String.valueOf(ErrorCodes.MANAGING_EXTERNAL_EXISTS_NODE_ERROR.getCode()))
                    .msg(ErrorCodes.MANAGING_EXTERNAL_EXISTS_NODE_ERROR.getMessage() + ConstantsContainer.IP_PORT_SEP + e.getMessage() + ",请手动重启旧节点").build();
        }
    }

    @Override
    public Response updateNode(@ApiParam(value = "节点升级请求体") @Valid @RequestBody UpgradeNodeRequest upgradeNodeRequest,
                               HttpSession session) throws Exception {
        Response response = null;
        try {
            response = nodeUpdateService.updateNode(upgradeNodeRequest.getNodeName(), upgradeNodeRequest.getUsername(), upgradeNodeRequest.getPassword(), session);
        } catch (LoginShellException | NoSuchAlgorithmException e) {
            log.error("远程登录服务器异常：{}", e);
            response = Response.builder().code(String.valueOf(ErrorCodes.SFTP_LOGIN_FAILED.getCode())).msg(ErrorCodes.SFTP_LOGIN_FAILED.getMessage()).data(null).build();

        } catch (Exception e) {
            log.error("{}节点升级异常{}", session.getId(), e);
            response = Response.builder().code(String.valueOf(ErrorCodes.UPGRADE_NODE_FAIL.getCode()))
                    .msg(ErrorCodes.UPGRADE_NODE_FAIL.getMessage() + ConstantsContainer.IP_PORT_SEP + e.getMessage()).build();
        }
        return response;
    }

    @Override
    public Response reStartNode(@Valid @NotBlank String nodeName, HttpSession session) {
        try {
            log.info("用户{}开始重启节点{}", session.getId(), nodeName);
            return reStartCitaNodeService.reStartCita(nodeName, session);
        } catch (Exception e) {
            log.error("{}重启节点{}出现异常{}", session.getId(), nodeName, e.getMessage());
            return Response.builder().code(String.valueOf(ErrorCodes.NODE_RESTART_FAILED_ERROR.getCode()))
                    .msg(ErrorCodes.NODE_RESTART_FAILED_ERROR.getMessage() + ConstantsContainer.IP_PORT_SEP + e.getMessage()).build();
        }
    }
}