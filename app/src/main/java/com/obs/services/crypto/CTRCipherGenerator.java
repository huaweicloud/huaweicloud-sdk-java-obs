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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class CTRCipherGenerator {
    public static final String ENCRYPTED_ALGORITHM = "AES256-Ctr/iv_base64/NoPadding";
    public static final int CRYPTO_KEY_BYTES_LEN = 32;
    public static final int CRYPTO_IV_BYTES_LEN = 16;

    // HMAC EtM Mode Constants (方案B)
    public static final String ENCRYPTED_ALGORITHM_CTR_HMAC = "AES256-Ctr-Hmac/iv_base64/HmacSHA256/NoPadding";
    public static final String HMAC_ALGORITHM = "HmacSHA256";
    public static final int HMAC_SHA256_BYTES_LEN = 32;
    public static final int HMAC_IV_BYTES_LEN = 16;  // HMAC mode uses 16-byte IV (same as CTR)

    private String masterKeyInfo;
    private byte[] cryptoIvBytes;
    private byte[] cryptoKeyBytes;

    private boolean needSha256;

    private SecureRandom secureRandom;

    protected static final String AES_ALGORITHM = "AES/CTR/NoPadding";

    static int sha256BufferLen = 65536;

    private static String AesCipherProvider = "";

    public static String getAesCipherProvider() {
        return AesCipherProvider;
    }

    public static void setAesCipherProvider(String aesCipherProvider) {
        AesCipherProvider = aesCipherProvider;
    }

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
            InputStream ciphertextInput, byte[] objectCryptoIvBytes, byte[] objectCryptoKeyBytes)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException,
                    InvalidKeyException, NoSuchProviderException {
        SecretKeySpec keySpec = new SecretKeySpec(objectCryptoKeyBytes, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(objectCryptoIvBytes);
        Cipher cipher = getAesCipher();
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
        return new CipherInputStream(ciphertextInput, cipher);
    }

    public CipherInputStream getAES256EncryptedStream(
            InputStream plaintextInput, byte[] objectCryptoIvBytes, byte[] objectCryptoKeyBytes)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException,
                    InvalidKeyException, NoSuchProviderException {
        SecretKeySpec keySpec = new SecretKeySpec(objectCryptoKeyBytes, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(objectCryptoIvBytes);
        Cipher cipher = getAesCipher();
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
        return new CipherInputStream(plaintextInput, cipher);
    }

    // ========== HMAC EtM Mode Methods ==========

    /**
     * Get random IV bytes for HMAC mode (16 bytes, same as CTR mode)
     */
    public byte[] getRandomHMACModeIvBytes() {
        return getRandomBytes(HMAC_IV_BYTES_LEN);
    }

    /**
     * Encrypt with HMAC integrity protection (Encrypt-then-MAC).
     * Output format: IV (16 bytes) || encrypted_data || HMAC (32 bytes)
     * <p>
     * Uses a temporary file instead of buffering the entire output in memory,
     * reducing peak memory from O(N) to O(1) (64KB buffer).
     *
     * @param plaintextInput The plaintext data to encrypt
     * @param objectCryptoIvBytes 16-byte IV
     * @param objectCryptoKeyBytes 32-byte AES key
     * @return InputStream containing IV || ciphertext || HMAC tag;
     *         the returned stream is a TempFileCleanupInputStream (extends FileInputStream)
     *         so that MayRepeatableInputStream can use FileChannel for mark/reset
     */
    public InputStream getAES256CTRHMACEncryptedStream(
            InputStream plaintextInput,
            byte[] objectCryptoIvBytes,
            byte[] objectCryptoKeyBytes)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException,
                    InvalidKeyException, NoSuchProviderException, IOException, IllegalBlockSizeException, BadPaddingException {
        // Step 1: Initialize Cipher and Mac
        SecretKeySpec keySpec = new SecretKeySpec(objectCryptoKeyBytes, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(objectCryptoIvBytes);
        Cipher cipher = getAesCipher();
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);

        byte[] hmacKey = deriveMacKey(objectCryptoKeyBytes);
        SecretKeySpec macKeySpec = new SecretKeySpec(hmacKey, HMAC_ALGORITHM);
        Mac mac = Mac.getInstance(HMAC_ALGORITHM);
        mac.init(macKeySpec);

        // Step 2: Write to temp file: IV || encrypted_data || HMAC
        File tempFile = File.createTempFile("obs-enc-", ".tmp");
        boolean success = false;
        try {
            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                // Write IV first, also feed IV into MAC
                fos.write(objectCryptoIvBytes);
                mac.update(objectCryptoIvBytes);

                // Stream plaintext through cipher to temp file in 64KB chunks
                byte[] buffer = new byte[65536];
                int bytesRead;
                while ((bytesRead = plaintextInput.read(buffer, 0, buffer.length)) != -1) {
                    byte[] encryptedChunk = cipher.update(buffer, 0, bytesRead);
                    if (encryptedChunk != null) {
                        fos.write(encryptedChunk);
                        mac.update(encryptedChunk);
                    }
                }
                byte[] finalChunk = cipher.doFinal();
                if (finalChunk != null) {
                    fos.write(finalChunk);
                    mac.update(finalChunk);
                }

                // Compute and write HMAC tag
                byte[] hmacTag = mac.doFinal();
                fos.write(hmacTag);
            }
            success = true;
        } finally {
            if (!success) {
                deleteQuietly(tempFile);
            }
            try {
                plaintextInput.close();
            } catch (IOException e) {
                if (log.isWarnEnabled()) {
                    log.warn("close plaintextInput failed.", e);
                }
            }
        }

        return new TempFileCleanupInputStream(tempFile);
    }

    /**
     * Decrypt with HMAC integrity verification (Encrypt-then-MAC).
     * Throws HMACVerificationException if integrity check fails.
     * <p>
     * Uses a temporary file to hold the ciphertext (excluding IV and HMAC),
     * reducing peak memory from O(N) to O(1) (64KB buffer + 32-byte tail buffer).
     * After HMAC verification, returns a CipherInputStream that streams decrypted
     * data from the temporary file. The temp file is deleted when the returned
     * stream (or its wrapping CipherInputStream) is closed.
     *
     * @param ciphertextInput InputStream containing IV || ciphertext || HMAC tag
     * @param objectCryptoIvBytes 16-byte IV expected from metadata header; will be validated
     *                              against the IV embedded in the ciphertext stream
     * @param objectCryptoKeyBytes 32-byte AES key
     * @return InputStream containing decrypted plaintext
     */
    public InputStream getAES256CTRHMACDecryptedStream(
            InputStream ciphertextInput,
            byte[] objectCryptoIvBytes,
            byte[] objectCryptoKeyBytes)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException,
                    InvalidKeyException, NoSuchProviderException, IOException, IllegalBlockSizeException, BadPaddingException {
        // Step 1: Read IV (16 bytes) from the input
        byte[] iv = new byte[HMAC_IV_BYTES_LEN];
        int ivRead = 0;
        while (ivRead < HMAC_IV_BYTES_LEN) {
            int n = ciphertextInput.read(iv, ivRead, HMAC_IV_BYTES_LEN - ivRead);
            if (n == -1) {
                throw new HMACVerificationException("Invalid ciphertext: data too short to contain IV");
            }
            ivRead += n;
        }

        // Validate that the IV extracted from the stream matches the IV from metadata header
        if (objectCryptoIvBytes != null && !constantTimeEquals(iv, objectCryptoIvBytes)) {
            throw new HMACVerificationException("IV mismatch: ciphertext IV does not match metadata header IV");
        }

        // Step 2: Initialize Mac with IV
        byte[] hmacKey = deriveMacKey(objectCryptoKeyBytes);
        SecretKeySpec macKeySpec = new SecretKeySpec(hmacKey, HMAC_ALGORITHM);
        Mac mac = Mac.getInstance(HMAC_ALGORITHM);
        mac.init(macKeySpec);
        mac.update(iv);

        // Step 3: Stream remaining ciphertext to temp file while computing MAC.
        // Use a 32-byte tail buffer to always retain the last 32 bytes,
        // which are the HMAC tag and must NOT be written to the temp file or fed into MAC.
        File tempFile = File.createTempFile("obs-dec-", ".tmp");
        boolean success = false;
        try {
            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                byte[] tailBuffer = new byte[HMAC_SHA256_BYTES_LEN];
                int tailLen = 0;
                byte[] readBuffer = new byte[65536];
                int bytesRead;

                while ((bytesRead = ciphertextInput.read(readBuffer, 0, readBuffer.length)) != -1) {
                    int offset = 0;
                    // If we have existing tail data, try to fill up beyond 32 bytes
                    if (tailLen > 0) {
                        // We need to flush everything except the last 32 bytes
                        int combinedLen = tailLen + bytesRead;
                        if (combinedLen <= HMAC_SHA256_BYTES_LEN) {
                            // Still not enough data to exceed the tail; just append to tailBuffer
                            System.arraycopy(readBuffer, 0, tailBuffer, tailLen, bytesRead);
                            tailLen = combinedLen;
                            continue;
                        }
                        // Flush (tailLen + bytesRead - 32) bytes
                        int flushLen = combinedLen - HMAC_SHA256_BYTES_LEN;
                        // First flush from tailBuffer
                        int flushFromTail = Math.min(flushLen, tailLen);
                        fos.write(tailBuffer, 0, flushFromTail);
                        mac.update(tailBuffer, 0, flushFromTail);
                        flushLen -= flushFromTail;
                        // Then flush from readBuffer
                        if (flushLen > 0) {
                            fos.write(readBuffer, 0, flushLen);
                            mac.update(readBuffer, 0, flushLen);
                        }
                        offset = flushLen;
                    } else {
                        // No tail data yet
                        if (bytesRead <= HMAC_SHA256_BYTES_LEN) {
                            // Not enough data; store in tailBuffer
                            System.arraycopy(readBuffer, 0, tailBuffer, 0, bytesRead);
                            tailLen = bytesRead;
                            continue;
                        }
                        // Flush all but the last 32 bytes
                        int flushLen = bytesRead - HMAC_SHA256_BYTES_LEN;
                        fos.write(readBuffer, 0, flushLen);
                        mac.update(readBuffer, 0, flushLen);
                        offset = flushLen;
                    }
                    int newTailLen = bytesRead - offset;
                    System.arraycopy(readBuffer, offset, tailBuffer, 0, newTailLen);
                    tailLen = newTailLen;
                }

                // After the loop, tailBuffer[0..tailLen) holds the last bytes read.
                // If tailLen < 32, the input was too short to contain a valid HMAC tag.
                if (tailLen < HMAC_SHA256_BYTES_LEN) {
                    throw new HMACVerificationException("Invalid ciphertext: data too short to contain HMAC tag");
                }

                // The last 32 bytes in tailBuffer are the stored HMAC tag
                byte[] storedHmacTag = new byte[HMAC_SHA256_BYTES_LEN];
                System.arraycopy(tailBuffer, tailLen - HMAC_SHA256_BYTES_LEN, storedHmacTag, 0, HMAC_SHA256_BYTES_LEN);

                // If tailLen > 32, the extra bytes before the HMAC tag are ciphertext
                // that haven't been flushed yet
                if (tailLen > HMAC_SHA256_BYTES_LEN) {
                    int extraCiphertextLen = tailLen - HMAC_SHA256_BYTES_LEN;
                    fos.write(tailBuffer, 0, extraCiphertextLen);
                    mac.update(tailBuffer, 0, extraCiphertextLen);
                }

                // Step 4: Verify HMAC
                byte[] computedHmac = mac.doFinal();
                if (!constantTimeEquals(storedHmacTag, computedHmac)) {
                    throw new HMACVerificationException("HMAC verification failed: data integrity violation detected");
                }
            }
            success = true;
        } finally {
            if (!success) {
                deleteQuietly(tempFile);
            }
            try {
                ciphertextInput.close();
            } catch (IOException e) {
                if (log.isWarnEnabled()) {
                    log.warn("close ciphertextInput failed.", e);
                }
            }
        }

        // Step 5: Return CipherInputStream wrapping the temp file
        SecretKeySpec keySpec = new SecretKeySpec(objectCryptoKeyBytes, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        Cipher cipher = getAesCipher();
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

        TempFileCleanupInputStream tempInput = new TempFileCleanupInputStream(tempFile);
        return new CipherInputStream(tempInput, cipher);
    }

    /**
     * Derive HMAC key from encryption key using a simple key derivation.
     * Uses SHA256(key || info) as the MAC key.
     */
    private byte[] deriveMacKey(byte[] encryptionKey) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        digest.update(encryptionKey);
        digest.update("hmac-integrity".getBytes(StandardCharsets.UTF_8));
        return digest.digest();
    }

    /**
     * Delete a file quietly, logging any failure but not throwing.
     * Used for cleanup on error paths.
     */
    private static void deleteQuietly(File file) {
        if (file != null && file.exists() && !file.delete() && log.isWarnEnabled()) {
            log.warn("Failed to delete temporary file: " + file.getAbsolutePath());
        }
    }

    /**
     * Constant-time comparison to prevent timing attacks.
     * Always iterates over the full length of both arrays to avoid
     * leaking length information through timing side-channels.
     */
    private boolean constantTimeEquals(byte[] a, byte[] b) {
        int result = a.length ^ b.length;
        int maxLen = Math.max(a.length, b.length);
        for (int i = 0; i < maxLen; i++) {
            result |= (i < a.length ? a[i] : 0) ^ (i < b.length ? b[i] : 0);
        }
        return result == 0;
    }

    /**
     * Check if algorithm string indicates HMAC EtM mode
     */
    public static boolean isCTRHMACAlgorithm(String algorithm) {
        return algorithm != null && algorithm.contains("Hmac");
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
                    InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException {
        SecretKeySpec keySpec = new SecretKeySpec(aesKeyBytes, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(aesIvBytes);
        Cipher cipher = getAesCipher();
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

    public static Cipher getAesCipher()
            throws NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException {
        return AesCipherProvider.equals("")
                ? Cipher.getInstance(AES_ALGORITHM)
                : Cipher.getInstance(AES_ALGORITHM, AesCipherProvider);
    }

    public SHA256Info computeSHA256HashAES(
            InputStream plainTextStream,
            byte[] objectCryptoIvBytes,
            byte[] objectCryptoKeyBytes,
            boolean needPlainTextSha256)
            throws NoSuchAlgorithmException, IOException, ObsException {
        BufferedInputStream bis = null;
        try {
            SecretKeySpec keySpec = new SecretKeySpec(objectCryptoKeyBytes, "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(objectCryptoIvBytes);
            Cipher cipher = getAesCipher();
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
