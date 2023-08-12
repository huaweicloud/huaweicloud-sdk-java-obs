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

package com.obs.log;

import com.obs.services.LogConfigurator;
import com.obs.services.internal.utils.AccessLoggerUtils;

public class BasicLogger implements ILogger {
    private final java.util.logging.Logger logger;

    BasicLogger(java.util.logging.Logger logger) {
        this.logger = logger;
    }

    @Override
    public boolean isInfoEnabled() {
        return this.logger.isLoggable(LogConfigurator.INFO);
    }

    @Override
    public void info(CharSequence msg) {
        if (msg != null) {
            this.logger.info(msg.toString());
            AccessLoggerUtils.appendLog(msg, "info");
        }
    }

    @Override
    public void info(Object obj) {
        if (obj != null) {
            this.logger.info(obj.toString());
            AccessLoggerUtils.appendLog(obj, "info");
        }
    }

    @Override
    public void info(Object obj, Throwable e) {
        if (obj != null) {
            this.logger.log(LogConfigurator.INFO, obj.toString(), e);
            AccessLoggerUtils.appendLog(obj, "info");
        }
    }

    @Override
    public boolean isWarnEnabled() {
        return this.logger.isLoggable(LogConfigurator.WARN);
    }

    @Override
    public void warn(CharSequence msg) {
        if (msg != null) {
            this.logger.warning(msg.toString());
            AccessLoggerUtils.appendLog(msg, "warn");
        }
    }

    @Override
    public void warn(Object obj) {
        if (obj != null) {
            this.logger.warning(obj.toString());
            AccessLoggerUtils.appendLog(obj, "warn");
        }
    }

    @Override
    public void warn(Object obj, Throwable e) {
        if (obj != null) {
            this.logger.log(LogConfigurator.WARN, obj.toString(), e);
            AccessLoggerUtils.appendLog(obj, "warn");
        }
    }

    @Override
    public boolean isErrorEnabled() {
        return this.logger.isLoggable(LogConfigurator.ERROR);
    }

    @Override
    public void error(CharSequence msg) {
        if (msg != null) {
            this.logger.severe(msg.toString());
            AccessLoggerUtils.appendLog(msg, "error");
        }
    }

    @Override
    public void error(Object obj) {
        if (obj != null) {
            this.logger.severe(obj.toString());
            AccessLoggerUtils.appendLog(obj, "error");
        }
    }

    @Override
    public void error(Object obj, Throwable e) {
        if (obj != null) {
            this.logger.log(LogConfigurator.ERROR, obj.toString(), e);
            AccessLoggerUtils.appendLog(obj, "error");
        }
    }

    @Override
    public boolean isDebugEnabled() {
        return this.logger.isLoggable(LogConfigurator.DEBUG);
    }

    @Override
    public void debug(CharSequence msg) {
        if (msg != null) {
            this.logger.log(LogConfigurator.DEBUG, msg.toString());
            AccessLoggerUtils.appendLog(msg, "debug");
        }
    }

    @Override
    public void debug(Object obj) {
        if (obj != null) {
            this.logger.log(LogConfigurator.DEBUG, obj.toString());
            AccessLoggerUtils.appendLog(obj, "debug");
        }
    }

    @Override
    public void debug(Object obj, Throwable e) {
        if (obj != null) {
            this.logger.log(LogConfigurator.DEBUG, obj.toString(), e);
            AccessLoggerUtils.appendLog(obj, "debug");
        }
    }

    @Override
    public boolean isTraceEnabled() {
        return this.logger.isLoggable(LogConfigurator.TRACE);
    }

    @Override
    public void trace(CharSequence msg) {
        if (msg != null) {
            this.logger.log(LogConfigurator.TRACE, msg.toString());
            AccessLoggerUtils.appendLog(msg, "trace");
        }
    }

    @Override
    public void trace(Object obj) {
        if (obj != null) {
            this.logger.log(LogConfigurator.TRACE, obj.toString());
            AccessLoggerUtils.appendLog(obj, "trace");
        }
    }

    @Override
    public void trace(Object obj, Throwable e) {
        if (obj != null) {
            this.logger.log(LogConfigurator.TRACE, obj.toString(), e);
            AccessLoggerUtils.appendLog(obj, "trace");
        }
    }

    @Override
    public void accessRecord(Object obj) {
        if (obj != null) {
            this.logger.log(LogConfigurator.INFO, obj.toString());
        }
    }
}
