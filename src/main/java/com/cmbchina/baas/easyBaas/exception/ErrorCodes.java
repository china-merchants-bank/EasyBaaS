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

package com.cmbchina.baas.easyBaas.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCodes {
    RESOURCE_NOT_FOUND_OR_DELETED("404001", "resource not found or deleted", HttpStatus.NOT_FOUND),

    UNKNOWN_ERROR("500001", "UNKNOWN ERROR", HttpStatus.INTERNAL_SERVER_ERROR),

    CONTEXT_TYPE_ERROR("500000", "请求方式有误", HttpStatus.NOT_IMPLEMENTED),

    //成功
    OK("SUC0000", "成功", HttpStatus.OK),
    //请求参数格式错误
    PARAM_FORMAT_INVALID("EASYBASS101", "请求参数格式错误", HttpStatus.INTERNAL_SERVER_ERROR),
    //请求参数不能为空
    PARAM_IS_EMPTY("EASYBASS102", "请求参数不能为空", HttpStatus.INTERNAL_SERVER_ERROR),
    //请求参数类型错误
    PARAM_TYPE_ERROR("EASYBASS103", "请求参数类型错误", HttpStatus.INTERNAL_SERVER_ERROR),
    //请求参数不完整
    PARAM_NOT_COMPLETE("EASYBASS104", "请求参数不完整", HttpStatus.INTERNAL_SERVER_ERROR),
    //请求参数无效
    PARAM_IS_INVALID("EASYBASS105", "请求参数无效", HttpStatus.INTERNAL_SERVER_ERROR),
    //数据库数据不存在
    DATABASEDATA_NOT_FOUND("EASYBASS201", "数据库数据不存在", HttpStatus.INTERNAL_SERVER_ERROR),
    //数据库数据已存在
    DATABASEDATA_ALREADY_EXIST("EASYBASS202", "数据库数据已存在", HttpStatus.INTERNAL_SERVER_ERROR),
    //文件类型错误
    FILEDATA_TYPE_ERROR("EASYBASS203", "文件类型错误", HttpStatus.INTERNAL_SERVER_ERROR),
    //文件大小超过阈值
    FILEDATA_SIZE_ERROR("EASYBASS204", "文件大小超过阈值", HttpStatus.INTERNAL_SERVER_ERROR),
    //文件数据异常
    FILEDATA_DATA_ERROR("EASYBASS205", "文件数据异常", HttpStatus.INTERNAL_SERVER_ERROR),
    //找不到文件
    FILEDATA_NOT_FOUND("EASYBASS206", "找不到文件", HttpStatus.INTERNAL_SERVER_ERROR),
    //文件复制失败
    FILEDATA_DUPLICATION_ERROR("EASYBASS207", "文件复制失败", HttpStatus.INTERNAL_SERVER_ERROR),
    //文件创建失败
    FILEDATA_CREATE_FAIL("EASYBASS208", "文件创建失败", HttpStatus.INTERNAL_SERVER_ERROR),
    //文件打包失败
    FILEDATA_PACKAGING_FAIL("EASYBASS209", "文件打包失败", HttpStatus.INTERNAL_SERVER_ERROR),
    //文件名错误
    FILEDATA_NAME_ERROR("EASYBASS210", "文件名错误", HttpStatus.INTERNAL_SERVER_ERROR),
    //业务数据异常
    BUSINESS_DATA_ERROR("EASYBASS211", "业务数据异常", HttpStatus.INTERNAL_SERVER_ERROR),
    //文件写入异常
    FILEDATA_WRITE_ERROR("EASYBASS212", "文件写入异常", HttpStatus.INTERNAL_SERVER_ERROR),
    //文件解压失败
    FILEDATA_UNPACKAGING_FAIL("EASYBASS213", "文件解压失败", HttpStatus.INTERNAL_SERVER_ERROR),
    //文件操作失败
    FILEDATA_OPERATOR_FAIL("EASYBASS214", "操作失败", HttpStatus.INTERNAL_SERVER_ERROR),
    //文件内容错误
    FILEDATA_CONTENT_ERROR("EASYBASS215", "文件内容错误", HttpStatus.INTERNAL_SERVER_ERROR),

    // 验证码生成异常
    OUTPUTSTREAM_ERROR("EASYBASS301", "验证码生成异常", HttpStatus.INTERNAL_SERVER_ERROR),
    //未获取到验证码
    GET_VERCODE_ERROR("EASYBASS302", "未获取到验证码", HttpStatus.INTERNAL_SERVER_ERROR),
    //验证码错误
    VALIDATE_VERCODE_ERROR("EASYBASS303", "验证码错误", HttpStatus.INTERNAL_SERVER_ERROR),
    //用户名或密码错误
    USER_LOGIN_ERROR("EASYBASS304", "用户名或密码错误", HttpStatus.INTERNAL_SERVER_ERROR),
    //旧密码不一致
    OLD_PASSWORD_ERROR("EASYBASS305", "旧密码错误", HttpStatus.INTERNAL_SERVER_ERROR),
    //新旧密码不致
    PASSWORD_EQUALS_ERROR("EASYBASS306", "新旧密码一致，无需修改", HttpStatus.INTERNAL_SERVER_ERROR),
    //用户不存在
    USER_NOT_EXIST("EASYBASS307", "用户不存在", HttpStatus.INTERNAL_SERVER_ERROR),
    //登录异常
    LOGIN_FAILED_ERROR("EASYBASS308", "登录异常", HttpStatus.INTERNAL_SERVER_ERROR),
    //退出异常
    LOGINOUT_FAILED_ERROR("EASYBASS309", "退出异常", HttpStatus.INTERNAL_SERVER_ERROR),
    //修改密码异常
    UPDATE_PASSWD_FAILED_ERROR("EASYBASS310", "修改密码异常", HttpStatus.INTERNAL_SERVER_ERROR),
    //修改密码异常
    RESET_PASSWD_FAILED_ERROR("EASYBASS311", "重置密码异常", HttpStatus.INTERNAL_SERVER_ERROR),
    //获取菜单异常
    MENU_OPTION_FAILED_ERROR("EASYBASS312", "获取菜单异常", HttpStatus.INTERNAL_SERVER_ERROR),
    //镜像导入失败
    IMAGE_IMPORT_FAILED_ERROR("EASYBASS313", "镜像导入失败", HttpStatus.INTERNAL_SERVER_ERROR),
    //镜像导入可选项查询失败
    IMAGE_OPTION_FAILED_ERROR("EASYBASS314", "镜像导入可选项查询失败", HttpStatus.INTERNAL_SERVER_ERROR),
    //镜像回退失败
    IMAGE_REMOVE_FAILED_ERROR("EASYBASS315", "镜像回退失败", HttpStatus.INTERNAL_SERVER_ERROR),
    //镜像回退列表查询失败
    IMAGE_REMOVE_LIST_FAILED_ERROR("EASYBASS316", "镜像回退列表查询失败", HttpStatus.INTERNAL_SERVER_ERROR),
    //节点日志查询
    QUERY_LOG_FAILED_ERROR("EASYBASS317", "节点日志查询失败", HttpStatus.INTERNAL_SERVER_ERROR),
    //节点不存在
    NODE_NOT_EXIST("EASYBASS318", "节点不存在", HttpStatus.INTERNAL_SERVER_ERROR),
    //节点启动失败
    NODE_START_FAILED_ERROR("EASYBASS319", "节点启动失败", HttpStatus.INTERNAL_SERVER_ERROR),
    //节点停止失败
    NODE_STOP_FAILED_ERROR("EASYBASS320", "节点停止失败", HttpStatus.INTERNAL_SERVER_ERROR),
    //节点删除失败
    NODE_DELETE_FAILED_ERROR("EASYBASS321", "节点删除失败", HttpStatus.INTERNAL_SERVER_ERROR),
    //节点部署失败
    NODE_DEPLOYMENT_FAILED_ERROR("EASYBASS322", "节点部署失败", HttpStatus.INTERNAL_SERVER_ERROR),
    //Token校验失败
    VALIDATE_TOKEN_FAILED_ERROR("EASYBASS323", "Token校验失败或已过期", HttpStatus.INTERNAL_SERVER_ERROR),
    //节点纳入异常
    MANAGING_EXTERNAL_EXISTS_NODE_ERROR("EASYBASS324", "节点纳入异常", HttpStatus.INTERNAL_SERVER_ERROR),

    //alertmanager创建应用失败
    ALERTMANAGER_DEPLOYMENT_FAILED_ERROR("EASYBASS330", "alertmanager部署失败", HttpStatus.INTERNAL_SERVER_ERROR),
    //grafana创建应用失败
    GRAFANA_DEPLOYMENT_FAILED_ERROR("EASYBASS331", "grafana部署失败", HttpStatus.INTERNAL_SERVER_ERROR),
    //prometheus创建应用失败
    PROMETHEUS_DEPLOYMENT_FAILED_ERROR("EASYBASS332", "prometheus部署失败", HttpStatus.INTERNAL_SERVER_ERROR),

    //查询监控组件列表失败
    QUERY_MONITOR_COMPONENT_FAILED_ERROR("EASYBASS341", "查询监控组件列表失败", HttpStatus.INTERNAL_SERVER_ERROR),
    //启停监控组件失败
    START_OR_STOP_MONITOR_COMPONENT_FAILED_ERROR("EASYBASS342", "启停监控组件失败", HttpStatus.INTERNAL_SERVER_ERROR),
    //配置SMTP服务失败
    CONFIG_SMTP_FAILED_ERROR("EASYBASS343", "配置SMTP服务失败", HttpStatus.INTERNAL_SERVER_ERROR),
    //添加告警接收人异常
    ADD_ALERTER_FAILED_ERROR("EASYBASS344", "添加告警接收人异常", HttpStatus.INTERNAL_SERVER_ERROR),
    //更新告警接收人异常
    UPDATE_ALERTER_FAILED_ERROR("EASYBASS345", "更新告警接收人异常", HttpStatus.INTERNAL_SERVER_ERROR),
    //删除告警接收人异常
    DELETE_ALERTER_FAILED_ERROR("EASYBASS346", "删除告警接收人异常", HttpStatus.INTERNAL_SERVER_ERROR),
    //查询告警接收人列表异常
    QUERY_ALERTER_FAILED_ERROR("EASYBASS347", "查询告警接收人列表异常", HttpStatus.INTERNAL_SERVER_ERROR),
    //用户未登录或登录异常
    USER_LOGIN_FAILED_ERROR("EASYBASS348", "用户未登录或登录异常", HttpStatus.INTERNAL_SERVER_ERROR),
    //服务器登录失败
    SFTP_LOGIN_FAILED("EASYBASS349", "服务器操作失败，登录异常或权限不足", HttpStatus.INTERNAL_SERVER_ERROR),
    //首次管理已部署节点请输入有权限的服务器用户
    NO_LOGIN_SHELL_USER_ERROR("EASYBASS350", "首次管理已部署节点请输入有权限的服务器用户", HttpStatus.INTERNAL_SERVER_ERROR),
    //节点名称已存在或格式不正确
    NODE_NAME_ERROR("EASYBASS351", "节点名称已存在或格式不正确", HttpStatus.INTERNAL_SERVER_ERROR),
    //privateKey或address必须以0x开头
    PRIVATEKEY_ADDRESS_ERROR("EASYBASS352", "privateKey或address必须以0x开头", HttpStatus.INTERNAL_SERVER_ERROR),
    //节点地址已存在
    NODE_ADDRESS_ERROR("EASYBASS353", "该地址已经部署节点", HttpStatus.INTERNAL_SERVER_ERROR),
    //解密异常
    DECRYPT_ERROR("EASYBASS354", "解密异常", HttpStatus.INTERNAL_SERVER_ERROR),
    //节点网络不通
    NETWORK_TELNET_ERROR("EASYBASS355", "节点网络不通", HttpStatus.INTERNAL_SERVER_ERROR),
    //节点版本错误
    NODE_VERSION_ERROR("EASYBASS356", "节点版本错误", HttpStatus.INTERNAL_SERVER_ERROR),
    //创建应用异常
    CREATE_APPLICATION_ERROR("EASYBASS357", "创建应用异常", HttpStatus.INTERNAL_SERVER_ERROR),
    //高度不满足升级条件
    BLOCK_HEIGHT_ERROR("EASYBASS358", "区块高度不满足升级条件", HttpStatus.INTERNAL_SERVER_ERROR),
    //未安装telnet命令
    IS_NOT_HAVE_TELNET("EASYBASS359", "服务器尚未安装telnet命令", HttpStatus.INTERNAL_SERVER_ERROR),
    //高度获取失败
    GET_BLOCK_HEIGHT_ERROR("EASYBASS360", "区块高度获取失败", HttpStatus.INTERNAL_SERVER_ERROR),
    //节点升级失败
    UPGRADE_NODE_FAIL("EASYBASS361", "节点升级失败", HttpStatus.INTERNAL_SERVER_ERROR),
    //解密失败
    PASSWORD_DECRYPT_ERROR("EASYBASS362", "解密失败", HttpStatus.INTERNAL_SERVER_ERROR),
    //服务器操作异常
    SERVER_OPERATOR_ERROR("EASYBASS363", "服务器操作异常", HttpStatus.INTERNAL_SERVER_ERROR),
    //版本已是最新
    CHAIN_VERSION_IS_NEWEST("EASYBASS364", "CITA版本已是最新，无须更新", HttpStatus.INTERNAL_SERVER_ERROR),
    //节点列表获取失败
    GET_NODE_LIST_ERROR("EASYBASS365", "获取节点列表异常", HttpStatus.INTERNAL_SERVER_ERROR),
    //节点重启失败
    NODE_RESTART_FAILED_ERROR("EASYBASS366", "节点重启失败", HttpStatus.INTERNAL_SERVER_ERROR),
    //开启trace日志失败
    OPEN_TRACE_LOG_FAILED_ERROR("EASYBASS367", "开始trace日志失败", HttpStatus.INTERNAL_SERVER_ERROR),
    //关闭trace日志失败
    CLOSE_TRACE_LOG_FAILED_ERROR("EASYBASS368", "关闭trace日志失败", HttpStatus.INTERNAL_SERVER_ERROR),
    //获取trace状态失败
    STATUS_TRACE_LOG_FAILED_ERROR("EASYBASS369", "获取trace状态失败", HttpStatus.INTERNAL_SERVER_ERROR),
    //获取日志文件夹大小失败
    GET_LOG_SIZE_FAILED_ERROR("EASYBASS370", "获取日志文件夹大小失败", HttpStatus.INTERNAL_SERVER_ERROR),
    //清理日志文件失败
    CLEAR_LOG_FILE_FAILED_ERROR("EASYBASS371", "清理日志文件失败", HttpStatus.INTERNAL_SERVER_ERROR),
    //SMTP为空
    SMTP_ISNULL_FAILED_ERROR("EASYBASS372", "请先配置告警服务发件邮箱", HttpStatus.INTERNAL_SERVER_ERROR),
    //查询告警接收人列表异常
    UPDATE_GRAFANA_CONFIG_ERROR("EASYBASS373", "修改监控配置失败", HttpStatus.INTERNAL_SERVER_ERROR),

    //系统内部异常
    SEVER_ERROR("EASYBASS500", "系统内部异常", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;

    ErrorCodes(final String code, final String message, final HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}