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
 
package com.cmbchina.baas.easyBaas.util.secret;

import com.cmbchina.baas.easyBaas.constant.ConstantsContainer;
import com.cmbchina.baas.easyBaas.exception.exceptions.DecryptException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Slf4j
@Component
public class EncryptTools {

    public static String algorithmCode;

    /**
     * 加密
     *
     * @param content
     * @return
     */
    public static String encrypt(String content) throws Exception {
        try {
            Cipher cipher = Cipher.getInstance(ConstantsContainer.ENCRYPT_INSTANCE);
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(algorithmCode.getBytes(), "AES"));

            byte[] encryptStr = cipher.doFinal(content.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encryptStr);
        } catch (Exception e) {
            log.error("加密异常,[{}]", e.getMessage());
            throw new Exception("加密异常");
        }
    }

    /**
     * 解密
     *
     * @param content
     * @return
     */
    public static String decrypt(String content) throws DecryptException {
        try {
            byte[] encryptByte = Base64.getDecoder().decode(content);
            Cipher cipher = Cipher.getInstance(ConstantsContainer.ENCRYPT_INSTANCE);
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(algorithmCode.getBytes(), "AES"));
            byte[] decryptBytes = cipher.doFinal(encryptByte);
            return new String(decryptBytes);
        } catch (Exception e) {
            log.error("解密异常,[{}]", e.getMessage());
            throw new DecryptException("解密异常");
        }
    }

}
