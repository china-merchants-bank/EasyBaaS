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

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ZipUtil;
import com.cmbchina.baas.easyBaas.exception.exceptions.UnZipException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.io.IOUtil;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

@Slf4j
public class LocalFileOperation implements FileOperation {
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
    public void deleteFile(String srcFile) {
        FileUtil.del(srcFile);
    }

    @Override
    public void deleteFolder(String srcFolder) {

    }

    @Override
    public void unzipFile(String srcFile, String destParentPath) throws UnZipException {
        File file = ZipUtil.unzip(srcFile, destParentPath);
        if (null == file) {
            log.info("解压文件[{}]异常", srcFile);
            throw new UnZipException("解压文件异常");
        }
    }

    @Override
    public void unTarGzFile(String srcFile, String destParentPath) throws IOException {
        try (FileInputStream fileInputStream = new FileInputStream(new File(srcFile));
             GZIPInputStream gzipInputStream = new GZIPInputStream(fileInputStream);
             TarArchiveInputStream fin = new TarArchiveInputStream(gzipInputStream);) {
            TarArchiveEntry entry;
            while ((entry = fin.getNextTarEntry()) != null) {
                if (entry.isDirectory()) {
                    continue;
                }
                File curfile = new File(new File(destParentPath), entry.getName());
                File parent = curfile.getParentFile();
                if (!parent.exists()) {
                    parent.mkdirs();
                }
                try (OutputStream outputStream = new FileOutputStream(curfile)) {
                    IOUtil.copy(fin, outputStream);
                }
            }
        }
    }

    @Override
    public File packingTarGzFile(String srcFile, String destParentPath, String fileName) throws IOException {
        File tarFile = new File(destParentPath, fileName + ".tar");
        File file = new File(srcFile);
        try (FileOutputStream fileOutputStream = new FileOutputStream(tarFile);
             BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
             TarArchiveOutputStream tarArchiveOutputStream = new TarArchiveOutputStream(bufferedOutputStream);) {
            tarArchiveOutputStream.setLongFileMode(TarArchiveOutputStream.LONGFILE_GNU);
            addFileToPackTarFile(file, tarArchiveOutputStream, "");
        }
        File filePath = packingGzFile(destParentPath, fileName, tarFile);
        tarFile.delete();
        return filePath;
    }

    private File packingGzFile(String destParentPath, String fileName, File tarFile) throws IOException {
        File resultFile = new File(destParentPath, fileName + ".tar.gz");
        try (FileInputStream in = new FileInputStream(tarFile);
             FileOutputStream outputStream = new FileOutputStream(resultFile);
             GZIPOutputStream out = new GZIPOutputStream(outputStream)) {
            byte[] array = new byte[1024];
            int number = -1;
            while ((number = in.read(array, 0, array.length)) != -1) {
                out.write(array, 0, number);
            }
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException("打包文件异常");
        } catch (IOException e) {
            throw new IOException("打包文件异常");
        }
        return resultFile;
    }

    private static void addFileToPackTarFile(File file, TarArchiveOutputStream tar, String prefix) throws IOException {
        TarArchiveEntry entry = new TarArchiveEntry(file, prefix + File.separator + file.getName());
        if (file.isFile()) {
            entry.setSize(file.length());
            tar.putArchiveEntry(entry);
            try (FileInputStream fileInputStream = new FileInputStream(file); BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream)) {
                IOUtil.copy(bufferedInputStream, tar);
            }
            tar.closeArchiveEntry();
        } else {
            tar.putArchiveEntry(entry);
            tar.closeArchiveEntry();
            prefix += File.separator + file.getName();
            if (null != file.listFiles()) {
                for (File f : file.listFiles()) {
                    addFileToPackTarFile(f, tar, prefix);
                }
            }
        }
    }


    public FileInputStream readFile(String srcFile) throws FileNotFoundException {
        File file = new File(srcFile);
        if (!file.exists()) {
            return null;
        }
        return new FileInputStream(file);
    }

    public void writeFile(String context, String destFile) {
    }
}
