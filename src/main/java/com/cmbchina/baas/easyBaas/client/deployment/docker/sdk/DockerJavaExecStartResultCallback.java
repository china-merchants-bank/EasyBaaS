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
 
package com.cmbchina.baas.easyBaas.client.deployment.docker.sdk;

import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.core.command.ExecStartResultCallback;

/**
 * @Description: 接收响应类
 * @Date 2021/7/23 10:59
 */
public class DockerJavaExecStartResultCallback extends ExecStartResultCallback {
    private final StringBuilder log = new StringBuilder();

    @Override
    public void onNext(Frame item) {
        log.append(new String(item.getPayload()));

        super.onNext(item);
    }

    @Override
    public String toString() {
        return log.toString();
    }
}
