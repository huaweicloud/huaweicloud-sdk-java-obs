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

package com.obs.services.model.crr;

import com.obs.services.model.HeaderResponse;
import java.util.Date;

public class GetCrrProgressResult extends HeaderResponse {
    public GetCrrProgressResult() {
        this.time = new Date();
        this.ruleId = "";
        this.rulePrefix = "";
        this.ruleTargetBucket = "";
        this.ruleNewPendingCount = -1L;
        this.ruleNewPendingSize = -1L;
        this.ruleHistoricalProgress = "";
        this.ruleHistoricalPendingCount = -1L;
        this.ruleHistoricalPendingSize = -1L;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getRuleId() {
        return ruleId;
    }

    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }

    public String getRulePrefix() {
        return rulePrefix;
    }

    public void setRulePrefix(String rulePrefix) {
        this.rulePrefix = rulePrefix;
    }

    public String getRuleTargetBucket() {
        return ruleTargetBucket;
    }

    public void setRuleTargetBucket(String ruleTargetBucket) {
        this.ruleTargetBucket = ruleTargetBucket;
    }

    public Long getRuleNewPendingCount() {
        return ruleNewPendingCount;
    }

    public void setRuleNewPendingCount(Long ruleNewPendingCount) {
        this.ruleNewPendingCount = ruleNewPendingCount;
    }

    public Long getRuleNewPendingSize() {
        return ruleNewPendingSize;
    }

    public void setRuleNewPendingSize(Long ruleNewPendingSize) {
        this.ruleNewPendingSize = ruleNewPendingSize;
    }

    public String getRuleHistoricalProgress() {
        return ruleHistoricalProgress;
    }

    public void setRuleHistoricalProgress(String ruleHistoricalProgress) {
        this.ruleHistoricalProgress = ruleHistoricalProgress;
    }

    public Long getRuleHistoricalPendingCount() {
        return ruleHistoricalPendingCount;
    }

    public void setRuleHistoricalPendingCount(Long ruleHistoricalPendingCount) {
        this.ruleHistoricalPendingCount = ruleHistoricalPendingCount;
    }

    public Long getRuleHistoricalPendingSize() {
        return ruleHistoricalPendingSize;
    }

    public void setRuleHistoricalPendingSize(Long ruleHistoricalPendingSize) {
        this.ruleHistoricalPendingSize = ruleHistoricalPendingSize;
    }

    protected Date time;
    protected String ruleId;
    protected String rulePrefix;
    protected String ruleTargetBucket;
    protected Long ruleNewPendingCount;
    protected Long ruleNewPendingSize;
    protected String ruleHistoricalProgress;
    protected Long ruleHistoricalPendingCount;
    protected Long ruleHistoricalPendingSize;
}
