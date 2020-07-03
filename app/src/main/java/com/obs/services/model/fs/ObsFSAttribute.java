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

package com.obs.services.model.fs;

import com.obs.services.model.ObjectMetadata;

/**
 * 文件/文件夹的属性
 *
 */
public class ObsFSAttribute extends ObjectMetadata {
    private int mode = -1;

    /**
     * 获取文件/文件夹的类型
     * 
     * @return 文件/文件夹的类型
     */
    public int getMode() {
        return mode;
    }

    /**
     * 设置文件/文件夹的类型
     * 
     * @param mode
     *            文件/文件夹的类型
     */
    public void setMode(int mode) {
        this.mode = mode;
    }
}
