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
 
package com.cmbchina.baas.easyBaas.constant;

/**
 * @description 常量池：记录需要用到的常量
 * @data 2021/06/24 9:44
 */
public class ConstantsContainer {
    //登录验证码标识
    public static final String LOGIN_VER_CODE_SIGN = "loginVerifyCode";
    //修改验证码标识
    public static final String RESET_VER_CODE_SIGN = "resetVerifyCode";
    //用户登录标识
    public static final String USER = "user";
    //用户token标识
    public static final String TOKEN = "token";
    //用户token过期时间标识
    public static final String TOKEN_TIME = "expirestTime";
    //TOKEN校验密钥
    public static final byte[] TOKEN_KEY = "EasyBaasSecret".getBytes();
    //为用户生成随机id的标识
    public static final String USER_WEB_SSH_KEY = "user_key_id";
    //指令的类别:登录
    public static final String WEBSSH_OPERATE_CONNECT = "connect";
    //指令的类别：命令
    public static final String WEBSSH_OPERATE_COMMAND = "command";
    //字节读取长度
    public static final Integer BYTE_LENGTH = 1024;
    //shell方式连接
    public static final String CHANNEL_TYPE_SHELL = "shell";
    //xftp方式连接
    public static final String CHANNEL_TYPE_SFTP = "sftp";
    //exec方式连接
    public static final String CHANNEL_TYPE_EXEC = "exec";
    //节点维护查询状态的操作标识
    public static final String NODE_STATUS = "status";
    //节点维护启动节点的操作标识
    public static final String NODE_OPERATE_START = "start";
    //节点维护停止节点的操作标识
    public static final String NODE_OPERATE_STOP = "stop";
    //节点维护删除节点的操作标识
    public static final String NODE_OPERATE_DELETE = "delete";
    // 数据库中host字段，ip端口的分隔符
    public static final String IP_PORT_SEP = ":";
    // 镜像配置中，镜像目录与镜像ID的分隔符
    public static final String ID_PATH_SEP = ";";
    // docker端口
    public static final String DOCKER_API_PORT = "2375";
    //用户路径
    public static final String USER_HOME_PATH = "/home/userClient";
    //上传文件路径（需要与上面的路径拼接）
    public static final String UPLOAD_FILE_PATH = "/uploadFile";
    //原始压缩包上传以及解压路径（需要与上面的路径拼接）
    public static final String UPLOAD_ORIGINALFILE_PATH = "/original";
    //重新打包文件路径（需要与上面的路径拼接），写两个路径防止重新打包后重名覆盖
    public static final String UPLOAD_PACKINGFILE_PATH = "/packing";
    //文件名后缀分隔符
    public static final String FILE_NAME_SEP = ".";
    //zip文件后缀
    public static final String SUFFIX_FOR_ZIP = "zip";
    //tar.gz文件后缀
    public static final String SUFFIX_FOR_TARGZ = "tar.gz";
    //network.toml文件名
    public static final String NETWORK_TOML_FILE = "network.toml";
    //jsonrpc.toml文件名
    public static final String JSONRPC_TOML_FILE = "jsonrpc.toml";
    //address文件
    public static final String ADDRESS_FILE = "address";
    //privkey文件
    public static final String PRIVKEY_FILE = "privkey";
    //telnet命令
    public static final String COMMAND_TELNET = "(sleep 1;echo flush cmd_$1;sleep 1; echo quit;sleep 1) | telnet";
    //tenlnet结果分割
    public static final String COMMAND_TELNET_RESULT_SEP = "Escape character is '\\^]'.";
    //容器中路径
    public static final String CONTAINER_INTERNAL_PATH = "/home/cmb/cita/cmb-cita-runtime";
    //容器中mq路径
    public static final String CONTAINER_INTERNAL_MQLOG_PATH = "/home/cmb/cita/cmb-cita-runtime/mqlogs";
    //ip校验的正则
    public static final String IP_REGIX = "((2(5[0-5]|[0-4]\\d))|[0-1]?\\d{1,2})(\\.((2(5[0-5]|[0-4]\\d))|[0-1]?\\d{1,2})){3}";
    //AES使用的KEY
    public static final String AES_KEY = "awZM9q5goz8By1ck";
    //AES加密KEY的长度
    public static final Integer AES_KEY_SIZE = 128;
    //AES加密KEY的长度
    public static final String AES = "AES";
    //账号密码类型-client客户端
    public static final String USER_TYPE_CLIENT = "client";
    //账号密码类型-shell
    public static final String USER_TYPE_SHELL = "shell";
    //cadvisor暴露端口
    public static final Integer CADVISOR_PORT = 8899;
    //cadvisor容器端口
    public static final Integer CADVISOR_CON_PORT = 8080;
    //cita-app路径
    public static final String CITA_APP_PATH = "/home/userClient/files/CitaApps.zip";
    //cita-app文件命名
    public static final String CITA_APP_NAME = "CitaApps.zip";
    //shell登录客户端用户名
    public static final String SHELL_USER_NAME = "userClient";
    //连接符
    public static final String CONNECTION_SEP = "_";
    //node_exporter端口
    public static final Integer NODE_EXPORTER_PORT = 9100;
    //PROCESS_EXPORTER端口
    public static final Integer PROCESS_EXPORTER_PORT = 9256;
    //CITA-EXPORTER本地端口
    public static final Integer CITA_EXPORTER_LOCAL_PORT = 1920;
    //CITA-EXPORTER容器端口
    public static final Integer CITA_EXPORTER_CON_PORT = 1923;
    //1337端口
    public static final Integer CITA_PORT = 1337;
    //rabbitMQ端口
    public static final Integer RABBIT_MQ_PORT = 9419;
    //cita-forever进程
    public static final String CITA_FOREVER = "cita-forever";
    //cita-auth进程
    public static final String CITA_AUTH = "cita-auth";
    //cita-chain进程
    public static final String CITA_CHAIN = "cita-chain";
    //cita-jsonrpc进程
    public static final String CITA_JSONRPC = "cita-jsonrpc";
    //cita-jsonrpc进程
    public static final String CITA_EXECTOR = "cita-executor";
    //cita-network进程
    public static final String CITA_NETWORK = "cita-network";
    //cita-bft进程
    public static final String CITA_BFT = "cita-bft";
    //容器运行状态
    public static final String CONTAINER_STATUS = "Up";
    //外部节点纳管类型
    public static final String EXTERNAL_TYPE = "_external";
    //节点更新类型
    public static final String UPDATE_TYPE = "_update";
    //节点日志类型-chain
    public static final String CHAIN_TYPE = "chain";
    //节点升级查询的日志条数
    public static final Integer LOGS_NUM = 5;
    //节点升级查询的日志条数
    public static final Integer TRACE_LOGS_NUM = 15;
    //可升级块高
    public static final Integer CAN_UPGRADE_HEIGHT = 14617145;
    //rpm方式检查是否含有telnet命令
    public static final String RPM_CHECK_IS_HAVE_TELNET = "rpm -qa |grep telnet;";
    //dpkg方式检查是否含有telnet命令
    public static final String DPKG_CHECK_IS_HAVE_TELNET = "dpkg -l | grep telnet;";
    //yum方式检查是否含有telnet命令
    public static final String YUM_CHECK_IS_HAVE_TELNET = "yum list installed | grep telnet;";
    //telnet命令
    public static final String TELNET = "telnet";
    //获取节点版本的命令
    public static final String GET_CITA_VERSION_COMMAND = "curl -X POST --data '{\"jsonrpc\":\"2.0\",\"method\":\"getVersion\",\"params\":[],\"id\":83}' 127.0.0.1:1337";
    //1.4.0版本
    public static final String VERSION_140 = "1.4.0";
    //1.5.0版本
    public static final String VERSION_150 = "1.5.0";
    //BIN方式启动节点的容器前缀
    public static final String BIN_START_CITA_NAME_PREFIX = "cita_run";
    //纳管的节点的标识
    public static final String EXTERNAL_NAME = "External";
    //CITA镜像的名字
    public static final String CITA_IMAGE_NAME = "cita/rivbase_sm2_sm3_recover";
    //节点运行状态
    public static final String CITA_RUN_STATUS = "Up";
    //sha256开头
    public static final String CON_ID_SHA = "sha256:";
    //开启trace日志，老容器的前缀
    public static final String TRACE_PREFIX_OPEN = "openTrace";
    //关闭trace日志，老容器的前缀
    public static final String TRACE_PREFIX_CLOSE = "closeTrace";
    //TRACE标识
    public static final String TRACE = "TRACE";

    public static final String ENCRYPT_INSTANCE = "AES/ECB/PKCS5Padding";
}