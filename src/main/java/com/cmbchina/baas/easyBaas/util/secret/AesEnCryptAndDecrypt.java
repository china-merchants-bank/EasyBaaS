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

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import com.cmbchina.baas.easyBaas.constant.ConstantsContainer;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class AesEnCryptAndDecrypt {

    /**
     * 进行加密操作
     *
     * @param context 加密原文
     * @return
     */
    public static String enCrypt(String context) throws NoSuchAlgorithmException {
        SymmetricCrypto aes = getSymmetricCrypto();
        return aes.encryptHex(context);
    }

    /**
     * 进行解密操作
     *
     * @param secret 解密的密文
     * @return
     */
    public static String deCrypt(String secret) throws NoSuchAlgorithmException {
        SymmetricCrypto aes = getSymmetricCrypto();
        return aes.decryptStr(secret, CharsetUtil.CHARSET_UTF_8);
    }

    /**
     * 生成加密对象
     *
     * @return
     */
    private static SymmetricCrypto getSymmetricCrypto() throws NoSuchAlgorithmException {
        //der编码存储的密钥
        KeyGenerator keyGenerator = KeyGenerator.getInstance(SymmetricAlgorithm.AES.getValue());
        keyGenerator.init(ConstantsContainer.AES_KEY_SIZE, new SecureRandom(ConstantsContainer.AES_KEY.getBytes()));
        SecretKey secretKey = keyGenerator.generateKey();
        byte[] encodeformat = secretKey.getEncoded();
        //生成对应key
        byte[] key = SecureUtil.generateKey(SymmetricAlgorithm.AES.getValue(), encodeformat).getEncoded();
        //加密对象
        SymmetricCrypto aes = new SymmetricCrypto(SymmetricAlgorithm.AES, key);
        return aes;
    }

}
