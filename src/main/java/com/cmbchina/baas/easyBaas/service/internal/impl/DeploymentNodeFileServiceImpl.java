/**
 * Copyright (c) 2023 招商银行股份有限公司
 * EasyBaaS is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND,
 * EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT,
 * MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.cmbchina.baas.easyBaas.service.internal.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ZipUtil;
import com.cmbchina.baas.easyBaas.constant.ConstantsContainer;
import com.cmbchina.baas.easyBaas.exception.exceptions.FileContentException;
import com.cmbchina.baas.easyBaas.exception.exceptions.FileFormatException;
import com.cmbchina.baas.easyBaas.exception.exceptions.IsNotHaveTelnetException;
import com.cmbchina.baas.easyBaas.exception.exceptions.LoginShellException;
import com.cmbchina.baas.easyBaas.exception.exceptions.NetworkTelnetException;
import com.cmbchina.baas.easyBaas.exception.exceptions.NoLoginShellUserException;
import com.cmbchina.baas.easyBaas.exception.exceptions.UnZipException;
import com.cmbchina.baas.easyBaas.model.NetworkToml;
import com.cmbchina.baas.easyBaas.model.WebConnectionInfo;
import com.cmbchina.baas.easyBaas.util.connection.SSHConnection;
import com.cmbchina.baas.easyBaas.util.file.FileOperation;
import com.cmbchina.baas.easyBaas.util.file.LocalFileOperation;
import com.cmbchina.baas.easyBaas.util.socket.SocketUtil;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import com.moandjiezana.toml.Toml;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

/**
 * @description 部署节点部分文件操作部分
 * @data 2021/07/01 17:22
 */
@Service
@Slf4j
public class DeploymentNodeFileServiceImpl {

    private FileOperation localFileOperation = new LocalFileOperation();

    @Autowired
    SSHConnection sshConnection;

    @Autowired
    ErrorOperationClearServiceImpl deploymentNodeErrorOperationService;

    protected String getUploadFile(String saveFilePath, MultipartFile multipartFile) throws IOException {
        File file = new File(saveFilePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        File destFile = new File(file + File.separator + multipartFile.getOriginalFilename());
        String suffixOfOriginalFile =
                multipartFile.getOriginalFilename().substring(multipartFile.getOriginalFilename().indexOf(ConstantsContainer.FILE_NAME_SEP) + 1);
        if (!ConstantsContainer.SUFFIX_FOR_ZIP.equalsIgnoreCase(suffixOfOriginalFile)) {
            throw new FileFormatException("文件格式不正确");
        }
        try {
            multipartFile.transferTo(destFile);
        } catch (IOException e) {
            log.error("上传文件异常，[{}]", e.getMessage());
            throw new IOException("上传文件异常");
        }
        return destFile.getAbsolutePath();
    }

    protected void unZipOrTarGzFile(String saveFilePath, String originalFilePath)
            throws UnZipException, LoginShellException, JSchException, IOException {
        File file = new File(originalFilePath);
        String suffixOfOriginalFile = file.getName().substring(file.getName().indexOf(ConstantsContainer.FILE_NAME_SEP) + 1);
        if (checkZipFile(file)) {
            localFileOperation.deleteFile(originalFilePath);
            throw new FileContentException("文件内容不正确");
        }
        if (ConstantsContainer.SUFFIX_FOR_ZIP.equalsIgnoreCase(suffixOfOriginalFile)) {
            localFileOperation.unzipFile(originalFilePath, saveFilePath);
        } else {
            throw new FileFormatException("文件格式不正确");
        }

        localFileOperation.deleteFile(originalFilePath);
    }

    //读取network.toml文件中其他节点的IP地址和端口，登录节点机器地址，检测连通性
    protected NetworkToml readFileContentAndTestNetworkConnectivity(File nodeFolder, WebConnectionInfo webConnectionInfo, HttpSession session)
            throws NetworkTelnetException, LoginShellException, IOException, JSchException {
        //读取文件
        File networkFile = new File(nodeFolder, ConstantsContainer.NETWORK_TOML_FILE);
        Toml toml = new Toml().read(networkFile);
        NetworkToml networkToml = toml.to(NetworkToml.class);
        //检查是否包含telnet命令

        //获取访问列表
        List<String> telnetCommandList = new ArrayList<>(networkToml.getPeers().size());
        networkToml.getPeers().forEach(item -> {
            telnetCommandList.add(ConstantsContainer.COMMAND_TELNET + " " + item.getIp() + " " + item.getPort());
        });
        validatePingNetwork(telnetCommandList, webConnectionInfo, session, networkToml);
        return networkToml;
    }

    protected void replaceAddressAndPrivatekey(File nodeFolder, String privateKey, String address) throws IOException {
        File addressFile = new File(nodeFolder, ConstantsContainer.ADDRESS_FILE);
        try (FileWriter fileWriter = new FileWriter(addressFile);
             BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);) {
            bufferedWriter.write(address);
            bufferedWriter.flush();
        }
        File privateKeyFile = new File(nodeFolder, ConstantsContainer.PRIVKEY_FILE);
        try (FileWriter fileWriter = new FileWriter(privateKeyFile);
             BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);) {
            bufferedWriter.write(privateKey);
            bufferedWriter.flush();
        }
    }

    private void validatePingNetwork(List<String> telnetCommandList, WebConnectionInfo webConnectionInfo, HttpSession session,
                                     NetworkToml networkToml)
            throws NetworkTelnetException, LoginShellException, IOException {
        validateSocketNetwork(networkToml, webConnectionInfo.getHost());
        int count = 0;
        for (int i = 0; i < telnetCommandList.size(); i++) {
            ChannelExec channel = null;
            try {
                channel = (ChannelExec) sshConnection.open(webConnectionInfo, session.getId());
                channel.setCommand(telnetCommandList.get(i) + ";");
                channel.connect();
            } catch (JSchException e) {
                throw new LoginShellException(e.getMessage());
            }
            try (InputStream inputStream = channel.getInputStream()) {
                log.info("{}用户监测节点连通性{}", session.getId(), telnetCommandList.get(i) + ";");
                String result = IOUtil.toString(inputStream, "UTF-8");
                if (result.contains("Escape character is")) {
                    count++;
                }
            }
            sshConnection.close(session.getId());
        }
        if (count == 0) {
            throw new NetworkTelnetException("节点网络不通");
        }
    }

    private void validateSocketNetwork(NetworkToml networkToml, String ip) {
        String port = networkToml.getPort() == null ? networkToml.getTls_port() : networkToml.getPort();
        SocketUtil.checkIPPortIsUsed(ip, Integer.valueOf(port));
    }

    protected File packageNewFile(File file) throws IOException {
        File netWorkFolder = file.listFiles()[0];
        String packFilePath = ConstantsContainer.USER_HOME_PATH + ConstantsContainer.UPLOAD_FILE_PATH + ConstantsContainer.UPLOAD_PACKINGFILE_PATH;
        File packFile = new File(packFilePath);
        if (!packFile.exists()) {
            packFile.mkdirs();
        }
        return localFileOperation.packingTarGzFile(netWorkFolder.getAbsolutePath(), packFilePath, netWorkFolder.getName());
    }

    //将打包好的文件上传到远程目录
    protected String sftpPutFileToServer(File packFile, WebConnectionInfo webConnectionInfo, HttpSession session) throws IOException, JSchException
            , SftpException {
        webConnectionInfo.setChannelType(ConstantsContainer.CHANNEL_TYPE_SFTP);
        ChannelSftp channel = (ChannelSftp) sshConnection.open(webConnectionInfo, session.getId());
        channel.connect();
        //tar.gz文件
        String descPath = getMaxSpacePath(webConnectionInfo, session);
        String[] paths = descPath.split(File.separator);
        gotoPathAndMkdirFile(channel, paths);
        try (FileInputStream fileInputStream = new FileInputStream(packFile)) {
            channel.put(fileInputStream, packFile.getName());
        }
        sshConnection.close(session.getId());
        deploymentNodeErrorOperationService.deleteFolder();
        return descPath + File.separator + packFile.getName();
    }

    private void gotoPathAndMkdirFile(ChannelSftp channelSftp, String[] paths) throws SftpException {
        String pointer = "";
        for (String path : paths) {
            if (StrUtil.isEmpty(path)) {
                continue;
            }
            pointer += File.separator + path;
            try {
                channelSftp.cd(pointer);
            } catch (SftpException e) {
                log.error("gotoPathAndMkdirFile error:[{}]", e.getMessage());
                channelSftp.mkdir(pointer);
                channelSftp.cd(pointer);
            }
        }
    }

    //获取远程服务器当前路径
    public String getMaxSpacePath(WebConnectionInfo webConnectionInfo, HttpSession session) throws NoLoginShellUserException {
        String path = null;
        String id = UUID.randomUUID().toString().replaceAll("-", "");

        try {
            webConnectionInfo.setChannelType(ConstantsContainer.CHANNEL_TYPE_SFTP);
            ChannelSftp channel = (ChannelSftp) sshConnection.open(webConnectionInfo, session == null ? id : session.getId());
            channel.connect();
            path = channel.pwd();
            sshConnection.close(session == null ? id : session.getId());
        } catch (JSchException | SftpException e) {
            log.error("获取路径异常{}", e);
            throw new NoLoginShellUserException("获取路径异常");
        }
        return path;
    }

    /**
     * 检查服务器是否包含telnet命令
     */
    public void checkServerIsHaveTelnet(WebConnectionInfo webConnectionInfo, HttpSession session) throws JSchException {
        if (rpmCheck(webConnectionInfo, session).contains(ConstantsContainer.TELNET)) {
            log.info("rpm方式检测telnet通过");
        } else if (dpkgCheck(webConnectionInfo, session).contains(ConstantsContainer.TELNET)) {
            log.info("dpkg方式检测telnet通过");
        } else if (yumCheck(webConnectionInfo, session).contains(ConstantsContainer.TELNET)) {
            log.info("yum方式检测telnet通过");
        } else {
            throw new IsNotHaveTelnetException("请安装telnet命令");
        }

    }

    /**
     * 使用rpm的方式检查
     */
    private String rpmCheck(WebConnectionInfo webConnectionInfo, HttpSession session) throws JSchException {
        String result = null;
        try {
            webConnectionInfo.setChannelType(ConstantsContainer.CHANNEL_TYPE_EXEC);
            ChannelExec channel = (ChannelExec) sshConnection.open(webConnectionInfo, session.getId());
            channel.setCommand(ConstantsContainer.RPM_CHECK_IS_HAVE_TELNET);
            channel.connect();
            InputStream inputStream = channel.getInputStream();
            result = IOUtil.toString(inputStream, "UTF-8");
            inputStream.close();
        } catch (JSchException | IOException e) {
            throw new JSchException("服务器命令执行错误:" + e.getMessage());
        }
        return result;
    }

    /**
     * 使用dpkg的方式检查
     */
    private String dpkgCheck(WebConnectionInfo webConnectionInfo, HttpSession session) throws JSchException {
        String result = null;
        try {
            webConnectionInfo.setChannelType(ConstantsContainer.CHANNEL_TYPE_EXEC);
            ChannelExec channel = (ChannelExec) sshConnection.open(webConnectionInfo, session.getId());
            channel.setCommand(ConstantsContainer.DPKG_CHECK_IS_HAVE_TELNET);
            channel.connect();
            InputStream inputStream = channel.getInputStream();
            result = IOUtil.toString(inputStream, "UTF-8");
            inputStream.close();
        } catch (JSchException | IOException e) {
            throw new JSchException("服务器命令执行错误:" + e.getMessage());
        }
        return result;
    }

    /**
     * 使用yum的方式检查
     */
    private String yumCheck(WebConnectionInfo webConnectionInfo, HttpSession session) throws JSchException {
        String result = null;
        try {
            webConnectionInfo.setChannelType(ConstantsContainer.CHANNEL_TYPE_EXEC);
            ChannelExec channel = (ChannelExec) sshConnection.open(webConnectionInfo, session.getId());
            channel.setCommand(ConstantsContainer.YUM_CHECK_IS_HAVE_TELNET);
            channel.connect();
            InputStream inputStream = channel.getInputStream();
            result = IOUtil.toString(inputStream, "UTF-8");
            inputStream.close();
        } catch (JSchException | IOException e) {
            throw new JSchException("服务器命令执行错误:" + e.getMessage());
        }
        return result;
    }

    public String getNetWorkName(String originalFilePath) throws IOException {
        log.info("获取网络名称");
        Map<Integer, String> map = new HashMap<>();
        try (FileInputStream fileInputStream = new FileInputStream(originalFilePath);
             ZipInputStream zipInputStream = new ZipInputStream(new BufferedInputStream(fileInputStream), Charset.forName("GBK"))) {
            ZipEntry ze = null;
            List<List<Object>> list;
            int i = 1;
            while (null != (ze = zipInputStream.getNextEntry())) {
                if (ze.isDirectory()) {
                    map.put(i, ze.getName().split("/")[i - 1]);
                    i++;
                }
            }
        }

        AtomicReference<String> networkName = new AtomicReference<>("");
        map.forEach((key, value) -> {
            log.info("当前判断内容为：[{}] | [{}]", key, value);
            if ("tls".equals(value) && StringUtils.isNumeric(map.get(key - 1))) {
                networkName.set(map.get(key - 2));
            }
        });
        return networkName.get();
    }

    private boolean checkZipFile(File file) throws IOException {
        boolean flag = false;
        ZipFile zipFile = ZipUtil.toZipFile(file, Charset.forName("UTF-8"));
        Enumeration enumeration = zipFile.entries();
        while (enumeration.hasMoreElements()) {
            ZipEntry zipEntry = (ZipEntry) enumeration.nextElement();
            if (zipEntry.getName().contains("..")) {
                return true;
            }
        }
        return flag;
    }

}
