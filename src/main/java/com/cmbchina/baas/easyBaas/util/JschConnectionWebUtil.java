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
 
package com.cmbchina.baas.easyBaas.util;

import com.cmbchina.baas.easyBaas.constant.ConstantsContainer;
import com.cmbchina.baas.easyBaas.model.JschConnectWebInfo;
import com.cmbchina.baas.easyBaas.model.WebConnectionInfo;
import com.cmbchina.baas.easyBaas.util.connection.SSHConnection;
import com.cmbchina.baas.easyBaas.util.connection.SSHConnectionImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class JschConnectionWebUtil {

    @Autowired
    SSHConnection sshConnection;

    //线程池
    private ExecutorService executorService = new ThreadPoolExecutor(0, Integer.MAX_VALUE,
            60L, TimeUnit.SECONDS,
            new SynchronousQueue<Runnable>());

    //初始化连接数据
    public void initConnection(WebSocketSession webSocketSession) {
        JSch jSch = new JSch();
        JschConnectWebInfo jschConnectWebInfo = new JschConnectWebInfo(webSocketSession, jSch);
        SSHConnectionImpl.sshMap.put(getSessionId(webSocketSession), jschConnectWebInfo);
    }

    //处理客户端发送的信息
    public void receiveHandle(String buffer, WebSocketSession session) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        WebConnectionInfo webConnectionInfo = null;
        try {
            webConnectionInfo = objectMapper.readValue(buffer, WebConnectionInfo.class);
            webConnectionInfo.setChannelType(ConstantsContainer.CHANNEL_TYPE_SHELL);
        } catch (Exception e) {
            log.error("用户:{}请求，处理客户端发送的信息数据读取异常{}", getSessionId(session), e.getMessage());
            throw new Exception("处理客户端发送的信息数据读取异常:" + e.getMessage());
        }
        String id = getSessionId(session);
        JschConnectWebInfo jschConnectWebInfo = (JschConnectWebInfo) SSHConnectionImpl.sshMap.get(id);
        if (ConstantsContainer.WEBSSH_OPERATE_CONNECT.equalsIgnoreCase(webConnectionInfo.getOperate())) {
            //连接操作
            connectOperate(jschConnectWebInfo, webConnectionInfo, session);
        } else if (ConstantsContainer.WEBSSH_OPERATE_COMMAND.equalsIgnoreCase(webConnectionInfo.getOperate())) {
            //普通命令
            commandOperate(webConnectionInfo, jschConnectWebInfo, session);
        } else {
            sshConnection.close(id);
        }
    }

    //发送命令
    private void commandOperate(WebConnectionInfo webConnectionInfo, JschConnectWebInfo jschConnectWebInfo, WebSocketSession session) {
        String command = webConnectionInfo.getCommand();
        if (jschConnectWebInfo != null) {
            try {
                tranInfoToSsh(jschConnectWebInfo.getChannel(), command);
            } catch (IOException e) {
                log.error("用户:{}请求，发送命令到终端并读取返回异常{}", getSessionId(session), e.getMessage());
                sshConnection.close(getSessionId(session));
            }
        }
    }

    //连接操作
    private void connectOperate(JschConnectWebInfo jschConnectWebInfo, WebConnectionInfo webConnectionInfo, WebSocketSession session) {
        //启动线程异步处理
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    connectToSSH(jschConnectWebInfo, webConnectionInfo, session);
                } catch (Exception e) {
                    log.error("用户:{}请求，异步建立连接异常{}", getSessionId(session), e.getMessage());
                    sshConnection.close(getSessionId(session));
                }
            }
        });
    }

    private void connectToSSH(JschConnectWebInfo jschConnectWebInfo, WebConnectionInfo webConnectionInfo, WebSocketSession webSocketSession) {
        Channel channel = null;
        try {
            channel = sshConnection.open(webConnectionInfo, String.valueOf(webSocketSession.getAttributes().get(ConstantsContainer.USER_WEB_SSH_KEY)));
            channel.connect(30000);
            jschConnectWebInfo.setChannel(channel);
            tranInfoToSsh(channel, "");
            getReturnMessage(channel, webSocketSession);
        } catch (JSchException | IOException e) {
            log.error("用户:{}请求，建议连接并返回显示异常{}", getSessionId(webSocketSession), e.getMessage());
        } finally {
            sshConnection.close(getSessionId(webSocketSession));
        }
    }

    //发送消息到终端并读取返回值
    private void tranInfoToSsh(Channel channel, String command) throws IOException {
        if (null != channel) {
            OutputStream outputStream = channel.getOutputStream();
            outputStream.write(command.getBytes());
            outputStream.flush();
        }
    }

    //读取终端返回值
    private void getReturnMessage(Channel channel, WebSocketSession webSocketSession) throws IOException {
        try (InputStream inputStream = channel.getInputStream()) {
            //循环读取
            byte[] buffer = new byte[ConstantsContainer.BYTE_LENGTH];
            int i = 0;
            //如果没有数据来，线程会一直阻塞在这个地方等待数据。
            while ((i = inputStream.read(buffer)) != -1) {
                sendMessage(webSocketSession, Arrays.copyOfRange(buffer, 0, i));
            }
        }
    }

    //发送消息
    private void sendMessage(WebSocketSession session, byte[] buffer) throws IOException {
        session.sendMessage(new TextMessage(buffer));
    }

    private String getSessionId(WebSocketSession session) {
        return String.valueOf(session.getAttributes().get(ConstantsContainer.USER_WEB_SSH_KEY));
    }

    //关闭连接
    public void closeSession(WebSocketSession session) {
        sshConnection.close(getSessionId(session));
    }
}