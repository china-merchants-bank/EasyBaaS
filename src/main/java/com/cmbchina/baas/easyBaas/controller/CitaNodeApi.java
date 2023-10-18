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

import com.cmbchina.baas.easyBaas.request.BringNodeRequest;
import com.cmbchina.baas.easyBaas.request.UpgradeNodeRequest;
import com.cmbchina.baas.easyBaas.response.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import javax.servlet.http.HttpSession;
import org.hibernate.validator.constraints.Length;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Validated
@Api(value = "citaNode")
@RequestMapping(value = "/easyBaas")
public interface CitaNodeApi {

    @ApiOperation(value = "节点部署", nickname = "deploymentNode", notes = "节点部署", response = Response.class, tags = {"节点操作"})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "节点部署成功", response = Response.class),
            @ApiResponse(code = 400, message = "节点部署失败，系统错误", response = Response.class)})
    @RequestMapping(value = "/citaNode/deployNode",
            produces = {"application/json"},

            method = RequestMethod.POST)
    Response deploymentNode(@ApiParam(value = "节点配置文件") @Valid @RequestPart(value = "nodeConfigFile", required = false) @NotNull MultipartFile nodeConfigFile,
                            @ApiParam(value = "私钥") @RequestParam(value = "privateKey", required = false) @NotBlank @Length(max = 70) String privateKey,
                            @ApiParam(value = "私钥对应地址") @RequestParam(value = "address", required = false) @NotBlank @Length(max = 50) String address,
                            @ApiParam(value = "所需部署节点") @RequestParam(value = "nodeAddress", required = false) @NotBlank
                            @Pattern(regexp = "^((2[0-4]\\d|25[0-5]|[01]?\\d\\d?)\\.){3}(2[0-4]\\d|25[0-5]|[01]?\\d\\d?)$") String nodeAddress,
                            @ApiParam(value = "节点名称") @RequestParam(value = "nodeName", required = false) @NotBlank @Length(max = 16) String nodeName,
                            @ApiParam(value = "服务器用户名") @RequestParam(value = "username", required = false) String username,
                            @ApiParam(value = "服务器密码") @RequestParam(value = "password", required = false) String password,
                            HttpSession session) throws Exception;


    @ApiOperation(value = "节点日志查询", nickname = "queryLog", notes = "节点日志查询", response = Response.class, tags = {"节点操作"})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "节点日志查询成功", response = Response.class),
            @ApiResponse(code = 400, message = "节点日志查询失败，系统错误", response = Response.class)})
    @RequestMapping(value = "/citaNode/queryLog",
            produces = {"application/json"},
            method = RequestMethod.GET)
    Response queryLog(@ApiParam(value = "节点名称") @RequestParam(value = "nodeName", required = true) @NotBlank String nodeName,
                      @ApiParam(value = "节点日志类型") @RequestParam(value = "type", required = true) @NotBlank String logType,
                      @ApiParam(value = "节点日志行数") @RequestParam(value = "num", required = false, defaultValue = "100") @NotNull int num,
                      HttpSession session);


    @ApiOperation(value = "节点启动", nickname = "startNode", notes = "节点启动", response = Response.class, tags = {"节点操作"})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "节点启动成功", response = Response.class),
            @ApiResponse(code = 400, message = "节点启动失败，系统错误", response = Response.class)})
    @RequestMapping(value = "/citaNode/startNode",
            produces = {"application/json"},
            method = RequestMethod.GET)
    Response startNode(@ApiParam(value = "节点名称") @Valid @RequestParam(value = "nodeName", required = false) @NotBlank String nodeName, HttpSession session);


    @ApiOperation(value = "节点停止", nickname = "stopNode", notes = "节点停止", response = Response.class, tags = {"节点操作"})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "节点停止成功", response = Response.class),
            @ApiResponse(code = 400, message = "节点停止失败，系统错误", response = Response.class)})
    @RequestMapping(value = "/citaNode/stopNode",
            produces = {"application/json"},
            method = RequestMethod.GET)
    Response stopNode(@ApiParam(value = "节点名称") @Valid @RequestParam(value = "nodeName", required = false) @NotBlank String nodeName, HttpSession session);

    @ApiOperation(value = "节点删除", nickname = "deleteNode", notes = "节点删除", response = Response.class, tags = {"节点操作"})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "节点删除成功", response = Response.class),
            @ApiResponse(code = 400, message = "节点删除失败，系统错误", response = Response.class)})
    @RequestMapping(value = "/citaNode/deleteNode",
            produces = {"application/json"},
            method = RequestMethod.DELETE)
    Response deleteNode(@ApiParam(value = "节点删除") @Valid @RequestParam(value = "nodeName", required = false) @NotBlank String nodeName,
                        @ApiParam(value = "服务器用户名") @RequestParam(value = "username", required = false) @NotBlank String username,
                        @ApiParam(value = "服务器密码") @RequestParam(value = "password", required = false) @NotBlank String password,
                        HttpSession session);

    @ApiOperation(value = "节点列表", nickname = "nodeLists", notes = "节点列表", response = Response.class, tags = {"节点操作"})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "节点列表获取成功", response = Response.class),
            @ApiResponse(code = 400, message = "节点列表获取失败，系统错误", response = Response.class)})
    @RequestMapping(value = "/citaNode/nodeLists",
            produces = {"application/json"},
            consumes = {"application/json"},
            method = RequestMethod.GET)
    Response nodeLists(HttpSession session);


    @ApiOperation(value = "外部节点纳入", nickname = "bringIntoNode", notes = "外部节点纳入", response = Response.class, tags = {"节点操作"})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "外部节点纳入成功", response = Response.class),
            @ApiResponse(code = 400, message = "外部节点纳入失败，系统错误", response = Response.class)})
    @RequestMapping(value = "/citaNode/deploymentExternalExistsNode",
            produces = {"application/json"},
            consumes = {"application/json"},
            method = RequestMethod.POST)
    Response bringIntoNode(@ApiParam(value = "外部节点纳管请求体") @Valid @RequestBody BringNodeRequest bringNodeRequest,
                                   HttpSession session);

    @ApiOperation(value = "节点升级", nickname = "updateNode", notes = "节点升级", response = Response.class, tags = {"节点操作"})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "外部节点纳入成功", response = Response.class),
            @ApiResponse(code = 400, message = "外部节点纳入失败，系统错误", response = Response.class)})
    @RequestMapping(value = "/citaNode/updateNode",
            produces = {"application/json"},
            consumes = {"application/json"},
            method = RequestMethod.POST)

    Response updateNode(@ApiParam(value = "节点升级请求体") @Valid @RequestBody UpgradeNodeRequest upgradeNodeRequest,
                        HttpSession session) throws Exception;

    @ApiOperation(value = "节点重启", nickname = "reStartNode", notes = "节点重启", response = Response.class, tags = {"节点操作"})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "节点重启成功", response = Response.class),
            @ApiResponse(code = 400, message = "节点重启失败，系统错误", response = Response.class)})
    @RequestMapping(value = "/citaNode/reStartNode",
            produces = {"application/json"},
            method = RequestMethod.GET)
    Response reStartNode(@ApiParam(value = "节点名称") @Valid @RequestParam(value = "nodeName", required = false) @NotBlank String nodeName, HttpSession session);
}