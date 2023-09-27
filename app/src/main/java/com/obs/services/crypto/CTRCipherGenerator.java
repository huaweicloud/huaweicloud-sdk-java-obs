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

import com.obs.log.ILogger;
import com.obs.log.LoggerBuilder;
import com.obs.services.exception.ObsException;
import com.obs.services.internal.utils.ServiceUtils;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class CTRCipherGenerator {
    public static final String ENCRYPTED_ALGORITHM = "AES256-Ctr/iv_base64/NoPadding";
    public static final int CRYPTO_KEY_BYTES_LEN = 32;
    public static final int CRYPTO_IV_BYTES_LEN = 16;
    private String masterKeyInfo;
    private byte[] cryptoIvBytes;
    private byte[] cryptoKeyBytes;

    private boolean needSha256;

    private SecureRandom secureRandom;

    protected static final String AES_ALGORITHM = "AES/CTR/NoPadding";

    static int sha256BufferLen = 65536;

    public SecureRandom getSecureRandom() {
        return secureRandom;
    }

    public void setSecureRandom(SecureRandom secureRandom) {
        this.secureRandom = secureRandom;
    }

    public String getMasterKeyInfo() {
        return masterKeyInfo;
    }

    public void setMasterKeyInfo(String masterKeyInfo) {
        this.masterKeyInfo = masterKeyInfo;
    }

    public byte[] getCryptoIvBytes() {
        return cryptoIvBytes;
    }

    public byte[] getRandomCryptoIvBytes() {
        return getRandomBytes(CRYPTO_IV_BYTES_LEN);
    }

    public void setCryptoIvBytes(byte[] cryptoIvBytes) {
        this.cryptoIvBytes = cryptoIvBytes;
    }

    public byte[] getCryptoKeyBytes() {
        return cryptoKeyBytes;
    }

    public byte[] getRandomCryptoKeyBytes() {
        return getRandomBytes(CRYPTO_KEY_BYTES_LEN);
    }
    public byte[] getRandomBytes(int randomBytesLen) {
        byte[] randomBytes;
        randomBytes = new byte[randomBytesLen];
        secureRandom.nextBytes(randomBytes);
        return randomBytes;
    }

    public void setCryptoKeyBytes(byte[] cryptoKeyBytes) {
        this.cryptoKeyBytes = cryptoKeyBytes;
    }

    public boolean isNeedSha256() {
        return needSha256;
    }

    public void setNeedSha256(boolean needSha256) {
        this.needSha256 = needSha256;
    }

    public CTRCipherGenerator(
            String masterKeyInfo,
            byte[] cryptoIvBytes,
            byte[] cryptoKeyBytes,
            boolean needSha256,
            SecureRandom secureRandom) {
        this.masterKeyInfo = masterKeyInfo;
        this.cryptoIvBytes = cryptoIvBytes;
        this.cryptoKeyBytes = cryptoKeyBytes;
        this.needSha256 = needSha256;
        this.secureRandom = secureRandom;
    }

    public CTRCipherGenerator(
            String masterKeyInfo, byte[] cryptoKeyBytes, boolean needSha256, SecureRandom secureRandom) {
        this.cryptoKeyBytes = cryptoKeyBytes;
        this.masterKeyInfo = masterKeyInfo;
        this.secureRandom = secureRandom;
        this.needSha256 = needSha256;
    }

    public CipherInputStream getAES256DecryptedStream(
            InputStream ciphertextInput, byte[] object_CryptoIvBytes, byte[] object_CryptoKeyBytes)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException,
                    InvalidKeyException {
        SecretKeySpec keySpec = new SecretKeySpec(object_CryptoKeyBytes, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(object_CryptoIvBytes);
        Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
        return new CipherInputStream(ciphertextInput, cipher);
    }

    public CipherInputStream getAES256EncryptedStream(
            InputStream plaintextInput, byte[] object_CryptoIvBytes, byte[] object_CryptoKeyBytes)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException,
                    InvalidKeyException {
        SecretKeySpec keySpec = new SecretKeySpec(object_CryptoKeyBytes, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(object_CryptoIvBytes);
        Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);

        return new CipherInputStream(plaintextInput, cipher);
    }

    public static String getBase64Info(byte[] cryptoInfo) {
        return ServiceUtils.toBase64(cryptoInfo);
    }

    public static byte[] getBytesFromBase64(String cryptoInfo) throws UnsupportedEncodingException {
        return ServiceUtils.fromBase64(cryptoInfo);
    }

    public static byte[] getAESEncryptedBytes(
            byte[] plainText, int plainTextOffset, int plainTextLength, byte[] aesKeyBytes, byte[] aesIvBytes)
            throws IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException,
                    InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException {
        SecretKeySpec keySpec = new SecretKeySpec(aesKeyBytes, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(aesIvBytes);
        Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
        return cipher.doFinal(plainText, plainTextOffset, plainTextLength);
    }

    public void setBase64AES256Key(String cryptoKeyBase64) throws UnsupportedEncodingException, ObsException {
        byte[] keyBytes = ServiceUtils.fromBase64(cryptoKeyBase64);
        if (keyBytes.length != CRYPTO_KEY_BYTES_LEN) {
            throw new ObsException("cryptoKeyBytes.length must be " + CRYPTO_KEY_BYTES_LEN);
        }
        cryptoKeyBytes = keyBytes;
    }

    public void setBase64AES256Iv(String cryptoIvBase64) throws UnsupportedEncodingException, ObsException {
        byte[] ivBytes = ServiceUtils.fromBase64(cryptoIvBase64);
        if (ivBytes.length != CRYPTO_IV_BYTES_LEN) {
            throw new ObsException("cryptoIvBytes.length must be " + CRYPTO_IV_BYTES_LEN);
        } else {
            cryptoIvBytes = ivBytes;
        }
    }

    class SHA256Info {
        protected String sha256ForPlainText;
        protected String sha256ForAESEncrypted;

        public String getSha256ForPlainText() {
            return sha256ForPlainText;
        }

        public void setSha256ForPlainText(String sha256ForPlainText) {
            this.sha256ForPlainText = sha256ForPlainText;
        }

        public String getSha256ForAESEncrypted() {
            return sha256ForAESEncrypted;
        }

        public void setSha256ForAESEncrypted(String sha256ForAESEncrypted) {
            this.sha256ForAESEncrypted = sha256ForAESEncrypted;
        }

        public SHA256Info(String sha256ForPlainText, String sha256ForAESEncrypted) {
            this.sha256ForPlainText = sha256ForPlainText;
            this.sha256ForAESEncrypted = sha256ForAESEncrypted;
        }
    }

    public static byte[] getFileSha256Bytes(String filePath) throws IOException, NoSuchAlgorithmException {
        MessageDigest fileSha256Digest = MessageDigest.getInstance("SHA-256");
        try (FileInputStream fileInputStream = new FileInputStream(filePath)) {
            byte[] buffer = new byte[sha256BufferLen];
            int bytesRead;
            while ((bytesRead = fileInputStream.read(buffer, 0, buffer.length)) != -1) {
                // 更新文件的sha256
                fileSha256Digest.update(buffer, 0, bytesRead);
            }
        }
        return fileSha256Digest.digest();
    }

    public SHA256Info computeSHA256HashAES(
            InputStream plainTextStream,
            byte[] object_CryptoIvBytes,
            byte[] object_CryptoKeyBytes,
            boolean needPlainTextSha256)
            throws NoSuchAlgorithmException, IOException, ObsException {
        BufferedInputStream bis = null;
        try {
            SecretKeySpec keySpec = new SecretKeySpec(object_CryptoKeyBytes, "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(object_CryptoIvBytes);
            Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
            bis = new BufferedInputStream(plainTextStream);
            MessageDigest plainTextSha256 = MessageDigest.getInstance("SHA-256");
            MessageDigest encryptedInfoSha256 = MessageDigest.getInstance("SHA-256");
            byte[] buffer = new byte[sha256BufferLen];
            int bytesRead;
            while ((bytesRead = bis.read(buffer, 0, buffer.length)) != -1) {
                // 加密
                byte[] encryptedData = cipher.update(buffer, 0, bytesRead);
                // 计算加密块的sha256
                encryptedInfoSha256.update(encryptedData, 0, bytesRead);
                if (needPlainTextSha256) {
                    // 计算明文的sha256
                    plainTextSha256.update(buffer, 0, bytesRead);
                }
            }

            SHA256Info sha256Info = new SHA256Info("", "");
            byte[] encryptedInfoSha256Bytes = encryptedInfoSha256.digest();
            // 转为16进制
            StringBuilder sha256Builder = new StringBuilder();
            for (byte aB : encryptedInfoSha256Bytes) {
                sha256Builder.append(String.format("%02x", aB));
            }
            sha256Info.setSha256ForAESEncrypted(sha256Builder.toString());

            if (needPlainTextSha256) {
                byte[] plainTextSha256Bytes = plainTextSha256.digest();
                sha256Builder.setLength(0);
                for (byte aB : plainTextSha256Bytes) {
                    sha256Builder.append(String.format("%02x", aB));
                }
                sha256Info.setSha256ForPlainText(sha256Builder.toString());
            }
            return sha256Info;
        } catch (Exception e) {
            throw ServiceUtils.changeFromException(e);
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    if (log.isWarnEnabled()) {
                        log.warn("close failed.", e);
                    }
                }
            }
        }
    }

    public static int getSha256BufferLen() {
        return sha256BufferLen;
    }

    public static void setSha256BufferLen(int sha256BufferLen) {
        CTRCipherGenerator.sha256BufferLen = sha256BufferLen;
    }

    public static final String ENCRYPTED_ALGORITHM_META_NAME = "encrypted-algorithm";
    public static final String ENCRYPTED_START_META_NAME = "encrypted-start";
    public static final String MASTER_KEY_INFO_META_NAME = "master-key-info";
    public static final String PLAINTEXT_SHA_256_META_NAME = "plaintext-sha256";
    public static final String PLAINTEXT_CONTENT_LENGTH_META_NAME = "plaintext-content-length";
    public static final String ENCRYPTED_SHA_256_META_NAME = "encrypted-sha256";
    private static final ILogger log = LoggerBuilder.getLogger(CTRCipherGenerator.class);
}
