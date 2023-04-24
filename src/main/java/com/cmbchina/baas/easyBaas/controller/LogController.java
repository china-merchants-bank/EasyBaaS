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
import com.cmbchina.baas.easyBaas.request.ClearFileLogsRequest;
import com.cmbchina.baas.easyBaas.request.NodeFileLogRequest;
import com.cmbchina.baas.easyBaas.response.Response;
import com.cmbchina.baas.easyBaas.service.internal.NodeFileLogService;
import com.cmbchina.baas.easyBaas.service.internal.NodeLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.security.NoSuchAlgorithmException;

@RestController
@Validated
@Api(value = "log")
@RequestMapping(value = "/easyBaas")
@Slf4j
public class LogController {

    @Autowired
    NodeLogService nodeLogService;
    @Autowired
    NodeFileLogService nodeFileLogService;

    @ApiOperation(value = "开启trace日志", nickname = "openTrace", notes = "打开节点Trace日志", response = Response.class, tags = {"日志操作"})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "打开节点Trace日志成功", response = Response.class),
            @ApiResponse(code = 400, message = "打开节点Trace日志失败，系统错误", response = Response.class)})
    @RequestMapping(value = "/nodeLog/openTrace",
            produces = {"application/json"},
            method = RequestMethod.GET)
    Response openTrace(@ApiParam(value = "节点名称") @Valid @RequestParam(value = "nodeName", required = false) @NotBlank String nodeName, HttpSession session) {
        try {
            log.info("用户{}开始开启节点trace日志{}", session.getId(), nodeName);
            return nodeLogService.nodeTrace(nodeName, true, session);
        } catch (Exception e) {
            log.error("{}开启节点{}trace日志出现异常{}", session.getId(), nodeName, e.getMessage());
            return Response.builder().code(String.valueOf(ErrorCodes.OPEN_TRACE_LOG_FAILED_ERROR.getCode()))
                    .msg(ErrorCodes.OPEN_TRACE_LOG_FAILED_ERROR.getMessage() + ConstantsContainer.IP_PORT_SEP + e.getMessage()).build();
        }
    }

    @ApiOperation(value = "关闭trace日志", nickname = "closeTrace", notes = "关闭节点Trace日志", response = Response.class, tags = {"日志操作"})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "关闭节点Trace日志成功", response = Response.class),
            @ApiResponse(code = 400, message = "关闭节点Trace日志失败，系统错误", response = Response.class)})
    @RequestMapping(value = "/nodeLog/closeTrace",
            produces = {"application/json"},
            method = RequestMethod.GET)
    Response closeTrace(@ApiParam(value = "节点名称") @Valid @RequestParam(value = "nodeName", required = false) @NotBlank String nodeName, HttpSession session) {
        try {
            log.info("用户{}开始关闭节点trace日志{}", session.getId(), nodeName);
            return nodeLogService.nodeTrace(nodeName, false, session);
        } catch (Exception e) {
            log.error("{}关闭节点{}trace日志出现异常{}", session.getId(), nodeName, e.getMessage());
            return Response.builder().code(String.valueOf(ErrorCodes.CLOSE_TRACE_LOG_FAILED_ERROR.getCode()))
                    .msg(ErrorCodes.CLOSE_TRACE_LOG_FAILED_ERROR.getMessage() + ConstantsContainer.IP_PORT_SEP + e.getMessage()).build();
        }
    }

    @ApiOperation(value = "查询trace状态", nickname = "statusTrace", notes = "查询节点Trace状态", response = Response.class, tags = {"日志操作"})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "查询节点Trace状态成功", response = Response.class),
            @ApiResponse(code = 400, message = "查询节点Trace状态失败，系统错误", response = Response.class)})
    @RequestMapping(value = "/nodeLog/statusTrace",
            produces = {"application/json"},
            method = RequestMethod.GET)
    Response statusTrace(@ApiParam(value = "节点名称") @Valid @RequestParam(value = "nodeName", required = false) @NotBlank String nodeName, HttpSession session) {
        try {
            log.info("用户{}开始获取节点trace状态{}", session.getId(), nodeName);
            return nodeLogService.statusTrace(nodeName, session);
        } catch (Exception e) {
            log.error("{}获取节点{}trace状态出现异常{}", session.getId(), nodeName, e.getMessage());
            return Response.builder().code(String.valueOf(ErrorCodes.STATUS_TRACE_LOG_FAILED_ERROR.getCode()))
                    .msg(ErrorCodes.STATUS_TRACE_LOG_FAILED_ERROR.getMessage() + ConstantsContainer.IP_PORT_SEP + e.getMessage()).build();
        }
    }

    @ApiOperation(value = "获取节点日志文件大小", nickname = "logFileSize", notes = "获取节点日志文件大小", response = Response.class, tags = {"日志操作"})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "获取节点日志文件大小成功", response = Response.class),
            @ApiResponse(code = 400, message = "获取节点日志文件大小失败，系统错误", response = Response.class)})
    @RequestMapping(value = "/nodeLog/logFileSize",
            produces = {"application/json"},
            method = RequestMethod.POST)
    Response getLogFileSize(@ApiParam(value = "节点名称") @Valid @RequestBody NodeFileLogRequest nodeFileLogRequest, HttpSession session) {
        try {
            log.info("用户{}开始获取节点日志文件大小{}", session.getId(), nodeFileLogRequest.getNodeName());
            return nodeFileLogService.getNodeLogFileSize(nodeFileLogRequest.getNodeName(), nodeFileLogRequest.getUsername(), nodeFileLogRequest.getPassword(), session);
        } catch (LoginShellException | NoSuchAlgorithmException e) {
            log.error("远程登录服务器异常：{}", e);
            return Response.builder().code(String.valueOf(ErrorCodes.SFTP_LOGIN_FAILED.getCode())).msg(ErrorCodes.SFTP_LOGIN_FAILED.getMessage()).data(null).build();
        } catch (Exception e) {
            log.error("{}获取节点{}日志文件大小{}", session.getId(), nodeFileLogRequest.getNodeName(), e.getMessage());
            return Response.builder().code(String.valueOf(ErrorCodes.GET_LOG_SIZE_FAILED_ERROR.getCode()))
                    .msg(ErrorCodes.GET_LOG_SIZE_FAILED_ERROR.getMessage() + ConstantsContainer.IP_PORT_SEP + e.getMessage()).build();
        }
    }

    @ApiOperation(value = "清理日志文件", nickname = "clearLogFiles", notes = "清理日志文件", response = Response.class, tags = {"日志操作"})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "清理日志文件成功", response = Response.class),
            @ApiResponse(code = 400, message = "清理日志文件失败，系统错误", response = Response.class)})
    @RequestMapping(value = "/nodeLog/clearLogFiles",
            produces = {"application/json"},
            method = RequestMethod.POST)
    Response clearLogFiles(@ApiParam(value = "节点名称") @Valid @RequestBody ClearFileLogsRequest clearFileLogsRequest, HttpSession session) {
        try {
            log.info("用户{}开始清理日志文件{}", session.getId(), clearFileLogsRequest.getNodeName());
            return nodeFileLogService.clearNodeLogFile(clearFileLogsRequest.getNodeName(), clearFileLogsRequest.getUsername(),
                    clearFileLogsRequest.getPassword(), clearFileLogsRequest.getLine(), session);
        } catch (LoginShellException | NoSuchAlgorithmException e) {
            log.error("远程登录服务器异常：{}", e);
            return Response.builder().code(String.valueOf(ErrorCodes.SFTP_LOGIN_FAILED.getCode())).msg(ErrorCodes.SFTP_LOGIN_FAILED.getMessage()).data(null).build();
        } catch (Exception e) {
            log.error("{}清理{}日志文件失败{}", session.getId(), clearFileLogsRequest.getNodeName(), e.getMessage());
            return Response.builder().code(String.valueOf(ErrorCodes.CLEAR_LOG_FILE_FAILED_ERROR.getCode()))
                    .msg(ErrorCodes.CLEAR_LOG_FILE_FAILED_ERROR.getMessage() + ConstantsContainer.IP_PORT_SEP + e.getMessage()).build();
        }
    }
}
