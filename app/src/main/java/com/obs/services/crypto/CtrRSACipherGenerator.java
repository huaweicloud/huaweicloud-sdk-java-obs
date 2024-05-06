/**
 * Copyright 2019 Huawei Technologies Co.,Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use
 * this file except in compliance with the License.  You may obtain a copy of the
 * License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.obs.services.crypto;

import com.obs.services.exception.ObsException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class CtrRSACipherGenerator extends CTRCipherGenerator {
    public static final String ENCRYPTED_AES_KEY_META_NAME = "encrypted-object-key";
    protected static final String CIPHER_RSA_ALGORITHM = "RSA/ECB/OAEPWITHSHA-256ANDMGF1PADDING";
    protected static final String KEY_FACTORY_RSA_ALGORITHM = "RSA";
    private static String RsaCipherProvider = "";
    public static final String ENCRYPTED_ALGORITHM =
            CTRCipherGenerator.ENCRYPTED_ALGORITHM + " " + CIPHER_RSA_ALGORITHM;
    public static final int MIN_RSA_KEY_LENGTH = 3072;
    private PrivateKey privateKey;
    private PublicKey publicKey;

    public CtrRSACipherGenerator(
            String masterKeyInfo,
            boolean needSha256,
            SecureRandom secureRandom,
            PrivateKey privateKey,
            PublicKey publicKey)
            throws NoSuchAlgorithmException, InvalidKeySpecException, ObsException {
        super(masterKeyInfo, null, null, needSha256, secureRandom);
        checkKeyLength(privateKey);
        checkKeyLength(publicKey);
        this.privateKey = privateKey;
        this.publicKey = publicKey;
    }

    public static void checkKeyLength(Key key) throws ObsException {
        if (key instanceof RSAPrivateKey) {
            RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) key;
            int rsaPrivateKeyBitLength = rsaPrivateKey.getModulus().bitLength();
            if (rsaPrivateKeyBitLength < MIN_RSA_KEY_LENGTH) {
                String errorMsg =
                        "Your RSAPrivateKey bitLength is "
                                + rsaPrivateKeyBitLength
                                + ", for data safety, please use RSAKeyPair with at least "
                                + MIN_RSA_KEY_LENGTH
                                + " bitLength";
                throw new ObsException(errorMsg);
            }
        }
        if (key instanceof RSAPublicKey) {
            RSAPublicKey rsaPublicKey = (RSAPublicKey) key;
            int rsaPublicKeyBitLength = rsaPublicKey.getModulus().bitLength();
            if (rsaPublicKeyBitLength < MIN_RSA_KEY_LENGTH) {
                String errorMsg =
                        "Your RSAPublicKey bitLength is "
                                + rsaPublicKeyBitLength
                                + ", for data safety, please use RSAKeyPair with at least "
                                + MIN_RSA_KEY_LENGTH
                                + " bitLength";
                throw new ObsException(errorMsg);
            }
        }
    }

    public static PrivateKey importPKCS8PrivateKey(String filePath)
            throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        StringBuilder sb = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean startKey = false;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("-----BEGIN PRIVATE KEY-----")) {
                    startKey = true;
                } else if (line.startsWith("-----END PRIVATE KEY-----")) {
                    startKey = false;
                } else if (startKey) {
                    sb.append(line);
                }
            }
        }

        byte[] keyBytes = Base64.getDecoder().decode(sb.toString());
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance(KEY_FACTORY_RSA_ALGORITHM);
        return kf.generatePrivate(spec);
    }

    public static PublicKey importPublicKey(String filename)
            throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        StringBuilder sb = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            boolean startKey = false;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("-----BEGIN PUBLIC KEY-----")) {
                    startKey = true;
                } else if (line.startsWith("-----END PUBLIC KEY-----")) {
                    startKey = false;
                } else if (startKey) {
                    sb.append(line);
                }
            }
        }

        byte[] keyBytes = Base64.getDecoder().decode(sb.toString());
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance(KEY_FACTORY_RSA_ALGORITHM);
        return kf.generatePublic(spec);
    }

    public byte[] RSAEncrypted(byte[] plaintext)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException,
                    BadPaddingException, NoSuchProviderException {
        Cipher cipher = getRsaCipher();
        // 加密
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(plaintext);
    }

    public byte[] RSADecrypted(byte[] cipherInfo)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException,
                    BadPaddingException, NoSuchProviderException {
        Cipher cipher = getRsaCipher();
        // 解密
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(cipherInfo);
    }

    public static Cipher getRsaCipher()
            throws NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException
    {
        return RsaCipherProvider.equals("")
                ? Cipher.getInstance(CIPHER_RSA_ALGORITHM)
                : Cipher.getInstance(CIPHER_RSA_ALGORITHM, RsaCipherProvider);
    }

    public void setPrivateKey(PrivateKey privateKey) {
        this.privateKey = privateKey;
    }

    public void setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
    }

    public static String getRsaCipherProvider() {
        return RsaCipherProvider;
    }

    public static void setRsaCipherProvider(String rsaCipherProvider) {
        RsaCipherProvider = rsaCipherProvider;
    }
}
