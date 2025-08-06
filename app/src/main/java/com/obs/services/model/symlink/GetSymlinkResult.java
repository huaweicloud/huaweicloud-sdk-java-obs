/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2025-2025. All rights reserved.
 */

package com.obs.services.model.symlink;

import com.obs.services.model.HeaderResponse;

public class GetSymlinkResult extends HeaderResponse {
    private String symlinkTarget;

    public String getSymlinkTarget() {
        return symlinkTarget;
    }

    public void setSymlinkTarget(String symlinkTarget) {
        this.symlinkTarget = symlinkTarget;
    }
}
