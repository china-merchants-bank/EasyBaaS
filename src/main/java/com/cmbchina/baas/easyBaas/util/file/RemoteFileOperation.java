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
 
package com.cmbchina.baas.easyBaas.util.file;

import com.cmbchina.baas.easyBaas.constant.ConstantsContainer;
import com.cmbchina.baas.easyBaas.exception.ErrorCodes;
import com.cmbchina.baas.easyBaas.exception.exceptions.LoginShellException;
import com.cmbchina.baas.easyBaas.model.WebConnectionInfo;
import com.cmbchina.baas.easyBaas.util.connection.SSHConnection;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSchException;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

@Data
@NoArgsConstructor
@Slf4j
public class RemoteFileOperation implements FileOperation {

    private WebConnectionInfo webConnectionInfo;

    private String jschId;

    private SSHConnection sshConnection;

    public RemoteFileOperation(WebConnectionInfo webConnectionInfo, String jschId) {
        this.webConnectionInfo = webConnectionInfo;
        this.jschId = jschId;
    }

    public RemoteFileOperation(WebConnectionInfo webConnectionInfo, String jschId, SSHConnection sshConnection) {
        this.webConnectionInfo = webConnectionInfo;
        this.jschId = jschId;
        this.sshConnection = sshConnection;
    }

    @Override
    public void copyFile(String srcFile, String destFile) {

    }

    @Override
    public void copyFolder(String srcFolder, String destParentPath) {

    }

    @Override
    public void moveFile(String srcFile, String destFile) {

    }

    @Override
    public void moveFolder(String srcFolder, String destParentPath) {

    }

    @Override
    public void deleteFile(String srcFile) throws LoginShellException {
        webConnectionInfo.setChannelType(ConstantsContainer.CHANNEL_TYPE_EXEC);
        ChannelExec channelExec = null;
        try {
            channelExec = (ChannelExec) sshConnection.open(webConnectionInfo, jschId);
            String commond = "rm -rf " + srcFile;
            channelExec.setCommand(commond);
            channelExec.connect();
        } catch (JSchException e) {
            log.error("登录远程服务器{}异常{}", srcFile, e);
            throw new LoginShellException(ErrorCodes.SFTP_LOGIN_FAILED.getMessage());
        }
        try (InputStream inputStream = channelExec.getInputStream()) {
            String result = IOUtil.toString(inputStream, "UTF-8");
            log.info(result);
        } catch (IOException e) {
            log.error("删除远程文件{}异常{}", srcFile, e);
        }
        sshConnection.close(jschId);
    }

    @Override
    public void deleteFolder(String srcFolder) throws LoginShellException, IOException {
        log.info("删除文件{}", srcFolder);
        deleteFile(srcFolder);
    }

    @Override
    public void unzipFile(String srcFile, String destParentPath) {

    }

    @Override
    public void unTarGzFile(String srcFile, String destParentPath) throws JSchException, IOException {
        webConnectionInfo.setChannelType(ConstantsContainer.CHANNEL_TYPE_EXEC);
        ChannelExec channelExec = (ChannelExec) sshConnection.open(webConnectionInfo, jschId);
        String commond = "tar -zxvf " + srcFile + " -C " + destParentPath;
        channelExec.setCommand(commond);
        channelExec.connect();
        try (InputStream inputStream = channelExec.getInputStream()) {
            String result = IOUtil.toString(inputStream, "UTF-8");
            log.info(result);
        }
        sshConnection.close(jschId);
        log.info("解压完成");
    }

    @Override
    public File packingTarGzFile(String srcFile, String destParentPath, String fileName) {

        return null;
    }

    public void uploadFile(String localSrcFile, String remoteDestParentPath) {

    }
}
