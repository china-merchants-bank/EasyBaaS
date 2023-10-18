package com.cmbchina.baas.easyBaas.util.shell;

import cn.hutool.core.util.StrUtil;
import com.cmbchina.baas.easyBaas.constant.ConstantsContainer;
import com.cmbchina.baas.easyBaas.mapper.UserMapper;
import com.cmbchina.baas.easyBaas.model.User;
import com.cmbchina.baas.easyBaas.model.WebConnectionInfo;
import com.cmbchina.baas.easyBaas.util.connection.SSHConnection;
import com.cmbchina.baas.easyBaas.util.secret.EncryptTools;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.SftpException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtil;

import javax.servlet.http.HttpSession;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Map;

@Slf4j
public class ShellUtils {

    /**
     * 执行shell命令
     *
     * @param webConnectionInfo
     * @param sshConnection
     * @param session
     * @param command
     * @return
     */
    public static String invokeShell(WebConnectionInfo webConnectionInfo, SSHConnection sshConnection, HttpSession session, String command)
            throws Exception {
        InputStream inputStream = null;
        String result = null;
        try {
            log.info("开始连接主机执行命令，命令为{}", command);
            webConnectionInfo.setChannelType(ConstantsContainer.CHANNEL_TYPE_EXEC);
            ChannelExec channel = (ChannelExec) sshConnection.open(webConnectionInfo, session.getId());
            channel.setCommand(command);
            channel.setPty(true);
            channel.connect();
            inputStream = channel.getInputStream();
            result = IOUtil.toString(inputStream, "UTF-8");
            log.info("连接主机执行命令结束,命令返回为{}", result);
        } catch (Exception e) {
            try {
                if (null != inputStream) {
                    inputStream.close();
                }
            } catch (IOException ioException) {
                log.error("关闭流失败{}", ioException);
            }
            log.error("登录服务器失败或执行脚本失败,[{}]", e);
            throw new Exception("登录服务器失败或执行脚本失败");
        } finally {
            if (null != inputStream) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    log.error("关闭流异常");
                }
            }
            sshConnection.close(session.getId());
        }
        return result;
    }

    /**
     * 执行shell命令
     *
     * @param webConnectionInfo
     * @param sshConnection
     * @param session
     * @param command
     * @return
     */
    public static String invokeShell2(WebConnectionInfo webConnectionInfo, SSHConnection sshConnection, HttpSession session, String command)
            throws Exception {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        PrintWriter printWriter = null;

        try {
            log.info("开始连接主机执行命令，命令为{}", command);
            webConnectionInfo.setChannelType(ConstantsContainer.CHANNEL_TYPE_SHELL);
            ChannelShell channel = (ChannelShell) sshConnection.open(webConnectionInfo, session.getId());
            channel.setPty(true);
            channel.connect();
            outputStream = channel.getOutputStream();
            outputStream.write((command + "\r\n").getBytes());
            outputStream.flush();
            Thread.sleep(3000);
            inputStream = channel.getInputStream();
            byte[] tmp = new byte[1024];
            int j = 10;
            while (j > 10) {
                j--;
                while (inputStream.available() > 0) {
                    int i = inputStream.read(tmp, 0, 1024);
                    if (i < 0) {
                        break;
                    }
                    log.info(new String(tmp, 0, i));
                }
                if (channel.isClosed()) {
                    if (inputStream.available() > 0) {
                        continue;
                    }
                    break;
                }
            }
        } catch (Exception e) {
            if (null != inputStream) {
                try {
                    inputStream.close();
                } catch (IOException ioException) {
                    log.error("关闭流失败{}", ioException);
                }
            }
            if (null != printWriter) {
                try {
                    printWriter.close();
                } catch (Exception ex) {
                    log.error("关闭流失败{}", ex);
                }
            }
            if (null != outputStream) {
                try {
                    outputStream.close();
                } catch (IOException ioException) {
                    log.error("关闭流失败{}", ioException);
                }
            }
            log.error("登录服务器失败或执行脚本失败,[{}]", e);
            throw new Exception("登录服务器失败或执行脚本失败");
        } finally {
            if (null != inputStream) {
                try {
                    inputStream.close();
                } catch (IOException ioException) {
                    log.error("关闭流失败{}", ioException);
                }
            }
            if (null != printWriter) {
                try {
                    printWriter.close();
                } catch (Exception e) {
                    log.error("关闭流失败{}", e);
                }
            }
            if (null != outputStream) {
                try {
                    outputStream.close();
                } catch (IOException ioException) {
                    log.error("关闭流失败{}", ioException);
                }
            }
            sshConnection.close(session.getId());
        }
        return null;
    }

    /**
     * sftp方式获取文件
     *
     * @param webConnectionInfo
     * @param sshConnection
     * @param session
     * @param path
     * @param fileMap
     */
    public static void useSftpToGetFiles(WebConnectionInfo webConnectionInfo, SSHConnection sshConnection, HttpSession session, String path,
                                         Map<String, String> fileMap) throws Exception {
        try {
            log.info("开始连接主机获取文件");
            webConnectionInfo.setChannelType(ConstantsContainer.CHANNEL_TYPE_SFTP);
            ChannelSftp channel = (ChannelSftp) sshConnection.open(webConnectionInfo, session.getId());
            channel.connect();
            if (StrUtil.isNotEmpty(path)) {
                channel.cd(path);
            }
            fileMap.forEach((key, value) -> {
                try {
                    channel.get(key, value);
                } catch (SftpException e) {
                    log.error("登录服务器获取文件失败{}", e);
                }
            });
            log.info("连接主机获取文件结束");
        } catch (Exception e) {
            log.error("登录服务器获取文件失败,[{}]", e);
            throw new Exception("登录服务器获取文件失败");
        } finally {
            sshConnection.close(session.getId());
        }
    }

    /**
     * sftp方式上传文件
     *
     * @param webConnectionInfo
     * @param sshConnection
     * @param session
     * @param path
     * @param fileMap
     */
    public static void useSftpToPutFiles(WebConnectionInfo webConnectionInfo, SSHConnection sshConnection, HttpSession session, String path,
                                         Map<String, String> fileMap)
            throws Exception {
        try {
            log.info("开始连接主机上传文件");
            webConnectionInfo.setChannelType(ConstantsContainer.CHANNEL_TYPE_SFTP);
            ChannelSftp channel = (ChannelSftp) sshConnection.open(webConnectionInfo, session.getId());
            channel.connect();
            if (StrUtil.isNotEmpty(path)) {
                channel.cd(path);
            }
            fileMap.forEach((key, value) -> {
                try (FileInputStream fileInputStream = new FileInputStream(key)) {
                    channel.put(fileInputStream, value);
                } catch (Exception e) {
                    log.error("登录服务器上传文件失败{}", e);
                }
            });
            log.info("连接主机上传文件结束");
        } catch (Exception e) {
            log.error("登录服务器上传文件失败,[{}]", e);
            throw new Exception("登录服务器上传文件失败");
        } finally {
            sshConnection.close(session.getId());
        }
    }

    /**
     * 获取WebconnectionInfo对象
     *
     * @param userName
     * @param passWord
     * @param nodeAddress
     * @return
     */
    public static WebConnectionInfo getWebConnectionInfo(String userName, String passWord, String nodeAddress, UserMapper userMapper,
                                                         String defaultPassword) {
        String password = "";
        if (StrUtil.isBlank(userName) && StrUtil.isBlank(password)) {
            //证明需要查库，从数据库获取数据
            User user = userMapper.selectByTypeAndAddress(ConstantsContainer.USER_TYPE_SHELL, nodeAddress);
            if (null != user) {
                userName = user.getUserName();
                password = EncryptTools.decrypt(user.getPassword());
            } else {
                userMapper.insertSelective(new User(ConstantsContainer.SHELL_USER_NAME, defaultPassword,
                        ConstantsContainer.USER_TYPE_SHELL,
                        nodeAddress, String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis())));
                userName = ConstantsContainer.SHELL_USER_NAME;
                password = EncryptTools.decrypt(defaultPassword);
            }
        } else {
            //前端使用AES加密后传递给后台，后台进行AES解密
            password = EncryptTools.decrypt(passWord);
            userMapper.updateByAddress(new User(userName, passWord, nodeAddress, String.valueOf(System.currentTimeMillis())));
        }
        return WebConnectionInfo.builder().userName(userName).password(password)
                .port(22).host(nodeAddress).channelType(ConstantsContainer.CHANNEL_TYPE_EXEC).build();
    }

}
