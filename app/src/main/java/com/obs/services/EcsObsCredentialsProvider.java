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
package com.obs.services;

import com.obs.services.internal.security.EcsSecurityUtils;
import com.obs.services.internal.security.SecurityKey;
import com.obs.services.internal.security.SecurityKeyBean;
import com.obs.services.model.ISecurityKey;
import com.obs.services.internal.security.LimitedTimeSecurityKey;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

public class EcsObsCredentialsProvider implements IObsCredentialsProvider {
    private volatile LimitedTimeSecurityKey securityKey;
    private AtomicBoolean getNewKeyFlag = new AtomicBoolean(false);

    @Override
    public void setSecurityKey(ISecurityKey securityKey) {
        throw new UnsupportedOperationException("EcsObsCredentialsProvider class does not support this method");
    }

    @Override
    public ISecurityKey getSecurityKey() {
        if (securityKey == null || securityKey.willSoonExpire()) {
            synchronized (this) {
                if (securityKey == null || securityKey.willSoonExpire()) {
                    securityKey = getNewSecurityKey();
                }
            }
        } else if (securityKey.aboutToExpire()) {
            refresh();
        }

        return securityKey;
    }

    private void refresh() {
        if (getNewKeyFlag.compareAndSet(false, true)) {
            try {
                securityKey = getNewSecurityKey();
            } finally {
                getNewKeyFlag.set(false);
            }
        }
    }

    private LimitedTimeSecurityKey getNewSecurityKey() {
        SecurityKey securityInfo = null;
        try {
            securityInfo = EcsSecurityUtils.getSecurityKey();
        } catch (IOException e) {
            throw new IllegalArgumentException("Get securityKey form ECS failed :" + e.getMessage());
        }

        if (securityInfo == null) {
            throw new IllegalArgumentException("Invalid securityKey");
        }

        Date expiryDate = new Date();
        SecurityKeyBean bean = securityInfo.getBean();
        try {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'");
            expiryDate = df.parse(bean.getExpiresDate());
        } catch (ParseException e) {
            throw new IllegalArgumentException("Date parse failed :" + e.getMessage());
        }

        return new LimitedTimeSecurityKey(bean.getAccessKey(), bean.getSecretKey(), bean.getSecurityToken(), expiryDate);
    }
}
