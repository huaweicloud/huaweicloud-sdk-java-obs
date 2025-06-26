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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.obs.services.internal.utils.AccessLoggerUtils;

public class Log4j2Logger implements ILogger {
    private final Object logger;

    private static class LoggerMethodHolder {
        private static Method info;

        private static Method warn;

        private static Method debug;

        private static Method trace;

        private static Method error;

        private static Method isInfo;
        private static Method isDebug;
        private static Method isError;
        private static Method isWarn;
        private static Method isTrace;

        static {
            try {
                if (LoggerBuilder.GetLoggerHolder.loggerClass != null) {
                    info = LoggerBuilder.GetLoggerHolder.loggerClass.getMethod("info", Object.class, Throwable.class);
                    warn = LoggerBuilder.GetLoggerHolder.loggerClass.getMethod("warn", Object.class, Throwable.class);
                    error = LoggerBuilder.GetLoggerHolder.loggerClass.getMethod("error", Object.class, Throwable.class);
                    debug = LoggerBuilder.GetLoggerHolder.loggerClass.getMethod("debug", Object.class, Throwable.class);
                    trace = LoggerBuilder.GetLoggerHolder.loggerClass.getMethod("trace", Object.class, Throwable.class);
                    isInfo = LoggerBuilder.GetLoggerHolder.loggerClass.getMethod("isInfoEnabled");
                    isDebug = LoggerBuilder.GetLoggerHolder.loggerClass.getMethod("isDebugEnabled");
                    isError = LoggerBuilder.GetLoggerHolder.loggerClass.getMethod("isErrorEnabled");
                    isWarn = LoggerBuilder.GetLoggerHolder.loggerClass.getMethod("isWarnEnabled");
                    isTrace = LoggerBuilder.GetLoggerHolder.loggerClass.getMethod("isTraceEnabled");
                }
            } catch (NoSuchMethodException | SecurityException e) {
            }
        }
    }

    private volatile int isInfoE = -1;
    private volatile int isDebugE = -1;
    private volatile int isErrorE = -1;
    private volatile int isWarnE = -1;
    private volatile int isTraceE = -1;

    Log4j2Logger(Object logger) {
        this.logger = logger;
    }

    public boolean isInfoEnabled() {
        if (isInfoE == -1) {
            try {
                isInfoE = (this.logger != null && LoggerMethodHolder.isInfo != null
                        && (Boolean) (LoggerMethodHolder.isInfo.invoke(this.logger))) ? 1 : 0;
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                isInfoE = 0;
            }
        }
        return isInfoE == 1;
    }

    public void info(CharSequence msg) {
        if (this.logger != null && LoggerMethodHolder.info != null) {
            try {
                LoggerMethodHolder.info.invoke(this.logger, msg, null);
                AccessLoggerUtils.appendLog(msg, "info");
            } catch (Exception ex) {
            }
        }
    }

    public void info(Object obj) {
        if (this.logger != null && LoggerMethodHolder.info != null) {
            try {
                LoggerMethodHolder.info.invoke(this.logger, obj, null);
                AccessLoggerUtils.appendLog(obj, "info");
            } catch (Exception ex) {
            }
        }
    }

    public void info(Object obj, Throwable e) {
        if (this.logger != null && LoggerMethodHolder.info != null) {
            try {
                LoggerMethodHolder.info.invoke(this.logger, obj, e);
                AccessLoggerUtils.appendLog(obj, "info");
            } catch (Exception ex) {
            }
        }
    }

    public boolean isWarnEnabled() {
        if (isWarnE == -1) {
            try {
                isWarnE = (this.logger != null && LoggerMethodHolder.isWarn != null
                        && (Boolean) (LoggerMethodHolder.isWarn.invoke(this.logger))) ? 1 : 0;
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                isWarnE = 0;
            }
        }
        return isWarnE == 1;
    }

    public void warn(CharSequence msg) {
        if (this.logger != null && LoggerMethodHolder.warn != null) {
            try {
                LoggerMethodHolder.warn.invoke(this.logger, msg, null);
                AccessLoggerUtils.appendLog(msg, "warn");
            } catch (Exception ex) {
            }
        }
    }

    public void warn(Object obj) {
        if (this.logger != null && LoggerMethodHolder.warn != null) {
            try {
                LoggerMethodHolder.warn.invoke(this.logger, obj, null);
                AccessLoggerUtils.appendLog(obj, "warn");
            } catch (Exception ex) {
            }
        }
    }

    public void warn(Object obj, Throwable e) {
        if (this.logger != null && LoggerMethodHolder.warn != null) {
            try {
                LoggerMethodHolder.warn.invoke(this.logger, obj, e);
                AccessLoggerUtils.appendLog(obj, "warn");
            } catch (Exception ex) {
            }
        }
    }

    public boolean isErrorEnabled() {
        if (isErrorE == -1) {
            try {
                isErrorE = (this.logger != null && LoggerMethodHolder.isError != null
                        && (Boolean) (LoggerMethodHolder.isError.invoke(this.logger))) ? 1 : 0;
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                isErrorE = 0;
            }
        }
        return isErrorE == 1;
    }

    public void error(CharSequence msg) {
        if (this.logger != null && LoggerMethodHolder.error != null) {
            try {
                LoggerMethodHolder.error.invoke(this.logger, msg, null);
                AccessLoggerUtils.appendLog(msg, "error");
            } catch (Exception ex) {
            }
        }
    }

    public void error(Object obj) {
        if (this.logger != null && LoggerMethodHolder.error != null) {
            try {
                LoggerMethodHolder.error.invoke(this.logger, obj, null);
                AccessLoggerUtils.appendLog(obj, "error");
            } catch (Exception ex) {
            }
        }
    }

    public void error(Object obj, Throwable e) {
        if (this.logger != null && LoggerMethodHolder.error != null) {
            try {
                LoggerMethodHolder.error.invoke(this.logger, obj, e);
                AccessLoggerUtils.appendLog(obj, "error");
            } catch (Exception ex) {
            }
        }
    }

    public boolean isDebugEnabled() {
        if (isDebugE == -1) {
            try {
                isDebugE = (this.logger != null && LoggerMethodHolder.isDebug != null
                        && (Boolean) (LoggerMethodHolder.isDebug.invoke(this.logger))) ? 1 : 0;
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                isDebugE = 0;
            }
        }
        return isDebugE == 1;
    }

    public void debug(CharSequence msg) {
        if (this.logger != null && LoggerMethodHolder.debug != null) {
            try {
                LoggerMethodHolder.debug.invoke(this.logger, msg, null);
                AccessLoggerUtils.appendLog(msg, "debug");
            } catch (Exception ex) {
            }
        }
    }

    public void debug(Object obj) {
        if (this.logger != null && LoggerMethodHolder.debug != null) {
            try {
                LoggerMethodHolder.debug.invoke(this.logger, obj, null);
                AccessLoggerUtils.appendLog(obj, "debug");
            } catch (Exception ex) {
            }
        }
    }

    public void debug(Object obj, Throwable e) {
        if (this.logger != null && LoggerMethodHolder.debug != null) {
            try {
                LoggerMethodHolder.debug.invoke(this.logger, obj, e);
                AccessLoggerUtils.appendLog(obj, "debug");
            } catch (Exception ex) {
            }
        }
    }

    public boolean isTraceEnabled() {
        if (isTraceE == -1) {
            try {
                isTraceE = (this.logger != null && LoggerMethodHolder.isTrace != null
                        && (Boolean) (LoggerMethodHolder.isTrace.invoke(this.logger))) ? 1 : 0;
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                isTraceE = 0;
            }
        }
        return isTraceE == 1;
    }

    public void trace(CharSequence msg) {
        if (this.logger != null && LoggerMethodHolder.trace != null) {
            try {
                LoggerMethodHolder.trace.invoke(this.logger, msg, null);
                AccessLoggerUtils.appendLog(msg, "trace");
            } catch (Exception ex) {
            }
        }
    }

    public void trace(Object obj) {
        if (this.logger != null && LoggerMethodHolder.trace != null) {
            try {
                LoggerMethodHolder.trace.invoke(this.logger, obj, null);
                AccessLoggerUtils.appendLog(obj, "trace");
            } catch (Exception ex) {
            }
        }
    }

    public void trace(Object obj, Throwable e) {
        if (this.logger != null && LoggerMethodHolder.trace != null) {
            try {
                LoggerMethodHolder.trace.invoke(this.logger, obj, e);
                AccessLoggerUtils.appendLog(obj, "trace");
            } catch (Exception ex) {
            }
        }
    }

    public void accessRecord(Object obj) {
        if (this.logger != null && LoggerMethodHolder.info != null) {
            try {
                LoggerMethodHolder.info.invoke(this.logger, obj, null);
            } catch (Exception ex) {
            }
        }
    }

}
