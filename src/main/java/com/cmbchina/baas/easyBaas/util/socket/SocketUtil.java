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

package com.cmbchina.baas.easyBaas.util.socket;

import com.cmbchina.baas.easyBaas.constant.ConstantsContainer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * @Title: SocketUtil
 * @Description: 使用socket检测端口的可用性(能联通的就不可用 ， 不能连通的可用)
 * @Date 2021/7/1 15:58
 */
@Slf4j
public class SocketUtil {

    public static boolean checkIPPortIsUsed(String ip, int port) {
        if (StringUtils.isEmpty(ip) || !ip.matches(ConstantsContainer.IP_REGIX)) {
            return false;
        }
        Socket socket = new Socket();
        try {
            socket.connect(new InetSocketAddress(ip, port), 3000);
            log.info("IP[{}]端口[{}]可以连通，因此不能使用", ip, port);
            return false;
        } catch (Exception e) {
            log.info("IP[{}]端口[{}]不可以连通，因此可以使用", ip, port);
            return true;
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                log.warn("检测IP端口是否被占用的socket关闭失败，请尝试手动关闭");
            }
        }
    }

}
