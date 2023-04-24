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
 
package com.cmbchina.baas.easyBaas.webSocket;

import com.cmbchina.baas.easyBaas.constant.ConstantsContainer;
import com.cmbchina.baas.easyBaas.util.JschConnectionWebUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.CloseStatus;

@Component
@Slf4j
public class WebSSHWebSocketHandler implements WebSocketHandler {

    @Autowired
    JschConnectionWebUtil jschConnectionWebUtil;

    /**
     * 连接以后的回调
     *
     * @param webSocketSession
     * @throws Exception
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession webSocketSession) throws Exception {
        log.info("用户:{},连接webssh", webSocketSession.getAttributes().get(ConstantsContainer.USER_WEB_SSH_KEY));
        jschConnectionWebUtil.initConnection(webSocketSession);
    }

    /**
     * 收到消息以后的回调
     *
     * @param webSocketSession
     * @param webSocketMessage
     * @throws Exception
     */
    @Override
    public void handleMessage(WebSocketSession webSocketSession, WebSocketMessage<?> webSocketMessage) throws Exception {
        if (webSocketMessage instanceof TextMessage) {
            log.info("用户:{},发送命令{}", webSocketSession.getAttributes().get(ConstantsContainer.USER_WEB_SSH_KEY), webSocketMessage.getPayload().toString());
            jschConnectionWebUtil.receiveHandle(((TextMessage) webSocketMessage).getPayload(), webSocketSession);
        }
    }

    /**
     * 出现错误时的回调WebConnectionInfo
     *
     * @param webSocketSession
     * @param throwable
     * @throws Exception
     */
    @Override
    public void handleTransportError(WebSocketSession webSocketSession, Throwable throwable) throws Exception {
        log.error("用户:{}发送命令,出现错误", webSocketSession.getAttributes().get(ConstantsContainer.USER_WEB_SSH_KEY));
        jschConnectionWebUtil.closeSession(webSocketSession);
    }

    /**
     * 关闭连接时的回调
     *
     * @param webSocketSession
     * @param closeStatus
     * @throws Exception
     */
    @Override
    public void afterConnectionClosed(WebSocketSession webSocketSession, CloseStatus closeStatus) throws Exception {
        log.info("用户:{},断开连接", webSocketSession.getAttributes().get(ConstantsContainer.USER_WEB_SSH_KEY));
        jschConnectionWebUtil.closeSession(webSocketSession);
    }

    /**
     * @return
     */
    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
