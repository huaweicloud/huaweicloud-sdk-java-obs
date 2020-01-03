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

import com.obs.log.ILogger;
import com.obs.log.LoggerBuilder;
import com.obs.services.internal.ServiceException;
import com.obs.services.internal.security.EcsSecurityUtils;
import com.obs.services.internal.security.SecurityKey;
import com.obs.services.internal.security.SecurityKeyBean;
import com.obs.services.internal.utils.JSONChange;
import com.obs.services.model.ISecurityKey;
import com.obs.services.internal.security.LimitedTimeSecurityKey;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class EcsObsCredentialsProvider implements IObsCredentialsProvider {
    private volatile LimitedTimeSecurityKey securityKey;
    private AtomicBoolean getNewKeyFlag = new AtomicBoolean(false);
    private static final ILogger ILOG = LoggerBuilder.getLogger(ObsClient.class);

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
        String detail = null;
        try {
            List<String> list = EcsSecurityUtils.getSecurityKeyInfoWithDetail();
            if (list != null && list.size() == 2) {
            	String securityKeyInfo = list.get(0);
                detail = list.get(1);
                securityInfo = (SecurityKey) JSONChange.jsonToObj(new SecurityKey(), securityKeyInfo);
            }
        } catch (ServiceException se) {
            String errorMessage = "Get securityKey form ECS failed :" + se.getMessage() + " \n the detail : " + detail;
            ILOG.warn(errorMessage);
            throw new IllegalArgumentException(errorMessage, se);
        } catch (IOException e) {
            String errorMessage = "Get securityKey form ECS failed :" + e.getMessage() + " \n the detail : " + detail;
            ILOG.warn(errorMessage);
            throw new IllegalArgumentException(errorMessage, e);
        }

        if (securityInfo == null) {
            throw new IllegalArgumentException("Invalid securityKey");
        }

        Date expiryDate = null;
        SecurityKeyBean bean = securityInfo.getBean();
        try {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
            String strDate = bean.getExpiresDate();
            expiryDate = df.parse(strDate.substring(0, strDate.length() - 4));
        } catch (ParseException e) {
            throw new IllegalArgumentException("Date parse failed :" + e.getMessage());
        }

        StringBuilder strAccess = new StringBuilder();
        String accessKey = bean.getAccessKey();
        int length = accessKey.length();
        strAccess.append(accessKey.substring(0, length / 3));
        strAccess.append("******");
        strAccess.append(accessKey.substring(2 * length / 3, length - 1));
        ILOG.warn("the AccessKey : " + strAccess.toString() + "will expiry at UTC time : " + expiryDate);

        return new LimitedTimeSecurityKey(bean.getAccessKey(), bean.getSecretKey(), bean.getSecurityToken(), expiryDate);
    }
}
