/**
 * Copyright 2019 Huawei Technologies Co.,Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use
 * this file except in compliance with the License.  You may obtain a copy of the
 * License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 */


package com.obs.services.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * the Custom Domain Name for Bucket
 * 
 * @since
 */
public class BucketCustomDomainInfo extends HeaderResponse {
    private List<Domains> domains;
    
    public List<Domains> getDomains() {
        if (domains == null) {
            domains = new ArrayList<Domains>();
        }
        return domains;
    }

    public Domains addDomain(String domainName, Date createTime, String certificateId) {
        Domains domain = new Domains(domainName, createTime, certificateId);
        this.getDomains().add(domain);
        return domain;
    } 
    
    @Override
    public String toString() {
        return "BucketCustomDomainInfo [domains=" + Arrays.toString(this.getDomains().toArray()) + "]";
    }

    public static class Domains {
        private String domainName;
        private Date createTime;
        private String certificateId;
        
        public Domains(String domainName, Date createTime) {
            super();
            this.domainName = domainName;
            this.createTime = createTime;
        }

        public Domains(String domainName, Date createTime, String certificateId) {
            super();
            this.domainName = domainName;
            this.createTime = createTime;
            this.certificateId = certificateId;
        }
        
        public String getDomainName() {
            return domainName;
        }
        
        public void setDomainName(String domainName) {
            this.domainName = domainName;
        }

        public Date getCreateTime() {
            return createTime;
        }

        public void setCreateTime(Date createTime) {
            this.createTime = createTime;
        }

        public String getCertificateId() {
            return certificateId;
        }

        @Override
        public String toString() {
            return "Domains [domainName=" + domainName + ", " +
                    "createTime=" + createTime + ", " +
                    "certificateId=" + certificateId + "]";
        }
    }
}
