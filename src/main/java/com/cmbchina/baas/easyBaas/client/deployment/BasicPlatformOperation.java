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
 
package com.cmbchina.baas.easyBaas.client.deployment;

import com.cmbchina.baas.easyBaas.domain.deployment.ApplicationInfo;
import com.cmbchina.baas.easyBaas.domain.deployment.ImageInfo;

import java.util.List;

public interface BasicPlatformOperation {
    boolean loadImage(String srcImageFile);

    boolean tagImage(ImageInfo imageInfo);

    boolean deleteImage(ImageInfo imageInfo);

    List<ImageInfo> listImages();

    boolean createApplication(ImageInfo imageInfo, ApplicationInfo applicationInfo);

    boolean stopApplication(ApplicationInfo applicationInfo);

    boolean startApplication(ApplicationInfo applicationInfo);

    List<ApplicationInfo> listApplications();

    boolean deleteApplication(ApplicationInfo applicationInfo);

    String execApplication(ApplicationInfo applicationInfo, String[] execCmd) throws Exception;

    boolean rename(ApplicationInfo applicationInfo, String newName);

    boolean reStartApplication(ApplicationInfo applicationInfo);
}
