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

import com.jcraft.jsch.JSchException;

import java.io.File;
import java.io.IOException;

public interface FileOperation {
    void copyFile(String srcFile, String destFile);

    void copyFolder(String srcFolder, String destParentPath);

    void moveFile(String srcFile, String destFile);

    void moveFolder(String srcFolder, String destParentPath);

    void deleteFile(String srcFile) throws JSchException;

    void deleteFolder(String srcFolder) throws JSchException, IOException;

    void unzipFile(String srcFile, String destParentPath) throws IOException;

    void unTarGzFile(String srcFile, String destParentPath) throws IOException, JSchException;

    File packingTarGzFile(String srcFile, String destParentPath, String fileName) throws IOException;
}
