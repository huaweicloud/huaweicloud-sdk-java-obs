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

/**
 * Exception thrown when HMAC verification fails during CTR+HMAC EtM decryption.
 * This indicates that the ciphertext has been tampered with or corrupted.
 */
public class HMACVerificationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new HMACVerificationException with the specified detail message.
     *
     * @param message the detail message
     */
    public HMACVerificationException(String message) {
        super(message);
    }

    /**
     * Constructs a new HMACVerificationException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause
     */
    public HMACVerificationException(String message, Throwable cause) {
        super(message, cause);
    }
}