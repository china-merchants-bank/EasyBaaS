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
 
package com.cmbchina.baas.easyBaas.util.connection;

import com.cmbchina.baas.easyBaas.model.JschConnectWebInfo;
import com.cmbchina.baas.easyBaas.model.WebConnectionInfo;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class SSHConnectionImpl implements SSHConnection {

    //存放ssh连接信息的map
    public static Map<String, Object> sshMap = new ConcurrentHashMap<>();

    @Override
    public Channel open(WebConnectionInfo webConnectionInfo, String jschInfoId) throws JSchException {
        JSch jSch = new JSch();
        Session session = jSch.getSession(webConnectionInfo.getUserName(), webConnectionInfo.getHost(), webConnectionInfo.getPort());
        session.setConfig("StrictHostKeyChecking", "no");
        session.setConfig("userauth.gssapi-with-mic", "no");
        session.setPassword(webConnectionInfo.getPassword());
        session.setTimeout(webConnectionInfo.getMaxConnectionTime());
        session.connect();
        Channel channel = session.openChannel(webConnectionInfo.getChannelType());
        JschConnectWebInfo jschConnectWebInfo = (JschConnectWebInfo) sshMap.get(jschInfoId);
        if (null != jschConnectWebInfo) {
            jschConnectWebInfo.setSession(session);
            jschConnectWebInfo.setChannel(channel);
        } else {
            jschConnectWebInfo = new JschConnectWebInfo(session, channel);
            sshMap.put(jschInfoId, jschConnectWebInfo);
        }
        log.info("用户{}，使用账户{}连接建立完成", jschInfoId, webConnectionInfo.getUserName());

        return channel;
    }

    @Override
    public void close(String jschInfoId) {
        log.info("用户{}，开始关闭连接", jschInfoId);
        JschConnectWebInfo jschConnectWebInfo = (JschConnectWebInfo) sshMap.get(jschInfoId);
        if (null != jschConnectWebInfo) {
            if (null != jschConnectWebInfo.getChannel()) {
                jschConnectWebInfo.getChannel().disconnect();
            }
            if (null != jschConnectWebInfo.getSession()) {
                jschConnectWebInfo.getSession().disconnect();
            }
        }
        sshMap.remove(jschInfoId);
        log.info("用户{}，连接关闭完成", jschInfoId);
    }

}
